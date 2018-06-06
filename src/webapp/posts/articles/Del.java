package webapp.posts.articles;

import java.io.IOException;
import java.util.Map;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;
/**
 * 删除文章
 * @author sc000749
 *
 */
public class Del extends ControllerBase{
	public void index() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "article");
			String aid = form_data.get("aid");
			if(aid == null || aid.equals("")){
				return_json(1001,"文章ID为空");
				return;
			}
			if(!new Util().check_article(aid,uid)){
				return_json(1002,"文章无权删除");
				return;
			}
			//删除文章
			_model.mysql_model.executeUpdate("UPDATE `articles` SET `is_del`=1 WHERE `id`="+aid);
			return_json(200,"文章删除成功");
		}
	}
}
