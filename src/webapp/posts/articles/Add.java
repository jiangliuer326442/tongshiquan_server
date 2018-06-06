package webapp.posts.articles;

import java.io.IOException;
import java.util.Map;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

public class Add extends ControllerBase{
	
	//上传公告文章
	public void index() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "article");
			String sectionid = form_data.get("sectionid");
			String title = form_data.get("title");
			String desc = form_data.get("desc");
			String orgin_content = form_data.get("content");
			String content = orgin_content;
			String content_text = form_data.get("content_txt");
			String is_allow_comment = form_data.get("is_allow_comment");
			String is_hide_comment = form_data.get("is_hide_comment");
			String is_top = form_data.get("is_top");
			String showtime = form_data.get("showtime");
			String thumb = new Util().getcontentthumb(content);
			//保存文章
			String sql = "INSERT INTO `articles` (`title`,`thumb`,`desc`,`content`,`content_text`,`userid`,`inserttime`,`sectionid`,`is_allow_comment`,`is_hide_comment`,`is_top`,`showtime`) SELECT '"+title+"','"+thumb+"','"+desc+"','"+content+"','"+content_text+"',"+uid+",NOW(),id,"+is_allow_comment+","+is_hide_comment+","+is_top+", '"+showtime+"' FROM post_models WHERE companyid = '"+companyid+"' AND id = "+sectionid;
			_model.mysql_model.executeUpdate(sql);
			return_json(200,"文章添加成功");
		}
	}

}
