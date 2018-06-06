package webapp.posts.articles;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import webapp.user.Cklogin;

/**
 * 获取文章列表
 * @author Administrator
 *
 */

public class List extends ControllerBase{
	
	/**
	 * 根据文章id获取该栏目文章列表
	 * @throws IOException 
	 */
	public void getsectionlistbyarticleid() throws IOException{
		String aid = CommonFunction.getParameter(request, "aid", true);
		if(Integer.parseInt(aid)==0){
			return_json(1001,"文章id为空");
			return;
		}
		int sectionid = new Util().getarticlecolumn(Integer.parseInt(aid));
		if(sectionid==0){
			return_json(1002,"文章id不存在");
			return;
		}
		int page = Integer.parseInt(CommonFunction.getParameter(request, "p", true));
		if(page==0) page=1;
		int pagenum = Integer.parseInt(CommonFunction.getParameter(request, "pnum", true));
		if(pagenum==0) pagenum=20;
		int from = pagenum*(page-1);
		JSONArray articlelist = getbysectionid(sectionid,from, pagenum);
		JSONObject result = new JSONObject();
		result.put("list", articlelist);
		result.put("num", getnumbysectionid(sectionid));
		return_json(200,"获取文章列表成功",result);
	}
	
	/**
	 * 根据栏目id获取文章
	 * @throws IOException
	 */
	public void getbysectionid() throws IOException{
		int from = 0;
		int size = 20;
		String uid = CommonFunction.getParameter(request, "uid", true);
		if(new Cklogin().cklogin(request,response)){
			Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
			String companyid = userinfo.get("companyid").toString();
			Map<String, String> form_data = new Form(context).createForm(request, "article");
			String sectionid = form_data.get("sectionid");
			String result;
			//超级管理员
			if(_model.redis_model.get("admin_"+ uid +"_isadmin").equals("1")){
				result = _model.mysql_model.executeQuery("SELECT id,title,readtimes,commenttimes,is_top,showtime FROM articles WHERE sectionid = " + sectionid + " AND is_del = 0 AND sectionid in ( SELECT id FROM post_models WHERE companyid = '" + companyid + "' ) ORDER BY showtime DESC limit "+Integer.toString(from)+","+Integer.toString(size));
			}else{
				result = _model.mysql_model.executeQuery("SELECT id,title,readtimes,commenttimes,is_top,showtime FROM articles WHERE sectionid = " + sectionid + " AND is_del = 0 AND sectionid in ( SELECT id FROM post_models WHERE companyid = '" + companyid + "' AND leaderid = " + uid + " ) ORDER BY showtime DESC");
			}
			JSONArray array = new JSONArray(result);
			return_json(200,"获取文章列表成功",array);
		}
	}
	
	private JSONArray getbysectionid(int sectionid,int from, int size){
		String result = _model.mysql_model.executeQuery("SELECT id,title,`desc`,thumb,is_top FROM articles WHERE sectionid = "+Integer.toString(sectionid)+" AND is_del = 0 AND showtime < NOW() ORDER BY is_top desc,id desc LIMIT "+Integer.toString(from)+","+Integer.toString(size));
		JSONArray array = new JSONArray(result);
		if(array.length()>0){
			return array;
		}else{
			return null;
		}
	}
	
	private int getnumbysectionid(int sectionid){
		String result = _model.mysql_model.executeQuery("SELECT COUNT(1) as article_num FROM articles WHERE sectionid = "+Integer.toString(sectionid)+" AND is_del = 0 AND showtime < NOW()");
		return  new JSONArray(result).getJSONObject(0).getInt("article_num");
	}
	
	/**
	 * 获取其他栏目列表
	 * @throws IOException
	 */
	public void getothersectionlist() throws IOException{
		Map<String, String> form_data = new Form(context).createForm(request, "article");
		String companyid = form_data.get("companyid");
		String result = _model.mysql_model.executeQuery("SELECT pm.id, pm.`name` FROM post_models pm LEFT JOIN company_models cm ON pm.companyid = cm.companyid AND pm.bigkind_id = cm.id WHERE pm.companyid = '"+companyid+"' AND pm.is_del = 0 AND cm.model_id = 2 AND pm.sort > 1 ORDER BY pm.sort ASC, pm.addtime ASC");
		JSONArray model_list = new JSONArray(result);
		JSONArray return_array = new JSONArray();
		int from = 0;
		int size = Integer.valueOf(form_data.get("size"));
		for(int i=0; i<model_list.length(); i++){
			JSONObject return_object = new JSONObject();
			int section_id = model_list.getJSONObject(i).getInt("id");
			JSONArray article_list = getbysectionid(section_id,from,size);
			return_object.put("id", section_id);
			return_object.put("name", model_list.getJSONObject(i).getString("name"));
			return_object.put("list", article_list != null ? article_list : new JSONArray());
			return_array.put(i, return_object);
		}
		return_json(200,"获取列表成功",return_array);
	}
	
	/**
	 * 获取第一个栏目的文章列表
	 * @throws IOException
	 */
	public void getfirstsectionlist() throws IOException{
		JSONObject result_object = new JSONObject();
		Map<String, String> form_data = new Form(context).createForm(request, "article");
		String companyid = form_data.get("companyid");
		String result = _model.mysql_model.executeQuery("SELECT pm.id,pm.`name`,pm.logo FROM company_models cm LEFT JOIN post_models pm on cm.companyid = pm.companyid AND cm.id = pm.bigkind_id WHERE cm.companyid = '"+companyid+"' AND cm.model_id = 2 AND pm.is_del=0 ORDER BY pm.sort ASC LIMIT 1");
		JSONObject sectioninfo = new JSONArray(result).getJSONObject(0);
		result_object.put("model_name", sectioninfo.getString("name"));
		String logo = sectioninfo.getString("logo");
		int sectionid = sectioninfo.getInt("id");
		JSONArray article_list = getbysectionid(sectionid,0,6);
		JSONArray return_alist = new JSONArray();
		if(article_list != null){
			for(int i=0; i<article_list.length(); i++){
				JSONObject article = article_list.getJSONObject(i);
				JSONObject return_article = new JSONObject();
				return_article.put("id", article.getInt("id"));
				return_article.put("title", article.getString("title"));
				return_article.put("desc", article.getString("desc"));
				return_article.put("thumb", article.getString("thumb"));
				return_article.put("is_top", article.getBoolean("is_top"));
				return_alist.put(i, return_article);
			}
			result_object.put("list", return_alist);
			return_json(200, "获取文章列表成功", result_object);
		}else{
			return_json(1001, "栏目数据为空");
		}
	}
}
