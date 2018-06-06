package webapp.posts.articles;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.Form;
import com.ruby.framework.function.CommonFunction;

//文章图片处理相关逻辑
public class Image extends ControllerBase{
	public void upload() throws IOException {
		response.addHeader("Access-Control-Allow-Origin","*");

		Map<String, String> form_data = new Form(context).createForm(request, "article");
		String upfile = form_data.get("upfile");
		
		JSONObject result = new JSONObject();
		if(upfile.length()>0){
			result.put("errno", 0);
		}else{
			result.put("errno", 1);
		}
		JSONArray imgarr = new JSONArray();
		if(upfile.length()>0){
			imgarr.put(0, upfile);
		}
		result.put("data", imgarr);
		response.getWriter().write(result.toString());
	}

}
