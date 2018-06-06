package webapp.tieba.articles;

import java.io.IOException;
import java.util.Map;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.posts.articles.Util;
import webapp.user.Cklogin;

/**
 * 添加帖子
 * @author 方海亮
 *
 */
public class Add extends ControllerBase{
	
	//发帖
	public void index() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "article");
			String sectionid = form_data.get("sectionid");
			String title = form_data.get("title");
			String content = form_data.get("content").replaceAll("\"","\\\\\"");
			String content_text = form_data.get("content_txt");
			//保存文章
			_model.mysql_model.executeUpdate("INSERT INTO `articles` (`title`,`thumb`,`desc`,`content`,`content_text`,`userid`,`inserttime`,`sectionid`,`is_allow_comment`,`is_hide_comment`,`is_top`,`showtime`) SELECT '"+title+"','','','"+content+"','"+content_text+"',"+uid+",NOW(),id,is_allow_comment,is_hide_comment,0, NOW() FROM post_models WHERE companyid = '"+companyid+"' AND id = "+sectionid);
			//更新帖子数量
			_model.mysql_model.executeUpdate("UPDATE postbar_cfg SET total_posts = ( SELECT count(1) FROM articles WHERE sectionid = "+sectionid+" AND is_del = 0), today_posts = ( SELECT count(1) FROM articles WHERE sectionid = "+sectionid+" AND showtime = date_format(now(),'%y-%m-%d') AND is_del = 0) WHERE id = "+sectionid);
			return_json(200,"发帖成功");
		}
	}
}
