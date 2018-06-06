package webapp.posts.comments;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.company.cfg.Cfg;
import webapp.company.employee.Info;
import webapp.user.Cklogin;

public class Lists extends ControllerBase{
	
	public void getcommentlist_flat() throws IOException{
		String my_phone = "";
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(uid != null && !uid.equals("")){
			if(new Cklogin().cklogin(request,response)){
				my_phone = new Cklogin().getuserinfoByuid(uid).getString("phone");
			}else{
				uid = "0";
			}
		}else{
			uid = "0";
		}
		String page = CommonFunction.getParameter(request, "p", true);
		if(page == null || page.equals("")){
			page = "1";
		}
		String pagenum = CommonFunction.getParameter(request, "pnum", true);
		if(pagenum == null || pagenum.equals("")){
			pagenum = "20";
		}
		Map<String, String> form_data = new Form(context).createForm(request, "comment");
		String companyid = form_data.get("companyid");
		if(companyid == null || companyid.equals("")){
			return_json(1001,"公司id不存在");
			return;
		}
		String platcode = new Cfg().getcompanycfg(companyid).getString("platformcode");
		String aid = form_data.get("aid");
		if(aid == null || aid.equals("")){
			return_json(1002,"文章id不存在");
			return;
		}
		String result = CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_list_flat.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platcode="+platcode+"&article_id="+aid+"&phone="+my_phone+"&page="+page+"&pagenum="+pagenum);
		JSONObject result_object = new JSONObject(result);
		String comment_num = result_object.getString("info");
		JSONArray commentList = result_object.getJSONArray("data");
		JSONArray return_commentlist = new JSONArray();
		for(int i=0; i<commentList.length(); i++){
			JSONObject return_commentobject = new JSONObject();
			JSONObject comment_object = commentList.getJSONObject(i);
			String phone = comment_object.getString("user_phone");
			int commenter_id = new Cklogin().getuidbyphone(phone);
			//判断当前用户和评论者的关系
			result = _model.mysql_model.executeQuery("SELECT 1 as care_flg FROM club_friends WHERE ((user1 = "+Integer.valueOf(commenter_id)+" AND user2 = "+uid+" AND `status` = '好友') OR (user1 = "+uid+" AND user2 = "+Integer.valueOf(commenter_id)+"))");
			return_commentobject.put("care_flg", new JSONArray(result).length()>0 ? 1:0);
			result = _model.mysql_model.executeQuery("SELECT getusernick("+uid+",\""+companyid+"\","+commenter_id+") as nick, getuseravatar('"+companyid+"', "+commenter_id+") as avatar LIMIT 1");
			JSONObject userinfo = new JSONArray(result).getJSONObject(0);
			return_commentobject.put("avatar", userinfo.getString("avatar"));
			return_commentobject.put("uid", commenter_id);
			return_commentobject.put("phone", phone);
			return_commentobject.put("iszan", comment_object.getInt("iszan"));
			return_commentobject.put("nick", userinfo.getString("nick"));
			return_commentobject.put("comment", comment_object.getString("comments"));
			return_commentobject.put("zannum", comment_object.getInt("comment_zannum"));
			return_commentobject.put("time", comment_object.getString("comment_inserttime"));
			return_commentobject.put("id", comment_object.getInt("comment_id"));
			return_commentobject.put("to", comment_object.getString("to_username"));
			return_commentobject.put("to_content", comment_object.getString("to_content"));
			return_commentlist.put(i, return_commentobject);
		}
		return_json(200,comment_num,return_commentlist);
	}
	
	public void getcommentlist_hierarchal() throws IOException{
		String my_phone = "";
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(uid != null && !uid.equals("")){
			if(new Cklogin().cklogin(request,response)){
				my_phone = new Cklogin().getuserinfoByuid(uid).getString("phone");
			}else{
				return;
			}
		}
		String page = CommonFunction.getParameter(request, "p", true);
		String pagenum = "20";
		if(page == null || page.equals("")){
			page = "1";
		}
		Map<String, String> form_data = new Form(context).createForm(request, "comment");
		String companyid = form_data.get("companyid");
		if(companyid == null || companyid.equals("")){
			return_json(1001,"公司id不存在");
			return;
		}
		String platcode = new Cfg().getcompanycfg(companyid).getString("platformcode");
		String aid = form_data.get("aid");
		if(aid == null || aid.equals("")){
			return_json(1002,"文章id不存在");
			return;
		}
		String result = CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_list.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platcode="+platcode+"&article_id="+aid+"&phone="+my_phone+"&page="+page+"&pagenum="+pagenum);
		JSONObject result_object = new JSONObject(result);
		JSONArray commentList = result_object.getJSONArray("data");
		JSONArray return_commentlist = new JSONArray();
		for(int i=0; i<commentList.length(); i++){
			JSONObject return_commentobject = new JSONObject();
			JSONObject comment_object = commentList.getJSONObject(i);
			String phone = comment_object.getString("user_phone");
			String avatar = new Info().getavatarbyphone(companyid, phone);
			return_commentobject.put("avatar", avatar);
			return_commentobject.put("phone", phone);
			return_commentobject.put("iszan", comment_object.getInt("iszan"));
			return_commentobject.put("nick", comment_object.getString("user_name"));
			return_commentobject.put("comment", comment_object.getString("comments"));
			return_commentobject.put("zannum", comment_object.getInt("comment_zannum"));
			return_commentobject.put("time", comment_object.getString("comment_inserttime"));
			return_commentobject.put("id", comment_object.getInt("comment_id"));
			return_commentobject.put("history_records", comment_object.getJSONArray("history_records"));
			return_commentlist.put(i, return_commentobject);
		}
		return_json(200,"获取评论列表成功",return_commentlist);
	}
}
