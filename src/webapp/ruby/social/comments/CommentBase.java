package webapp.ruby.social.comments;

import java.io.IOException;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;

import webapp.ruby.social.comments.register.PlatRegister;

/**
 * 社会化评论组件基类
 * <p>Title:CommentBase</p>
 * <p>Description:</p>
 * <p>Company:上海夜伦网络信息技术有限公司</p>
 * @author 方海亮
 * @date 2016年8月5日 下午1:32:34
 */
public abstract class CommentBase extends ControllerBase {
	protected String database;
	protected String plat_id = null;
	
	public CommentBase(){
		this.init();
	}
	
	/**
	 * 验证网站code和平台code
	 * @return boolean
	 * @date 2016年8月15日 下午3:55:16
	 */
	public boolean init_chk() {
		String domaincode = CommonFunction.getParameter(request, "domaincode",true);
		if(domaincode==null || domaincode.equals("")){
			try {
				return_json(1001,"domaincode:请传递域名注册时返回的域名token");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		String platcode = CommonFunction.getParameter(request, "platcode",true);
		if(platcode==null || platcode.equals("")){
			try {
				return_json(1002,"platcode:请传递平台注册时返回的平台token");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		plat_id = new PlatRegister().getplatform(domaincode, platcode);
		if(plat_id==null || plat_id.equals("")){
			try {
				return_json(1003,"platcode或domaincode错误");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		return true;
	};
	
	public void init() {
		super.init();
		database = "comments";
	}
}
