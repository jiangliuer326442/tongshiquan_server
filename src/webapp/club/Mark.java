package webapp.club;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

public class Mark extends ControllerBase{
	
	/**
	 * 添加备注
	 * @throws IOException
	 */
	public void addmark() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "club");
			String touid = form_data.get("touid");
			if(Integer.parseInt(touid)<=0){
				return_json(1001,"用户不存在");
			}
			String mname = form_data.get("mname");
			if(mname == null || mname.equals("")){
				return_json(1002,"备注不能为空");
			}
			_model.mysql_model.executeUpdate("INSERT INTO `club_user_marker` (`operator_id`, `markeed_user`, `markname`) VALUES ("+uid+", "+touid+", '"+mname+"') ON DUPLICATE KEY UPDATE markname = '"+mname+"'");
			return_json(200,"备注成功");
		}
	}
	
	/**
	 * 获取备注名
	 * @param uid
	 * @param touid
	 * @return
	 */
	public String getMark(String uid,String touid){
		String result = _model.mysql_model.executeQuery("SELECT markname FROM club_user_marker WHERE operator_id = " + uid + " AND markeed_user = " + touid + " LIMIT 1");
		String markedname = "";
		if(new JSONArray(result).length() > 0){
			markedname = new JSONArray(result).getJSONObject(0).getString("markname");
		}
		return markedname;
		
	}
}
