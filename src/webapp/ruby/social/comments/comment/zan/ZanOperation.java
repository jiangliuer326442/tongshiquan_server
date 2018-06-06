package webapp.ruby.social.comments.comment.zan;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;

import webapp.ruby.social.comments.CommentBase;

public class ZanOperation extends CommentBase {
	
	//评论点赞
	public void adds(){
		if(!init_chk()) return;
		String phone = CommonFunction.getParameter(request, "phone",true);
		if(phone==null || phone.equals("")){
			try {
				return_json(1008,"phone:手机号没有提交");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		if(!CommonFunction.isMobileNO(phone)){
			try {
				return_json(1009,"phone:手机号格式错误");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		String result = _model.mysql_model.executeQuery("select userid from `" + this.database + "`.`V_PLATFORM_USERS` where `phone`='"+phone+"' and `platformid`="+plat_id+" limit 1");
		JSONArray array = new JSONArray(result);
		if(array.length()<1){
			try {
				return_json(1010,"phone:平台不存在该用户");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		JSONObject json_object = array.getJSONObject(0);
		String user_id = json_object.get("userid").toString();
		String comment_id = CommonFunction.getParameter(request, "comment_id",true);
		if(comment_id==null || comment_id.equals("")){
			try {
				return_json(1004,"comment_id:请传递评论id");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		result = _model.mysql_model.executeQuery("select 1 from `" + this.database + "`.`comment_zan` where `comment_id`="+comment_id+" and `userid`="+user_id+" limit 1");
		array = new JSONArray(result);
		if(array.length()>0){
			try {
				return_json(1005,"comment_id:评论id重复");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		_model.mysql_model.executeUpdate("insert into `" + this.database + "`.`comment_zan` (`comment_id`,`userid`,`inserttime`) VALUES ("+comment_id+","+user_id+",NOW())");
		try {
			return_json(200, "成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//评论取消点赞
	public void removes(){
		if(!init_chk()) return;
		String phone = CommonFunction.getParameter(request, "phone",true);
		if(phone==null || phone.equals("")){
			try {
				return_json(1008,"phone:手机号没有提交");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		if(!CommonFunction.isMobileNO(phone)){
			try {
				return_json(1009,"phone:手机号格式错误");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		String result = _model.mysql_model.executeQuery("select userid from `" + this.database + "`.`V_PLATFORM_USERS` where `phone`='"+phone+"' and `platformid`="+plat_id+" limit 1");
		JSONArray array = new JSONArray(result);
		if(array.length()<1){
			try {
				return_json(1010,"phone:平台不存在该用户");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		JSONObject json_object = array.getJSONObject(0);
		String user_id = json_object.get("userid").toString();
		String comment_id = CommonFunction.getParameter(request, "comment_id",true);
		if(comment_id==null || comment_id.equals("")){
			try {
				return_json(1004,"comment_id:请传递评论id");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		result = _model.mysql_model.executeQuery("select 1 from `" + this.database + "`.`comment_zan` where `comment_id`="+comment_id+" and `userid`="+user_id+" limit 1");
		array = new JSONArray(result);
		if(array.length()<1){
			try {
				return_json(1005,"comment_id:评论id不存在");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		_model.mysql_model.executeUpdate("delete from `" + this.database + "`.`comment_zan` where `comment_id`="+comment_id+" and `userid`="+user_id);
		try {
			return_json(200, "成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
