package webapp.twitter;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;

import webapp.club.Care;
import webapp.user.Cklogin;
import webapp.user.Register;

/**
 * 推特列表
 * @author 方海亮
 *
 */
public class List extends ControllerBase {
	
	/**
	 * 面向企业全员的推特列表
	 * 需要满足以下条件：
	 * 1.未删除
	 * 2.对全员或者同事可见
	 * 3.未离职
	 * @throws IOException
	 */
	public void getbymycompany() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String page = CommonFunction.getParameter(request, "p", true);
			if(page == null || page.equals("")){
				page = "1";
			}
			String pagenum = CommonFunction.getParameter(request, "pnum", true);
			if(pagenum == null || pagenum.equals("")){
				pagenum = "20";
			}
			String sql = "SELECT\r\n" + 
					"	m.id, e.user_name, "
					+ "e.user_phone, "
					+ "e.user_mail, "
					+ "m.content, "
					+ "DATE_FORMAT(m.create_time,'%c月%e日 %k:%i') as create_time\r\n" + 
					"FROM\r\n" + 
					"	twt_message m\r\n" + 
					"LEFT JOIN companys_employees e ON m.uid = e.userid AND m.companyid = e.companyid\r\n" + 
					"WHERE\r\n" + 
					"	m.companyid = '"+companyid+"'\r\n" + 
					"AND m.is_del = 0\r\n" + 
					"AND m.visiblity IN (1, 3)\r\n" + 
					"AND m.uid in (\r\n" + 
					"	SELECT userid FROM companys_employees WHERE companyid = '"+companyid+"' AND stop_flg = 0\r\n" + 
					") ORDER BY m.create_time DESC LIMIT "+Integer.valueOf(Integer.parseInt(pagenum)*(Integer.parseInt(page)-1))+","+pagenum;
			JSONArray twt_list = new JSONArray(_model.mysql_model.executeQuery(sql));
			return_json(200, "success", twt_list);
		}
	}
	
	/**
	 * 用户自己视图的推特列表
	 * 展现的推特需要符合以下条件：
	 * 1.不在屏蔽的范围内（特定推特或用户）
	 * 2.未删除
	 * 以下条件至少满足一项：
	 * 1.同一个公司的在职员工 对所有人或对同事可见的推特
	 * 2.自己发布的非私密推特
	 * 3.好友发布的 对所有人，对好友或匿名推特
	 */
	public void getbymyview() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if (new Cklogin().cklogin(request, response)) {
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String page = CommonFunction.getParameter(request, "p", true);
			if(page == null || page.equals("")){
				page = "1";
			}
			String pagenum = CommonFunction.getParameter(request, "pnum", true);
			if(pagenum == null || pagenum.equals("")){
				pagenum = "20";
			}
			String sql = "SELECT "+
					"	m.id, "+
					"	m.uid, "+
					"	getuseravatar('"+companyid+"', m.uid) as avatar, "+
					"	getusernick("+uid+",'"+companyid+"', m.uid) as nick, "+
					"	m.content, "+
					"	m.image, "+
					"	m.link_id, "+
					"	m.visiblity, "+
					"	IFNULL((SELECT 1 from twt_zan WHERE msgid = m.id AND uid = "+uid+"), 0) as zan_flg, "+
					"	m.zan_num, "+
					"	DATE_FORMAT(m.create_time,'%c月%e日 %k:%i') as create_time "+
					"FROM "+
					"	twt_message m "+
					"WHERE "+
					"	m.id NOT IN (SELECT twt_id from twt_endidlog where uid = "+uid+") "+
					"AND m.uid NOT IN (SELECT tuid FROM twt_enduserlog where fuid = "+uid+") "+
					"AND is_del = 0 "+
					"AND ( "+
					"		( "+
					"			companyid = '"+companyid+"' "+
					"			AND visiblity IN (1, 3) "+
					"			AND uid in (SELECT userid FROM companys_employees WHERE companyid = '"+companyid+"' AND stop_flg = 0)"+
					"		) "+
					"		OR ( "+
					"			m.uid = "+uid+" "+
					"			AND visiblity != 4 "+
					"			) "+
					"		OR ( "+
					"			m.uid IN ( "+
					"				SELECT "+
					"					user1 "+
					"				FROM "+
					"					( "+
					"						SELECT "+
					"							user1 "+
					"						FROM "+
					"							club_friends "+
					"						WHERE "+
					"							user2 = "+uid+" "+
					"						AND `status` = 2 "+
					"						UNION "+
					"							SELECT "+
					"								user2 "+
					"							FROM "+
					"								club_friends "+
					"							WHERE "+
					"								user1 = "+uid+" "+
					"							AND `status` = 2 "+
					"					) AS a "+
					"				WHERE "+
					"					user1 != "+uid+" "+
					"			) "+
					"			AND m.visiblity IN (1, 2, 5) "+
					"		) "+
					"	) "+
					"ORDER BY "+
					"	m.create_time DESC "+
					"LIMIT "+Integer.valueOf(Integer.parseInt(pagenum)*(Integer.parseInt(page)-1))+","+pagenum;
			JSONArray twt_list = new JSONArray(_model.mysql_model.executeQuery(sql));
			for(int i=0; i<twt_list.length(); i++){
				JSONObject twt_item = twt_list.getJSONObject(i);
				//处理评论
				JSONArray comment_ls = new webapp.twitter.comment.List().index(twt_item.getInt("id"), twt_item.getInt("uid"), Integer.parseInt(uid), companyid);
				twt_item.put("comment", comment_ls);
				//处理匿名动态
				int visibility = twt_item.getInt("visiblity");
				if(visibility == 5){
					twt_item.put("uid", 0);
					twt_item.put("avatar", new Register().getdefaultavatar());
					twt_item.put("nick", "匿名动态");
				}
				//处理外链
				if(twt_item.has("link_id")){
					String link_id = Integer.toString(twt_item.getInt("link_id"));
					JSONObject link_obj = new JSONArray(_model.mysql_model.executeQuery("select url,title,content from twt_links where id = "+link_id)).getJSONObject(0);
					twt_item.put("link_title", link_obj.getString("title"));
					twt_item.put("link_content", link_obj.getString("content"));
					twt_item.put("link_url", link_obj.getString("url"));
				}
				twt_list.put(i, twt_item);
			}
			return_json(200, "success", twt_list);
		}	
	}
	
}
