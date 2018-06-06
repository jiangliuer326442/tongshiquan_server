package webapp.posts.comments;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.company.cfg.Cfg;
import webapp.user.Cklogin;

public class Del extends ControllerBase{
	public void index() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String platcode = new Cfg().getcompanycfg(companyid).getString("platformcode");
			Map<String, String> form_data = new Form(context).createForm(request, "comment");
			String aid = form_data.get("aid");
			if(aid == null || aid.equals("")){
				return_json(1003,"文章id不存在");
				return;
			}
			String comment_id = form_data.get("cid");
			if(comment_id == null || comment_id.equals("")){
				return_json(1004,"评论id不存在");
				return;
			}
			String result = CommonFunction.getUrlData(webapp.posts.comments.Config.comment_domin+"/comment_del.jsp?domaincode="+webapp.posts.comments.Config.domain_token+"&platcode="+platcode+"&comment_id="+comment_id);
			JSONObject jsonObject = new JSONObject(result);
			//社会化评论系统平台码
			if(jsonObject.getInt("status") != 200){
				return_json(500, "内部错误");
				return;
			}else{
				String comment_num = Integer.toString(jsonObject.getInt("data"));
				_model.mysql_model.executeUpdate("UPDATE articles SET commenttimes = "+comment_num+" WHERE id = " + aid);
			}
			return_json(200,"删除成功");
		}
	}
}
