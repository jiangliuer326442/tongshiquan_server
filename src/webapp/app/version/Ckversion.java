package webapp.app.version;

import webapp.app.AppBase;
import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.function.Form;

public class Ckversion extends AppBase {
	/**
	 * 安卓端版本更新
	 * 获取最新版信息，如果最新版的版本号与当前版本号不一致
	 * 首先查找当前版本号的升级文件，如果能够找到，就从当前版本适用差量升级
	 * 如果没有找到，使用最新版的wgt包
	 * 如果没有wgt包，使用APK
	 * 优先等级为 差量升级 > wgt升级  > apk升级
	 * @throws IOException 
	 */
	public void android() throws IOException{
		Map<String, String> form_data = new Form(context).createForm(request, "app");
		String version = form_data.get("version");
		if(version == null || version.equals("")){
			return_json(1001,"版本号错误");
			return;
		}
		String sql = "SELECT version,minversion,apk_url,wgt_url,remark,`force` FROM " + this.database + ".app_version ORDER BY create_time DESC LIMIT 1";
		String result = _model.mysql_model.executeQuery(sql);
		JSONArray array = new JSONArray(result);
		if(array.length()>=1){
			JSONObject json_object = array.getJSONObject(0);
			String new_version = json_object.getString("version");
            if(!new_version.equals(version)){
            	String url = null; //升级包下载地址
							if(Integer.parseInt(version.replace(".", "")) < json_object.getInt("minversion")){
								url = json_object.getString("apk_url");
							}else{
								url = json_object.getString("wgt_url");
							}
            	json_object.put("url", url);
                return_json(1003,"发现新版本"+new_version, json_object);
            }else{
                return_json(1002,"当前是最新版本");
            }
		}else{
			return_json(1002,"当前是最新版本");
		}
	}
}
