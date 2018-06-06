package webapp.twitter;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

public class Del extends ControllerBase {
	
	/**
	 * 删除用户自己的推特
	 * @throws IOException
	 */
	public void delmytwt() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String twtid = form_data.get("twtid");
			if(twtid == null || twtid.equals("")){
				return_json(1001, "推特ID错误");
				return;
			}
			int twt_uid = new JSONArray(_model.mysql_model.executeQuery("SELECT uid FROM twt_message WHERE id = "+twtid+" LIMIT 1")).getJSONObject(0).getInt("uid");
			if(!Integer.toString(twt_uid).equals(uid)){
				return_json(1002, "只能删除自己的推特");
				return;
			}
			_model.mysql_model.executeUpdate("UPDATE twt_message SET is_del = 1 WHERE id = "+twtid);
			return_json(200, "删除成功");
		}
	}
	
	/**
	 * 删除员工的推特
	 * @throws IOException
	 */
	public void delemptwt() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String twtid = form_data.get("twtid");
			if(twtid == null || twtid.equals("")){
				return_json(1001, "推特ID错误");
				return;
			}
			if(!_model.redis_model.get("admin_"+uid+"_isadmin").equals("1")){
				return_json(1002, "无权删除");
				return;
			}
			JSONArray twt_arr = new JSONArray(_model.mysql_model.executeQuery("SELECT uid FROM twt_message WHERE id = "+twtid+" AND companyid = '"+companyid+"' AND visiblity in (1,3) AND is_del = 0 AND uid in (SELECT userid FROM companys_employees WHERE companyid = '"+companyid+"' AND stop_flg = 0)"));
			if(twt_arr.length() == 0) {
				return_json(1003, "推特不存在或已删除");
				return;
			}
			_model.mysql_model.executeUpdate("UPDATE twt_message SET is_del = 1, delete_user = "+uid+", delete_time = NOW() WHERE id = "+twtid);
			//记录删除日志
			_model.mysql_model.executeUpdate("INSERT INTO twt_users (`uid`, `companyid`, `twitter_deltimes`) VALUES ("+Integer.toString(twt_arr.getJSONObject(0).getInt("uid"))+", '"+companyid+"', 1) ON DUPLICATE KEY UPDATE twitter_deltimes = twitter_deltimes + 1");
			return_json(200, "删除成功");
		}	
	}
	
}
