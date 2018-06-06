package webapp.twitter.comment;

import java.io.IOException;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;

import webapp.club.Care;

/**
 * 评论列表
 * @author babytree
 *
 */
public class List extends ControllerBase {
	
	/**
	 * 获取单个推特的评论列表
	 * @param twtid 推特ID
	 * @param twtuid 发表推特人的用户ID
	 * @param uid 当前用户id
	 * @param companyid 当前用户所在的公司ID
	 * @return
	 * @throws IOException
	 * 每个推特展现的评论列表需要满足以下条件：
	 * 1.未删除
	 * 自己发布的推特，展现全部评论
	 * 非自己发布的推特，展现的评论需要符合以下条件：
	 * 非好友关系，只展现自己的评论
	 * 好友关系，展现与彼此都是好友关系的评论
	 */
	public JSONArray index(int twtid, int twtuid, int uid, String companyid) throws IOException {
		//获取评论列表
		JSONArray comment_ls = new JSONArray();
		//自己发表的推特，展现全部评论
		if(twtuid == uid) {
			comment_ls = new JSONArray(_model.mysql_model.executeQuery("SELECT\r\n" + 
					"	id,\r\n" + 
					"	uid,\r\n" + 
					"	getusernick (\r\n" + 
					"		"+uid+",\r\n" + 
					"		\""+companyid+"\",\r\n" + 
					"		uid\r\n" + 
					"	) AS nick,\r\n" + 
					"	getuseravatar (\r\n" + 
					"		\""+companyid+"\",\r\n" + 
					"		uid\r\n" + 
					"	) AS avatar,\r\n" + 
					"	content,\r\n" + 
					"	DATE_FORMAT(create_time,'%c月%e日 %k:%i') as create_time,\r\n" +
					"	uperid,\r\n" + 
					"	getusernick (\r\n" + 
					"		"+uid+",\r\n" + 
					"		\""+companyid+"\",\r\n" + 
					"		uperid\r\n" + 
					"	) AS reply_nick\r\n" + 
					"FROM\r\n" + 
					"	twt_comment\r\n" + 
					"WHERE\r\n" + 
					"	twt_id = "+Integer.toString(twtid)+
					"	AND is_del = 0 ORDER BY create_time DESC"));
		}else if(new Care().is_friend(Integer.toString(twtuid), Integer.toString(uid))) {
			String sql = "SELECT\r\n" + 
					"	id,\r\n" + 
					"	uid,\r\n" + 
					"	getusernick (\r\n" + 
					"		"+uid+",\r\n" + 
					"		\""+companyid+"\",\r\n" + 
					"		uid\r\n" + 
					"	) AS nick,\r\n" + 
					"	getuseravatar (\r\n" + 
					"		\""+companyid+"\",\r\n" + 
					"		uid\r\n" + 
					"	) AS avatar,\r\n" + 
					"	content,\r\n" + 
					"	DATE_FORMAT(create_time,'%c月%e日 %k:%i') as create_time,\r\n" +
					"	uperid,\r\n" + 
					"	getusernick (\r\n" + 
					"		"+uid+",\r\n" + 
					"		\""+companyid+"\",\r\n" + 
					"		uperid\r\n" + 
					"	) AS reply_nick\r\n" + 
					"FROM\r\n" + 
					"	twt_comment\r\n" + 
					"WHERE\r\n" + 
					"	twt_id = "+twtid+
					"	AND ("
					+ "	uid = "+uid+""
					+ " 	OR uperid = "+uid+""+
					"		OR ("+
					"			uid IN ( "+
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
					"			) AND "+
					"			(uperid IN ( SELECT id FROM twt_comment WHERE uid IN( "+
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
					"			)) or uperid = 0) "+
					"		)"+
					")"+
					"	AND is_del = 0 ORDER BY create_time DESC";
			//好友关系，展示和自己有关以及和自己的好友有关的评论
			comment_ls = new JSONArray(_model.mysql_model.executeQuery(sql));
		}else {
			//陌生人，只展示和自己相关的评论
			comment_ls = new JSONArray(_model.mysql_model.executeQuery("SELECT\r\n" + 
					"	id,\r\n" + 
					"	uid,\r\n" + 
					"	getusernick (\r\n" + 
					"		"+uid+",\r\n" + 
					"		\""+companyid+"\",\r\n" + 
					"		uid\r\n" + 
					"	) AS nick,\r\n" + 
					"	getuseravatar (\r\n" + 
					"		\""+companyid+"\",\r\n" + 
					"		uid\r\n" + 
					"	) AS avatar,\r\n" + 
					"	content,\r\n" + 
					"	DATE_FORMAT(create_time,'%c月%e日 %k:%i') as create_time,\r\n" +
					"	uperid,\r\n" + 
					"	getusernick (\r\n" + 
					"		"+uid+",\r\n" + 
					"		\""+companyid+"\",\r\n" + 
					"		uperid\r\n" + 
					"	) AS reply_nick\r\n" + 
					"FROM\r\n" + 
					"	twt_comment\r\n" + 
					"WHERE\r\n" + 
					"	twt_id = "+twtid+
					"	AND (uid = "+uid+" OR uperid = "+uid+")"+
					"	AND is_del = 0 ORDER BY create_time DESC"));
		}
		return comment_ls;
	}
}
