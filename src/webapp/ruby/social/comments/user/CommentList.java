package webapp.ruby.social.comments.user;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;

import webapp.ruby.social.comments.CommentBase;

public class CommentList extends CommentBase {
	
	public void getOtherCommentsbyphone() throws IOException{
		if(!init_chk()) return;
		String phone = CommonFunction.getParameter(request, "phone",true);
		if(phone==null || phone.equals("")){
			return_json(1008,"phone:手机号没有提交");
			return;
		}
		if(!CommonFunction.isMobileNO(phone)){
			return_json(1009,"phone:手机号格式错误");
			return;
		}
		String result = _model.mysql_model.executeQuery("select userid from `" + this.database + "`.`V_PLATFORM_USERS` where `phone`='"+phone+"' and `platformid`="+plat_id+" limit 1");
		JSONArray array = new JSONArray(result);
		if(array.length()<1){
			return_json(1010,"phone:平台不存在该用户");
			return;
		}
		JSONObject json_object = array.getJSONObject(0);
		String user_id = json_object.get("userid").toString();
		String page = CommonFunction.getParameter(request, "page",true);
		if(page==null || Integer.parseInt(page)<1){
			page = "1";
		}
		String pagenum = CommonFunction.getParameter(request, "pagenum",true);
		if(pagenum==null || Integer.parseInt(pagenum)<1){
			pagenum = "10";
		}
		String from = String.valueOf((Integer.parseInt(page)-1)*Integer.parseInt(pagenum));
		result = _model.mysql_model.executeQuery("SELECT "+
"	c.id, "+
"	u.phone, "+
"	a.article_id, "+
"	c.inserttime, "+
"	c.`comment` "+
"FROM "+
"	`" + this.database + "`.comments c "+
"LEFT JOIN `" + this.database + "`.articles a ON c.article_id = a.id "+
"LEFT JOIN `" + this.database + "`.users u ON c.user_id = u.id "+
"WHERE "+
"	c.user_id != "+user_id+" "+
"AND a.id IN (SELECT DISTINCT article_id FROM `" + this.database + "`.comments WHERE user_id = "+user_id+") "+
"AND a.plat_id = "+this.plat_id+" "+
"ORDER BY "+
"	c.inserttime DESC LIMIT "+from+","+pagenum);
		JSONArray json = new JSONArray(result);
		return_json(200, "获取我参与的评论列表成功", json);
	}
	
	/**
	 * 我发表的评论列表
	 * @throws IOException
	 */
	public void getCommentlistbyphone() throws IOException{
		if(!init_chk()) return;
		String phone = CommonFunction.getParameter(request, "phone",true);
		if(phone==null || phone.equals("")){
			return_json(1008,"phone:手机号没有提交");
			return;
		}
		if(!CommonFunction.isMobileNO(phone)){
			return_json(1009,"phone:手机号格式错误");
			return;
		}
		String result = _model.mysql_model.executeQuery("select userid from `" + this.database + "`.`V_PLATFORM_USERS` where `phone`='"+phone+"' and `platformid`="+plat_id+" limit 1");
		JSONArray array = new JSONArray(result);
		if(array.length()<1){
			return_json(1010,"phone:平台不存在该用户");
			return;
		}
		JSONObject json_object = array.getJSONObject(0);
		String user_id = json_object.get("userid").toString();
		String page = CommonFunction.getParameter(request, "page",true);
		if(page==null || Integer.parseInt(page)<1){
			page = "1";
		}
		String pagenum = CommonFunction.getParameter(request, "pagenum",true);
		if(pagenum==null || Integer.parseInt(pagenum)<1){
			pagenum = "10";
		}
		String from = String.valueOf((Integer.parseInt(page)-1)*Integer.parseInt(pagenum));
		result = _model.mysql_model.executeQuery("SELECT "+
"	c.id, "+
"	a.article_id, "+
"	c.zannum, "+
"	c.inserttime, "+
"	c.`comment` "+
"FROM "+
"	`" + this.database + "`.comments c "+
"LEFT JOIN `" + this.database + "`.articles a ON c.article_id = a.id "+
"WHERE "+
"	c.user_id = "+user_id+" "+
"AND c.lastcomment_id = 0 "+
"AND a.plat_id = "+this.plat_id+" "+
"ORDER BY "+
"	c.inserttime DESC LIMIT "+from+","+pagenum);
		JSONArray json = new JSONArray(result);
		return_json(200, "获取我的评论列表成功", json);
	}
	
}
