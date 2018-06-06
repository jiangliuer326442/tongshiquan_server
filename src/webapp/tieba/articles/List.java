package webapp.tieba.articles;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

/**
 * 获取文章列表
 * @author Administrator
 *
 */

public class List extends ControllerBase{
	
	public void getbysection() throws IOException{
		Map<String, String> form_data = new Form(context).createForm(request, "article");
		String companyid = form_data.get("companyid");
		String sectionid = form_data.get("sectionid");
		String result = _model.mysql_model.executeQuery("SELECT total_posts FROM V_POSTBAR_MODELS WHERE id = "+sectionid+" AND companyid = '"+companyid+"' LIMIT 1");
		int article_num = new JSONArray(result).getJSONObject(0).getInt("total_posts");
		String page = CommonFunction.getParameter(request, "p", true);
		if(page == null || page.equals("")){
			page = "1";
		}
		String pagenum = CommonFunction.getParameter(request, "pnum", true);
		if(pagenum == null || pagenum.equals("")){
			pagenum = "20";
		}
		result = _model.mysql_model.executeQuery("SELECT id, title, content_text, userid, user_avatar, user_nick, readtimes, commenttimes, comment_nick, showtime, comment_time FROM V_ARTICLES WHERE sectionid = "+sectionid+" AND companyid = '"+companyid+"' ORDER BY inserttime DESC LIMIT "+Integer.valueOf(Integer.parseInt(pagenum)*(Integer.parseInt(page)-1))+","+pagenum);
		JSONArray article_list = new JSONArray(result);
		return_json(200,Integer.toString(article_num),article_list);
	}
	
	/**
	 * 获取一周内的帖子列表
	 * @throws IOException
	 */
	public void getallsectionlist() throws IOException{
		Map<String, String> form_data = new Form(context).createForm(request, "article");
		String companyid = form_data.get("companyid");
		String page = CommonFunction.getParameter(request, "p", true);
		if(page == null || page.equals("")){
			page = "1";
		}
		String pagenum = CommonFunction.getParameter(request, "pnum", true);
		if(pagenum == null || pagenum.equals("")){
			pagenum = "20";
		}
		String result = _model.mysql_model.executeQuery("SELECT a.id as article_id, a.title as article_title, a.content_text, a.sectionid,	ce.userid,	ce.user_avatar,	ce.user_name, pm.`name` as section_name, a.readtimes, a.commenttimes, a.showtime FROM articles a LEFT JOIN post_models pm ON a.sectionid = pm.id LEFT JOIN company_models cm ON pm.bigkind_id = cm.id LEFT JOIN companys_employees ce ON a.userid = ce.userid WHERE cm.model_id = 3 AND cm.companyid = '"+companyid+"' AND ce.companyid = '"+companyid+"' AND ce.stop_flg = 0 AND a.is_del = 0 AND a.showtime < NOW() ORDER BY a.inserttime desc LIMIT "+Integer.valueOf(Integer.parseInt(pagenum)*(Integer.parseInt(page)-1))+","+pagenum);
		JSONArray article_list = new JSONArray(result);
		return_json(200,"success",article_list);
	}
}
