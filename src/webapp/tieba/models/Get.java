package webapp.tieba.models;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

/**
 * 贴吧模块的获取
 * @author 方海亮
 *
 */
public class Get extends ControllerBase {

	/**
	 * 向用户展示贴吧下的模块列表
	 */
	public void lists_byuser() throws IOException{
		Map<String, String> form_data = new Form(context).createForm(request, "postmodels");
		String companyid = form_data.get("companyid");
		String result = _model.mysql_model.executeQuery("SELECT id, article_id, article_title, article_writer, article_time, `name`, logo, descs, leadername, today_posts, total_posts, leaderid, can_post_flg  FROM V_POSTBAR_MODELS WHERE companyid = '"+companyid+"' AND is_del = 0 ORDER BY sort ASC,id DESC");
		JSONArray array = new JSONArray(result);
		return_json(200,"获取栏目信息成功",array);
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
				result = _model.mysql_model.executeQuery("SELECT `name`,logo,is_allow_comment,is_hide_comment,can_post_flg,leaderid,sort,descs FROM V_POSTBAR_MODELS WHERE companyid = '"+companyid+"' AND id="+sectionid);
			}else{
				result = _model.mysql_model.executeQuery("SELECT `name`,logo,is_allow_comment,is_hide_comment,can_post_flg,leaderid,sort,descs FROM V_POSTBAR_MODELS WHERE companyid = '"+companyid+"' AND id="+sectionid+" AND leaderid = "+uid);
			}
			JSONObject object = new JSONArray(result).getJSONObject(0);
			return_json(200,"获取贴吧信息成功",object);
		}
	}
}
