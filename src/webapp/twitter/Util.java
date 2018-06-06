package webapp.twitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

public class Util extends ControllerBase {

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
