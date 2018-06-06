package webapp.posts.models;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;

/**
 * 文章模块的批量创建
 * @author Administrator
 *
 */
public class Create extends ControllerBase {
	//变更文章模块的管理员强制制
	public void chgleader(String companyid, String ouid, String nuid){
		_model.mysql_model.executeUpdate("UPDATE `company_models` SET `leaderid` = "+nuid+",`updatetime` = NOW() WHERE `companyid` = '"+companyid+"' AND `leaderid` = "+ouid);
		_model.mysql_model.executeUpdate("UPDATE `post_models` SET `leaderid` = "+nuid+" WHERE `companyid` = '"+companyid+"' AND `leaderid` = "+ouid);
	}
	
	//初始化文章模块
	public void init(String companyid,String uid){
		//选择默认企业模块大分类
		String result = _model.mysql_model.executeQuery("select id,`name` from company_models_default order by id asc");
		JSONArray json = new JSONArray(result);
		for(int i = 0; i< json.length(); i++){
			JSONObject json_object = json.getJSONObject(i);
			String id = Integer.toString(json_object.getInt("id"));
			String name = json_object.getString("name");
			//创建企业默认大分类
			_model.mysql_model.executeUpdate("INSERT INTO `company_models` (`companyid`, `model_id`, `name`, `leaderid`) VALUES ('"+companyid+"', "+id+", '"+name+"', "+uid+")");	
			//创建企业默认中分类
			result = _model.mysql_model.executeQuery("select `name`,logo,is_allow_comment,is_hide_comment,sort FROM post_models_default WHERE model_name = "+id+" ORDER BY sort ASC");
			JSONArray post_models_json = new JSONArray(result);
			for(int t = 0; t< post_models_json.length(); t++){
				json_object = post_models_json.getJSONObject(t);
				new Add().addmodel(uid, id, json_object.getString("name"), json_object.getString("logo"), json_object.getBoolean("is_allow_comment")?"1":"0", json_object.getBoolean("is_hide_comment")?"1":"0", String.valueOf(json_object.getInt("sort")), uid, "");
			}
		}
	}
}
