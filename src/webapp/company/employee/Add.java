package webapp.company.employee;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import webapp.company.cfg.Cfg;
import webapp.user.Cklogin;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

public class Add extends ControllerBase {

	/**
	 * 添加员工
	 * @throws IOException
	 */
	public void index() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			JSONObject company_cfg_obj = new Cfg().getcompanycfg(companyid); 
			int max_employee_num = company_cfg_obj.getInt("max_employee_num");
			int current_employee_num = new Info().getcurrentempnum(companyid);
			if(current_employee_num >= max_employee_num){
				return_json(1001,"公司员工超过上限");
				return;
			}
			Map<String, String> form_data = new Form(context).createForm(request, "employee");
			String structerid = form_data.get("structerid");
			if(structerid == null || structerid.equals("0") || structerid.equals("")){
				return_json(1006, "公司部门必填");
				return;
			}
			String user_phone = form_data.get("user_phone");
			if(user_phone == null || user_phone.equals("")){
				return_json(1002, "手机号必填");
				return;
			}
			String user_namev = form_data.get("user_name");
			if(user_namev == null || user_namev.equals("")){
				return_json(1003, "姓名必填");
				return;
			}
			String user_nick = form_data.get("user_nick");
			if(user_nick == null || user_nick.equals("")){
				user_nick = user_namev;
			}
			String user_mail = form_data.get("user_mail");
			if(user_mail == null || user_mail.equals("")){
				user_mail = "";
			}
			if(!CommonFunction.isMobileNO(user_phone)){
				return_json(1004, "手机号格式不正确");
				return;
			}
			String result = _model.mysql_model.executeQuery("select 1 from companys_employees where companyid = '" + companyid + "' and user_phone = '" + user_phone + "' and stop_flg = 0");
			if(new JSONArray(result).length()>0){
				return_json(1005, "手机号已被使用");
				return;
			}
			String user_avatar = form_data.get("avatar");
			_model.mysql_model.executeUpdate("INSERT INTO `companys_employees` (`companyid`, `structerid`, `user_avatar`, `user_name`, `user_phone`, `user_nick`, `user_mail`, `createtime`, `addtime`) VALUES ('" + companyid + "', " + structerid + ", '" + user_avatar + "', '" + user_namev + "', '" + user_phone + "', '" + user_nick + "', '" + user_mail + "', NOW(), NOW())");
			return_json(200, "添加用户成功");
		}
	}
}
