package webapp.twitter;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

public class Hide extends ControllerBase {
	
	/**
	 * 获取屏蔽的用户列表
	 * @throws IOException
	 */
	public void gethideuidls() throws IOException {
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String result = _model.mysql_model.executeQuery("SELECT tuid,getuseravatar('"+companyid+"', tuid) as avatar,getusernick("+uid+",'"+companyid+"', tuid) as nick,DATE_FORMAT(addtime,'%c月%e日') as addtime FROM twt_enduserlog WHERE fuid = "+uid+" AND ouid = "+uid);
			return_json(200,"获取屏蔽的用户列表成功",new JSONArray(result));
		}
	}
	
	/**
	 * 删除屏蔽的用户
	 * @throws IOException
	 */
	public void delhideuid() throws IOException {
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String tuid = form_data.get("tuid");
			if(tuid == null || tuid.equals("")){
				return_json(1001, "用户ID错误");
				return;
			}
			_model.mysql_model.executeUpdate("DELETE FROM twt_enduserlog WHERE fuid = "+uid+" AND tuid = "+tuid+" AND ouid = "+uid);
			return_json(200,"取消屏蔽成功");
		}
	}
	
	/**
	 * 删除我操作的屏蔽我的同事圈的用户
	 * @throws IOException
	 */
	public void delmyhideuid() throws IOException {
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String fuid = form_data.get("fuid");
			if(fuid == null || fuid.equals("")){
				return_json(1001, "用户ID错误");
				return;
			}
			_model.mysql_model.executeUpdate("DELETE FROM twt_enduserlog WHERE fuid = "+fuid+" AND tuid = "+uid+" AND ouid = "+uid);
			return_json(200,"取消屏蔽成功");
		}
	}
	
	/**
	 * 屏蔽指定ID的推特
	 * @throws IOException
	 */
	public void hidetwtbyid() throws IOException {
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String twtid = form_data.get("twtid");
			if(twtid == null || twtid.equals("")){
				return_json(1001, "推特ID错误");
				return;
			}
			_model.mysql_model.executeUpdate("INSERT IGNORE INTO `twt_endidlog` (`twt_id`, `uid`) VALUES ("+twtid+", "+uid+")");
			return_json(200, "屏蔽推特成功");
		}
	}
	
	/**
	 * 不让对方看到我的动态
	 * @throws IOException
	 */
	public void hidemytwtbyuid() throws IOException {
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String fuid = form_data.get("fuid");
			if(fuid == null || fuid.equals("")){
				return_json(1001, "屏蔽用户错误");
				return;
			}
			if(fuid.equals(uid)){
				return_json(1002, "不能屏蔽自己");
				return;
			}
			_model.mysql_model.executeUpdate("INSERT IGNORE INTO `twt_enduserlog` (`fuid`, `tuid`, `ouid`) VALUES ("+fuid+", "+uid+","+uid+")");
			return_json(200, "隐藏该用户推特成功");
		}
	}
	
	/**
	 * 屏蔽指定用户
	 * @throws IOException
	 */
	public void hidetwtbyuid() throws IOException {
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map<String, String> form_data = new Form(context).createForm(request, "twitter");
			String tuid = form_data.get("tuid");
			if(tuid == null || tuid.equals("")){
				return_json(1001, "屏蔽用户错误");
				return;
			}
			if(tuid.equals(uid)){
				return_json(1002, "不能屏蔽自己");
				return;
			}
			_model.mysql_model.executeUpdate("INSERT IGNORE INTO `twt_enduserlog` (`fuid`, `tuid`, `ouid`) VALUES ("+uid+", "+tuid+","+uid+")");
			return_json(200, "隐藏该用户推特成功");
		}
	}
	
}
