package webapp.mobilemsg;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;
import com.ruby.framework.function.FormInterface;
import com.ruby.framework.function.message.MessageFactory;
import com.ruby.framework.function.message.MessageInterface;

import webapp.user.Cklogin;

/**
 * 短信发送处理类
 * <p>Title:Send</p>
 * <p>Description:</p>
 * <p>Company:上海夜伦网络信息技术有限公司</p>
 * @author 方海亮
 * @date 2016年8月25日 下午4:56:16
 */
public class Send extends ControllerBase {
	private MessageInterface msg_obj;
	
	public void init(){
		super.init();
		msg_obj = MessageFactory.produce(context);
	}
	
	/**
	 * 发送登录短信
	 * @throws IOException
	 */
	public void sendLoginmsg() throws IOException{
		Map<String, String> _values = new Form(context).createForm(request, "registermsg_form");
		String phone = _values.get("phone");
		if(phone == null || phone.equals("")){
			return_json(2007, "请填写手机号");
			return;
		}
		if(!CommonFunction.isMobileNO(phone)){
			return_json(2008, "手机号错误");
			return;
		}
		String result = _model.mysql_model.executeQuery("select 1 from `users` where `phone` = '"+phone+"' limit 1");
		JSONArray json = new JSONArray(result);
		if(json.length()==0){
			return_json(2009, "手机号不存在");
			return;
		}
		result = _model.mysql_model.executeQuery("SELECT COUNT(1) as msgnums FROM msgrecords WHERE phone = '"+phone+"' AND type=2 AND to_days(inserttime) >= to_days(now())");
		json = new JSONArray(result);
		int msgnums = json.getJSONObject(0).getInt("msgnums");
		if(msgnums>=3){
			return_json(2010, "发送次数超过上限");
			return;
		}
		String codes = Integer.toString((int)(Math.random()*9000+1000));
		if(msg_obj.send(phone, 239377, new String[]{codes})){
			_model.redis_model.set("log_"+phone, codes, 180);
			_model.mysql_model.executeUpdate("INSERT INTO `msgrecords` (`phone`, `code`, `type`) VALUES ('"+phone+"', '"+codes+"', 2)");
			return_json(200, "短信发送成功");
		}else{
			return_json(2011, "发送失败");
		}
	}

	/**
	 * 用户更换手机号短信发送
	 * @return void
	 * @throws IOException 
	 */
	public void sendChangemsg() throws IOException{
		FormInterface _form = new Form(context);
		Map<String, String> _values = _form.createForm(request, "registermsg_form");
		String phone = _values.get("phone");
		if(phone == null || phone.equals("")){
			return_json(2007, "请填写手机号");
			return;
		}
		if(!CommonFunction.isMobileNO(phone)){
			return_json(2008, "手机号错误");
			return;
		}
		String result = _model.mysql_model.executeQuery("SELECT COUNT(1) as msgnums FROM msgrecords WHERE phone = '"+phone+"' AND type=3 AND to_days(inserttime) >= to_days(now())");
		JSONArray json = new JSONArray(result);
		int msgnums = json.getJSONObject(0).getInt("msgnums");
		if(msgnums>=3){
			return_json(2010, "发送次数超过上限");
			return;
		}
		result = _model.mysql_model.executeQuery("SELECT 1 FROM companys_employees WHERE user_phone = '"+phone+"' AND stop_flg = 0 AND userid IS NOT NULL");
		if(new JSONArray(result).length()>0){
			return_json(2011, "新手机号已经被使用");
			return;
		}
		String codes = Integer.toString((int)(Math.random()*9000+1000));
		if(msg_obj.send(phone, 239396, new String[]{codes})){
			_model.redis_model.set("chg_"+phone, codes, 180);
			_model.mysql_model.executeUpdate("INSERT INTO `msgrecords` (`phone`, `code`, `type`) VALUES ('"+phone+"', '"+codes+"', 3)");
			return_json(200, "短信发送成功");
		}else{
			return_json(2011, "发送失败");
		}

	}
	
	/**
	 * 用户注册短信发送
	 * @return void
	 * @throws IOException 
	 * @date 2016年8月25日 下午4:56:35
	 */
	public void sendRegistermsg() throws IOException{ 
			FormInterface _form = new Form(context);
			Map<String, String> _values = _form.createForm(request, "registermsg_form");
			String phone = _values.get("phone");
			if(phone == null || phone.equals("")){
				return_json(2007, "请填写手机号");
				return;
			}
			if(!CommonFunction.isMobileNO(phone)){
				return_json(2008, "手机号错误");
				return;
			}
			String result = _model.mysql_model.executeQuery("SELECT COUNT(1) as msgnums FROM msgrecords WHERE phone = '"+phone+"' AND type=1 AND to_days(inserttime) >= to_days(now())");
			JSONArray json = new JSONArray(result);
			int msgnums = json.getJSONObject(0).getInt("msgnums");
			if(msgnums>=3){
				return_json(2010, "发送次数超过上限");
				return;
			}
			result = _model.mysql_model.executeQuery("SELECT 1 FROM companys_employees WHERE user_phone = '"+phone+"' AND stop_flg = 0 AND userid IS NOT NULL");
			if(new JSONArray(result).length()>0){
				return_json(2011, "手机号已被注册");
				return;
			}
			String codes = Integer.toString((int)(Math.random()*9000+1000));
			if(msg_obj.send(phone, 239377, new String[]{codes})){
				_model.redis_model.set("reg_"+phone, codes, 180);
				_model.mysql_model.executeUpdate("INSERT INTO `msgrecords` (`phone`, `code`, `type`) VALUES ('"+phone+"', '"+codes+"', 1)");
				return_json(200, "短信发送成功");
			}else{
				return_json(2011, "发送失败");
			}
	}
	
	/**
	 * 验证注册验证码是否正确
	 * @return boolean
	 * @date 2016年8月31日 下午1:26:50
	 * @param phone
	 * @param codes
	 * @return
	 */
	public boolean ckregcode(String phone, String codes){
		String code = _model.redis_model.get("reg_"+phone);
		if(code.equals(codes)){
			return true;
		}else{
			return false;
		}
	}

	public boolean cklogcode(String phone, String codes){
		String code = _model.redis_model.get("log_"+phone);
		if(code.equals(codes)){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean ckchgcode(String phone, String codes){
		String code = _model.redis_model.get("chg_"+phone);
		if(code != null && code.equals(codes)){
			return true;
		}else{
			return false;
		}
	}
}
