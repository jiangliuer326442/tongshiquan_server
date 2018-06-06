package webapp.club;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

public class Info extends ControllerBase{
	
	public void getchatuserinfo() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
		Map<String, String> form_data = new Form(context).createForm(request, "chat");
		String touid = form_data.get("touid");
		if(Integer.parseInt(touid)<=0){
			return_json(1001,"用户不存在");
		}
		String sql = "SELECT "+
				"	IFNULL(cu.markname, u.username) AS friend_name, "+
				"	ce.user_avatar AS avatar "+
				"FROM "+
				"	users u "+
				"LEFT JOIN club_user_marker cu ON u.id = cu.markeed_user AND cu.operator_id = "+uid+" "+
				"LEFT JOIN user_bindcompany_record ub ON u.id = ub.userid "+
				"AND ub.bindtime = ( "+
				"	SELECT "+
				"		MAX(bindtime) "+
				"	FROM "+
				"		user_bindcompany_record "+
				"	WHERE "+
				"		userid = "+touid+" "+
				") "+
				"LEFT JOIN companys_employees ce ON ce.companyid = ub.companyid "+
				"AND ce.userid = ub.userid "+
				"WHERE "+
				"	u.id = "+touid+" "+
				"LIMIT 1";
		String result = _model.mysql_model.executeQuery(sql);
		return_json(200,"获取用户信息成功",new JSONArray(result).getJSONObject(0));
		}
	}
}
