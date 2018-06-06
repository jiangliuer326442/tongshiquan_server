package webapp.posts.models;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.company.manager.smanager;
import webapp.user.Cklogin;
import webapp.user.Logout;

/**
 * 设置文章模块信息
 * @author Administrator
 *	变更模块管理员后需要迫使当前管理员和新管理员强制退出
 */
public class Set extends ControllerBase {
	
	public void setmodels() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "postmodels");
			String sectionid = form_data.get("sectionid");
			String model_name = form_data.get("model_name");
			String logo = form_data.get("logo");
			String is_allow_comment = form_data.get("is_allow_comment");
			String is_hide_comment = form_data.get("is_hide_comment");
			String model_sort = form_data.get("model_sort");
			String leaderid = form_data.get("leaderid");
			//获取原来模块信息
			String result = _model.mysql_model.executeQuery("select leaderid from `post_models` where companyid = '" + companyid + "' and id = " + sectionid + " limit 1");
			if(new JSONArray(result).length()>0){
				int oldleaderid = new JSONArray(result).getJSONObject(0).getInt("leaderid");
				int superuser = new smanager().getsmanager(companyid);
				if(uid.equals(Integer.toString(superuser))){
					_model.mysql_model.executeUpdate("update `post_models` set `name` = '"+model_name+"', `logo` = '"+logo+"',  `is_allow_comment` = " + is_allow_comment + ", `is_hide_comment` = " + is_hide_comment + ", `sort` = " + model_sort + ", `leaderid` = " + leaderid + ", `adduser` = " + uid + " where companyid = '" + companyid + "' and id = " + sectionid);
				}else{
					_model.mysql_model.executeUpdate("update `post_models` set `name` = '"+model_name+"', `logo` = '"+logo+"',  `is_allow_comment` = " + is_allow_comment + ", `is_hide_comment` = " + is_hide_comment + ", `sort` = " + model_sort + ", `leaderid` = " + leaderid + ", `adduser` = " + uid + " where companyid = '" + companyid + "' and id = " + sectionid + " and leaderid=" + uid);
				}
				return_json(200,"保存成功",logo);
			}else{
				return_json(1001,"没有找到该模块");
			}
		}
	}
}
