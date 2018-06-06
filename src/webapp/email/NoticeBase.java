package webapp.email;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.email.EmailSend;
import com.ruby.framework.model.DbManager;

import webapp.company.info.Getcompany;

public class NoticeBase extends EmailSend {
	protected DbManager _model;
	
	/**
	 * 获取收件人/发件人邮箱信息
	 */
	public JSONObject getuserinfo(int userid){
		String companyid = new Getcompany().getcompanyidbyuser(userid);
		String result = _model.mysql_model.executeQuery("SELECT user_name,user_mail,user_avatar FROM companys_employees WHERE companyid = '"+companyid+"' AND userid = "+Integer.valueOf(userid)+" LIMIT 1");
		JSONObject user_object = new JSONArray(result).getJSONObject(0);
		return user_object;
	}
	
	@Override
	protected void init(ServletContext context) {
		final String CONF_FOLDER = context.getAttribute("CONF_FOLDER").toString();
		this.MAIL_ACCOUNT = CommonFunction.readPropertiesFile(CONF_FOLDER + "mail.txt","NOTICE_MAIL_ACCOUNT");
		this.MAIL_NAME = "同事俱乐部";
		this.MAIL_PASSWD = CommonFunction.readPropertiesFile(CONF_FOLDER + "mail.txt","NOTICE_MAIL_PASSWD");
		this.MAIL_SMTP = CommonFunction.readPropertiesFile(CONF_FOLDER + "mail.txt","NOTICE_MAIL_SMTP");
		this.MAIL_PORT = CommonFunction.readPropertiesFile(CONF_FOLDER + "mail.txt","NOTICE_MAIL_PORT");
		this._model = DbManager.getInstance(context);
	}

}
