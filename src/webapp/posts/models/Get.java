package webapp.posts.models;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

/**
 * 文章模块获取
 * @author Administrator
 *
 */
public class Get extends ControllerBase {
	
	/**
	 * 获取模块下的二级模块列表
	 */
	public void lists() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "postmodels");
			String bigkindid = form_data.get("modelid");
			String result;
			if(_model.redis_model.get("admin_"+uid+"_isadmin").equals("1")){
				result = _model.mysql_model.executeQuery("SELECT id,`name` FROM post_models WHERE companyid = '"+companyid+"' AND bigkind_id = "+bigkindid+" AND is_del = 0 ORDER BY sort ASC");
			}else{
				result = _model.mysql_model.executeQuery("SELECT id,`name` FROM post_models WHERE companyid = '"+companyid+"' AND bigkind_id = "+bigkindid+" AND leaderid = "+uid+" AND is_del = 0 ORDER BY sort ASC");
			}
			JSONArray array = new JSONArray(result);
			return_json(200,"获取模块列表成功",array);
		}
	}
	
	/**
	 * 获取二级模块详细配置信息
	 * @throws IOException
	 */
	public void info() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "postmodels");
			String sectionid = form_data.get("sectionid");
			String result;
			if(_model.redis_model.get("admin_"+uid+"_isadmin").equals("1")){
				result = _model.mysql_model.executeQuery("SELECT `name`,logo,is_allow_comment,is_hide_comment,leaderid,sort FROM post_models WHERE companyid = '"+companyid+"' AND id="+sectionid);
			}else{
				result = _model.mysql_model.executeQuery("SELECT `name`,logo,is_allow_comment,is_hide_comment,leaderid,sort FROM post_models WHERE companyid = '"+companyid+"' AND id="+sectionid+" AND leaderid = "+uid);
			}
			JSONObject object = new JSONArray(result).getJSONObject(0);
			return_json(200,"获取栏目信息成功",object);
		}
	}
}
