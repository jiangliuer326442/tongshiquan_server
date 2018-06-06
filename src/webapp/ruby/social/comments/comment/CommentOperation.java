package webapp.ruby.social.comments.comment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;

import webapp.ruby.social.comments.CommentBase;
import webapp.ruby.social.comments.article.ArticleOperates;
import webapp.ruby.social.comments.register.PlatRegister;

public class CommentOperation extends CommentBase {
	
	/**
	 * 增加评论
	 * @param domaincode 域名注册时返回的code
	 * @param platcode 子平台在网站下的标识ID（唯一）
	 * @param phone 评论的用户手机号
	 * @param article_id 评论的文章标识
	 * @param content 评论内容
	 * @return void
	 * @throws IOException 
	 * @date 2016年8月11日 上午11:37:12
	 */
	public void add() throws IOException{
		if(!init_chk()) return;
		String article_id = CommonFunction.getParameter(request, "article_id",true);
		if(article_id==null || article_id.equals("")){
			return_json(1004,"article_id:请传递文章id");
			return;
		}
		if(article_id.length()>50){
			try {
				return_json(1005,"article_id:文章id过长");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		String content = CommonFunction.getParameter(request, "content");
		if(content==null || content.equals("")){
			return_json(1006,"content:评论内容不能为空");
			return;
		}
		if(content.length()>2000){
			return_json(1007,"content:评论内容过长");
			return;
		}
		String phone = CommonFunction.getParameter(request, "phone",true);
		if(phone==null || phone.equals("")){
			return_json(1008,"phone:手机号没有提交");
			return;
		}
		if(!CommonFunction.isMobileNO(phone)){
			return_json(1009,"phone:手机号格式错误");
			return;
		}
		String result = _model.mysql_model.executeQuery("select userid from `" + this.database + "`.`V_PLATFORM_USERS` where `phone`='"+phone+"' and `platformid`="+plat_id+" limit 1");
		JSONArray array = new JSONArray(result);
		if(array.length()<1){
			return_json(1010,"phone:平台不存在该用户");
			return;
		}
		JSONObject json_object = array.getJSONObject(0);
		String user_id = json_object.get("userid").toString();
		String aid = new ArticleOperates().insert(plat_id, article_id);
		_model.mysql_model.executeUpdate("insert into `" + this.database + "`.`comments` (`article_id`,`user_id`,`comment`,`inserttime`) VALUES ("+aid+","+user_id+",'"+content+"',NOW())");
		//获取评论数量
		result = _model.mysql_model.executeQuery("SELECT commentnum FROM `" + this.database + "`.articles WHERE plat_id = "+plat_id+" AND id = "+aid);
		array = new JSONArray(result);
		return_json(200,"评论成功",array.getJSONObject(0).getInt("commentnum"));
	}
	
	/**
	 * 增加评论的回复
	 * @param domaincode 域名注册时返回的code
	 * @param platcode 子平台在网站下的标识ID（唯一）
	 * @param phone 评论的用户手机号
	 * @param comment_id 顶级评论的id
	 * @param content 评论内容
	 * @return void
	 * @throws IOException 
	 * @date 2016年10月9日 下午3:38:07
	 */
	public void reply_add() throws IOException{
		if(!init_chk()) return;
                String article_id;
		String comment_id = CommonFunction.getParameter(request, "comment_id",true);
		if(comment_id==null || comment_id.equals("")){
			return_json(1004,"comment_id:请传递顶级评论id");
			return;
		}
		String result = _model.mysql_model.executeQuery("select aid from `" + this.database + "`.`V_ARTICLE_COMMENTS` where `comment_id`='"+comment_id+"' and `plat_id`="+plat_id+" limit 1");
		JSONArray array = new JSONArray(result);
		if(array.length()<1){
			return_json(1005,"comment_id:评论id不存在");
			return;
		}else{
			JSONObject json_object = array.getJSONObject(0);
			article_id = Integer.toString(json_object.getInt("aid"));
        }
		String content = CommonFunction.getParameter(request, "content",true);
		if(content==null || content.equals("")){
			return_json(1006,"content:评论内容不能为空");
			return;
		}
		if(content.length()>200){
			return_json(1007,"content:评论内容过长");
			return;
		}
		String phone = CommonFunction.getParameter(request, "phone",true);
		if(phone==null || phone.equals("")){
			return_json(1008,"phone:手机号没有提交");
			return;
		}
		if(!CommonFunction.isMobileNO(phone)){
			return_json(1009,"phone:手机号格式错误");
			return;
		}
		result = _model.mysql_model.executeQuery("select userid from `" + this.database + "`.`V_PLATFORM_USERS` where `phone`='"+phone+"' and `platformid`="+plat_id+" limit 1");
		array = new JSONArray(result);
		if(array.length()<1){
			return_json(1010,"phone:平台不存在该用户");
			return;
		}
		JSONObject json_object = array.getJSONObject(0);
		String user_id = json_object.get("userid").toString();
		_model.mysql_model.executeUpdate("insert into `" + this.database + "`.`comments` (`article_id`,`user_id`,`comment`,`lastcomment_id`,`inserttime`) VALUES ("+article_id+","+user_id+",'"+content+"',"+comment_id+",NOW())");
		//获取评论数量
		result = _model.mysql_model.executeQuery("SELECT commentnum FROM `" + this.database + "`.articles WHERE plat_id = "+plat_id+" AND id = "+article_id);
		array = new JSONArray(result);
		return_json(200,"评论成功",array.getJSONObject(0).getInt("commentnum"));
	}

}
