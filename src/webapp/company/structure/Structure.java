package webapp.company.structure;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import webapp.user.Cklogin;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

public class Structure extends ControllerBase {
	
	/**
	 * 删除部门
	 * 要求：
	 * 该部门没有下属部门
	 * 该部门没有员工
	 */
	public void deldepartment() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "department");
			String departmentid = form_data.get("departmentid");
			if(departmentid == null || departmentid.equals("")){
				return_json(1001,"部门ID为空");
				return;
			} 
			String result = _model.mysql_model.executeQuery("SELECT 1 FROM companys_structure WHERE companyid = '"+companyid+"' AND superid = "+departmentid+" AND delete_flg = 0 LIMIT 1");
			if(new JSONArray(result).length()>0){
				return_json(1002,"请先删除下属部门");
				return;
			}
			result = _model.mysql_model.executeQuery("SELECT 1 FROM companys_employees WHERE companyid = '"+companyid+"' AND structerid = "+departmentid+" AND stop_flg = 0 LIMIT 1");
			if(new JSONArray(result).length()>0){
				return_json(1003,"不能删除有员工的部门");
				return;
			}
			_model.mysql_model.executeUpdate("UPDATE companys_structure SET delete_flg = 1 WHERE companyid = '"+companyid+"' AND id = "+departmentid);
			return_json(200,"删除部门成功");
		}
	}
	
	/**
	 * 部门重命名
	 */
	public void renamestructure() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "company_structure");
			String groupname = form_data.get("name");
			if(groupname == null || groupname.equals("")){
				return_json(1001, "部门名称不能为空");
				return;
			}
			String departmentid = form_data.get("id");
			if(departmentid == null || departmentid.equals("")){
				return_json(1002, "部门ID为空");
				return;
			}
			_model.mysql_model.executeUpdate("UPDATE companys_structure SET groupname = '"+groupname+"' WHERE companyid = '"+companyid+"' AND id = "+departmentid);
			return_json(200,"修改部门名称成功");
		}
	}
	
	/**
	 * 获取树状公司结构模型
	 * @throws IOException
	 */
	public void getstructtree() throws IOException{ 
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			JSONArray struct_tree = new JSONArray();
			//获取顶级数据列表
			String result = _model.mysql_model.executeQuery("select id,groupname,membernum from companys_structure where superid = 0 and companyid = '"+companyid+"' AND delete_flg = 0 order by groupname asc");
			JSONArray array = new JSONArray(result);
			for(int i=0; i<array.length(); i++){
				JSONObject obj = array.getJSONObject(i);
				obj.put("level", 1);
				//获取二级数据列表
				result = _model.mysql_model.executeQuery("select id,groupname,membernum from companys_structure where superid = "+Integer.valueOf(obj.getInt("id"))+" and companyid = '"+companyid+"' AND delete_flg = 0 order by groupname asc");
				JSONArray array2 = new JSONArray(result);
				for(int j=0; j<array2.length(); j++){
					JSONObject obj2 = array2.getJSONObject(j);
					obj2.put("level", 2);
					//获取三级数据列表
					result = _model.mysql_model.executeQuery("select id,groupname,membernum from companys_structure where superid = "+Integer.valueOf(obj2.getInt("id"))+" and companyid = '"+companyid+"' AND delete_flg = 0 order by groupname asc");
					JSONArray array3 = new JSONArray(result);
					for(int k=0; k<array3.length(); k++){
						JSONObject obj3 = array3.getJSONObject(k);
						obj3.put("level", 3);
						obj3.put("list", new JSONArray());
						array3.put(k,obj3);
					}
					obj2.put("list", array3);
					array2.put(j,obj2);
				}
				obj.put("list", array2);
				array.put(i, obj);
			}
			struct_tree = array;
			return_json(200,"获取树状结构成功",struct_tree);
		}
	}
	
	/**
	 * 获取部门下的员工以及子部门
	 * @throws IOException
	 */
	public void getstructurewithemployee() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			//上级部门id
			Map<String, String> form_data = new Form(context).createForm(request, "department");
			String superid = form_data.get("superid");
			if(superid == null || superid.equals("")){
				superid = "0";
			}
			//部门列表
			String result = _model.mysql_model.executeQuery("SELECT id,groupname,membernum from companys_structure WHERE superid = "+superid+" AND companyid = '"+companyid+"' AND delete_flg = 0 order by convert(groupname using gbk) asc");
			JSONArray structure_list = new JSONArray(result);
			//员工列表
			result = _model.mysql_model.executeQuery("SELECT id,user_name,user_avatar,user_phone,user_mail,userid FROM companys_employees WHERE companyid = '"+companyid+"' AND structerid = "+superid+" AND stop_flg = 0 order by convert(user_name using gbk) asc");
			JSONArray employee_list = new JSONArray(result);
			JSONObject return_object = new JSONObject();
			return_object.put("structure", structure_list);
			return_object.put("employee", employee_list);
			return_json(200,"获取数据成功",return_object);
		}
	}
	
	/**
	 * 获取部门名称列表
	 * @throws IOException
	 */
	public void getstructure() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			String result = _model.mysql_model
			.executeQuery("select id,groupname,superid from `companys_structure` where companyid='"
					+  companyid + "' AND delete_flg = 0");
			JSONArray structure_array = new JSONArray(result);
			for(int i = 0; i < structure_array.length(); i++){
				JSONObject structure_obj = structure_array.getJSONObject(i);
				int superid = structure_obj.getInt("superid");
				if(superid > 0){
					String groupname = getstrutss(superid, structure_obj.getString("groupname"));
					structure_obj.put("groupname", groupname);
					structure_array.put(i, structure_obj);
				}
			}
			return_json(200,"获取数据成功",structure_array);
		}
	}
	
	/**
	 * 添加部门
	 * @throws IOException
	 */
	public void setstructure() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "company_structure");
			String groupname = form_data.get("name");
			if(groupname == null || groupname.equals("")){
				return_json(1001, "部门名称不能为空");
				return;
			}
			String superid = form_data.get("superid");
			if(superid == null || superid.equals("")){
				superid = "0";
			}
			_model.mysql_model.executeUpdate("INSERT INTO `companys_structure` (`companyid`, `groupname`, `superid`, `create_time`) VALUES ('" + companyid + "', '" + groupname + "', " + superid + ", NOW())");
			return_json(200,"添加部门成功");
		}
	}
	/**
	 * 获取下属全部角色
	 * 使用递归实现
	 * @return
	 */
	public String getstrutss(int superid, String basestr){
		String str = basestr;
		String sql = "SELECT superid,groupname FROM companys_structure WHERE `id` = "+Integer.toString(superid) + " limit 1";
		String result = _model.mysql_model.executeQuery(sql);
		JSONArray array = new JSONArray(result);
		if(array.length()==0){
			//尝试记入错误日志失败
			return str;
		}else{
			JSONObject structure_obj = array.getJSONObject(0);
			str = str + "/" + structure_obj.getString("groupname");
			if(structure_obj.getInt("superid") > 0){
				return getstrutss(structure_obj.getInt("superid"), str);
			}else{
				return str;
			}
		}
	}
	
	/**
	 * 减少部门员工数量
	 * @param companyid
	 * @param deapartmentid
	 */
	public void decreaseemployeenum(String companyid, int deapartmentid){
		_model.mysql_model.executeUpdate("update companys_structure set membernum = membernum -1 where id = "+Integer.toString(deapartmentid)+" and companyid = '"+companyid+"'");
		String result = _model.mysql_model.executeQuery("select superid from companys_structure where id = "+Integer.toString(deapartmentid)+" and companyid = '"+companyid+"'");
		int superid = new JSONArray(result).getJSONObject(0).getInt("superid");
		if(!(superid==0)){
			decreaseemployeenum(companyid, superid);
		}
	}
	
	/**
	 * 增加部门员工数量
	 * @param companyid
	 * @param deapartmentid
	 */
	public void increaseemployeenum(String companyid, int deapartmentid){
		_model.mysql_model.executeUpdate("update companys_structure set membernum = membernum +1 where id = "+Integer.toString(deapartmentid)+" and companyid = '"+companyid+"'");
		String result = _model.mysql_model.executeQuery("select superid from companys_structure where id = "+Integer.toString(deapartmentid)+" and companyid = '"+companyid+"'");
		int superid = new JSONArray(result).getJSONObject(0).getInt("superid");
		if(!(superid==0)){
			increaseemployeenum(companyid, superid);
		}
	}
	
	/**
	 * 获取用户的部门id
	 * @param companyid
	 * @param uid
	 * @return
	 */
	public int getemployeestructure(String companyid,int uid){
		String result = _model.mysql_model.executeQuery("select structerid from companys_employees where companyid = '"+companyid+"' and userid = "+Integer.toString(uid)+" order by id desc limit 1");
		if(new JSONArray(result).length()>0){
			return new JSONArray(result).getJSONObject(0).getInt("structerid");
		}else{
			return 0;
		}
	}
	
	
	/**
	 * 设置员工所属部门
	 * 对于已经加入进来的员工变更部门需要做如下处理：
	 * 对原来部门的员工数量减1，对新部门的员工数量增1
	 * @throws IOException
	 */
	public void setemployeestructure() throws IOException{
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "employee");
			String[] userlist = form_data.get("userlist").split(",");
			if(userlist.length==0){
				return_json(1001,"请选择用户");
				return;
			}
			String departmentid = form_data.get("departmentid");
			if(departmentid == null || departmentid.equals("")){
				return_json(1002,"请填写上传的部门");
				return;
			}
			for(int i=0; i<userlist.length; i++){
				String userid = userlist[i];
				String result = _model.mysql_model.executeQuery("select structerid,userid from companys_employees where id = "+userid+" and companyid = '"+companyid+"' limit 1");
				if(new JSONArray(result).length()==0){
					return_json(1003,"内部错误");
					return;
				}
				if(new JSONArray(result).getJSONObject(0).has("userid")){
					int old_structerid = new JSONArray(result).getJSONObject(0).getInt("structerid");
					//旧部门员工数量减1
					decreaseemployeenum(companyid, old_structerid);
					//新部门员工数增1
					increaseemployeenum(companyid, Integer.parseInt(departmentid));
				}
				//修改员工所属部门
				_model.mysql_model.executeUpdate("update companys_employees set structerid = "+ departmentid + " where id = "+userid+" and companyid = '"+companyid+"'");
			}
			return_json(200,"修改部门成功");
		}
	}
	
}
