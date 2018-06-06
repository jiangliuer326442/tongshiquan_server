package webapp.email.notice;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;

import webapp.email.NoticeBase;

/**
 * 发送站内信
 * @author babytree
 *
 */
public class ShortMessage extends ControllerBase {
	
	private String from_account_name = null; //发件人邮箱账号
	private String from_account = null; //发件人邮箱
	private String to_name = null; //收件人姓名
	public void send(ServletContext context, int from_uid, int to_uid, String content){
		//发送站内信
		NoticeBase _notice_base = new NoticeBase();
		//发送邮件通知
		JSONObject from_userinfo = _notice_base.getuserinfo(from_uid);
		JSONObject to_userinfo = _notice_base.getuserinfo(to_uid);
		from_account_name = from_userinfo.getString("user_name");
		from_account = from_userinfo.getString("user_mail");
		to_name = to_userinfo.getString("user_name");
		if(!to_userinfo.getString("user_mail").equals("")){
			//获取邮件模板
			String result = _model.mysql_model.executeQuery("SELECT template FROM email_template WHERE id = 1");
			String template = new JSONArray(result).getJSONObject(0).getString("template");
			//内容替换
			template = template.replace("%to_name%", to_name);
			template = template.replace("%content%", content);
			template = template.replace("%from_account%", from_account);
			template = template.replace("%from_account_name%", from_account_name);
			template = template.replace("%date%", new SimpleDateFormat("MM/dd HH:mm").format(new Date()));
			try {
				_notice_base.send(context, to_userinfo.getString("user_mail"), to_name, from_account_name+"给您发来一条信息", template);
			} catch (Exception e) {
				System.out.println("邮件发送失败，收件人地址是："+to_userinfo.getString("user_mail"));
			}
		}
	}
}
