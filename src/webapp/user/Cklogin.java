package webapp.user;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

public class Cklogin extends Userbase {
	public String uid = "0";
	public String openid = "";
	public String nickname = "";
	public String avatar1 = "";
	public String avatar2 = "";
	public String qqcode = "";
	public String phone = "";
	
	/**
	 * 通过openid获取用户token等信息
	 * 保存第三方推送的id
	 * @return void
	 * @date 2016年10月28日 上午11:47:34
	 * @throws IOException
	 */
	public void gettokenByopenid() throws IOException{
		Map<String, String> form_data = new Form(context).createForm(request, "register");
		openid = form_data.get("openid");
		if(openid == null){
			return_json(500, "内部错误");
			return;
		}
		//生产厂商
		String vendor = form_data.get("vendor");
		if(vendor == null){
			vendor = "";
		}
		//唯一标识
		String uuid = form_data.get("uuid");
		if(uuid == null){
			uuid = "";
		}
		JSONObject user = gettokenByopenid(openid, uuid, vendor);
		String jpushid = form_data.get("jpushid");
		if(jpushid != null && !jpushid.equals("")){
			_model.mysql_model.executeUpdate("replace into app_push values("+user.getString("uid")+",'"+jpushid+"')");
		}
		return_json(200, "获取用户信息成功", user);
	}

	public JSONObject gettokenByopenid(String openid, String uuid, String vendor) {
		String token;
		String result = _model.mysql_model
				.executeQuery("select id,username,phone,avatar,avatar2 from `users` where qqcode1='"
						+ openid + "' or weixinunioncode1='" + openid + "' or dingdingunioncode1='" + openid + "' limit 1");
		JSONArray array = new JSONArray(result);
		if (array.length() >= 1) {
			JSONObject json_object = array.getJSONObject(0); 
			uid = json_object.get("id").toString();
			nickname = json_object.get("username").toString();
			avatar1 = json_object.get("avatar").toString();
			avatar2 = json_object.get("avatar2").toString();
			phone = json_object.get("phone").toString();
			//保存设备信息
			int device_id = new webapp.user.device.reg().getdeviceid(vendor, uuid, Integer.parseInt(uid));
			token = new Register().addtoken(uid, device_id);
		} else {
			uid = "0";
			nickname = "";
			avatar1 = "";
			avatar2 = "";
			phone = "";
			token = "";
		}
		JSONObject user = new JSONObject();
		user.put("uid", uid);
		user.put("nickname", nickname);
		user.put("avatar1", avatar1);
		user.put("avatar2", avatar2);
		user.put("phone", phone);
		user.put("token", token);
		return user;
	}
	
	/**
	 * 验证登录
	 * @throws IOException 
	 */
	public boolean cklogin(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		String token = CommonFunction.getParameter(request, "token", true);
		String uuid = CommonFunction.getParameter(request, "uuid", true);
		if (token == null || token.equals("") || token.length()==0) {
			return_json(502, "token为空",response);
			return false;
		}
		int device_id = 0;
		if (uuid != null && !uuid.equals("")) {
			device_id = new JSONArray(_model.mysql_model.executeQuery("SELECT id FROM `devices` WHERE `uuid` = '"+uuid+"'")).getJSONObject(0).getInt("id");
		}
		if(!token.equals(_model.redis_model.get(uid+"_"+Integer.toString(device_id)+"_login"))){
			return_json(502, "尚未登录",response);
			return false;
		}
		return true;
	}
	
	public void getuserinfoBytoken() throws IOException{
		JSONObject userinfo = getuserinfoBytoken(request);
		if(userinfo != null){
			return_json(200,"获取用户信息成功",userinfo);
		}
	} 
	
