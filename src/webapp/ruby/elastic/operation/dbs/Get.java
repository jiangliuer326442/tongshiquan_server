package webapp.ruby.elastic.operation.dbs;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.RestClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.function.BASE64;
import com.ruby.framework.function.Form;

import webapp.ruby.elastic.operation.ElasticBase;

public class Get extends ElasticBase {
	
	public void index() throws IOException{
		Map<String, String> form_data = new Form(context).createForm(request, "elastic");
		String username = form_data.get("username");
		if(username == null || username.equals("")){
			return_json(1001, "用户名为空");
			return;
		}
		String password = form_data.get("password");
		if(password == null || password.equals("")){
			return_json(1002, "密码为空");
			return;
		}
		String host = form_data.get("host");
		if(host == null || host.equals("")){
			return_json(1003, "主机为空");
			return;
		}	
		String port = form_data.get("port");
		if(port == null || port.equals("")){
			return_json(1004, "端口号为空");
			return;
		}
		JSONArray db_arr = new JSONArray();
		String response = "";
		BasicHeader header = new BasicHeader("Authorization", "Basic "+BASE64.encode((username+":"+password).getBytes()));
		RestClient restClient = RestClient.builder(
		        new HttpHost(host, Integer.parseInt(port), "http")).setDefaultHeaders(new Header[]{header}).build();
		try{
		response = EntityUtils.toString(restClient.performRequest(
		        "GET",
		        "http://"+host+":"+port+"/_cat/indices",
		        Collections.<String, String>emptyMap()).getEntity());
		}catch(org.elasticsearch.client.ResponseException e){
			return_json(401,"连接失败");
			return;
		}
		String[] db_line = response.split("\n");
		for(int i=0; i<db_line.length; i++){
			String db_name = db_line[i].split(" ")[2];
			db_arr.put(i, db_name);
		}
		return_json(200,"获取数据库列表成功", new JSONObject().put("list", db_arr));
	}
}
