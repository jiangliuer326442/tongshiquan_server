package webapp.posts.articles;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

/**
 * 文章修改模块
 * @author 方海亮
 *
 */
public class Edit extends ControllerBase{
	
	//修改文章
	public void setarticle() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "article");
			String aid = form_data.get("aid");
			if(aid == null || aid.equals("")){
				return_json(1001,"文章ID为空");
				return;
			}
			if(!new Util().check_article(aid,uid)){
				return_json(1002,"文章无权修改");
				return;
			}
			String title = form_data.get("title");
			String desc = form_data.get("desc");
			String content = form_data.get("content");
			String thumb = new Util().getcontentthumb(content);
			String is_allow_comment = form_data.get("is_allow_comment");
			String is_hide_comment = form_data.get("is_hide_comment");
			String is_top = form_data.get("is_top");
			String showtime = form_data.get("showtime");
			//保存文章
			_model.mysql_model.executeUpdate("UPDATE `articles` SET `title`='"+title+"', `desc`='"+desc+"', `thumb`='"+thumb+"', `content`='"+content+"', `userid`="+uid+", `updatetime`=NOW(), `is_allow_comment`="+is_allow_comment+", `is_hide_comment`="+is_hide_comment+", `is_top`="+is_top+", `showtime`='"+showtime+"' WHERE `id`="+aid);
			return_json(200,"文章修改成功");
		}
	}
	
	//修改置顶状态
	public void setistop() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "article");
			String aid = form_data.get("aid");
			if(aid == null || aid.equals("")){
				return_json(1001,"文章ID为空");
				return;
			}
			if(!new Util().check_article(aid,uid)){
				return_json(1002,"文章无权修改");
			}
			String is_top = form_data.get("is_top");
			if(is_top == null || is_top.equals("")){
				return_json(1003,"置顶状态为空");
				return;
			}
			//变更置顶状态
			_model.mysql_model.executeUpdate("UPDATE articles SET is_top = "+is_top+", updatetime=NOW() WHERE id = "+aid);
			return_json(200,"修改成功");
		}
	}
}
