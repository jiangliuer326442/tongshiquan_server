package webapp.innermsg.user;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

public class Lists extends ControllerBase {
	
	private int page;
	private int pagenum;
	private int from;
	
	private void pageoperation(){
		page = Integer.parseInt(CommonFunction.getParameter(request, "p", true));
		if(page==0) page=1;
		pagenum = Integer.parseInt(CommonFunction.getParameter(request, "pnum", true));
		if(pagenum==0) pagenum=20;
		from = pagenum*(page-1);
	}
	
	/**
	 * 设置阅读状态
	 * @throws IOException
	 */
	public void setreadflg() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map<String, String> form_data = new Form(context).createForm(request, "innermsg");
			String id = form_data.get("id");
			if(id == null || id.equals("") || Integer.parseInt(id)==0){
				return_json(1001, "缺少参数");
				return;
			}
			String sql = "update club_message set read_flg = 1 where id = "+id;
			_model.mysql_model.executeUpdate(sql);
			return_json(200, "设置成功");
		}
	}
	
	/**
	 * 未读私信数量
	 * @throws IOException
	 */
	public void unreadnum() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			String sql = "select count(1) as num from club_message where to_uid = "+uid+" and read_flg = 0";
			String result = _model.mysql_model.executeQuery(sql);
			return_json(200,"获取未读消息数量成功", new JSONArray(result).getJSONObject(0).getInt("num"));
		}
	}
	
	/**
	 * 我接受的私信
	 * @throws IOException 
	 */
	public void myreceived() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			pageoperation();
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String sql = "SELECT "+
					"	cm.id, "+
					"	cm.from_uid, "+
					"	ifnull(cum.markname, ce.user_name) as username, "+
					"	ce.user_avatar, "+
					"	cm.content, "+
					"	cm.read_flg, "+
					"	cm.create_time "+
					"FROM "+
					"	club_message cm "+
					"INNER JOIN companys_employees ce ON cm.from_uid = ce.userid and ce.companyid = '"+companyid+"' "+
					"LEFT JOIN club_user_marker cum on cm.from_uid = cum.markeed_user and cum.operator_id = "+uid+" "+
					"WHERE "+
					"	to_uid = "+uid+" "+
					"ORDER BY "+
					"	create_time DESC "+
					"LIMIT "+Integer.toString(from)+", "+
					" "+Integer.toString(pagenum);
			String result = _model.mysql_model.executeQuery(sql);
			return_json(200,"获取列表成功",new JSONArray(result));
		}
	}
	
	/**
	 * 我发送的私信
	 * @throws IOException
	 */
	public void mysended() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			pageoperation();
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String sql = "SELECT "+
					"	cm.id, "+
					"	cm.to_uid, "+
					"	ifnull(cum.markname, ce.user_name) as username, "+
					"	ce.user_avatar, "+
					"	cm.content, "+
					"	cm.read_flg, "+
					"	cm.create_time "+
					"FROM "+
					"	club_message cm "+
					"INNER JOIN companys_employees ce ON cm.to_uid = ce.userid and ce.companyid = '"+companyid+"' "+
					"LEFT JOIN club_user_marker cum on cm.to_uid = cum.markeed_user and cum.operator_id = "+uid+" "+
					"WHERE "+
					"	from_uid = "+uid+" "+
					"ORDER BY "+
					"	create_time DESC "+
					"LIMIT "+Integer.toString(from)+", "+
					" "+Integer.toString(pagenum);
			String result = _model.mysql_model.executeQuery(sql);
			return_json(200,"获取列表成功",new JSONArray(result));
		}
	}
	
}
