package webapp.company.manager;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;
import webapp.posts.models.Create;

/**
 * 超级管理员管理模块
 * @author 方海亮
 *
 */
public class smanager extends ControllerBase {
	public int getsmanager(String companyid){
		String result = _model.mysql_model.executeQuery("select userid from companys_manager_record where companyid = '"+companyid+"' order by create_time desc limit 1");
		JSONArray array = new JSONArray(result);
		int uid = array.getJSONObject(0).getInt("userid");
		return uid;
	}
	
	//变更超级管理员
	public void set() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "companymanager");
			//新管理员id
			String manager_id = form_data.get("manager_id");
			//获取旧超级管理员id
			String ouid = Integer.toString(getsmanager(companyid));
			if(ouid.equals(manager_id)){
				return_json(1001,"新管理员与旧管理员相同");
			}else{
				_model.mysql_model.executeUpdate("INSERT INTO `companys_manager_record` (`companyid`, `userid`) VALUES ('"+companyid+"', "+manager_id+")");
				//变更模块管理员
				new Create().chgleader(companyid,ouid,manager_id);
				return_json(200,"变更成功");
			}
		}
	}
	
	//添加超级管理员
	public void add(String companyid,String uid){
		//创建企业模块
		new webapp.posts.models.Create().init(companyid, uid);
		//记录企业超级管理员变更日志
		_model.mysql_model.executeUpdate("INSERT INTO `companys_manager_record` (`companyid`, `userid`) VALUES ('"+companyid+"', "+uid+")");
	}
}
