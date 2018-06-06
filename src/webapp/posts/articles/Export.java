package webapp.posts.articles;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;

/**
 * 文章导出模块
 * @author 方海亮
 *
 */
public class Export extends ControllerBase{
	
	public void index() throws Exception{
		//获取模块列表
		String result = _model.mysql_model.executeQuery("SELECT pm.id,pm.companyid,pm.logo,pm.`name`,pm.sort,pm.leaderid,pm.is_del FROM post_models pm LEFT JOIN company_models cm ON pm.companyid = cm.companyid AND pm.bigkind_id = cm.id WHERE cm.model_id in (2,3)");
		JSONArray model_list = new JSONArray(result);
		for(int i = 0; i < model_list.length(); i++){
			int model_id = model_list.getJSONObject(i).getInt("id");
			//获取文章列表
			result = _model.mysql_model.executeQuery("SELECT id,title,`desc`,thumb,content,is_allow_comment,is_hide_comment,is_top,showtime,readtimes,commenttimes,is_del FROM articles WHERE sectionid = " + Integer.toString(model_id));
			JSONArray article_list = new JSONArray(result);
			model_list.getJSONObject(i).put("list", article_list);
			JSONObject object = _model.elastic_model.put(_model.elastic_model.database+"/articles/"+Integer.toString(model_id), model_list.getJSONObject(i));
		}
		return_json(200,"导入成功");
	}
}
