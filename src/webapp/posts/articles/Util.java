package webapp.posts.articles;

import java.util.Map;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;

/**
 * 文章工具类
 * @author SC000749
 *
 */
public class Util extends ControllerBase{
	public String getcontentthumb(String content){
		String thumb = "";
		int begin_index = content.lastIndexOf(" src=\"")+6;
		int end_index = content.indexOf("\"",begin_index);
		if(begin_index > 0 && end_index >0){
			thumb = content.substring(begin_index, end_index).replace("http:","").replace("https:","");
			if(thumb.equals("")){
				begin_index = content.lastIndexOf(" src=\'")+6;
				end_index = content.indexOf("\'",begin_index);
				thumb = content.substring(begin_index, end_index).replace("http:","").replace("https:","");
			}
		}
		return thumb;
	}
	
	/**
	 * 获取文章对应的栏目
	 * @param article_id
	 * @return
	 */
	public int getarticlecolumn(int article_id){
		String result = _model.mysql_model.executeQuery("select sectionid from articles where id = "+Integer.toString(article_id)+" limit 1");
		if(new JSONArray(result).length()>0){
			return new JSONArray(result).getJSONObject(0).getInt("sectionid");
		}else{
			return 0;
		}
	}
	
	/**
	 * 文章修改权限检查
	 * 要求文章id所属栏目的管理员必须自己
	 * 或者文章为自己所写
	 * @param aid
	 * @param uid
	 * @return
	 */
	public boolean check_article(String aid, String uid){
		Map userinfo = new webapp.company.reg.Ckuser().getuserinfobycache(Integer.parseInt(uid));
		String companyid = userinfo.get("companyid").toString();
		String result = _model.mysql_model.executeQuery("SELECT 1 FROM V_ARTICLE_PRIV WHERE id = "+aid+" AND companyid='"+companyid+"' AND leaderid = "+uid+" LIMIT 1");
		if(new JSONArray(result).length()>0) return true;
		result = _model.mysql_model.executeQuery("SELECT 1 FROM articles WHERE id = "+aid+" AND userid = "+uid);
		if(new JSONArray(result).length()>0) return true;
		return false;
	}
}
