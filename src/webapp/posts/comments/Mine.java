package webapp.posts.comments;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;

import webapp.company.cfg.Cfg;
import webapp.user.Cklogin;

/**
 * 我的评论列表
 * @author 方海亮
 *
 */

public class Mine extends ControllerBase{
	
	public void getwithmycomments() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			String page = CommonFunction.getParameter(request, "p", true);
			if(page == null || page.equals("")){
				page = "1";
			}
			String pagenum = CommonFunction.getParameter(request, "pnum", true);
			if(pagenum == null || pagenum.equals("")){
				pagenum = "20";
			}
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String platcode = new Cfg().getcompanycfg(companyid).getString("platformcode");
			String phone = new Cklogin().getuserinfoByuid(uid).getString("phone");
			String result = CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_withmylist.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platcode="+platcode+"&phone="+phone+"&page="+page+"&pagenum="+pagenum);
			JSONArray comment_list = new JSONObject(result).getJSONArray("data");
			JSONArray return_array = new JSONArray();
			for(int i=0; i<comment_list.length(); i++){
				JSONObject return_object = new JSONObject();
				int article_id = comment_list.getJSONObject(i).getInt("article_id");
				JSONObject comment_obj = new JSONArray(_model.mysql_model.executeQuery("select id,title,commenttimes from articles where id = "+Integer.toString(article_id))).getJSONObject(0);
				String user_phone = comment_list.getJSONObject(i).getString("phone");
				JSONObject employee_obj = new JSONArray(_model.mysql_model.executeQuery("select userid,user_name,user_avatar from companys_employees where user_phone = '"+user_phone+"' limit 1")).getJSONObject(0);
				return_object.put("id", comment_list.getJSONObject(i).getInt("id"));
				return_object.put("uid", employee_obj.getInt("userid"));
				return_object.put("user_name", employee_obj.getString("user_name"));
				return_object.put("user_avatar", employee_obj.getString("user_avatar"));
				return_object.put("comment", comment_list.getJSONObject(i).getString("comment"));
				return_object.put("comment_time", comment_list.getJSONObject(i).getString("inserttime"));
				return_object.put("article_id", comment_obj.getInt("id"));
				return_object.put("article_title", comment_obj.getString("title"));
				return_array.put(i, return_object);
			}
			return_json(200,"获取参与的评论列表成功", return_array);
		}
	}
	
	public void getmycomments() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			String page = CommonFunction.getParameter(request, "p", true);
			if(page == null || page.equals("")){
				page = "1";
			}
			String pagenum = CommonFunction.getParameter(request, "pnum", true);
			if(pagenum == null || pagenum.equals("")){
				pagenum = "20";
			}
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String platcode = new Cfg().getcompanycfg(companyid).getString("platformcode");
			String phone = new Cklogin().getuserinfoByuid(uid).getString("phone");
			String result = CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_mylist.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platcode="+platcode+"&phone="+phone+"&page="+page+"&pagenum="+pagenum);
			JSONArray comment_list = new JSONObject(result).getJSONArray("data");
			for(int i=0; i<comment_list.length(); i++){
				int article_id = comment_list.getJSONObject(i).getInt("article_id");
				JSONObject comment_obj = new JSONArray(_model.mysql_model.executeQuery("select title,commenttimes from articles where id = "+Integer.toString(article_id))).getJSONObject(0);
				String title = comment_obj.getString("title");
				int commenttimes = comment_obj.getInt("commenttimes");
				comment_list.getJSONObject(i).put("title", title);
				comment_list.getJSONObject(i).put("commenttimes", commenttimes);
			}
			return_json(200,"获取评论列表成功",comment_list);
		}
	}
	
}
