package webapp.company.reg;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import webapp.company.cfg.Cfg;
import webapp.company.info.Getcompany;
import webapp.company.manager.smanager;
import webapp.user.Cklogin;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

public class Ckuser extends ControllerBase {
	
	/**
	 * 获取其他人的用户信息
	 * @throws IOException
	 */
	public void getsbuserinfo() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "user_company_form");
			//手机号
			String touid = form_data.get("touid");
			if(touid == null || touid.equals("")){
				return_json(1002, "系统错误");
				return;
			}
			Map userinfo = null;
			userinfo = getuserinfo(Integer.parseInt(touid));
			userinfo = getuserinfobycache(Integer.parseInt(touid));
			JSONObject userinfo_obj = new JSONObject();
			userinfo_obj.put("companyname", userinfo.get("companyname"));
			userinfo_obj.put("uname", userinfo.get("uname"));
			userinfo_obj.put("unick", userinfo.get("unick"));
			userinfo_obj.put("umail", userinfo.get("umail"));
			userinfo_obj.put("uphone", userinfo.get("uphone"));
			userinfo_obj.put("avatar", userinfo.get("avatar"));
			//获取备注姓名
			userinfo_obj.put("markedname", new webapp.club.Mark().getMark(uid, touid));
			//获取好友关系
			userinfo_obj.put("friend_flg", new webapp.club.Care().is_friend(uid, touid));
			//获取关注关系
			userinfo_obj.put("care_flg", new webapp.club.Care().is_care(uid, touid));
			//获取同事圈权限
			JSONObject couple_relation = new webapp.twitter.User().getusertwitter(uid, touid);
			userinfo_obj.put("allowhim_flg", couple_relation.getBoolean("allowhim_flg"));
			userinfo_obj.put("watchhim_flg", couple_relation.getBoolean("watchhim_flg"));
			return_json(200,"获取信息成功",userinfo_obj);
		}
	}
	
	/**
	 * 判断企业设否允许访问
	 * @return
	 */
	public boolean isAllowvisit(String companyid, int uid){
		if(companyid == null || companyid.equals("")){
			return false;
		}
		JSONObject companycfg = new Cfg().getcompanycfg(companyid);
		//论坛是否对外开放
		Boolean is_allowvisit = companycfg.getBoolean("is_allowvisit");
		//论坛允许访问的时间段
		String allow_visit_time = companycfg.getString("allow_visit_time");
		int min_visit_hour = Integer.parseInt(allow_visit_time.split("\\|\\|")[0]);
		int max_visit_hour = Integer.parseInt(allow_visit_time.split("\\|\\|")[1]);
		int current_hour = new Date().getHours();
		//非企业超级管理员不能在非工作时间访问企业
		int super_id = new smanager().getsmanager(companyid);
		if(!Integer.toString(super_id).equals(uid)){
			if(current_hour < min_visit_hour || current_hour >= max_visit_hour){
				return false;
			}
		}
		Boolean is_my_company = false;
		if(uid>0){
			String my_companyid = new webapp.company.info.Getcompany().getcompanyidbyuser(uid);
			if(my_companyid.equals(companyid))
				is_my_company = true;
		}
		if(!(is_my_company || is_allowvisit)){
			return false;
		}
		return true;
	}
	/**
	 * 判断用户是否是某企业的管理员
	 * @param userid
	 * @param companyid
	 * @return
	 */
	public Map getuserinfo(int userid){
		Map userinfo = new HashMap();
		JSONObject userinfo_object = new JSONObject();
		boolean is_manager = false;
		String companyid = new Getcompany().getcompanyidbyuser(userid);
		if(!companyid.equals("0")){
			_model.redis_model.set("admin_"+Integer.toString(userid)+"_companyid", companyid, 86400*12);
			_model.redis_model.set("admin_"+Integer.toString(userid)+"_company", new Getcompany().getnamebycompanyid(companyid), 86400*12);
			//判断当前用户是否是超级管理员
			String result = _model.mysql_model.executeQuery("select userid from companys_manager_record where companyid = '"+companyid+"' order by create_time desc limit 1");
			JSONArray array = new JSONArray(result);
			if(array.getJSONObject(0).getInt("userid") == userid){
				_model.redis_model.set("admin_"+Integer.toString(userid)+"_isadmin", "1", 86400*12);
			}else{
				_model.redis_model.set("admin_"+Integer.toString(userid)+"_isadmin", "0", 86400*12);
			}
			//超级管理员
			if(_model.redis_model.get("admin_"+Integer.toString(userid)+"_isadmin").equals("1")){
				//获取负责的模块id
				result = _model.mysql_model.executeQuery("SELECT id, NAME as name, leaderid FROM company_models WHERE companyid = '" + companyid + "'");
				array = new JSONArray(result);
			}else{
				//获取负责的模块id
				result = _model.mysql_model
						.executeQuery("SELECT cm.id as id, cm.NAME as name,cm.leaderid FROM company_models cm LEFT JOIN post_models pm ON cm.id = pm.bigkind_id AND pm.companyid = cm.companyid AND cm.companyid = '" + companyid + "' WHERE (cm.leaderid = "+Integer.toString(userid)+" OR pm.leaderid = " + Integer.toString(userid) + ") AND pm.is_del = 0");
				array = new JSONArray(result);
			}
			if (array.length() >= 1) {
				//爆粗用户管理的模块数据
				_model.redis_model.set("admin_"+Integer.toString(userid)+"_models", array.toString(), 86400*12);
				is_manager = true;
			}else{
				array = new JSONArray();
				_model.redis_model.set("admin_"+Integer.toString(userid)+"_models", array.toString(), 86400*12);
			}
			result = _model.mysql_model.executeQuery("SELECT `user_name`,`user_nick`,`user_mail`,`user_phone`,`user_avatar` from companys_employees WHERE companyid = '"+companyid+"' AND userid = "+Integer.toString(userid));
			array = new JSONArray(result);
			userinfo_object = array.getJSONObject(0);
			_model.redis_model.set("admin_"+Integer.toString(userid)+"_uname", userinfo_object.getString("user_name"), 86400*12);
			_model.redis_model.set("admin_"+Integer.toString(userid)+"_unick", userinfo_object.getString("user_nick"), 86400*12);
			_model.redis_model.set("admin_"+Integer.toString(userid)+"_umail", userinfo_object.getString("user_mail"), 86400*12);
			_model.redis_model.set("admin_"+Integer.toString(userid)+"_uphone", userinfo_object.getString("user_phone"), 86400*12);
			_model.redis_model.set("admin_"+Integer.toString(userid)+"_avatar", userinfo_object.getString("user_avatar"), 86400*12);
		}else{
			_model.redis_model.set("admin_"+Integer.toString(userid)+"_company", "", 86400*12);
			userinfo_object = new webapp.user.Cklogin().getuserinfoByuid(Integer.toString(userid));
			_model.redis_model.set("admin_"+Integer.toString(userid)+"_uname", userinfo_object.getString("nickname"), 86400*12);
			_model.redis_model.set("admin_"+Integer.toString(userid)+"_unick", userinfo_object.getString("nickname"), 86400*12);
			_model.redis_model.set("admin_"+Integer.toString(userid)+"_umail", "", 86400*12);
			_model.redis_model.set("admin_"+Integer.toString(userid)+"_uphone", userinfo_object.getString("phone"), 86400*12);
			_model.redis_model.set("admin_"+Integer.toString(userid)+"_avatar", userinfo_object.getString("avatar2"), 86400*12);
			JSONArray array = new JSONArray();
			_model.redis_model.set("admin_"+Integer.toString(userid)+"_models", array.toString(), 86400*12);
			_model.redis_model.set("admin_"+Integer.toString(userid)+"_isadmin", "1", 86400*12);
		}
		userinfo.put("companyid", companyid);
		userinfo.put("is_manager", Boolean.toString(is_manager));
		return userinfo;
	}
	
	/**
	 * 通过缓存获取用户信息
	 * @param userid
	 * @return
	 */
	public Map getuserinfobycache(int userid){
		Map userinfo = new HashMap();
		userinfo.put("companyid", _model.redis_model.get("admin_"+Integer.toString(userid)+"_companyid"));
		userinfo.put("companyname", _model.redis_model.get("admin_"+Integer.toString(userid)+"_company"));
		userinfo.put("models", new JSONArray(_model.redis_model.get("admin_"+Integer.toString(userid)+"_models")));
		userinfo.put("is_admin", _model.redis_model.get("admin_"+Integer.toString(userid)+"_isadmin"));
		userinfo.put("uname", _model.redis_model.get("admin_"+Integer.toString(userid)+"_uname"));
		userinfo.put("unick", _model.redis_model.get("admin_"+Integer.toString(userid)+"_unick"));
		userinfo.put("umail", _model.redis_model.get("admin_"+Integer.toString(userid)+"_umail"));
		userinfo.put("uphone", _model.redis_model.get("admin_"+Integer.toString(userid)+"_uphone"));
		userinfo.put("avatar", _model.redis_model.get("admin_"+Integer.toString(userid)+"_avatar"));
		return userinfo;
	}
	
	/**
	 * 获取用户信息
	 * @throws IOException 
	 */
	public void getuserinfo() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = getuserinfobycache(Integer.parseInt(uid));
			JSONObject userinfo_obj = new JSONObject();
			userinfo_obj.put("companyid", userinfo.get("companyid"));
			userinfo_obj.put("models", (JSONArray)userinfo.get("models"));
			userinfo_obj.put("is_admin", userinfo.get("is_admin"));
			userinfo_obj.put("uname", userinfo.get("uname"));
			userinfo_obj.put("unick", userinfo.get("unick"));
			userinfo_obj.put("umail", userinfo.get("umail"));
			userinfo_obj.put("uphone", userinfo.get("uphone"));
			userinfo_obj.put("avatar", userinfo.get("avatar"));
			return_json(200,"获取信息成功",userinfo_obj);
		}
	}
}
