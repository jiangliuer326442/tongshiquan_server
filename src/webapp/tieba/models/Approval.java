package webapp.tieba.models;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.innermsg.tieba.Approved;
import webapp.innermsg.tieba.Notapproved;
import webapp.user.Cklogin;

/**
 * 贴吧申请处理
 * @author 方海亮
 *
 */
public class Approval extends ControllerBase {
	/**
	 * 获取未处理的创建贴吧申请
	 * @throws IOException
	 */
	public void lists() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String result = _model.mysql_model.executeQuery("SELECT pa.id, pa.title, pa.logo, pa.`desc`, ce.user_name, pa.create_time FROM postbar_approval pa LEFT JOIN companys_employees ce ON pa.company_id = ce.companyid AND pa.employee_id = ce.userid WHERE company_id = '"+companyid+"' AND approval_flg = 1 AND "+uid+" = (SELECT leaderid FROM company_models WHERE companyid = '"+companyid+"' AND model_id = 3) ORDER BY pa.create_time desc");
			JSONArray array = new JSONArray(result);
			return_json(200,"success",array);
		}
	}
	
	public void handleapprov() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			Map<String, String> form_data = new Form(context).createForm(request, "postmodels");
			String id = form_data.get("id");
			if(id == null || id.equals("")){
				return_json(1005,"内部错误");
				return;
			}
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
			String apprv_rs = form_data.get("status");
			if(!apprv_rs.equals("2") && !apprv_rs.equals("3")){
				return_json(1004,"请选择是否允许创建贴吧");
				return;
			}
			String result = _model.mysql_model.executeQuery("select employee_id from postbar_approval WHERE id = "+id);
			if(new JSONArray(result).length()==0){
				return_json(1005,"申请不存在");
				return;
			}
			int employee_id = new JSONArray(result).getJSONObject(0).getInt("employee_id");
			_model.mysql_model.executeUpdate("UPDATE postbar_approval SET approval_flg = "+apprv_rs+",update_time = NOW() WHERE id = "+id);
			if(apprv_rs.equals("2")){
				new webapp.posts.models.Add().addmodel(uid, "3", model_name, logo, "1", "0", "0", Integer.toString(employee_id), model_desc);
				new Approved().send(context, Integer.parseInt(uid), employee_id, model_name);
				return_json(200,"已创建贴吧");
				return;
			}else{
				new Notapproved().send(context, Integer.parseInt(uid), employee_id, model_name);
				return_json(2000,"已处理申请");
			}
		}
	}
}
