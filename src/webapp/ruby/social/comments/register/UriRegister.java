package webapp.ruby.social.comments.register;

import java.io.IOException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;

import webapp.ruby.social.comments.CommentBase;

/**
 * 负责网址的注册和验证工作
 * <p>Title:UriRegister</p>
 * <p>Description:</p>
 * <p>Company:上海夜伦网络信息技术有限公司</p>
 * @author 方海亮
 * @date 2016年8月5日 下午1:40:18
 */
public class UriRegister extends CommentBase implements RegisterInterface {

	/**
	 * 网站域名注册
	 * @param domain 使用评论系统的网站域名
	 * @return void
	 * @throws IOException 
	 * @date 2016年8月5日 下午1:42:26
	 */
	public void register() throws IOException{
		String weburl = CommonFunction.getParameter(request, "domain", true);
		if(weburl==null || weburl.equals("")){
			return_json(1001, "domain:网站网址不能为空");
			return;
		}
		int point_num = weburl.length()-weburl.replaceAll("\\.", "").length();
		if(point_num<2 || point_num>3 || (weburl.indexOf("/")>=0)){
			return_json(1002, "domain:url不合法");
			return;
		}
		if(weburl.length()>35){
			return_json(1003, "domain:域名过长");
			return;
		}
		String auth = CommonFunction.EncoderByMd5(weburl+new Date());
		_model.mysql_model.executeUpdate("insert into `" + this.database + "`.`webs` (`url`,`auth`,`inserttime`) VALUES ('"+weburl+"','"+auth+"',NOW()) ON DUPLICATE KEY UPDATE `auth`='"+auth+"'");
		return_json(200, "网址登记成功，请记住该网址的令牌:"+auth,auth);
	}
	
	/**
	 * 获取域名
	 * @return String
	 * @date 2016年8月5日 下午2:38:16
	 * @param authcode
	 * @return
	 */
	public String getdomain(String authcode){
		String domain = "";
		String result = _model.mysql_model.executeQuery("select id,url from `" + this.database + "`.`webs` where `auth`='"+authcode+"' limit 1");
		JSONArray array = new JSONArray(result);
		if(array.length()!=1){
			return domain;
		}
		JSONObject json_object = array.getJSONObject(0);
		return json_object.get("id").toString();
	}
}
