package webapp.twitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

/**
 * 发表推特相关内容
 * 
 * @author 方海亮
 *
 */

public class Create extends ControllerBase {

	/**
	 * 发表推特 一天发表次数不能超过5次
	 * 
	 * @throws IOException
	 */
	public void index() throws IOException {
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String content = form_data.get("content");
			if (content == null || content.equals("")) {
				return_json(1001, "发表内容不能为空");
				return;
			}
			String visible = form_data.get("visible");
			if (visible == null || visible.equals("")) {
				visible = "1";
			}
			if(!new webapp.twitter.User().can_speak(uid)) {
				if(visible.equals("1") || visible.equals("3")) {
					return_json(1002, "很抱歉，管理员已禁止您的发言");
					return;
				}
			}
			JSONArray image_arr = new JSONArray(form_data.get("images").split(","));
			//分享链接
			String htmlurl = form_data.get("url");
			String linkid = "NULL";
			if (htmlurl != null && !htmlurl.equals("")) {
				JSONObject webcontent = catchurl(htmlurl);
				//保存链接
				_model.mysql_model.executeUpdate("INSERT IGNORE INTO `twt_links` (`url`, `uid`, `is_avaliable`, `title`, `content`) VALUES ('"+htmlurl+"', "+uid+", 1, '"+webcontent.getString("title")+"', '"+webcontent.getString("content")+"')");
				//获取链接ID
				linkid = Integer.toString(new JSONArray(_model.mysql_model.executeQuery("SELECT id FROM `twt_links` WHERE `url` = '"+htmlurl+"' LIMIT 1")).getJSONObject(0).getInt("id"));			
			}
			// 创建推特
			_model.mysql_model.executeUpdate(
					"INSERT INTO `twt_message` (`uid`, `companyid`, `content`, `image`, `link_id`, `visiblity`) VALUES (" + uid
							+ ", '" + companyid + "', '" + content + "', '" + image_arr.toString().replace('"', '\"')
							+ "', " + linkid + ", '" + visible + "')");
			return_json(200, "发表成功");
		}
	}

	/**
	 * 获取推特可见范围
	 * 
	 * @throws IOException
	 */
	public void getvisible() throws IOException {
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			JSONArray visible_arr;
			if(new webapp.twitter.User().can_speak(uid)) {
				visible_arr = new JSONArray(
				_model.mysql_model.executeQuery("select id,`name`,`icon` from twt_visible order by sort asc"));
			}else {
				visible_arr = new JSONArray(
				_model.mysql_model.executeQuery("select id,`name`,`icon` from twt_visible where id not in (1,3) order by sort asc"));
			}
			return_json(200, "成功", visible_arr);
		}
	}
	
	/**
	 * 上传推特文件
	 * 
	 * @throws IOException
	 */
	public void addtwtfile() throws IOException {
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String file = form_data.get("file");
			if (file == null || file.equals("")) {
				return_json(1001, "未选择文件");
			}
			return_json(200, "上传成功", file);
		}
	}
	
	/**
	 * 上传推特网页URL
	 * 
	 * @throws IOException
	 */
	public void addtwturl() throws IOException {
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			final HashMap<String, String> hm = new HashMap<String, String>();
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String htmlurl = form_data.get("url");
			if(htmlurl == null || htmlurl.equals("")){
				return_json(1002, "分享链接为空");
				return;
			}
			JSONObject webcontent = catchurl(htmlurl);
			if(webcontent != null){
				return_json(200, "抓取网页成功", webcontent);
			}
		}
	}
	
	public JSONObject catchurl(String htmlurl) throws IOException{
		Document doc = null;
		try {
			doc = Jsoup.connect(htmlurl).get();
		} catch (IOException e) {
			return_json(1001, "网址不存在");
			return null;
		}
		String meta_title = doc.title();
		String meta_keywords = "";
		String meta_content = "";
		Elements metas = doc.head().select("meta");
		for (Element meta : metas) {
			String content = meta.attr("content");
			if ("keywords".equalsIgnoreCase(meta.attr("name"))) {
				meta_keywords = content;
			}
			if ("description".equalsIgnoreCase(meta.attr("name"))) {
				meta_content = content;
			}
		}
		JSONObject webcontent = new JSONObject();
		webcontent.put("url", htmlurl);
		webcontent.put("title", meta_title);
		webcontent.put("keywords", meta_keywords);
		webcontent.put("content", meta_content);
		return webcontent;
	}

}