	/**
	 * 获取账户绑定的第三方登陆
	 * @throws IOException
	 */
	public void getbindinfobyuid() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(this.cklogin(request,response)){
			String result = _model.mysql_model.executeQuery("select qqcode1,weixincode1,dingdingcode1 from `users` where id = "+uid+" limit 1");
			JSONObject bind_result = new JSONObject();
			bind_result.put("qq_flg", false);
			bind_result.put("wx_flg", false);
			bind_result.put("dd_flg", false);
			if(new JSONArray(result).length()>0){
				JSONObject user_row = new JSONArray(result).getJSONObject(0);
				if(!user_row.getString("qqcode1").equals("")){
					bind_result.put("qq_flg", true);
				}
				if(!user_row.getString("weixincode1").equals("")){
					bind_result.put("wx_flg", true);
				}
				if(!user_row.getString("dingdingcode1").equals("")){
					bind_result.put("dd_flg", true);
				}
			}
			return_json(200,"获取第三方绑定信息成功",bind_result);
		}
	}

	public JSONObject getuserinfoBytoken(HttpServletRequest request) throws IOException {
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(this.cklogin(request,response)){
			JSONObject userinfo = getuserinfoByuid(uid);
			return userinfo;
		}
		return null;
	}
	
	/**
	 * 根据用户id获取手机号
	 * @throws IOException
	 */
	public void getphonebyuid() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			String result = _model.mysql_model
					.executeQuery("select phone from `users` where id="
							+ uid + " limit 1");
			String phone = new JSONArray(result).getJSONObject(0).getString("phone");
			return_json(200,"获取手机号成功",phone);
		}
	}
	
	/**
	 * 根据手机号和密码获取用户ID
	 * @param phone
	 * @param password
	 * @return
	 * @throws IOException
	 */
	public int getUidByPhoneAndPwd(String phone, String password) throws IOException{
		JSONArray result = new JSONArray(_model.mysql_model
			.executeQuery("select id from `users` where phone='"
				+ phone + "' and `password` = '"+password+"' limit 1"));
		int id = result.length()>0?result.getJSONObject(0).getInt("id"):0;
		return id;
	}

	/**
	 * 根据uid判断密码是否正确
	 * @param uid
	 * @param password
	 * @throws IOException
	 */
	public boolean checkUserPassword(int uid, String password) throws IOException{
		JSONArray result = new JSONArray(_model.mysql_model
			.executeQuery("select id from `users` where id="
				+ Integer.toString(uid) + " and `password` = '"+password+"' limit 1"));
		if(result.length() == 1){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 根据uid设置用户密码
	 * @param uid
	 * @param password
	 * @throws IOException
	 */
	public void setUserPassword(int uid, String password) throws IOException{
		_model.mysql_model.executeUpdate("update `users` set `password` = '"+password+"' where id = " + Integer.toString(uid));
	}

	
	/**
	 * 根据手机号获取用户id
	 * @param phone
	 * @return
	 * @throws IOException
	 */
	public int getuidbyphone(String phone) throws IOException{
		String result = _model.mysql_model
				.executeQuery("select id from `users` where phone='"
						+ phone + "' limit 1");
		int id = new JSONArray(result).getJSONObject(0).getInt("id");
		return id;
	}

	public JSONObject getuserinfoByuid(String uid) {
		JSONObject userinfo = new JSONObject();
		String result = _model.mysql_model
				.executeQuery("select id,username,phone,avatar,avatar2 from `users` where id="
						+ uid + " limit 1");
		JSONArray array = new JSONArray(result);
		if (array.length() >= 1) {
			JSONObject json_object = array.getJSONObject(0);
			userinfo.put("uid", json_object.get("id").toString());
			userinfo.put("nickname", json_object.get("username").toString());
			userinfo.put("avatar1", json_object.get("avatar").toString());
			userinfo.put("avatar2", json_object.get("avatar2").toString());
			userinfo.put("phone", json_object.get("phone").toString());
		}
		return userinfo;
	}
	
	public void getuserinfoBytoken(String token) {
		String result = _model.mysql_model
				.executeQuery("select id,username,phone,avatar,avatar2 from `V_USERINFO` where token='"
						+ token + "' and expiretime>NOW() limit 1");
		JSONArray array = new JSONArray(result);
		if (array.length() >= 1) {
			JSONObject json_object = array.getJSONObject(0);
			uid = json_object.get("id").toString();
			nickname = json_object.get("username").toString();
			avatar1 = json_object.get("avatar").toString();
			avatar2 = json_object.get("avatar2").toString();
			phone = json_object.get("phone").toString();
		}
	}

	public void getuserinfoByopenid(String qqcode) {
		if (qqcode != null && !qqcode.equals("")) {
			String qqcode_tmp = _model.redis_model.get(qqcode);
			if (qqcode_tmp != null && !qqcode_tmp.equals("")) {
				String[] user_arr = qqcode_tmp.split("\\|");
				openid = user_arr[0];
				String result = _model.mysql_model
						.executeQuery("select id,username,phone,avatar,avatar2 from `users` where qqcode1='"
								+ openid + "' limit 1");
				JSONArray array = new JSONArray(result);
				if (array.length() >= 1) {
					JSONObject json_object = array.getJSONObject(0);
					uid = json_object.get("id").toString();
					nickname = json_object.get("username").toString();
					avatar1 = json_object.get("avatar").toString();
					avatar2 = json_object.get("avatar2").toString();
					phone = json_object.get("phone").toString();
				} else {
					nickname = user_arr[1];
					avatar1 = user_arr[2];
					avatar2 = user_arr[3];
				}
			}
		}
	}
	
	public JSONObject gettokenByphone(String phone) {
		String result = _model.mysql_model
				.executeQuery("select id,username,avatar,avatar2 from `users` where phone='"
						+ phone + "' limit 1");
		JSONArray array = new JSONArray(result);
		if (array.length() >= 1) {
			JSONObject json_object = array.getJSONObject(0);
			uid = json_object.get("id").toString();
			nickname = json_object.get("username").toString();
			avatar1 = json_object.get("avatar").toString();
			avatar2 = json_object.get("avatar2").toString();
		} else {
			uid = "0";
			nickname = "";
			avatar1 = "";
			avatar2 = "";
		}
		JSONObject user = new JSONObject();
		user.put("uid", uid);
		user.put("nickname", nickname);
		user.put("avatar1", avatar1);
		user.put("avatar2", avatar2);
		return user;
	}

}
