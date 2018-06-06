package webapp.posts.articles;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

public class Detail extends ControllerBase{
	
	public void getarticleadminbyid() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "article");
			String aid = form_data.get("aid");
			if(aid == null || aid.equals("")){
				return_json(1001,"文章ID为空");
				return;
			}
			if(!new webapp.posts.articles.Util().check_article(aid,uid)){
				return_json(1002,"无权显示");
				return;
			}
			JSONObject json_object;
			String sql = "SELECT title,`desc`,showtime,content,is_top,is_allow_comment,is_hide_comment FROM articles WHERE id = " + aid + " LIMIT 1";
			String result = _model.mysql_model.executeQuery(sql);
			JSONArray array = new JSONArray(result);
			if(array.length() > 0){
				json_object = array.getJSONObject(0);
			}else{
				json_object = null;
			}
			return_json(200,"获取文章信息成功",json_object);
		}
	}
	
	public void getarticlebyid() throws IOException{
		Map<String, String> form_data = new Form(context).createForm(request, "article");
		String visited_companyid = form_data.get("companyid");
		String articleid = form_data.get("articleid");
		String uid = CommonFunction.getParameter(request, "uid", true);
		String token = CommonFunction.getParameter(request, "token", true);
		String uuid = CommonFunction.getParameter(request, "uuid", true);
		int device_id = 0;
		if (uuid != null && !uuid.equals("")) {
			device_id = new JSONArray(_model.mysql_model.executeQuery("SELECT id FROM `devices` WHERE `uuid` = '"+uuid+"'")).getJSONObject(0).getInt("id");
		}
		Boolean is_login = false;
		if(token.equals(_model.redis_model.get(uid+"_"+Integer.toString(device_id)+"_login"))){
			is_login = true;
		}else{
			uid = "0";
		}
		boolean ckresult = new webapp.company.reg.Ckuser().isAllowvisit(visited_companyid, Integer.parseInt(uid));
		if(ckresult){
			JSONObject json_object;
			if(articleid == null || articleid.equals("") || (Integer.parseInt(articleid)<1) ){
				json_object = null;
			}else{
				_model.mysql_model.executeUpdate("UPDATE articles SET readtimes = readtimes+1 WHERE id = " + articleid);
				String sql = "SELECT title,content,content_text,inserttime,commenttimes,a.userid,pe.user_nick,pe.user_avatar,readtimes,is_allow_comment,is_hide_comment,IFNULL((SELECT 1 FROM club_friends WHERE ((user1 = a.userid AND user2 = "+uid+" AND `status` = '好友') OR (user1 = "+uid+" AND user2 = a.userid))),0) as care_flg FROM articles a LEFT JOIN companys_employees pe ON a.userid = pe.userid and pe.companyid = (SELECT companyid FROM V_ARTICLE_PRIV WHERE id = a.id) WHERE a.id = " + articleid + " LIMIT 1";
				String result = _model.mysql_model.executeQuery(sql);
				JSONArray array = new JSONArray(result);
				if(array.length() > 0){
					json_object = array.getJSONObject(0);
				}else{
					json_object = null;
				}
			}
			if(json_object != null){
				return_json(200,"获取文章信息成功", json_object);
			}else{
				return_json(1001,"文章不存在");
			}
		}else{
			return_json(500,"非法访问");
		}
	}
}
