package webapp.ruby.social.comments.user;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;

import webapp.ruby.social.comments.CommentBase;

/**
 * 平台用户注册接口
 * <p>Title:UserRegister</p>
 * <p>Description:</p>
 * <p>Company:上海夜伦网络信息技术有限公司</p>
 * @author 方海亮
 * @date 2016年8月8日 下午5:24:19
 */
public class UserOperation extends CommentBase {

	/**
	 * 会员平台注册
	 * @param domaincode 域名注册时返回的code
	 * @param platcode 子平台在网站下的标识ID（唯一）
	 * @param phone 用户手机号
	 * @param username 用户在该平台的昵称
	 * @return void
	 * @throws IOException 
	 * @date 2016年8月8日 下午5:30:50
	 */
	public void register() throws IOException{
		if(!init_chk()) return;
		String phone = CommonFunction.getParameter(request, "phone",true);
		if(phone==null || phone.equals("")){
			return_json(1004,"phone:手机号没有提交");
			return;
		}
		if(!CommonFunction.isMobileNO(phone)){
			return_json(1005,"phone:手机号格式错误");
			return;
		}
		String uid = getuidByphone(phone);
		String username = java.net.URLDecoder.decode(CommonFunction.getParameter(request, "username",true), "utf-8");
		if(username==null || username.equals("")){
			return_json(1004,"username:昵称没有提交");
			return;
		}
		if(username.length()>30){
			return_json(1005,"username:昵称非法");
			return;
		}
		_model.mysql_model.executeUpdate("insert ignore into `" + this.database + "`.`platform_users` (`platformid`,`userid`,`inserttime`,`username`) VALUES ("+plat_id+","+uid+",NOW(),'"+username+"')");
		return_json(200,"注册平台用户成功");
	}
	
	public void chgnick() throws IOException{
		if(!init_chk()) return;
		String phone = CommonFunction.getParameter(request, "phone",true);
		if(phone==null || phone.equals("")){
			return_json(1004,"phone:旧手机号没有提交");
			return;
		}
		if(!CommonFunction.isMobileNO(phone)){
			return_json(1005,"phone:旧手机号格式错误");
			return;
		}
		String username = java.net.URLDecoder.decode(CommonFunction.getParameter(request, "username",true), "utf-8");
		if(username==null || username.equals("")){
			return_json(1004,"username:昵称没有提交");
			return;
		}
		if(username.length()>30){
			return_json(1005,"username:昵称非法");
			return;
		}
		String result = _model.mysql_model.executeQuery("select userid from `" + this.database + "`.`V_PLATFORM_USERS` where `phone`='"+phone+"' and `platformid`="+plat_id+" limit 1");
		JSONArray array = new JSONArray(result);
		if(array.length()==1){
			JSONObject json_object = array.getJSONObject(0);
			String userid = json_object.get("userid").toString();
			_model.mysql_model.executeUpdate("update `" + this.database + "`.`platform_users` set `username`='"+username+"' where userid = "+userid);
			return_json(200,"用户昵称更新成功");
		}else{
			return_json(1009,"phone:不存在该员工");
			return;
		}
	}
	
	/**
	 * 更新会员手机号
	 * 会员更新了手机号后需要同步更新到社会化评论系统中
	 * @param domaincode 域名注册时返回的code
	 * @param platcode 子平台在网站下的标识ID（唯一）
	 * @param oldphone 用户旧手机号
	 * @param newphone 用户新手机号
	 * @return void
	 * @throws IOException 
	 * @date 2016年8月15日 下午3:51:03
	 */
	public void chgphone() throws IOException{
		if(!init_chk()) return;
		String oldphone = CommonFunction.getParameter(request, "oldphone",true);
		if(oldphone==null || oldphone.equals("")){
			return_json(1004,"oldphone:旧手机号没有提交");
			return;
		}
		if(!CommonFunction.isMobileNO(oldphone)){
			return_json(1005,"oldphone:旧手机号格式错误");
			return;
		}
		String newphone = CommonFunction.getParameter(request, "newphone",true);
		if(newphone==null || newphone.equals("")){
			return_json(1006,"newphone:新手机号没有提交");
			return;
		}
		if(!CommonFunction.isMobileNO(newphone)){
			return_json(1007,"newphone:新手机号格式错误");
			return;
		}
		if(oldphone.equals(newphone)){
			return_json(1008,"newphone:新手机号和旧手机号不能一样");
			return;
		}
		String result = _model.mysql_model.executeQuery("select userid from `" + this.database + "`.`V_PLATFORM_USERS` where `phone`='"+oldphone+"' and `platformid`="+plat_id+" limit 1");
		JSONArray array = new JSONArray(result);
		if(array.length()==1){
			JSONObject json_object = array.getJSONObject(0);
			String userid = json_object.get("userid").toString();
			result = _model.mysql_model.executeQuery("select userid from `" + this.database + "`.`V_PLATFORM_USERS` where `phone`='"+newphone+"' and `platformid`="+plat_id+" limit 1");
			array = new JSONArray(result);
			if(array.length()==1){
				return_json(1010,"newphone:新手机号已被其他员工占用");
				return;
			}
			_model.mysql_model.executeUpdate("update `" + this.database + "`.`users` set `phone`='"+newphone+"' where id = "+userid);
			return_json(200,"用户手机号更新成功");
		}else{
			return_json(1009,"oldphone:不存在该员工");
			return;
		}
	}
	
	/**
	 * 通过手机号获取用户id
	 * @return String
	 * @date 2016年8月8日 下午5:48:13
	 * @param phone
	 * @return
	 */
	public String getuidByphone(String phone){
		String result = _model.mysql_model.executeQuery("select id from `" + this.database + "`.`users` where `phone`='"+phone+"' limit 1");
		JSONArray array = new JSONArray(result);
		if(array.length()==1){
			JSONObject json_object = array.getJSONObject(0);
			return json_object.get("id").toString();
		}
		_model.mysql_model.executeUpdate("insert into `" + this.database + "`.`users` (`phone`) VALUES ('"+phone+"')");
		result = _model.mysql_model.executeQuery("select @@IDENTITY as id");
		array = new JSONArray(result);
		JSONObject json_object = array.getJSONObject(0);
		return json_object.get("id").toString();
	}
}
