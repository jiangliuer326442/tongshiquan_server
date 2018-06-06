package webapp.club;

import java.io.IOException;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

/**
 * 聊天管理
 * @author 方海亮
 *
 */
public class Chat extends ControllerBase{

	/**
	 * 获取聊天服务器配置信息
	 */
	public void getchatcfg() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Date day=new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss"); 
			String timestamp = df.format(day);
			final String CONF_FOLDER = context.getAttribute("CONF_FOLDER").toString();
			String appid = CommonFunction.readPropertiesFile(CONF_FOLDER + "chat.txt","rongyun_appid");
			String apptoken = CommonFunction.readPropertiesFile(CONF_FOLDER + "chat.txt","rongyun_apptoken");
			String sig = CommonFunction.EncoderByMd5(appid+uid+timestamp+apptoken);
			JSONObject result = new JSONObject();
			result.put("appid", appid);
			result.put("username", uid);
			result.put("timestamp", timestamp);
			result.put("sig", sig);
			return_json(200, "获取用户信息成功", result);
		}
	}
	
	/**
	 * 获取最近聊天对象
	 */
	public void getrecentchater() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String page = CommonFunction.getParameter(request, "p", true);
			if(page == null || page.equals("")){
				page = "1";
			}
			String pagenum = CommonFunction.getParameter(request, "pnum", true);
			if(pagenum == null || pagenum.equals("")){
				pagenum = "20";
			}
			String from = Integer.toString((Integer.valueOf(page)-1)*Integer.valueOf(pagenum));
			String sql = "SELECT "+
					"	touid, "+
					"	getuseravatar('"+companyid+"', touid) as avatar, "+
					"	getusernick("+uid+",'"+companyid+"', touid) as username, "+
					"	content, "+
					"	content_type "+
					"FROM "+
					"	chat_user cu "+
					"LEFT JOIN users u ON cu.touid = u.id "+
					"LEFT JOIN club_user_marker cum on cu.touid = cum.markeed_user and cum.operator_id = " + uid + " "+
					"LEFT JOIN user_bindcompany_record ub ON cu.touid = ub.userid and bindtime = (select max(bindtime) FROM user_bindcompany_record WHERE userid = cu.touid limit 1) "+
					"WHERE "+
					"	cu.myuid = " + uid + " "+
					"ORDER BY "+
					"	chattime DESC "+
					"LIMIT " + from + ", "+
					" "+pagenum;
			String result = _model.mysql_model.executeQuery(sql);
			return_json(200,"获取最近聊天列表成功",new JSONArray(result));
		}
	}
	
	
	/**
	 * 删除聊天人
	 * 删除彼此聊天记录，聊天内容
	 * @throws IOException
	 */
	public void delrecentchater() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "club");
			String touid = form_data.get("touid");
			if(Integer.parseInt(touid)<=0){
				return_json(1001,"用户不存在");
			}
			//删除聊天内容
			_model.mysql_model.executeUpdate("DELETE FROM chat_log WHERE ((fuid = "+uid+" AND tuid = "+touid+") or (fuid = "+touid+" AND tuid = "+uid+"))");
			//删除最近联系人
			_model.mysql_model.executeUpdate("DELETE FROM chat_user WHERE ((myuid = "+uid+" AND touid = "+touid+") or (myuid = "+touid+" AND touid = "+uid+"))");
			return_json(200,"删除最近联系人成功");
		}
	}
	
	/**
	 * 上传聊天文件
	 * @throws IOException
	 */
	public void addchatfile() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "chat");
			String file = form_data.get("file");
			if(file == null || file.equals("")){
				return_json(1001,"未选择文件");
			}
			return_json(200,"上传成功",file);
		}
	}
	
	/**
	 * 添加聊天记录
	 */
	public void addchat() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "chat");
			String touid = form_data.get("touid");
			if(Integer.parseInt(touid)<=0){
				return_json(1001,"用户不存在");
			}
			String content = form_data.get("content");
			if(content == null || content.length()>100 || content.equals("")){
				return_json(1002,"内容不合要求");
			}
			String content_type = form_data.get("content_type");
			if(content_type == null || content_type.equals("")){
				return_json(1003,"内部错误");
			}
			_model.mysql_model.executeUpdate("INSERT INTO `chat_log` (`fuid`, `tuid`, `content`, `content_type`) VALUES ("+uid+", "+touid+", '"+content+"', '"+content_type+"')");
			//生成最近聊天人
			//System.out.println("INSERT INTO chat_user(myuid,touid,content,content_type) VALUES ("+uid+","+touid+",'"+content+"',"+content_type+") ON DUPLICATE KEY UPDATE content = '"+content+"',content_type = '"+content_type+"',chattime = NOW()");
			_model.mysql_model.executeUpdate("INSERT INTO chat_user(myuid,touid,content,content_type) VALUES ("+uid+","+touid+",'"+content+"','"+content_type+"') ON DUPLICATE KEY UPDATE content = '"+content+"',content_type = '"+content_type+"',chattime = NOW()");
			_model.mysql_model.executeUpdate("INSERT INTO chat_user(myuid,touid,content,content_type) VALUES ("+touid+","+uid+",'"+content+"','"+content_type+"') ON DUPLICATE KEY UPDATE content = '"+content+"',content_type = '"+content_type+"',chattime = NOW()");
			return_json(200,"发表聊天成功");
		}
	}
	
	/**
	 * 获取聊天记录
	 * @throws IOException
	 */
	public void getchatlog() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "chat");
			String touid = form_data.get("touid");
			if(Integer.parseInt(touid)<=0){
				return_json(1001,"用户不存在");
			}
			String page = CommonFunction.getParameter(request, "p", true);
			if(page == null || page.equals("")){
				page = "1";
			}
			String pagenum = CommonFunction.getParameter(request, "pnum", true);
			if(pagenum == null || pagenum.equals("")){
				pagenum = "20";
			}
			String from = Integer.toString((Integer.valueOf(page)-1)*Integer.valueOf(pagenum));
			String result = _model.mysql_model.executeQuery("SELECT * FROM (SELECT fuid,tuid,content,content_type,create_time FROM chat_log WHERE ((fuid = "+uid+" AND tuid = "+touid+") OR (fuid = "+touid+" AND tuid = "+uid+")) AND delete_flg = 0 ORDER BY create_time DESC LIMIT "+from+","+pagenum+") AS chat_log ORDER BY chat_log.create_time ASC");
			return_json(200,"获取聊天记录成功",new JSONArray(result));
		}
	}
}
