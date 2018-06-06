package webapp.company.employee;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import webapp.company.structure.Structure;
import webapp.user.Cklogin;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;

public class List extends ControllerBase {
	
	public void index() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String result = _model.mysql_model.executeQuery("SELECT "+
"	e.id, "+
"	e.structerid, "+
"	s.groupname, "+
"	s.superid, "+
"	e.user_avatar, "+
"	e.user_name, "+
"	e.user_nick, "+
"	e.user_phone, "+
"	e.user_mail, "+
"	e.userid "+
"FROM "+
"	companys_employees e "+
"LEFT JOIN companys_structure s ON e.structerid = s.id "+
"WHERE "+
"	e.stop_flg = 0 "+
"AND e.companyid = '" + companyid + "'");
			JSONArray array = new JSONArray(result);
			for(int i=0; i<array.length(); i++){
				JSONObject employee_obj = array.getJSONObject(i);
				employee_obj.put("groupname", new Structure().getstrutss(employee_obj.getInt("superid"), employee_obj.getString("groupname")));
			}
			return_json(200,"获取员工列表成功",array);
		}
	}
}