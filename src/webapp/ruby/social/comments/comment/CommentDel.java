package webapp.ruby.social.comments.comment;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;

import webapp.ruby.social.comments.CommentBase;

/**
 * 删除评论
 * 要求能够删除他的全部下级评论
 * @author sc000749
 *
 */
public class CommentDel extends CommentBase {
	public void delbycid() throws IOException{
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
		//删除评论
		del(comment_id);
		//获取评论数量
		result = _model.mysql_model.executeQuery("SELECT commentnum FROM `" + this.database + "`.articles WHERE plat_id = "+plat_id+" AND id = "+article_id);
		array = new JSONArray(result);
		return_json(200,"删除成功",array.getJSONObject(0).getInt("commentnum"));
	}
	
	private void del(String comment_id){
		_model.mysql_model.executeUpdate("DELETE FROM `" + this.database + "`.`comments` WHERE id = " + comment_id);
		//获取下线评论列表
		String result = _model.mysql_model.executeQuery("SELECT id FROM `" + this.database + "`.`comments` WHERE lastcomment_id = " + comment_id);
		JSONArray array = new JSONArray(result);
		if(array.length()>0){
			//删除下线评论
			for(int i=0; i<array.length(); i++){
				comment_id = Integer.toString(array.getJSONObject(i).getInt("id"));
				del(comment_id);
			}
		}
	}
}
