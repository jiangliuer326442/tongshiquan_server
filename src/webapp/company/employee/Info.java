package webapp.company.employee;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;

public class Info extends ControllerBase {
	
	/**
	 * 根据员工uid获取eid
	 * @param companyid
	 * @param uid
	 * @return
	 */
	public String geteid(String companyid, String uid){
		//获取员工的id
		String result = _model.mysql_model.executeQuery("SELECT id FROM companys_employees WHERE stop_flg = 0 AND companyid = '" + companyid + "' AND userid = " + uid + " LIMIT 1");
		JSONObject employee_object = new JSONArray(result).getJSONObject(0);
		String eid = Integer.toString(employee_object.getInt("id"));
		return eid;
	}
	
	/**
	 * 根据手机号获取头像
	 * @param companyid
	 * @param phone
	 * @return
	 */
	public String getavatarbyphone(String companyid, String phone){
		String result = _model.mysql_model.executeQuery("select user_avatar from companys_employees where companyid = '" + companyid + "' and user_phone = '" + phone + "' limit 1");
		JSONArray array = new JSONArray(result);
		String avatar = "";
		if (array.length() >= 1) {
			avatar = array.getJSONObject(0).getString("user_avatar");
		}
		return avatar;
	}
	
	/**
	 * 验证公司是否有某个手机号
	 * @param companyid
	 * @param phone
	 * @return
	 */
	public boolean ckemployeebyphone(String companyid, String phone){
		String result = _model.mysql_model.executeQuery("select 1 from companys_employees where companyid = '" + companyid + "' and user_phone = '" + phone + "' and userid is not NULL and stop_flg = 0");
		JSONArray array = new JSONArray(result);
		if (array.length() >= 1) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取公司员工数量
	 * @param companyid
	 * @return
	 */
	public int getcurrentempnum(String companyid){
		int current_employee_num = 0;
		//实际员工数量
		String result = _model.mysql_model.executeQuery("select count(1) as employee_num from companys_employees where companyid = '" + companyid + "' and stop_flg = 0");
		JSONArray array = new JSONArray(result);
		if (array.length() >= 1) {
			current_employee_num = array.getJSONObject(0).getInt("employee_num");
		}
		return current_employee_num;
	}
}
