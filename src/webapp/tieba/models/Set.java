package webapp.tieba.models;

import java.io.IOException;
import java.util.Map;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

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
			String desc = form_data.get("desc");
			String is_allow_comment = form_data.get("is_allow_comment");
			String is_hide_comment = form_data.get("is_hide_comment");
			String can_post_flg = form_data.get("can_post_flg");
			String model_sort = form_data.get("model_sort");
			String leaderid = form_data.get("leaderid");
			_model.mysql_model.executeUpdate("update `post_models` set `name` = '"+model_name+"', `logo` = '"+logo+"',  `is_allow_comment` = " + is_allow_comment + ", `is_hide_comment` = " + is_hide_comment + ", `sort` = " + model_sort + ", `leaderid` = " + leaderid + ", `adduser` = " + uid + " where companyid = '" + companyid + "' and id = " + sectionid + " and leaderid=" + uid);
			_model.mysql_model.executeUpdate("update `postbar_cfg` set descs = '"+desc+"',can_post_flg = " + can_post_flg + " where id = "+sectionid);
			return_json(200,"保存成功",logo);
		}
	}
}
