package webapp.innermsg.notice;

import java.io.IOException;
import java.util.Map;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

/**
 * 发送站内信
 * @author babytree
 *
 */
public class ShortMessage extends ControllerBase {
	
	public void send() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "message");
			String to_uid = form_data.get("tuid");
			if(to_uid == null || to_uid.equals("") || Integer.parseInt(uid) <= 0){
				return_json(1001,"收件人错误");
				return;
			}
			String content = form_data.get("content");
			if(content == null || content.equals("")){
				return_json(1002,"请填写发送信息");
				return;
			}
			_model.mysql_model.executeUpdate("INSERT INTO club_message(from_uid, to_uid, content) VALUES ("+uid+", "+to_uid+", \""+content+"\")");
			new webapp.email.notice.ShortMessage().send(context, Integer.parseInt(uid), Integer.parseInt(to_uid), content);
			return_json(200,"发送成功");
		}
	}
}
