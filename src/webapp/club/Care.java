package webapp.club;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

/**
 * 用户关注管理
 * @author 方海亮
 *
 */
public class Care extends ControllerBase{
	
	/**
	 * 获取好友列表
	 */
	public void friendslist() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String sql = "SELECT "+
					"	friend_uid, "+
					"	getuseravatar('"+companyid+"', friend_uid) as avatar, "+
					"	getusernick("+uid+",'"+companyid+"', friend_uid) as username, "+
					"	upper(left(to_pinyin(left(upper(getusernick("+uid+",'"+companyid+"', friend_uid)),1)),1)) as fletter "+
					"FROM "+
					"	( "+
					"		SELECT "+
					"		IF (user1 = "+uid+", user2, user1) AS friend_uid "+
					"		FROM "+
					"			club_friends "+
					"		WHERE "+
					"			(user1 = "+uid+" OR user2 = "+uid+") "+
					"		AND `status` = 2 "+
					"	) AS f";
			String result = _model.mysql_model.executeQuery(sql);
			JSONArray friend_list = new JSONArray(result);
			return_json(200,"获取好友列表成功",friend_list);
		}
	}
	
	/**
	 * 判断两个用户是否为好友
	 * @param user1
	 * @param user2
	 * @return
	 */
	public boolean is_friend(String user1,String user2){
		String sql = "SELECT 1 FROM club_friends WHERE ((user1 = "+user1+" AND user2 = "+user2+") or (user1 = "+user2+" AND user2 = "+user1+")) AND `status` = 2";
		String result = _model.mysql_model.executeQuery(sql);
		if(new JSONArray(result).length()>0) return true; else return false;
	}
	
	/**
	 * 获取是否关注对方
	 * @param user1
	 * @param user2
	 * @return
	 */
	public boolean is_care(String user1,String user2){
		String result = _model.mysql_model.executeQuery("SELECT 1 FROM club_friends WHERE (user1 = "+user1+" AND user2 = "+user2+") or (user2 = "+user2+" AND user1 = "+user1+" AND `status` = 2)");
		if(new JSONArray(result).length()>0) return true; else return false;
	}
	
	/**
	 * 删除好友
	 * 删除彼此聊天记录，聊天内容
	 * @throws IOException
	 */
	public void delfriend() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "club");
			String touid = form_data.get("touid");
			if(Integer.parseInt(touid)<=0){
				return_json(1001,"用户不存在");
			}
			//删除好友
			_model.mysql_model.executeUpdate("DELETE FROM club_friends WHERE ((user1 = "+uid+" AND user2 = "+touid+") or (user1 = "+touid+" AND user2 = "+uid+")) AND `status` = 2");
			//删除聊天内容
			_model.mysql_model.executeUpdate("DELETE FROM chat_log WHERE ((fuid = "+uid+" AND tuid = "+touid+") or (fuid = "+touid+" AND tuid = "+uid+"))");
			//删除最近联系人
			_model.mysql_model.executeUpdate("DELETE FROM chat_user WHERE ((myuid = "+uid+" AND touid = "+touid+") or (myuid = "+touid+" AND touid = "+uid+"))");
			return_json(200,"删除好友成功");
		}
	}
	
	/**
	 * 关注另一个用户
	 */
	public void makecare() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "club");
			String touid = form_data.get("touid");
			if(Integer.parseInt(touid)<=0){
				return_json(1001,"用户不存在");
			}
			//获取两人关系数据
			String result = _model.mysql_model.executeQuery("SELECT id,user1,user2,`status` FROM club_friends WHERE ((user1 = "+uid+" AND user2 = "+touid+") OR (user1 = "+touid+" AND user2 = "+uid+")) LIMIT 1");
			JSONArray finfo = new JSONArray(result);
			if(finfo.length()>0){
				//获取两个人关系
				String status = finfo.getJSONObject(0).getString("status");
				if(status.equals("关注")){
					//如果是你关注了对方，再次点击取消关注
					if(finfo.getJSONObject(0).getInt("user1") == Integer.parseInt(uid)){
						_model.mysql_model.executeUpdate("DELETE FROM club_friends WHERE user1 = "+uid+" AND user2 = "+touid);
						return_json(200,"取消关注成功");
					}else{
						//对方关注了你，此刻可称为朋友
						_model.mysql_model.executeUpdate("UPDATE club_friends SET `status` = 2 WHERE user1 = "+touid+" AND user2 = "+uid);
						return_json(200,"已成为好友");
					}
				}else{
					//删除好友
					_model.mysql_model.executeUpdate("DELETE FROM club_friends WHERE ((user1 = "+uid+" AND user2 = "+touid+") or (user1 = "+touid+" AND user2 = "+uid+")) AND `status` = 2");
					//两个人是陌生人，允许关注对方
					_model.mysql_model.executeUpdate("INSERT INTO club_friends(user1,user2) VALUES("+touid+","+uid+")");
					return_json(200,"取消关注成功");
				}
				
			}else{
				//两个人是陌生人，允许关注对方
				_model.mysql_model.executeUpdate("INSERT INTO club_friends(user1,user2) VALUES("+uid+","+touid+")");
				return_json(200,"关注成功");
			}
		}
	}
}
