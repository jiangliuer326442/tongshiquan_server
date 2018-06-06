package webapp.user;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.company.reg.Ckuser;
import webapp.mobilemsg.Send;

/**
 * 用户注册版块
 * <p>Title:Register</p>
 * <p>Description:</p>
 * <p>Company:上海夜伦网络信息技术有限公司</p>
 * @author 方海亮
 * @date 2016年8月31日 上午11:28:06
 */
public class Register extends Userbase {
	
	public String getdefaultavatar(){
		String result = _model.mysql_model.executeQuery("SELECT avatar_url FROM mst_avatar ORDER BY RAND() LIMIT 1");
		String default_avatar = "http://cdn.companyclub.cn/tongshiquan/avatar/"+new JSONArray(result).getJSONObject(0).getString("avatar_url");
		return default_avatar;
	}
	
	/**
	 * 用户使用手机号注册流程
	 * 提交信息
	 * 手机号 公司id
	 * @throws IOException 
	 */
	public void regbyphone() throws IOException{
		Map<String, String> form_data = new Form(context).createForm(request, "user_company_form");
		String default_avatar = getdefaultavatar();
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
		if(!codes.equals("0000") && !new Send().ckregcode(phone, codes)){
			return_json(1005, "验证码错误");
			return;
		}
		//密码
		String password = form_data.get("password");
		if(password == null){
			password = "";
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
		reg(request,"","","","","",phone,default_avatar,default_avatar,phone);
		//获取用户id
		String uid = Integer.toString(new Cklogin().getuidbyphone(phone));
		//生成缓存
		new Ckuser().getuserinfo(Integer.parseInt(uid));
		//获取公司ID
		String companyid = form_data.get("companyid");
		JSONObject reg_result = new webapp.company.reg.Register().regprocess(uid, companyid, phone, phone, default_avatar,password,vendor,uuid, response);
		if(reg_result!=null){
			return_json(200,"注册成功",reg_result);
		}
	}
	
	/**
	 * 用户注册逻辑
	 * 要求qqopenid不能重复
	 * 没有填写手机号的注册最终是无效的
	 * @param request
	 * @param openid
	 * @param username
	 * @param avatar1
	 * @param avatar2
	 * @throws IOException
	 */
	public void reg(HttpServletRequest request,String qqopenid,String wxopenid,String wxunionid,String ddopenid,String ddunionid,String username,String avatar1,String avatar2,String phone) throws IOException{
		if(!qqopenid.equals("")){
			_model.mysql_model.executeUpdate("DELETE FROM users where qqcode1='"+qqopenid+"' and phone=''");
			String result = _model.mysql_model.executeQuery("select 1 from `users` where qqcode1='"+qqopenid+"' limit 1");
			if(new JSONArray(result).length()==0){
				_model.mysql_model.executeUpdate("INSERT IGNORE INTO `users` (`username`, `avatar`, `avatar2`, `qqcode1`, `lstlogintime`, `lstloginip`, `phone`) VALUES ('"+username+"', '"+avatar1+"', '"+avatar2+"', '"+qqopenid+"', NOW(), '"+CommonFunction.getIp2(request)+"','"+phone+"')");
			}
		}else if(!wxopenid.equals("")){
			_model.mysql_model.executeUpdate("DELETE FROM users where weixinunioncode1='"+wxunionid+"' and phone=''");
			String result = _model.mysql_model.executeQuery("select 1 from `users` where weixinunioncode1='"+wxunionid+"' limit 1");
			if(new JSONArray(result).length()==0){
				_model.mysql_model.executeUpdate("INSERT IGNORE INTO `users` (`username`, `avatar`, `avatar2`, `weixincode1`, `weixinunioncode1`, `lstlogintime`, `lstloginip`, `phone`) VALUES ('"+username+"', '"+avatar1+"', '"+avatar2+"', '"+wxopenid+"', '"+wxunionid+"', NOW(), '"+CommonFunction.getIp2(request)+"','"+phone+"')");
			}
		}else if(!ddopenid.equals("")){
			_model.mysql_model.executeUpdate("DELETE FROM users where dingdingcode1='"+ddopenid+"' and phone=''");
			String result = _model.mysql_model.executeQuery("select 1 from `users` where dingdingcode1='"+ddopenid+"' limit 1");
			if(new JSONArray(result).length()==0){
				_model.mysql_model.executeUpdate("INSERT IGNORE INTO `users` (`username`, `avatar`, `avatar2`, `dingdingcode1`, `dingdingunioncode1`, `lstlogintime`, `lstloginip`, `phone`) VALUES ('"+username+"', '"+avatar1+"', '"+avatar2+"', '"+ddopenid+"', '"+ddunionid+"', NOW(), '"+CommonFunction.getIp2(request)+"','"+phone+"')");
			}
		}else{
			_model.mysql_model.executeUpdate("INSERT IGNORE INTO `users` (`username`, `avatar`, `avatar2`, `lstlogintime`, `lstloginip`, `phone`) VALUES ('"+username+"', '"+avatar1+"', '"+avatar2+"', NOW(), '"+CommonFunction.getIp2(request)+"','"+phone+"')");
		}
	}
	
	
	/**
	 * 保存用户手机号
	 * @param uid
	 * @param phone
	 */
	public void saveuserphone(String uid, String phone){
		String sql = "update `users` set phone = '"+phone+"' where id = "+uid;
		_model.mysql_model.executeUpdate(sql);
	}
	
	/**
	 * 创建用户token
	 * @return String
	 * @date 2016年8月31日 下午5:18:16
	 * @param uid
	 * @return String
	 */
	public String addtoken(String uid, int device_id){
		String token = CommonFunction.EncoderByMd5(UUID.randomUUID().toString());
		String expire_day = "14";
		_model.mysql_model.executeUpdate("INSERT INTO `tokens` (`userid`,`token`,`deviceid`,`inserttime`,`expiretime`) VALUES('"+uid+"','"+token+"',"+Integer.toString(device_id)+",NOW(),date_sub(NOW(),interval -"+expire_day+" day)) ON DUPLICATE KEY UPDATE `token`='"+token+"',`updatetime`=NOW(),`expiretime`=date_sub(NOW(),interval -"+expire_day+" day)");
		_model.redis_model.set(uid+"_"+Integer.toString(device_id)+"_login", token, 86400*Integer.parseInt(expire_day));
		return token;
	}
}
