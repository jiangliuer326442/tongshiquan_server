package webapp.posts.models;

import java.io.IOException;
import java.util.Map;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

/**
 * 删除模块
 * @author 方海亮
 *
 */
public class Del extends ControllerBase {
	
	/**
	 * 根据sectionid删除栏目
	 * @throws IOException
	 */
	public void delbysectionid() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "postmodels");
			String sectionid = form_data.get("sectionid");
			if(_model.redis_model.get("admin_"+uid+"_isadmin").equals("1")){
				_model.mysql_model.executeUpdate("UPDATE post_models SET is_del = 1 WHERE companyid = '"+companyid+"' AND id="+sectionid);
				return_json(200,"删除成功");
			}else{
				return_json(1001,"仅超级管理员有权删除模块");
			}
		}
	}
}
