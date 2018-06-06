package webapp.user;

import java.io.IOException;

import org.json.JSONArray;

import com.ruby.framework.function.CommonFunction;

public class Logout extends Userbase {
	
	public void index() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		String uuid = CommonFunction.getParameter(request, "uuid", true);
		int device_id = 0;
		if (uuid != null && !uuid.equals("")) {
			device_id = new JSONArray(_model.mysql_model.executeQuery("SELECT id FROM `devices` WHERE `uuid` = '"+uuid+"'")).getJSONObject(0).getInt("id");
		}
		do_logout(uid, device_id);
		return_json(200,"退出登陆");
	}
	
	public void do_logout(String uid, int device_id){
		_model.redis_model.remove(uid+"_"+Integer.toString(device_id)+"_login");
		_model.redis_model.remove("admin_"+uid+"_company");
		_model.redis_model.remove("admin_"+uid+"_isadmin");
		_model.redis_model.remove("admin_"+uid+"_models");
		_model.redis_model.remove("admin_"+uid+"_uname");
		_model.redis_model.remove("admin_"+uid+"_uphone");
		_model.redis_model.remove("admin_"+uid+"_avatar");
		_model.mysql_model.executeUpdate("DELETE FROM tokens WHERE userid = "+uid);
	}
}
