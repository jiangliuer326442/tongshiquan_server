package webapp.company.cfg;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import webapp.user.Cklogin;

/**
 * 获取企业的配置信息
 */
import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

public class Cfg extends ControllerBase {
	/**
	 * 获取指定企业的配置信息
	 * @param companyid
	 * @return
	 */
	public JSONObject getcompanycfg(String companyid){
		JSONObject cfg_obj = null;
		String result = _model.mysql_model
		.executeQuery("select * from `companys_cfg` where companyid='"
				+  companyid + "' limit 1");
		JSONArray array = new JSONArray(result);
		if (array.length() >= 1) {
			cfg_obj = array.getJSONObject(0);
		}
		return cfg_obj;
	}
	
	/**
	 * 保存企业配置信息
	 * 如果管理员设置了企业开放注册，所有管理员添加的尚未绑定的人员全部作废
	 * @param companyid
	 * @param allow_visit_time
	 * @param is_allowregister
	 * @param is_allowvisit
	 * @param is_postbar_audit
	 * @return
	 */
	public boolean setcompanycfg(String companyid, String allow_visit_time, String is_allowregister, String is_allowvisit, String is_postbar_audit, String logo){
		if(is_allowregister.equals("1")){
			_model.mysql_model.executeUpdate("update `companys_employees` set stop_flg = 1, stoptime = NOW() where userid is NULL and companyid = '"+companyid+"'");
		}
		_model.mysql_model.executeUpdate("update `companys_cfg` set `allow_visit_time` = '"+allow_visit_time+"', `is_allow_register` = " + is_allowregister + ", `is_allowvisit` = " + is_allowvisit + ", `is_postbar_audit` = " + is_postbar_audit + ", `logo` = '" + logo + "' where companyid = '" + companyid + "'");
		return true;
	}
	
	public void getcompanycfg() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			JSONObject cfg_obj = getcompanycfg(companyid);
			return_json(200,"获取信息成功",cfg_obj);
		}
	}
	
	public void setcompanycfg() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "company");
			String allow_visit_time = form_data.get("allow_visit_time");
			String is_allowregister = form_data.get("is_allowregister");
			String is_allowvisit = form_data.get("is_allowvisit");
			String is_postbar_audit = form_data.get("is_postbar_audit");
			String logo = form_data.get("logo");
			setcompanycfg(companyid, allow_visit_time, is_allowregister, is_allowvisit, is_postbar_audit, logo);
			return_json(200,"设置信息成功",logo);
		}
	}
}
