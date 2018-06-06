package webapp.email.tieba;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;

import webapp.email.NoticeBase;

public class Approved extends ControllerBase {
	public void send(ServletContext context, int from_uid, int to_uid, String tieba_name){
		NoticeBase _notice_base = new NoticeBase();
		JSONObject to_userinfo = _notice_base.getuserinfo(to_uid);
		String to_name = to_userinfo.getString("user_name");
		if(!to_userinfo.getString("user_mail").equals("")){
			//获取邮件模板
			String result = _model.mysql_model.executeQuery("SELECT template FROM email_template WHERE id = 2");
			String template = new JSONArray(result).getJSONObject(0).getString("template");
			//内容替换
			template = template.replace("%to_name%", to_name);
			template = template.replace("%tieba_name%", tieba_name);
			template = template.replace("%date%", new SimpleDateFormat("MM/dd HH:mm").format(new Date()));
			try {
				_notice_base.send(context, to_userinfo.getString("user_mail"), to_name, "贴吧审核通过通知", template);
			} catch (Exception e) {
				System.out.println("邮件发送失败，收件人地址是："+to_userinfo.getString("user_mail"));
			}
		}
	}
}
