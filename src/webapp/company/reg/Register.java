package webapp.company.reg;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import webapp.mobilemsg.Send;
import webapp.posts.models.Create;
import webapp.user.Cklogin;
import webapp.user.Logout;
import webapp.company.cfg.Cfg;
import webapp.company.employee.Info;
import webapp.company.info.Getcompany;
import webapp.company.manager.smanager;
import webapp.company.structure.Structure;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

public class Register extends ControllerBase {
	
	/**
	 * 允许非企业超级管理员解除企业注册
	 * 流程：
	 * 添加用户解绑的日志
	 * 该用户的管理权归管理员所有
	 * 企业停用该用户
	 * 用户注销登陆
	 * @throws IOException
	 */
	public void unreg() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			int super_id = new smanager().getsmanager(companyid);
			if(Integer.toString(super_id).equals(uid)){
				return_json(1001,"超级管理员不允许解绑");
				return;
			}
			//记录用户解绑日志
			new webapp.user.Log().bingcompanylog(uid,companyid,"0");
			//变更模块管理员
			new Create().chgleader(companyid,uid,Integer.toString(super_id));
			//停止企业该用户的使用权
			_model.mysql_model.executeUpdate("UPDATE companys_employees SET stop_flg = 1,stoptime = NOW() WHERE userid = "+uid+" AND companyid = '"+companyid+"'");
			//获取用户的部门id
			int department = new Structure().getemployeestructure(companyid,Integer.parseInt(uid));
			//员工数量减1
			new Structure().decreaseemployeenum(companyid, department);
			String uuid = CommonFunction.getParameter(request, "uuid", true);
			int device_id = 0;
			if (uuid != null && !uuid.equals("")) {
				device_id = new JSONArray(_model.mysql_model.executeQuery("SELECT id FROM `devices` WHERE `uuid` = '"+uuid+"'")).getJSONObject(0).getInt("id");
			}
			new Logout().do_logout(uid, device_id);
			return_json(200,"解绑成功");
		}
	}
	
	/**
	 * 用户绑定企业
	 * 条件：
	 * 		用户没有绑定企业
	 * 		企业没有管理员或开放注册或用户手机号在允许绑定的范围内
	 * 		不能超过企业员工上限
	 * 实现：
	 * 		保存用户手机号
	 * 		记录用户绑定日志
	 * 		将用户添加到员工表中
	 * 		添加到对应分组，确认是否为管理员
	 * 		管理员注册，生成企业配置信息
	 * 		记录企业管理员变更日志
	 * 		记录用户加入日志
	 * 		注册到社会化评论系统
	 * @author Administrator
	 */
	public void reg() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		JSONObject user_obj = new Cklogin().getuserinfoBytoken(request);
		if(user_obj != null){
			Map<String, String> form_data = new Form(context).createForm(request, "user_company_form");
			//手机号
			String phone = form_data.get("phone");
			if(phone == null || phone.equals("")){
				return_json(1002, "请填写手机号");
				return;
			}
			if(!CommonFunction.isMobileNO(phone)){
				return_json(1003, "手机号格式错误");
				return;
			}
			//验证码
			String codes = form_data.get("codes");
			if(codes == null || codes.equals("")){
				return_json(1004, "请填写验证码");
				return;
			}
			if(!new Send().ckregcode(phone, codes)){
				return_json(1005, "验证码错误");
				return;
			}
			//密码
			String password = form_data.get("password");
			if(password == null){
				password = "";
			}
			//生产厂商
			String vendor = form_data.get("vendor");
			if(vendor == null){
				vendor = "";
			}
			//唯一标识
			String uuid = form_data.get("uuid");
			if(uuid == null){
				uuid = "";
			}
			/**
			 * 保存用户手机号对应的验证码
			 * 需要根据手机号进行uid校正
			 */
			String result = _model.mysql_model.executeQuery("select id from users where phone = '"+phone+"' limit 1");
			if(new JSONArray(result).length()>0){
				String uid2 = Integer.toString(new JSONArray(result).getJSONObject(0).getInt("id"));
				if(!uid2.equals(uid)){
					_model.mysql_model.executeQuery("delete from users where id = "+uid);
					uid = uid2;
				}
			}
			new webapp.user.Register().saveuserphone(uid,phone);
			String companyid = form_data.get("companyid");
			String nickname = user_obj.getString("nickname");
			String avatar = user_obj.getString("avatar1");
			JSONObject reg_result = regprocess(uid,companyid,phone,nickname,avatar,password,vendor,uuid);
			if(reg_result != null){
				return_json(200,"注册成功", reg_result);
			}
		}else {
			return;
		}
	}

	public JSONObject regprocess(String uid,String companyid,String phone,String nickname,String avatar, String password,String vendor,String uuid, HttpServletResponse response) throws IOException{
		this.response = response;
		return regprocess(uid, companyid, phone, nickname, avatar, password, vendor, uuid);
	}
	
	/**
	 * 用户注册的过程
	 * @throws IOException 
	 */
	public JSONObject regprocess(String uid,String companyid,String phone,String nickname,String avatar, String password,String vendor,String uuid) throws IOException{
		//确认用户还没有绑定企业
		Getcompany getcompany_obj = new Getcompany();
		if(!getcompany_obj.getcompanyidbyuser(Integer.parseInt(uid)).equals("0")){
			return_json(1001, "已绑定企业");
			return null;
		}
		//公司id
		String company_name = getcompany_obj.getnamebycompanyid(companyid);
		if(company_name.equals("")){
			return_json(1006, "企业不存在");
			return null;
		}
		JSONObject company_cfg_obj = new Cfg().getcompanycfg(companyid);
		//企业还没有管理员，用户将成为企业的第一个员工，也将作为企业的管理员
		if(company_cfg_obj == null){
			_model.redis_model.set("admin_"+uid+"_companyid", companyid, 86400);
			//平台注册
			String result = CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_platregister.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platid="+companyid);
			JSONObject jsonObject = new JSONObject(result);
			//社会化评论系统平台码
			String platcode = null;
			if(jsonObject.getInt("status") != 200){
				return_json(500, "内部错误");
				return null;
			}else{
				platcode = jsonObject.getString("data");
			}
			//用户注册
			result = CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_userregister.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platcode="+platcode+"&phone="+phone+"&username="+java.net.URLEncoder.encode(nickname, "utf-8"));
			jsonObject = new JSONObject(result);
			//社会化评论系统平台码
			if(jsonObject.getInt("status") != 200){
				return_json(500, "内部错误");
				return null;
			}
			//生成企业配置信息文件
			_model.mysql_model.executeUpdate("INSERT INTO companys_cfg SELECT *,'"+companyid+"','"+platcode+"','' FROM companys_cfg_default");
			//当前用户设为企业超级管理员
			_model.mysql_model.executeUpdate("INSERT INTO `companys_structure` (`companyid`, `groupname`, `membernum`, `superid`, `create_time`) VALUES ('"+companyid+"', '"+company_name+"', '0', '0', NOW())");
			result = _model.mysql_model.executeQuery("select @@IDENTITY as structid");
			JSONArray array = new JSONArray(result);
			JSONObject json_object = array.getJSONObject(0);
			String structid = json_object.get("structid").toString();
			_model.mysql_model.executeUpdate("INSERT INTO `companys_employees` (`companyid`, `structerid`, `user_avatar`, `user_name`, `user_phone`, `user_nick`, `userid`, `addtime`) VALUES ('"+companyid+"', "+structid+", '"+avatar+"', '"+nickname+"', '"+phone+"', '"+nickname+"', "+uid+", NOW())");
			//部门人数增加1个
			new Structure().increaseemployeenum(companyid, Integer.parseInt(structid));
			//记录用户绑定企业日志
			new webapp.user.Log().bingcompanylog(uid,companyid,"1");
			//添加超级管理员
			new webapp.company.manager.smanager().add(companyid, uid);
		}else if(company_cfg_obj.getBoolean("is_allow_register")){
			//开放注册手机号不能重复
			if(new Info().ckemployeebyphone(companyid, phone)){
				return_json(1009,"手机号该企业已存在");
				return null;
			}
			//企业最大员工数量
			int max_employee_num = company_cfg_obj.getInt("max_employee_num");
			int current_employee_num = new Info().getcurrentempnum(companyid);
			if(current_employee_num>=max_employee_num){
				return_json(1007,"公司员工超过上限");
				return null;
			}
			//用户注册
			String result = CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_userregister.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platcode="+company_cfg_obj.getString("platformcode")+"&phone="+phone+"&username="+java.net.URLEncoder.encode(nickname, "utf-8"));
			JSONObject jsonObject = new JSONObject(result);
			//社会化评论系统平台码
			if(jsonObject.getInt("status") != 200){
				return_json(500, "内部错误");
				return null;
			}
			_model.redis_model.set("admin_"+uid+"_companyid", companyid, 86400);
			//判断该员工是否是已经预加好了的
			result = _model.mysql_model.executeQuery("SELECT id,structerid FROM companys_employees WHERE user_phone = '"+phone+"' AND companyid = '"+companyid+"' limit 1");
			if(new JSONArray(result).length()>0){
				String structid = Integer.toString(new JSONArray(result).getJSONObject(0).getInt("structerid"));
				_model.mysql_model.executeUpdate("update `companys_employees` set `userid` = "+uid+", `stop_flg` = 0 where id = "+Integer.toString(new JSONArray(result).getJSONObject(0).getInt("id")));
				new Structure().increaseemployeenum(companyid, Integer.parseInt(structid));
			}else{
				//获取员工的structid
				result = _model.mysql_model.executeQuery("SELECT id FROM companys_structure WHERE companyid = '" + companyid + "' AND superid = 0 limit 1");
				JSONArray array = new JSONArray(result);
				String structid = array.getJSONObject(0).get("id").toString();
				_model.mysql_model.executeUpdate("INSERT INTO `companys_employees` (`companyid`, `structerid`, `user_avatar`, `user_name`, `user_phone`, `user_nick`, `userid`, `addtime`) VALUES ('"+companyid+"', "+structid+", '"+avatar+"', '"+nickname+"', '"+phone+"', '"+nickname+"', "+uid+", NOW())");
				new Structure().increaseemployeenum(companyid, Integer.parseInt(structid));
			}
			//记录用户绑定企业日志
			_model.mysql_model.executeUpdate("INSERT INTO `user_bindcompany_record` (`userid`, `companyid`, `bindstatus`) VALUES ("+uid+", '"+companyid+"', 1)");
		}else{
			//企业禁止注册，验证当前手机号在企业允许的手机号当中
			String result = _model.mysql_model.executeQuery("SELECT id,structerid FROM companys_employees WHERE user_phone = '"+phone+"' AND companyid = '"+companyid+"' AND userid IS NULL AND stop_flg = 0 LIMIT 1");
			JSONArray array = new JSONArray(result);
			String employee_id = "";
			if (array.length() >= 1) {
				_model.redis_model.set("admin_"+uid+"_companyid", companyid, 86400);
				employee_id = array.getJSONObject(0).get("id").toString();
			}else{
				return_json(1008,"企业禁止注册");
				return null;
			}
			//用户注册
			result = CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_userregister.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platcode="+company_cfg_obj.getString("platformcode")+"&phone="+phone+"&username="+java.net.URLEncoder.encode(nickname, "utf-8"));
			JSONObject jsonObject = new JSONObject(result);
			//社会化评论系统平台码
			if(jsonObject.getInt("status") != 200){
				return_json(500, "内部错误");
				return null;
			}
			_model.mysql_model.executeUpdate("UPDATE companys_employees SET user_avatar = '"+avatar+"',userid = "+uid+",addtime = NOW() WHERE id = "+employee_id);
			String structid = array.getJSONObject(0).get("structerid").toString();
			_model.mysql_model.executeUpdate("update companys_structure set membernum=membernum+1 where id = "+structid);
			//记录用户绑定企业日志
			_model.mysql_model.executeUpdate("INSERT INTO `user_bindcompany_record` (`userid`, `companyid`, `bindstatus`) VALUES ("+uid+", '"+companyid+"', 1)");
		}
		//保存设备信息
		int device_id = 0;
		if(!uuid.equals("") && !vendor.equals("")){
			_model.mysql_model.executeUpdate("INSERT INTO `devices` (`uuid`, `vendor`, `uid`) VALUES ('"+uuid+"', '"+vendor+"', "+uid+") ON DUPLICATE KEY UPDATE `uid` = "+uid);
			device_id = new JSONArray(_model.mysql_model.executeQuery("SELECT id FROM `devices` WHERE `uuid` = '"+uuid+"'")).getJSONObject(0).getInt("id");
		}
		//保存密码
		if(password.equals("")){
			password = phone;
		}
		_model.mysql_model.executeUpdate("UPDATE `users` SET `password` = '"+password+"' WHERE id = "+uid);
		String token = new webapp.user.Register().addtoken(uid, device_id);
        //获取绑定的企业id
        Map<String,String> usercompanyinfo = new Ckuser().getuserinfo(Integer.parseInt(uid));
        JSONObject result = new JSONObject();
        result.put("uid", uid);
        result.put("token", token);
        result.put("is_manager", usercompanyinfo.get("is_manager"));
        result.put("companyid", usercompanyinfo.get("companyid"));
		return result;
	}
	
}
