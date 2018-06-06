package webapp.ruby.social.comments.register;

import java.io.IOException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;

import webapp.ruby.social.comments.CommentBase;

public class PlatRegister extends CommentBase implements RegisterInterface {
	
	/**
	 * 平台账户注册
	 * @param domaincode 域名注册时返回的code
	 * @param platid 子平台在网站下的标识ID（唯一）
	 * @return void
	 * @throws IOException 
	 * @date 2016年8月5日 下午3:28:13
	 */
	public void register() throws IOException{
		String domaincode = CommonFunction.getParameter(request, "domaincode",true);
		if(domaincode==null || domaincode.equals("")){
			return_json(1001,"domaincode:请传递域名注册时返回的域名token");
			return;
		}
		String domain_id = new UriRegister().getdomain(domaincode);
		if(domain_id==null || domain_id.equals("")){
			return_json(1002,"domaincode:域名token错误");
			return;
		}
		String plat_form_str = CommonFunction.getParameter(request, "platid", true);
		if(plat_form_str == null || plat_form_str.length()>100 || plat_form_str.length()<1){
			return_json(1003,"platid:平台标识符非法");
			return;
		}
		String result = _model.mysql_model.executeQuery("select id from `" + this.database + "`.`platforms` where `web_id`="+domain_id+" and plat_form_id='"+plat_form_str+"' limit 1");
		JSONArray array = new JSONArray(result);
		String auth = CommonFunction.EncoderByMd5(plat_form_str+new Date());
		if(array.length()==1){
			_model.mysql_model.executeUpdate("update `" + this.database + "`.`platforms` set `auth`='"+auth+"'  where `web_id`="+domain_id+" and plat_form_id='"+plat_form_str+"'");
		}else{
			_model.mysql_model.executeUpdate("insert into `" + this.database + "`.`platforms` (`web_id`,`plat_form_id`,`auth`,`inserttime`) VALUES ("+domain_id+",'"+plat_form_str+"','"+auth+"',NOW()) ON DUPLICATE KEY UPDATE `auth`='"+auth+"'");
		}
		return_json(200, "该网站平台登记成功，请记住平台令牌:"+auth,auth);
	}
	
	/**
	 * 获取平台
	 * @return String
	 * @date 2016年8月5日 下午4:36:18
	 * @param domaincode
	 * @param platcode
	 * @return
	 */
	public String getplatform(String domaincode,String platcode){
		String platform = "";
		String result = _model.mysql_model.executeQuery("SELECT plat.id FROM `" + this.database + "`.`platforms` plat LEFT JOIN `" + this.database + "`.`webs` w ON plat.web_id = w.id WHERE w.auth = '"+domaincode+"' AND plat.auth='"+platcode+"' limit 1");
		JSONArray array = new JSONArray(result);
		if(array.length()!=1){
			return platform;
		}
		JSONObject json_object = array.getJSONObject(0);
		return json_object.get("id").toString();
	}

}
