package webapp.company.employee;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.company.cfg.Cfg;
import webapp.company.manager.smanager;
import webapp.company.structure.Structure;
import webapp.posts.models.Create;
import webapp.user.Cklogin;
import webapp.user.Logout;

/**
 * 员工信息设置类
 * @author babytree
 *
 */
public class Set extends ControllerBase {
	private String employee_uid;
	
	/**
	 * 删除员工
	 * @throws IOException
	 */
	public void delemployee() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "employee");
			String[] userlist = form_data.get("userlist").split(",");
			if(userlist.length==0){
				return_json(1001,"请选择用户");
				return;
			}
			int super_id = new smanager().getsmanager(companyid);
			for(int i=0; i<userlist.length; i++){
				String userid = userlist[i];
				String result = _model.mysql_model.executeQuery("SELECT userid FROM companys_employees WHERE companyid = '" + companyid + "' AND id = " + userid + " LIMIT 1");
				JSONObject employee_object = new JSONArray(result).getJSONObject(0);
				if(employee_object.has("userid")){
					if(Integer.toString(super_id).equals(Integer.toString(employee_object.getInt("userid")))){
						continue;
					}
					//记录用户解绑日志
					new webapp.user.Log().bingcompanylog(Integer.toString(employee_object.getInt("userid")),companyid,"0");
					//变更模块管理员
					new Create().chgleader(companyid,Integer.toString(employee_object.getInt("userid")),Integer.toString(super_id));
					//获取用户的部门id
					int department = new Structure().getemployeestructure(companyid,employee_object.getInt("userid"));
					//员工数量减1
					new Structure().decreaseemployeenum(companyid, department);
					int device_id = 0;
					String uuid = CommonFunction.getParameter(request, "uuid", true);
					if (uuid != null && !uuid.equals("")) {
						device_id = new JSONArray(_model.mysql_model.executeQuery("SELECT id FROM `devices` WHERE `uuid` = '"+uuid+"'")).getJSONObject(0).getInt("id");
					}
					new Logout().do_logout(Integer.toString(employee_object.getInt("userid")), device_id);
				}
				//停止企业该用户的使用权
				_model.mysql_model.executeUpdate("UPDATE companys_employees SET stop_flg = 1,stoptime = NOW() WHERE id = "+userid+" AND companyid = '"+companyid+"'");
			}
		}
		return_json(200,"删除员工成功");
	}
	
	/**
	 * 变更员工个人信息
	 * @throws IOException
	 */
	public void chginfo() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "employee");
			String employeeid = form_data.get("userid");
			this.employee_uid = employeeid;
			if(employeeid == null || employeeid.equals("")){
				return_json(1001,"内部错误");
				return;
			}
			//获取旧信息
			String result = _model.mysql_model.executeQuery("SELECT userid, structerid, user_avatar, user_phone, user_name, user_nick, user_mail FROM companys_employees WHERE stop_flg = 0 AND companyid = '" + companyid + "' AND id = " + employeeid + " LIMIT 1");
			JSONObject employee_object = new JSONArray(result).getJSONObject(0);
			this.employee_uid = Integer.toString(employee_object.getInt("userid"));
			String user_nick = form_data.get("user_nick");
			if(user_nick == null || user_nick.equals("")){
				return_json(1002, "昵称必填");
				return;
			}
			//更改了昵称
			if(!user_nick.equals(employee_object.getString("user_nick"))){
				nick(user_nick, companyid, employee_object.getString("user_phone"), employeeid);
			}
			String user_name = form_data.get("user_name");
			if(user_name == null || user_name.equals("")){
				return_json(1004, "姓名必填");
				return;
			}
			//更改了姓名
			if(!user_name.equals(employee_object.getString("user_name"))){
				name(user_name, companyid, employeeid);
			}
			String user_avatar = form_data.get("user_avatar");
			if(user_avatar == null || user_avatar.equals("")){
				return_json(1003, "头像必填");
				return;
			}
			//更改了头像
			if(!user_avatar.equals(employee_object.getString("user_avatar"))){
				avatar(user_avatar, companyid, employeeid);
			}
			//邮箱
			String user_mail = form_data.get("user_mail");
			if(user_mail == null || user_mail.equals("")){
				user_mail = "";
			}
			if(!user_mail.equals("") && !user_mail.equals(employee_object.getString("user_mail"))){
				mail(user_mail, companyid, employeeid);
			}
			return_json(200,"更新个人信息成功",user_avatar);
		}
	}
	
	/*
	 * 设置自己头像
	 */
	public void avatar() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			this.employee_uid = uid;
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "employee");
			String avatar = form_data.get("avatar");
			avatar(avatar,companyid,new Info().geteid(companyid, uid));
			_model.mysql_model.executeUpdate("UPDATE users SET avatar = '"+avatar+"' WHERE id = "+uid);
			return_json(200,"更换成功",avatar);
		}
	}
	
	
	
	/**
	 * 设置自己昵称
	 */
	public void nick() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			this.employee_uid = uid;
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String phone = userinfo.get("uphone").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "employee");
			String nick = form_data.get("nick");
			nick(nick,companyid,phone,new Info().geteid(companyid, uid));
			return_json(200,"更换成功");
		}
	}
	
	private void nick(String nick, String companyid, String phone, String uid) throws UnsupportedEncodingException{
		String platcode = new Cfg().getcompanycfg(companyid).getString("platformcode");
		String result = CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_usernickchg.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platcode="+platcode+"&phone="+phone+"&username="+java.net.URLEncoder.encode(nick, "UTF-8"));
		JSONObject jsonObject = new JSONObject(result);
		//不存在该员工
		if(jsonObject.getInt("status") == 1009){
			CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_userregister.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platcode="+platcode+"&phone="+phone+"&username="+java.net.URLEncoder.encode(nick, "utf-8"));
		}
		_model.mysql_model.executeUpdate("UPDATE companys_employees SET user_nick = '"+nick+"' WHERE companyid = '"+companyid+"' AND id = "+uid+" AND stop_flg = 0");
		_model.mysql_model.executeUpdate("UPDATE users SET username = '"+nick+"' WHERE id = "+this.employee_uid);
		_model.redis_model.set("admin_"+this.employee_uid+"_unick", nick, 86400);
	}
	
	private void avatar(String avatar, String companyid, String uid){
		_model.mysql_model.executeUpdate("UPDATE companys_employees SET user_avatar = '"+avatar+"' WHERE companyid = '"+companyid+"' AND id = "+uid+" AND stop_flg = 0");
		_model.redis_model.set("admin_"+this.employee_uid+"_avatar", avatar, 86400);
	}
	
	private void mail(String mail, String companyid, String uid){
		_model.mysql_model.executeUpdate("UPDATE companys_employees SET user_mail = '"+mail+"' WHERE companyid = '"+companyid+"' AND id = "+uid+" AND stop_flg = 0");
		_model.redis_model.set("admin_"+this.employee_uid+"_umail", mail, 86400);
	}
	
	private void name(String name, String companyid, String uid){
		_model.mysql_model.executeUpdate("UPDATE companys_employees SET user_name = '"+name+"' WHERE companyid = '"+companyid+"' AND id = "+uid+" AND stop_flg = 0");
		_model.redis_model.set("admin_"+this.employee_uid+"_uname", name, 86400);
	}
}
