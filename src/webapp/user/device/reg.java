package webapp.user.device;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;

public class reg extends ControllerBase {
	
	/**
	 * 设备注册
	 * @param vendor
	 * @param uuid
	 * @return
	 */
	public int getdeviceid(String vendor, String uuid, int uid){
		int device_id = 0;
		if(!uuid.equals("") && !vendor.equals("")){
			_model.mysql_model.executeUpdate("INSERT IGNORE INTO `devices` (`uuid`, `vendor`, `uid`) VALUES ('"+uuid+"', '"+vendor+"', "+Integer.toString(uid)+")");
			device_id = new JSONArray(_model.mysql_model.executeQuery("SELECT id FROM `devices` WHERE `uuid` = '"+uuid+"'")).getJSONObject(0).getInt("id");
		}
		return device_id;
	}

}
