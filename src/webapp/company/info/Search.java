package webapp.company.info;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import webapp.user.Cklogin;
import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

/**
 * 用户企业检索模块
 * 条件：
 * 		检索文字为全中文，至少四个汉字，并包含地名
 * 实现：
 * 		若关键字已被检索过，则直接读取检索结果
 * 		否则调取接口，记录检索结果和搜索关键词，以便下一次使用
 * 		限制用户频繁调用接口导致资金浪费
 * @author SC000749
 */
public class Search extends ControllerBase{
	public void index() throws IOException{
			Map<String, String> form_data = new Form(context).createForm(request, "company_form");
			//公司名称
			String name = form_data.get("name");
			if(name == null || name.equals("")){
				return_json(1001, "请填写公司名称");
				return;
			}
			if(name.length()<4){
				return_json(1002, "公司名称需要至少四个汉字");
				return;
			}
			//读取检索结果
			String sql = "select result from companys_search where keyword = '"+name+"' limit 1";
			String result = _model.mysql_model.executeQuery(sql);
			JSONArray array = new JSONArray(result);
			String company_str = "";
			if(array.length()>0){
				JSONObject json_object = array.getJSONObject(0);
				company_str = json_object.getString("result").substring(1,json_object.getString("result").indexOf("]"));
			}else{
				//调用接口检索
				JSONArray company_arr = searchinterface(name);
				if(company_arr == null) return;
				//保存检索结果
				for(int i=0; i<company_arr.length(); i++){
					JSONObject company_obj = company_arr.getJSONObject(i);
					sql = "REPLACE INTO `companys` (`KeyNo`, `Name`, `OperName`, `StartDate`, `Status`, `No`) VALUES ('"+company_obj.getString("KeyNo")+"', '"+company_obj.getString("Name")+"', '"+company_obj.getString("OperName")+"', '"+company_obj.getString("StartDate").substring(0,10)+"', '"+company_obj.getString("Status")+"', '"+company_obj.getString("No")+"')";
					_model.mysql_model.executeUpdate(sql);
					company_str += "\\'"+company_obj.getString("KeyNo") + "\\',";
				}
				company_str = company_str.substring(0, company_str.length()-1);
				//保存检索结果和检索关键字，限制用户最多检索的次数
				sql = "INSERT INTO `companys_search` (`keyword`, `result`, `search_time`) VALUES ('"+name+"', '["+company_str+"]', NOW())";
				_model.mysql_model.executeUpdate(sql);
				company_str = company_str.replace("\\", "");
			}
			//返回检索到的企业结果
			sql = "select KeyNo,`Name` from companys where KeyNo in ("+company_str+")";
			result = _model.mysql_model.executeQuery(sql);
			array = new JSONArray(result);
			return_json(200, "检索成功",array);
	}
	
	/**
	 * 根据关键字检索企业信息
	 * 使用企查查接口平台查询企业信息
	 * @param keyword
	 * @return
	 * @throws IOException 
	 * @throws JSONException 
	 */
	private JSONArray searchinterface(String keyword) throws JSONException, IOException{
		String key = CommonFunction.readPropertiesFile(context.getAttribute("CONF_FOLDER").toString() + "company.txt","MY_KEY");
		String url = "http://i.yjapi.com/ECISimple/Search?key="+key+"&dtype=json&keyword="+java.net.URLEncoder.encode(keyword, "utf-8");
		String result = CommonFunction.getUrlData(url);
		JSONObject json_object = new JSONObject(result);
		String status = json_object.getString("Status");
		if(status.equals("200")){
			JSONArray array = json_object.getJSONArray("Result");
			return array;
		}
		return_json(1004,json_object.getString("Message"));
		return null;
	}
}
