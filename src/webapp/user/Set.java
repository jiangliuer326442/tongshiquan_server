package webapp.user;

import java.io.IOException;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;
import webapp.mobilemsg.Send;

public class Set extends Userbase {

	/**
	 * 修改手机号
	 * @throws IOException
	 */
	public void setphone() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		Cklogin cklogin = new Cklogin();
		if(cklogin.cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "login");
			String phone = form_data.get("phone");
			if(phone == null || phone.equals("")){
				return_json(2007, "请填写手机号");
				return;
			}
			if(!CommonFunction.isMobileNO(phone)){
				return_json(2008, "手机号错误");
			}
			String result = _model.mysql_model.executeQuery("SELECT 1 FROM companys_employees WHERE user_phone = '"+phone+"' AND stop_flg = 0 AND userid IS NOT NULL");
			if(new JSONArray(result).length()>0){
				return_json(2011, "新手机号已经被使用");
				return;
			}
			//验证码
			String codes = form_data.get("codes");
			if(codes == null || codes.equals("")){
				return_json(1004, "请填写验证码");
				return;
			}
			if(!new Send().ckchgcode(phone, codes)){
				return_json(1005, "验证码错误");
				return;
			}
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String platcode = new webapp.company.cfg.Cfg().getcompanycfg(companyid).getString("platformcode");
			String old_phone = userinfo.get("uphone").toString();
			result = CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_userphonechg.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platcode="+platcode+"&oldphone="+old_phone+"&newphone="+phone);					
			JSONObject jsonObject = new JSONObject(result);
			//社会化评论系统平台码
			if(jsonObject.getInt("status") != 200){
				return_json(500, "内部错误");
				return;
			}
	        _model.mysql_model.executeUpdate("UPDATE companys_employees SET user_phone = '"+phone+"' WHERE companyid = '"+companyid+"' AND userid = "+uid+" AND stop_flg = 0");
	        _model.mysql_model.executeUpdate("UPDATE users SET phone = '"+phone+"' WHERE id = "+uid);
	        _model.redis_model.set("admin_"+uid+"_uphone", phone, 86400);			
			return_json(200,"更换成功");
		}
	}


	/**
	 * 修改密码
	 * @throws IOException
	 */
	public void setpwd() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		Cklogin cklogin = new Cklogin();
		if(cklogin.cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "login");

			//密码
			String password = form_data.get("password");
			if(password == null || password.equals("")){
				return_json(1001, "请填写原来密码");
				return;
			}
			if(!cklogin.checkUserPassword(Integer.parseInt(uid), password)){
				return_json(1001, "原始密码错误");
				return;
			}
			String new_password = form_data.get("new_password");
			if(new_password == null || new_password.equals("")){
				return_json(1003, "请填写新密码");
				return;
			}
			cklogin.setUserPassword(Integer.parseInt(uid), new_password);
			return_json(200, "修改密码成功");
		}
	}

}
