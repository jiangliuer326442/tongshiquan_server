package webapp.posts.models;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.company.cfg.Cfg;
import webapp.user.Cklogin;

/**
 * 添加文章模块信息
 * @author 方海亮
 *
 */
public class Add extends ControllerBase {
	
	/**
	 * 增加公告模块
	 * @throws IOException
	 */
	public void addgonggao() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "postmodels");
			String model_name = form_data.get("model_name");
			if(model_name == null || model_name.equals("")){
				return_json(1001,"模块名称为空");
				return;
			}
			String logo = form_data.get("logo");
			if(logo == null || logo.equals("")){
				return_json(1002,"模块图标为空");
				return;
			}
			String is_allow_comment = form_data.get("is_allow_comment");
			if(is_allow_comment == null || is_allow_comment.equals("")){
				return_json(1003,"是否允许评论为空");
				return;
			}
			String is_hide_comment = form_data.get("is_hide_comment");
			if(is_hide_comment == null || is_hide_comment.equals("")){
				return_json(1004,"是否隐藏评论为空");
				return;
			}
			String model_sort = form_data.get("model_sort");
			if(model_sort == null || model_sort.equals("")){
				return_json(1005,"模块排序为空");
				return;
			}
			String leaderid = form_data.get("leaderid");
			if(leaderid == null || leaderid.equals("")){
				return_json(1006,"模块管理员为空");
				return;
			}
			addmodel(uid, "2", model_name, logo, is_allow_comment, is_hide_comment, model_sort, leaderid, "");
			return_json(200,"添加栏目成功");
		}
	}
	
	/**
	 * 添加模块
	 */
	public void addmodel(String uid, String bigkind_id, String model_name, String logo, String is_allow_comment, String is_hide_comment, String model_sort, String leaderid, String model_desc){
		Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
		String companyid = userinfo.get("companyid").toString();
		if(bigkind_id.equals("2")){
			_model.mysql_model.executeUpdate("INSERT INTO `post_models` (`companyid`,`bigkind_id`,`name`,`logo`,`is_allow_comment`,`is_hide_comment`,`sort`,`leaderid`,`adduser`) select '"+companyid+"',id,'"+model_name+"','"+logo+"',"+is_allow_comment+","+is_hide_comment+","+model_sort+","+leaderid+","+uid+" FROM company_models WHERE companyid = '"+companyid+"' AND leaderid = "+uid+" AND model_id = " + bigkind_id);
		}else{
			_model.mysql_model.executeUpdate("INSERT INTO `post_models` (`companyid`,`bigkind_id`,`name`,`logo`,`is_allow_comment`,`is_hide_comment`,`sort`,`leaderid`,`adduser`) select '"+companyid+"',id,'"+model_name+"','"+logo+"',"+is_allow_comment+","+is_hide_comment+","+model_sort+","+leaderid+","+uid+" FROM company_models WHERE companyid = '"+companyid+"' AND model_id = " + bigkind_id);
		}
		if(bigkind_id.equals("3")){
			String result = _model.mysql_model.executeQuery("select @@IDENTITY as pmid");
			JSONArray array = new JSONArray(result);
			String pmid = array.getJSONObject(0).get("pmid").toString();
			_model.mysql_model.executeUpdate("INSERT INTO `postbar_cfg` (`id`,`descs`) VALUES ("+pmid+",'" +model_desc+"')");
		}
	}
}
