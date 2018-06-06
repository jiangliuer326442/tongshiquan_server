package webapp.user;

import java.io.IOException;
import java.util.Map;

import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;
import com.ruby.framework.function.login.thirdparty.QQ;
import com.ruby.framework.function.login.thirdparty.ThirdpartyInterface;
import com.ruby.framework.function.login.thirdparty.WX;
import com.ruby.framework.function.login.thirdparty.DD;

import org.json.JSONArray;
import org.json.JSONObject;

import webapp.company.reg.Ckuser;
import webapp.mobilemsg.Send;

/**
 * 用户板块基本功能
 * <p>Title:Base</p>
 * <p>Description:</p>
 * <p>Company:上海夜伦网络信息技术有限公司</p>
 * @author 方海亮
 * @date 2016年8月22日 下午6:00:36
 */
public class Login extends Userbase {
	
	/**
	 * 通过手机号+密码登陆
	 * @throws IOException
	 */
	public void loginbypwd() throws IOException{
		Map<String, String> form_data = new Form(context).createForm(request, "login");
		//手机号
		String phone = form_data.get("phone");
		if(phone == null || phone.equals("")){
			return_json(1002, "请填写手机号");
			return;
		}
		if(!CommonFunction.isMobileNO(phone)){
			return_json(1003, "手机号格式错误");
			return;
		}
		//密码
		String password = form_data.get("password");
		if(password == null || password.equals("")){
			return_json(1004, "请填写密码");
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
		String uid = Integer.toString(new Cklogin().getUidByPhoneAndPwd(phone, password));
		if(uid.equals("0")){
			return_json(1005, "账号或密码错误");
			return;
		}
		//保存设备信息
		int device_id = new webapp.user.device.reg().getdeviceid(vendor, uuid, Integer.parseInt(uid));
		String token = new webapp.user.Register().addtoken(uid, device_id);
        //获取绑定的企业id
        Map<String,String> usercompanyinfo = new Ckuser().getuserinfo(Integer.parseInt(uid));
        JSONObject result = new JSONObject();
        result.put("uid", uid);
        result.put("token", token);
        result.put("is_manager", usercompanyinfo.get("is_manager"));
        result.put("companyid", usercompanyinfo.get("companyid"));
        return_json(200,"登录成功",result);
	}
	
	/**
	 * 使用手机号+验证码进行登陆
	 */
	public void loginbyphone() throws IOException{
		Map<String, String> form_data = new Form(context).createForm(request, "login");
		//手机号
		String phone = form_data.get("phone");
		if(phone == null || phone.equals("")){
			return_json(1002, "请填写手机号");
			return;
		}
		if(!CommonFunction.isMobileNO(phone)){
			return_json(1003, "手机号格式错误");
			return;
		}
		//验证码
		String codes = form_data.get("codes");
		if(codes == null || codes.equals("")){
			return_json(1004, "请填写验证码");
			return;
		}
		if(!new Send().cklogcode(phone, codes)){
			return_json(1005, "验证码错误");
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
		String uid = Integer.toString(new Cklogin().getuidbyphone(phone));
		//保存设备信息
		int device_id = new webapp.user.device.reg().getdeviceid(vendor, uuid, Integer.parseInt(uid));
		String token = new webapp.user.Register().addtoken(uid, device_id);
        //获取绑定的企业id
        Map<String,String> usercompanyinfo = new Ckuser().getuserinfo(Integer.parseInt(uid));
        JSONObject result = new JSONObject();
        result.put("uid", uid);
        result.put("token", token);
        result.put("is_manager", usercompanyinfo.get("is_manager"));
        result.put("companyid", usercompanyinfo.get("companyid"));
        return_json(200,"登录成功",result);
	}
	
	/**
	 * 设置用户的qq openid
	 * 要求当前用户openid为空，并且新的openid没有被使用
	 * @throws IOException
	 */
	public void setqqopenid() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "message");
			String openid = form_data.get("openid");
			if(openid == null|| openid.equals("")){
				return_json(1001,"ooenid为空");
				return;
			}
			String result = _model.mysql_model.executeQuery("select qqcode1 from `users` where id = "+uid+" limit 1");
			if(!new JSONArray(result).getJSONObject(0).getString("qqcode1").equals("")){
				return_json(1002,"已绑定qq");
				return;
			}
			_model.mysql_model.executeUpdate("DELETE FROM users where qqcode1='"+openid+"' and phone=''");
			result = _model.mysql_model.executeQuery("select 1 from `users` where qqcode1 = '"+openid+"' limit 1");
			if(new JSONArray(result).length()>0){
				return_json(1003,"该qq号已被绑定");
				return;
			}
			_model.mysql_model.executeUpdate("update `users` set qqcode1 = '"+openid+"' where id = "+uid);
			return_json(200,"绑定qq成功");
		}
	}
	
	public void setwxopenid() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "message");
			String openid = form_data.get("openid");
			if(openid == null|| openid.equals("")){
				return_json(1001,"ooenid为空");
				return;
			}
			String unionid = form_data.get("unionid");
			if(unionid == null|| unionid.equals("")){
				return_json(1004,"unionid为空");
				return;
			}
			String result = _model.mysql_model.executeQuery("select weixinunioncode1 from `users` where id = "+uid+" limit 1");
			if(!new JSONArray(result).getJSONObject(0).getString("weixinunioncode1").equals("")){
				return_json(1002,"已绑定微信");
				return;
			}
			_model.mysql_model.executeUpdate("DELETE FROM users where weixinunioncode1='"+unionid+"' and phone=''");
			result = _model.mysql_model.executeQuery("select 1 from `users` where weixinunioncode1 = '"+unionid+"' limit 1");
			if(new JSONArray(result).length()>0){
				return_json(1003,"该微信号已被绑定");
				return;
			}
			_model.mysql_model.executeUpdate("update `users` set weixincode1 = '"+openid+"', weixinunioncode1 = '"+unionid+"' where id = "+uid);
			return_json(200,"绑定微信成功");
		}
	}
	
	public void setddopenid() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "message");
			String openid = form_data.get("openid");
			if(openid == null|| openid.equals("")){
				return_json(1001,"ooenid为空");
				return;
			}
			String unionid = form_data.get("unionid");
			if(unionid == null|| unionid.equals("")){
				return_json(1004,"unionid为空");
				return;
			}
			String result = _model.mysql_model.executeQuery("select dingdingcode1 from `users` where id = "+uid+" limit 1");
			if(!new JSONArray(result).getJSONObject(0).getString("dingdingcode1").equals("")){
				return_json(1002,"已绑定叮叮");
				return;
			}
			_model.mysql_model.executeUpdate("DELETE FROM users where dingdingcode1='"+openid+"' and phone=''");
			result = _model.mysql_model.executeQuery("select 1 from `users` where dingdingcode1 = '"+openid+"' limit 1");
			if(new JSONArray(result).length()>0){
				return_json(1003,"该叮叮号已被绑定");
				return;
			}
			_model.mysql_model.executeUpdate("update `users` set dingdingcode1 = '"+openid+"', dingdingunioncode1 = '"+unionid+"' where id = "+uid);
			return_json(200,"绑定叮叮成功");
		}
	}
	
	public void wxbind() throws IOException{
		String userinfo = null;
		ThirdpartyInterface third_login = new WX(context);
		third_login.setRedirect_URI("http://www.companyclub.cn/wxbind.jsp");
		userinfo = third_login.dologin(request, response);
		if(userinfo != null && !userinfo.equals("")){
            String[] aa = userinfo.split("\\|");
            String openid = aa[0];
            String unionid = aa[4];
            response.getWriter().write("" +
            		"<script>" +
					"		localStorage.setItem('is_fresh',1);"+
					"		localStorage.setItem('wxopenid','"+openid+"');"+
					"		localStorage.setItem('wxunionid','"+unionid+"');"+
					"		window.opener.location.reload();"+
            		"window.close();" +
            		"</script>");
		}
	}
	
	public void ddbind() throws IOException{
		String userinfo = null;
		ThirdpartyInterface third_login = new DD(context);
		third_login.setRedirect_URI("http://www.companyclub.cn/ddbind.jsp");
		userinfo = third_login.dologin(request, response);
		if(userinfo != null && !userinfo.equals("")){
            String[] aa = userinfo.split("\\|");
            String openid = aa[0];
            String unionid = aa[4];
            response.getWriter().write("" +
            		"<script>" +
					"		localStorage.setItem('is_fresh',1);"+
					"		localStorage.setItem('ddopenid','"+openid+"');"+
					"		localStorage.setItem('ddunionid','"+unionid+"');"+
					"		window.opener.location.reload();"+
            		"window.close();" +
            		"</script>");
		}
	}
	
	public void qqbind() throws IOException{
		String userinfo = null;
		ThirdpartyInterface third_login = new QQ(context);
		third_login.setRedirect_URI("http://www.companyclub.cn/qqbind.jsp");
		userinfo = third_login.dologin(request, response);
		if(userinfo != null && !userinfo.equals("")){
            String[] aa = userinfo.split("\\|");
            String openid = aa[0];
            response.getWriter().write("" +
            		"<script>" +
					"		localStorage.setItem('is_fresh',1);"+
					"		localStorage.setItem('qqopenid','"+openid+"');"+
					"		window.opener.location.reload();"+
            		"window.close();" +
            		"</script>");
		}
	}
	
	/**
	 * 使用APP进行微信登录
	 * @throws IOException
	 */
	public void wxloginbyapp() throws IOException{
		Map<String, String> form_data = new Form(context).createForm(request, "message");
		String openid = form_data.get("openid");
		if(openid == null|| openid.equals("")){
			return_json(1001,"openid为空");
			return;
		}
		String username = form_data.get("username");
		if(username == null|| username.equals("")){
			return_json(1002,"昵称为空");
			return;
		}
		String avatar1 = form_data.get("avatar1");
		if(avatar1 == null|| avatar1.equals("")){
			return_json(1003,"头像为空");
			return;
		}
		String avatar2 = form_data.get("avatar2");
		if(avatar2 == null|| avatar2.equals("")){
			return_json(1004,"头像为空");
			return;
		}
		String unionid = form_data.get("unionid");
		if(unionid == null|| unionid.equals("")){
			return_json(1005,"unionid为空");
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
        //用户注册
        new Register().reg(request,"",openid,unionid,"","",username,avatar1,avatar2,"");
        //获取用户token
        JSONObject user = new Cklogin().gettokenByopenid(unionid, uuid, vendor);
        String token = user.getString("token");
        String uid = user.getString("uid");
        //获取绑定的企业id
        Map<String,String> usercompanyinfo = new Ckuser().getuserinfo(Integer.parseInt(uid));
        JSONObject result = new JSONObject();
        result.put("uid", uid);
        result.put("token", token);
        result.put("is_manager", usercompanyinfo.get("is_manager"));
        result.put("companyid", usercompanyinfo.get("companyid"));
        return_json(200,"登录成功",result);
	}
	
	/**
	 * 微信登陆
	 * @throws IOException
	 */
	public void wxlogin() throws IOException{
		String userinfo = null;
		ThirdpartyInterface third_login = new WX(context);
		userinfo = third_login.dologin(request, response);
		if(userinfo != null && !userinfo.equals("")){
            String[] aa = userinfo.split("\\|");
            String openid = aa[0];
            String username = aa[1];
            String avatar1 = aa[2];
            String avatar2 = aa[3];
            String unionid = aa[4];
            //用户注册
            new Register().reg(request,"",openid,unionid,"","",username,avatar1,avatar2,"");
            //获取用户token
            JSONObject user = new Cklogin().gettokenByopenid(unionid, "", "");
            String token = user.getString("token");
            String uid = user.getString("uid");
            //获取绑定的企业id
            Map<String,String> usercompanyinfo = new Ckuser().getuserinfo(Integer.parseInt(uid));
            //判断用户是否是企业管理员
            response.getWriter().write("" +
            		"<script>" +
					"		localStorage.setItem('is_fresh',1);"+
					"		localStorage.setItem('uid',"+uid+");"+
					"		localStorage.setItem('token','"+token+"');"+
					"		localStorage.setItem('is_manager',"+usercompanyinfo.get("is_manager")+");"+
					"		localStorage.setItem('companyid','"+usercompanyinfo.get("companyid")+"');"+
					"		window.opener.location.reload();"+
            		"window.close();" +
            		"</script>");
		}
	}
	
	/**
	 * 叮叮登陆
	 * @throws IOException
	 */
	public void ddlogin() throws IOException{
		String userinfo = null;
		ThirdpartyInterface third_login = new DD(context);
		userinfo = third_login.dologin(request, response);
		if(userinfo != null && !userinfo.equals("")){
            String[] aa = userinfo.split("\\|");
            String openid = aa[0];
            String username = aa[1];
            String avatar = new Register().getdefaultavatar();
            String avatar1 = avatar;
            String avatar2 = avatar;
            String unionid = aa[4];
            //用户注册
            new Register().reg(request,"","","",openid,unionid,username,avatar1,avatar2,"");
            //获取用户token
            JSONObject user = new Cklogin().gettokenByopenid(openid, "", "");
            String token = user.getString("token");
            String uid = user.getString("uid");
            //获取绑定的企业id
            Map<String,String> usercompanyinfo = new Ckuser().getuserinfo(Integer.parseInt(uid));
            //判断用户是否是企业管理员
            response.getWriter().write("" +
            		"<script>" +
					"		localStorage.setItem('is_fresh',1);"+
					"		localStorage.setItem('uid',"+uid+");"+
					"		localStorage.setItem('token','"+token+"');"+
					"		localStorage.setItem('is_manager',"+usercompanyinfo.get("is_manager")+");"+
					"		localStorage.setItem('companyid','"+usercompanyinfo.get("companyid")+"');"+
					"		window.opener.location.reload();"+
            		"window.close();" +
            		"</script>");
		}
	}
	
	/**
	 * 使用APP进行QQ登录
	 * @throws IOException
	 */
	public void qqloginbyapp() throws IOException{
		Map<String, String> form_data = new Form(context).createForm(request, "message");
		String openid = form_data.get("openid");
		if(openid == null|| openid.equals("")){
			return_json(1001,"openid为空");
			return;
		}
		String username = form_data.get("username");
		if(username == null|| username.equals("")){
			return_json(1002,"昵称为空");
			return;
		}
		String avatar1 = form_data.get("avatar1");
		if(avatar1 == null|| avatar1.equals("")){
			return_json(1003,"头像为空");
			return;
		}
		String avatar2 = form_data.get("avatar2");
		if(avatar2 == null|| avatar2.equals("")){
			return_json(1004,"头像为空");
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
        //用户注册
        new Register().reg(request,openid,"","","","",username,avatar1,avatar2,"");
        //获取用户token
        JSONObject user = new Cklogin().gettokenByopenid(openid, uuid, vendor);
        String token = user.getString("token");
        String uid = user.getString("uid");
        //获取绑定的企业id
        Map<String,String> usercompanyinfo = new Ckuser().getuserinfo(Integer.parseInt(uid));
        JSONObject result = new JSONObject();
        result.put("uid", uid);
        result.put("token", token);
        result.put("is_manager", usercompanyinfo.get("is_manager"));
        result.put("companyid", usercompanyinfo.get("companyid"));
        return_json(200,"登录成功",result);
	}
	
	/**
	 * 
	 * <p>Function:QQ登录<p>
	 * 在url.txt中配置的控制器地址需要和qq登录的回调地址一致
	 * 处理qq登录返回的openid
	 * @return void
	 * @date 2016年8月5日 上午9:53:58
	 * @throws IOException
	 */
	public void qqlogin() throws IOException{
		String userinfo = null;
		ThirdpartyInterface third_login = new QQ(context);
		userinfo = third_login.dologin(request, response);
		if(userinfo != null && !userinfo.equals("")){
            String[] aa = userinfo.split("\\|");
            String openid = aa[0];
            String username = aa[1];
            String avatar1 = aa[2];
            String avatar2 = aa[3];
            //用户注册
            new Register().reg(request,openid,"","","","",username,avatar1,avatar2,"");
            //获取用户token
            JSONObject user = new Cklogin().gettokenByopenid(openid, "", "");
            String token = user.getString("token");
            String uid = user.getString("uid");
            //获取绑定的企业id
            Map<String,String> usercompanyinfo = new Ckuser().getuserinfo(Integer.parseInt(uid));
            //判断用户是否是企业管理员
            response.getWriter().write("" +
            		"<script>" +
					"		localStorage.setItem('is_fresh',1);"+
					"		localStorage.setItem('uid',"+uid+");"+
					"		localStorage.setItem('token','"+token+"');"+
					"		localStorage.setItem('is_manager',"+usercompanyinfo.get("is_manager")+");"+
					"		localStorage.setItem('companyid','"+usercompanyinfo.get("companyid")+"');"+
					"		window.opener.location.reload();"+
            		"window.close();" +
            		"</script>");
		}
	}
}
