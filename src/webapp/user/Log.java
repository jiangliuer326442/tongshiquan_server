package webapp.user;

/**
 * 用户日志记录类
 * @author 方海亮
 *
 */
public class Log extends Userbase {
	public void bingcompanylog(String uid,String companyid,String result){
		_model.mysql_model.executeUpdate("INSERT INTO `user_bindcompany_record` (`userid`, `companyid`, `bindstatus`) VALUES ("+uid+", '"+companyid+"', "+result+")");
	}
}
