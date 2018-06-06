package webapp.twitter.comment;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

/**
 * 发表评论
 * @author babytree
 *
 */
public class Add extends ControllerBase {
	
	/**
	 * 发表回复
	 * @throws IOException
	 */
	public void reply() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String twtid = form_data.get("twtid");
			if(twtid == null || twtid.equals("")){
				return_json(1001, "推特ID错误");
				return;
			}
			String commentid = form_data.get("cmtid");
			if(commentid == null || commentid.equals("")){
				return_json(1002, "评论ID错误");
				return;
			}
			String content = form_data.get("content");
			if(content == null || content.equals("")){
				return_json(1003, "回复内容为空");
				return;
			}
			if(new JSONArray(_model.mysql_model.executeQuery("SELECT 1 FROM twt_comment WHERE twt_id = "+twtid+" AND uid != "+uid+" AND is_del = 0")).length() == 0) {
				return_json(1004, "评论不存在");
				return;
			}
			_model.mysql_model.executeUpdate("INSERT INTO twt_comment(`twt_id`, `uid`, `content`, `uperid`) VALUES ("+twtid+", "+uid+", '"+content+"', "+commentid+")");
			return_json(200, "评论成功");
		}
	}
	
	/**
	 * 添加评论
	 * @throws IOException
	 */
	public void index() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String twtid = form_data.get("twtid");
			if(twtid == null || twtid.equals("")){
				return_json(1001, "推特ID错误");
				return;
			}
			String content = form_data.get("content");
			if(content == null || content.equals("")){
				return_json(1002, "评论内容为空");
				return;
			}
			_model.mysql_model.executeUpdate("INSERT INTO twt_comment(`twt_id`, `uid`, `content`) VALUES ("+twtid+", "+uid+", '"+content+"')");
			return_json(200, "评论成功");
		}
	}
}
