package webapp.posts.comments;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.company.cfg.Cfg;
import webapp.user.Cklogin;

/**
 * 评论添加模块
 * @author 方海亮
 *
 */

public class Add extends ControllerBase{
	
	/**
	 * 评论点赞
	 * @throws IOException
	 */
	public void zan() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String platcode = new Cfg().getcompanycfg(companyid).getString("platformcode");
			String phone = new Cklogin().getuserinfoByuid(uid).getString("phone");
			Map<String, String> form_data = new Form(context).createForm(request, "comment");
			String comment_id = form_data.get("cid");
			if(comment_id == null || comment_id.equals("")){
				return_json(1003,"评论id不存在");
				return;
			}
			String result = CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_zan.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platcode="+platcode+"&comment_id="+comment_id+"&phone="+phone);
			JSONObject jsonObject = new JSONObject(result);
			//社会化评论系统平台码
			if(jsonObject.getInt("status") != 200){
				return_json(500, "内部错误");
			}else{
				return_json(200,"点赞成功");
			}
		}
	}
	
	/**
	 * 评论回复
	 * @throws IOException
	 */
	public void reply() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String platcode = new Cfg().getcompanycfg(companyid).getString("platformcode");
			String phone = new Cklogin().getuserinfoByuid(uid).getString("phone");
			Map<String, String> form_data = new Form(context).createForm(request, "comment");
			String content = form_data.get("content");
			if(content == null || content.equals("")){
				return_json(1001,"评论内容必填");
				return;
			}
			if(content.length()>200){
				return_json(1002,"评论过长");
				return;
			}
			String article_id = form_data.get("aid");
			if(article_id == null || article_id.equals("")){
				return_json(1003,"文章id不存在");
				return;
			}
			String comment_id = form_data.get("cid");
			if(comment_id == null || comment_id.equals("")){
				return_json(1004,"评论id不存在");
				return;
			}
			String result = CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_addreply.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platcode="+platcode+"&comment_id="+comment_id+"&content="+URLEncoder.encode(content,"utf-8")+"&phone="+phone);
			JSONObject jsonObject = new JSONObject(result);
			//社会化评论系统平台码
			if(jsonObject.getInt("status") != 200){
				return_json(500, "内部错误");
			}else{
				String comment_num = Integer.toString(jsonObject.getInt("data"));
				_model.mysql_model.executeUpdate("UPDATE articles SET commenttimes = "+comment_num+" WHERE id = " + article_id);
				return_json(200,"回复成功");
			}
		}
	}
	
	/**
	 * 添加评论
	 * @throws IOException
	 */
	public void index() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "comment");
			String aid = form_data.get("aid");
			if(aid == null || aid.equals("")){
				return_json(1003,"文章id不存在");
				return;
			}
			String content = form_data.get("content");
			if(content == null || content.equals("")){
				return_json(1001,"评论内容必填");
				return;
			}
			if(content.length()>1000){
				return_json(1002,"评论过长");
				return;
			}
			String platcode = new Cfg().getcompanycfg(companyid).getString("platformcode");
			String phone = new Cklogin().getuserinfoByuid(uid).getString("phone");
			String result = CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_add.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platcode="+platcode+"&article_id="+aid+"&content="+URLEncoder.encode(content,"utf-8")+"&phone="+phone);
			JSONObject jsonObject = new JSONObject(result);
			//社会化评论系统平台码
			if(jsonObject.getInt("status") != 200){
				return_json(500, "内部错误");
				return;
			}else{
				String comment_num = Integer.toString(jsonObject.getInt("data"));
				_model.mysql_model.executeUpdate("UPDATE articles SET commenttimes = "+comment_num+" WHERE id = " + aid);
				_model.mysql_model.executeUpdate("REPLACE INTO `comment` (`articleid`, `uid`, `content`, `inserttime`) VALUES ("+aid+", "+uid+", '"+content+"', NOW())");
			}
			return_json(200,"评论成功");
		}
	}
}
