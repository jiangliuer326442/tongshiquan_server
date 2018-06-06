package webapp.twitter;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

/**
 * 点赞相关
 * @author babytree
 *
 */
public class Zan extends ControllerBase {
	
	/**
	 * 点赞
	 * @throws IOException 
	 */
	public void addzan() throws IOException {
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String twtid = form_data.get("twtid");
			if(twtid == null || twtid.equals("")){
				return_json(1001, "推特ID错误");
				return;
			}
			String result = _model.mysql_model.executeQuery("SELECT 1 FROM twt_zan WHERE msgid = "+twtid+" AND uid = "+uid+" LIMIT 1");
			if(new JSONArray(result).length()>0) {
				return_json(1002, "不能重复点赞");
				return;
			}
			_model.mysql_model.executeUpdate("UPDATE twt_message SET zan_num = zan_num + 1 WHERE id = "+twtid);
			_model.mysql_model.executeUpdate("INSERT INTO twt_zan(`msgid`, `uid`) VALUES ("+twtid+", "+uid+")");
			return_json(200, "点赞成功");
		}
	}
	
	/**
	 * 取消点赞
	 * @throws IOException
	 */
	public void removezan() throws IOException {
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String twtid = form_data.get("twtid");
			if(twtid == null || twtid.equals("")){
				return_json(1001, "推特ID错误");
				return;
			}
			String result = _model.mysql_model.executeQuery("SELECT 1 FROM twt_zan WHERE msgid = "+twtid+" AND uid = "+uid+" LIMIT 1");
			if(new JSONArray(result).length()==0) {
				return_json(1002, "还未点赞");
				return;
			}
			_model.mysql_model.executeUpdate("UPDATE twt_message SET zan_num = zan_num - 1 WHERE id = "+twtid);
			_model.mysql_model.executeUpdate("DELETE FROM twt_zan WHERE `msgid` = "+twtid+" AND `uid` = "+uid);
			return_json(200, "取消点赞");
		}
	}
}
