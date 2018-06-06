package webapp.company.info;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;

import webapp.user.Cklogin;

/**
 * 获取用户对应的企业信息
 * @author SC000749
 *
 */
public class Getcompany extends ControllerBase {
	
	/**
	 * 获取我的企业信息
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public void getmycompanyinfo() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			JSONObject companyinfo = getcompanybycompanyid(companyid);
			return_json(200, "获取企业信息成功", companyinfo);
		}
	}
	
	/**
	 * 获取用户绑定的企业
	 * @param userid
	 * @return
	 */
	public String getcompanyidbyuser(int userid){
		String companyid = "0";
		String result = _model.mysql_model
				.executeQuery("select companyid,bindstatus from `user_bindcompany_record` where userid="
						+  Integer.toString(userid) + " order by bindtime desc limit 1");
		JSONArray array = new JSONArray(result);
		if (array.length() >= 1) {
			JSONObject json_object = array.getJSONObject(0);
			if(json_object.getBoolean("bindstatus")){
				companyid = json_object.getString("companyid");
			}
		}
		return companyid;
	}
	
	/**
	 * 根据ID获取企业信息
	 */
	public JSONObject getcompanybycompanyid(String companyid){
		String target_Name = "";
		String result = _model.mysql_model
				.executeQuery("SELECT c.`Name`,c.`OperName`,c.`StartDate`,c.`No`,f.logo FROM companys c INNER JOIN companys_cfg f ON c.KeyNo=f.companyid WHERE KeyNo = '"+companyid+"' limit 1");
		JSONArray array = new JSONArray(result);
		JSONObject json_object = new JSONObject();
		if (array.length() >= 1) {
			json_object = array.getJSONObject(0);
		}
		return json_object;
	}
	
	/**
	 * 获取企业名称
	 * @param companyid
	 * @return
	 */
	public String getnamebycompanyid(String companyid){
		String target_Name = "";
		String result = _model.mysql_model
				.executeQuery("select Name from `companys` where KeyNo='"
						+  companyid + "' limit 1");
		JSONArray array = new JSONArray(result);
		if (array.length() >= 1) {
			JSONObject json_object = array.getJSONObject(0);
			target_Name = json_object.getString("Name");
		}
		return target_Name;
	}
}
