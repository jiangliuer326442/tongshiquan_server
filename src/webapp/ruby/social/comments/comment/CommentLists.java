package webapp.ruby.social.comments.comment;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.function.CommonFunction;

import webapp.ruby.social.comments.CommentBase;

public class CommentLists extends CommentBase {
	
	//获取文章热评
	public void getByaidflathot() throws IOException{
		if(!init_chk()) return;
		String article_id = CommonFunction.getParameter(request, "article_id",true);
		if(article_id==null || article_id.equals("")){
			try {
				return_json(1004,"article_id:请传递文章id");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		if(article_id.length()>50){
			try {
				return_json(1005,"article_id:文章id过长");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		String phone = CommonFunction.getParameter(request, "phone",true);
		if(phone==null){
			phone = "";
		}
		String pagenum = CommonFunction.getParameter(request, "pagenum",true);
		if(pagenum==null || Integer.parseInt(pagenum)<1){
			pagenum = "10";
		}
		String sql = "SELECT "+
	"c.comment_id, "+
	"c.comment_zannum, "+
	"c.comments, "+
	"c.comment_inserttime, "+
	"c.user_phone, "+
	"c.user_name, "+
	"c.to_content, "+
	"c.to_username, "+
	"IF ( "+
	"	c.comment_id IN ( "+
	"		SELECT "+
	"			z.comment_id "+
	"		FROM "+
	"			`comments`.comment_zan z "+
	"		LEFT JOIN `" + this.database + "`.V_PLATFORM_USERS u ON z.userid = u.userid "+
	"		WHERE "+
	"			u.phone = '"+phone+"' "+
	"	), "+
	"	1, "+
	"	0 "+
	") AS iszan, "+
	"(SELECT count(1) FROM `" + this.database + "`.comments WHERE lastcomment_id = c.comment_id) AS reply_num "+
"FROM "+
	"`" + this.database + "`.V_ARTICLE_COMMENTS c "+
"WHERE "+
	"c.plat_id = "+plat_id+" "+
"AND c.article_id = "+article_id+" "+
"ORDER BY "+
"	GREATEST( "+
"		c.comment_zannum, "+
"		reply_num "+
"	) DESC, "+
"	c.comment_inserttime DESC "+
"LIMIT "+pagenum;
		String result = _model.mysql_model.executeQuery(sql);
		JSONArray json = new JSONArray(result);
		return_json(200, "获取热评成功", json);
	}
	
	//获取文章评论列表（水平）
	public void getByaidflat() throws IOException{
		if(!init_chk()) return;
		String article_id = CommonFunction.getParameter(request, "article_id",true);
		if(article_id==null || article_id.equals("")){
			try {
				return_json(1004,"article_id:请传递文章id");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		if(article_id.length()>50){
			try {
				return_json(1005,"article_id:文章id过长");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		String phone = CommonFunction.getParameter(request, "phone",true);
		if(phone==null){
			phone = "";
		}
		//获取总数量
		String sql = "SELECT count(1) AS comment_num "+
				"FROM "+
				"	`" + this.database + "`.V_ARTICLE_COMMENTS c "+
				"WHERE "+
				"	c.plat_id = "+plat_id+" "+
				"AND c.article_id = " + article_id;
		String result = _model.mysql_model.executeQuery(sql);
		JSONArray json = new JSONArray(result);
		int comment_num = json.getJSONObject(0).getInt("comment_num");
		String page = CommonFunction.getParameter(request, "page",true);
		if(page==null || Integer.parseInt(page)<1){
			page = "1";
		}
		String pagenum = CommonFunction.getParameter(request, "pagenum",true);
		if(pagenum==null || Integer.parseInt(pagenum)<1){
			pagenum = "10";
		}
		sql = "SELECT "+
				"	c.comment_id, "+
				"	c.comment_zannum, "+
				"	c.comments, "+
				"	c.comment_inserttime, "+
				"	c.user_phone, "+
				"	c.user_name, "+
				"	c.to_content, "+
				"	c.to_username, "+
				" "+
				"IF ( "+
				"	c.comment_id IN ( "+
				"		SELECT "+
				"			z.comment_id "+
				"		FROM "+
				"			`" + this.database + "`.comment_zan z "+
				"		LEFT JOIN `" + this.database + "`.V_PLATFORM_USERS u ON z.userid = u.userid "+
				"		WHERE "+
				"			u.phone = '"+phone+"' "+
				"	), "+
				"	1, "+
				"	0 "+
				") AS iszan "+
				"FROM "+
				"	`" + this.database + "`.V_ARTICLE_COMMENTS c "+
				"WHERE "+
				"	c.plat_id = "+plat_id+" "+
				"AND c.article_id = " + article_id + " "+
				"ORDER BY "+
				"GREATEST( "+
				"	c.comment_inserttime, "+
				"	IFNULL(c.comment_updatetime,'') "+
				") DESC limit " + 
				String.valueOf((Integer.parseInt(page)-1)*Integer.parseInt(pagenum)) + "," + pagenum;
		result = _model.mysql_model.executeQuery(sql);
		json = new JSONArray(result);
		return_json(200, Integer.toString(comment_num), json);
	}
	
	//根据文章id获取评论列表
	public void getByarticleid(){
		if(!init_chk()) return;
		String article_id = CommonFunction.getParameter(request, "article_id",true);
		if(article_id==null || article_id.equals("")){
			try {
				return_json(1004,"article_id:请传递文章id");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		if(article_id.length()>50){
			try {
				return_json(1005,"article_id:文章id过长");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		String phone = CommonFunction.getParameter(request, "phone",true);
		if(phone==null){
			phone = "";
		}
		String page = CommonFunction.getParameter(request, "page",true);
		if(page==null || Integer.parseInt(page)<1){
			page = "1";
		}
		String pagenum = CommonFunction.getParameter(request, "pagenum",true);
		if(pagenum==null || Integer.parseInt(pagenum)<1){
			pagenum = "10";
		}
		String sql = "SELECT "+
				"	c.comment_id, "+
				"	c.comment_zannum, "+
				"	c.comments, "+
				"	c.comment_inserttime, "+
				"	c.user_phone, "+
				"	c.user_name, "+
				" "+
				"IF ( "+
				"	c.comment_id IN ( "+
				"		SELECT "+
				"			z.comment_id "+
				"		FROM "+
				"			`" + this.database + "`.comment_zan z "+
				"		LEFT JOIN `" + this.database + "`.V_PLATFORM_USERS u ON z.userid = u.userid "+
				"		WHERE "+
				"			u.phone = '"+phone+"' "+
				"	), "+
				"	1, "+
				"	0 "+
				") AS iszan "+
				"FROM "+
				"	`" + this.database + "`.V_ARTICLE_COMMENTS c "+
				"WHERE "+
				"	c.plat_id = "+plat_id+" "+
				"AND c.article_id = " + article_id + " "+
                "AND c.lastcomment_id = 0 "+
				"ORDER BY "+
				"GREATEST( "+
				"	c.comment_inserttime, "+
				"	IFNULL(c.comment_updatetime,'') "+
				") DESC limit " + 
				String.valueOf((Integer.parseInt(page)-1)*Integer.parseInt(pagenum)) + "," + pagenum;
		String result = _model.mysql_model.executeQuery(sql);
		JSONArray json = new JSONArray(result);
		try {
			//获取上级评论
			for(int i = 0; i< json.length(); i++){
				JSONObject json_object = json.getJSONObject(i);
				String comment_id = Integer.toString(json_object.getInt("comment_id"));
				sql = "SELECT comment_id,user_name,comments,comment_inserttime,user_phone FROM `" + this.database + "`.V_ARTICLE_COMMENTS WHERE comment_id = "+
						 "(SELECT lastcomment_id FROM `" + this.database + "`.comments WHERE id = (SELECT lastcomment_id FROM `" + this.database + "`.comments WHERE id = (SELECT lastcomment_id FROM `" + this.database + "`.comments WHERE id = "+comment_id+"))) "+
						 "union "+
						 "SELECT comment_id,user_name,comments,comment_inserttime,user_phone FROM `" + this.database + "`.V_ARTICLE_COMMENTS WHERE comment_id = "+
						 "(SELECT lastcomment_id FROM `" + this.database + "`.comments WHERE id = (SELECT lastcomment_id FROM `" + this.database + "`.comments WHERE id = "+comment_id+")) "+
						 "union "+
						 "SELECT comment_id,user_name,comments,comment_inserttime,user_phone FROM `" + this.database + "`.V_ARTICLE_COMMENTS WHERE comment_id = "+
						 "(SELECT lastcomment_id FROM `" + this.database + "`.comments WHERE id = "+comment_id+")";
				result = _model.mysql_model.executeQuery(sql);
				JSONArray json2 = new JSONArray(result);
				json_object.put("history_records", json2);
				json.put(i, json_object);
			}
			return_json(200, "获取列表成功", json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
