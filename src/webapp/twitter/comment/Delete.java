package webapp.twitter.comment;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import webapp.user.Cklogin;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

public class Delete extends ControllerBase {
	
	/**
	 * 删除我的评论
	 * @throws IOException
	 */
	public void index() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String commentid = form_data.get("cmtid");
			if(commentid == null || commentid.equals("")){
				return_json(1001, "评论ID错误");
				return;
			}
			JSONObject cmt_obj = new JSONArray(_model.mysql_model.executeQuery("select uid from twt_comment where id = "+commentid)).getJSONObject(0);
			int commenter_uid = cmt_obj.getInt("uid");
			if(commenter_uid != Integer.parseInt(uid)){
				return_json(1002, "只能删除自己的评论");
				return;
			}
			_model.mysql_model.executeUpdate("delete from twt_comment where id = "+commentid);
			return_json(200, "删除成功");
		}	
	}
}
