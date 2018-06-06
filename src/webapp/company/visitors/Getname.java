package webapp.company.visitors;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import webapp.company.cfg.Cfg;
import webapp.company.info.Getcompany;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

public class Getname extends ControllerBase {
	//验证用户权限以及企业要求决定是否可访问
	public void index() throws IOException{
		Map<String, String> form_data = new Form(context).createForm(request, "company_info");
		String visited_companyid = form_data.get("companyid");
		String uid = CommonFunction.getParameter(request, "uid", true);
		String token = CommonFunction.getParameter(request, "token", true);
		String uuid = CommonFunction.getParameter(request, "uuid", true);
		int device_id = 0;
		if (uuid != null && !uuid.equals("")) {
			device_id = new JSONArray(_model.mysql_model.executeQuery("SELECT id FROM `devices` WHERE `uuid` = '"+uuid+"'")).getJSONObject(0).getInt("id");
		}
		Boolean is_login = false;
		if(uid != null && !uid.equals("") && token.equals(_model.redis_model.get(uid+"_"+Integer.toString(device_id)+"_login"))){
			is_login = true;
		}
		boolean ckresult = new webapp.company.reg.Ckuser().isAllowvisit(visited_companyid, is_login?Integer.parseInt(uid):0);
		if(ckresult){
			//允许访问，返回企业名称
			JSONObject companyinfo = new Getcompany().getcompanybycompanyid(visited_companyid);
			return_json(200,"获取信息成功",companyinfo);
		}else{
			return_json(500,"非法访问");
		}

	}
}
