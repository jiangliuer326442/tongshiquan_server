package webapp.tieba.models;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.company.cfg.Cfg;
import webapp.user.Cklogin;

public class Add extends ControllerBase {
	
	public void addtiebaimg() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "postmodels");
			String logo = form_data.get("logo");
			if(logo == null || logo.equals("")){
				return_json(1002,"贴吧图标未上传");
				return;
			}
			return_json(200, "贴吧图片上传成功", logo);
		}
	}
	
	/**
	 * 增加贴吧模块
	 * @throws IOException
	 */
	public void addtieba() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "postmodels");
			String model_name = form_data.get("title");
			if(model_name == null || model_name.equals("")){
				return_json(1001,"贴吧名称必填");
				return;
			}
			String logo = form_data.get("logo");
			if(logo == null || logo.equals("")){
				return_json(1002,"贴吧图标未上传");
				return;
			}
			String model_desc = form_data.get("desc");
			if(model_desc == null || model_desc.equals("")){
				return_json(1003,"请填写贴吧的描述内容");
				return;
			}
			//获取企业配置信息
			JSONObject company_cfg_obj = new Cfg().getcompanycfg(companyid);
			if(company_cfg_obj.getBoolean("is_postbar_audit")){
				//创建贴吧需要审核，记录贴吧信息到审核表
				_model.mysql_model.executeUpdate("INSERT INTO `postbar_approval`(title,logo,`desc`,company_id,employee_id,create_time) VALUES ('" + model_name + "','" + logo + "','" + model_desc + "','" + companyid + "'," + uid + ",NOW())");
				return_json(2000,"创建贴吧需要管理员审核，请耐心等待~");
			}else{
				//添加模块
				new webapp.posts.models.Add().addmodel(uid, "3", model_name, logo, "1", "0", "0", uid, model_desc);
				return_json(200,"创建贴吧成功！作为吧主，您将担负起管理贴吧的重任哦~");
			}
		}
	}
}
