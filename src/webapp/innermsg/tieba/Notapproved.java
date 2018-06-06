package webapp.innermsg.tieba;

import javax.servlet.ServletContext;

import com.ruby.framework.controller.ControllerBase;

public class Notapproved extends ControllerBase {
	public void send(ServletContext context, int from_uid, int to_uid, String tieba_name){
		//发送站内信
		_model.mysql_model.executeUpdate("INSERT INTO club_message(from_uid, to_uid, content) VALUES ("+Integer.valueOf(from_uid)+", "+Integer.valueOf(to_uid)+", \"贴吧\""+tieba_name+"\"管理员审核不予通过\")");
		new webapp.email.tieba.Notapproved().send(context, from_uid, to_uid, tieba_name);
	}
}
