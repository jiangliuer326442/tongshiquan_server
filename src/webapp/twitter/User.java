package webapp.twitter;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

/**
 * 推特用户管理
 * @author babytree
 *
 */
public class User extends ControllerBase {
	
	/**
	 * 判断两个用户的朋友圈关系
	 * @param uid 操作用户
	 * @param tuid 目标用户
	 * @return
	 */
	public JSONObject getusertwitter(String uid, String tuid){
		JSONObject user_inter = new JSONObject();
		//不允许他看我的朋友圈 标志位
		user_inter.put("allowhim_flg", false);
		//不看他的朋友圈 标志位
		user_inter.put("watchhim_flg", false);
		JSONArray priv_list = new JSONArray(_model.mysql_model.executeQuery("SELECT fuid, tuid FROM twt_enduserlog WHERE ouid = "+uid+" AND (fuid = "+tuid+" OR tuid = "+tuid+")"));
		for(int i=0; i<priv_list.length(); i++){
			if(priv_list.getJSONObject(i).getInt("fuid") == Integer.parseInt(uid)){
				user_inter.put("watchhim_flg", true);
			}else{
				user_inter.put("allowhim_flg", true);
			}
		}
		return user_inter;
	}
	
	/**
	 * 判断用户是否有权在企业发表推特
	 * @param uid
	 * @return
	 */
	public boolean can_speak(String uid) {
		Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
		String companyid = userinfo.get("companyid").toString();
		JSONArray us_list = new JSONArray(_model.mysql_model.executeQuery("SELECT can_speak FROM twt_users WHERE companyid = '"+companyid+"' AND uid = "+uid+" LIMIT 1"));
		if(us_list.length() == 0) 
			return true;
		else
			return us_list.getJSONObject(0).getBoolean("can_speak");
	}
	
	/**
	 * 用户列表
	 * @throws IOException
	 */
	public void list() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String page = CommonFunction.getParameter(request, "p", true);
			if(page == null || page.equals("")){
				page = "1";
			}
			String pagenum = CommonFunction.getParameter(request, "pnum", true);
			if(pagenum == null || pagenum.equals("")){
				pagenum = "20";
			}
			String sql = "SELECT \r\n" + 
					"u.uid,\r\n" + 
					"e.user_name,\r\n" + 
					"u.twitter_deltimes,\r\n" + 
					"u.can_speak\r\n" + 
					"FROM twt_users u INNER JOIN companys_employees e \r\n" + 
					"on u.uid = e.userid AND u.companyid = e.companyid \r\n" + 
					"WHERE u.companyid = '"+companyid+"'\r\n" + 
					"ORDER BY e.user_name ASC LIMIT "+Integer.valueOf(Integer.parseInt(pagenum)*(Integer.parseInt(page)-1))+","+pagenum;
			JSONArray twt_list = new JSONArray(_model.mysql_model.executeQuery(sql));
			return_json(200, "success", twt_list);
		}
	}
	
	/**
	 * 禁止某用户发言
	 * @throws IOException
	 */
	public void disableuser() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			if(!_model.redis_model.get("admin_"+uid+"_isadmin").equals("1")){
				return_json(1001, "无权删除");
				return;
			}
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String tuid = form_data.get("tuid");
			if(tuid == null || tuid.equals("")){
				return_json(1002, "用户错误");
				return;
			}
			_model.mysql_model.executeUpdate("UPDATE twt_users SET can_speak = 0 WHERE companyid = '"+companyid+"' AND uid = "+tuid);
			return_json(200, "成功");
		}
	}
	
	/**
	 * 允许某用户发言
	 * @throws IOException
	 */
	public void enableuser() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			if(!_model.redis_model.get("admin_"+uid+"_isadmin").equals("1")){
				return_json(1001, "无权删除");
				return;
			}
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String tuid = form_data.get("tuid");
			if(tuid == null || tuid.equals("")){
				return_json(1002, "用户错误");
				return;
			}
			_model.mysql_model.executeUpdate("UPDATE twt_users SET can_speak = 1 WHERE companyid = '"+companyid+"' AND uid = "+tuid);
			return_json(200, "成功");
		}
	}
}
