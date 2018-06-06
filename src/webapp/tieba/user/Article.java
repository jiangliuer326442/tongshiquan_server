package webapp.tieba.user;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;

import webapp.company.cfg.Cfg;
import webapp.user.Cklogin;

public class Article extends ControllerBase{
	
	/**
	 * 获取我发布的帖子列表
	 * @throws IOException
	 */
	public void getmylist() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			String page = CommonFunction.getParameter(request, "p", true);
			if(page == null || page.equals("")){
				page = "1";
			}
			String pagenum = CommonFunction.getParameter(request, "pnum", true);
			if(pagenum == null || pagenum.equals("")){
				pagenum = "20";
			}
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String platcode = new Cfg().getcompanycfg(companyid).getString("platformcode");
			//获取我发布的文章列表
			JSONArray article_list = new JSONArray(_model.mysql_model.executeQuery("SELECT "+
"	id, "+
"	title, "+
"	sectionid, "+
"	(SELECT `name` from post_models WHERE id = sectionid limit 1) as sectionname, "+
"	inserttime, "+
"	readtimes, "+
"	commenttimes "+
"FROM "+
"	articles "+
"WHERE "+
"	userid = "+uid+" "+
"AND is_del = 0 "+
"AND sectionid IN ( "+
"	SELECT "+
"		id "+
"	FROM "+
"		V_POSTBAR_MODELS "+
"	WHERE "+
"		companyid = '"+companyid+"' "+
"		ORDER BY inserttime desc "+
") LIMIT "+Integer.valueOf(Integer.parseInt(pagenum)*(Integer.parseInt(page)-1))+","+pagenum));
			for(int i=0; i<article_list.length(); i++){
				//获取每篇文章的最后一个评论
				int aid = article_list.getJSONObject(i).getInt("id");
				JSONArray comment_list = new JSONObject(CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_list_flat.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platcode="+platcode+"&article_id="+Integer.toString(aid)+"&phone=&page=1&pagenum=1")).getJSONArray("data");
				if(comment_list.length()>0){
					article_list.getJSONObject(i).put("comment", comment_list.getJSONObject(0));
				}
			}
			return_json(200,"成功",article_list);
		}
	}

}
