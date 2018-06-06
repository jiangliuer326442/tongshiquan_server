package webapp.ruby.social.comments.zan;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;

import webapp.ruby.social.comments.CommentBase;
import webapp.ruby.social.comments.article.ArticleOperates;

public class ArticlezanOperates extends CommentBase {
	
	/**
	 * 文章点赞
	 * @param domaincode 域名注册时返回的code
	 * @param platcode 子平台在网站下的标识ID（唯一）
	 * @param phone 点赞的用户手机号
	 * @param article_id 点赞的文章标识
	 * @return void
	 * @date 2016年8月17日 下午2:10:09
	 */
	public void zan(){
		if(!init_chk()) return;
		String article_id = CommonFunction.getParameter(request, "article_id",true);
		if(article_id==null || article_id.equals("")){
			try {
				return_json(1004,"article_id:请传递文章id");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		String phone = CommonFunction.getParameter(request, "phone",true);
		if(phone==null || phone.equals("")){
			try {
				return_json(1008,"phone:手机号没有提交");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		String result = _model.mysql_model.executeQuery("select userid from `" + this.database + "`.`V_PLATFORM_USERS` where `phone`='"+phone+"' and `platformid`="+plat_id+" limit 1");
		JSONArray array = new JSONArray(result);
		if(array.length()<1){
			try {
				return_json(1010,"phone:平台不存在该用户");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		JSONObject json_object = array.getJSONObject(0);
		String user_id = json_object.get("userid").toString();
		String aid = new ArticleOperates().insert(plat_id, article_id);
		_model.mysql_model.executeUpdate("insert ignore into `" + this.database + "`.`article_zan` (`article_id`,`userid`,`inserttime`) VALUES ("+aid+","+user_id+",NOW())");
		try {
			return_json(200,"点赞成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 取消点赞
	 * @param domaincode 域名注册时返回的code
	 * @param platcode 子平台在网站下的标识ID（唯一）
	 * @param phone 点赞的用户手机号
	 * @param article_id 点赞的文章标识
	 * @return void
	 * @date 2016年8月17日 下午2:10:09
	 */
	public void unzan(){
		if(!init_chk()) return;
		String article_id = CommonFunction.getParameter(request, "article_id",true);
		if(article_id==null || article_id.equals("")){
			try {
				return_json(1004,"article_id:请传递文章id");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		String phone = CommonFunction.getParameter(request, "phone",true);
		if(phone==null || phone.equals("")){
			try {
				return_json(1008,"phone:手机号没有提交");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		String result = _model.mysql_model.executeQuery("select userid from `" + this.database + "`.`V_PLATFORM_USERS` where `phone`='"+phone+"' and `platformid`="+plat_id+" limit 1");
		JSONArray array = new JSONArray(result);
		if(array.length()<1){
			try {
				return_json(1010,"phone:平台不存在该用户");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		JSONObject json_object = array.getJSONObject(0);
		String user_id = json_object.get("userid").toString();
		String aid = new ArticleOperates().insert(plat_id, article_id);
		_model.mysql_model.executeUpdate("delete from `" + this.database + "`.`article_zan` where `article_id` = "+aid+" and `userid` = "+user_id);
		try {
			return_json(200,"取消点赞成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
