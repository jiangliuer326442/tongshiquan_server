package webapp.ruby.social.comments.article;

import org.json.JSONArray;
import org.json.JSONObject;

import webapp.ruby.social.comments.CommentBase;

/**
 * 文章操作模块
 * <p>Title:ArticleOperates</p>
 * <p>Description:</p>
 * <p>Company:上海夜伦网络信息技术有限公司</p>
 * @author 方海亮
 * @date 2016年8月11日 上午11:12:41
 */
public class ArticleOperates extends CommentBase {
	
	/**
	 * 插入文章并返回文章id
	 * @return String
	 * @date 2016年8月11日 上午11:14:43
	 * @param plat_id
	 * @param article_id
	 * @return
	 */
	public String insert(String plat_id, String article_id){
		String result = _model.mysql_model.executeQuery("select id from `" + this.database + "`.`articles` where `plat_id`="+plat_id+" and `article_id` = "+article_id+" limit 1");
		JSONArray array = new JSONArray(result);
		if(array.length()==1){
			JSONObject json_object = array.getJSONObject(0);
			return json_object.get("id").toString();
		}
		_model.mysql_model.executeUpdate("insert into `" + this.database + "`.`articles` (`plat_id`,`article_id`) VALUES ("+plat_id+","+article_id+")");
		result = _model.mysql_model.executeQuery("select @@IDENTITY as id");
		array = new JSONArray(result);
		JSONObject json_object = array.getJSONObject(0);
		return json_object.get("id").toString();
	}

}
