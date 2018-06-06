package webapp.company.info;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ruby.framework.controller.ControllerBase;
import com.ruby.framework.function.CommonFunction;
import com.ruby.framework.function.Form;

import java.awt.Color;  
import java.awt.Graphics2D;  
import java.awt.image.BufferedImage;  
import java.io.File;  
  
import javax.imageio.ImageIO;  
  
import com.swetake.util.Qrcode;

public class CompanyQrcode extends ControllerBase{

	public void getregqrcode() throws IOException{
		Map<String, String> form_data = new Form(context).createForm(request, "company_form");
        String companyid = form_data.get("companyid");
        if(companyid == null || companyid.equals("")){
            return_json(1001, "公司id为空");
            return;
        }
		String url = "http://www.companyclub.cn/club?_s=shareregister#/?companyid=" + companyid;

		File file = new File("/home/wwwroot/files.companyclub.cn/tongshiquan/qrcode/company_"+companyid+".png");

		if(!file.exists()){
			Qrcode qrcode = new Qrcode();  
			qrcode.setQrcodeErrorCorrect('L');//纠错等级（分为L、M、H三个等级）  
			qrcode.setQrcodeEncodeMode('B');//N代表数字，A代表a-Z，B代表其它字符  
			qrcode.setQrcodeVersion(20);//版本  
			//生成二维码中要存储的信息  
			String qrData = url;  
			//设置一下二维码的像素  
			int width = 300;  
			int height = 300;  
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  
			//绘图  
			Graphics2D gs = bufferedImage.createGraphics();  
			gs.setBackground(Color.WHITE);  
			gs.setColor(Color.BLACK);  
			gs.clearRect(0, 0, width, height);//清除下画板内容  
         
			//设置下偏移量,如果不加偏移量，有时会导致出错。  
			int pixoff = 2;  
 
			byte[] d = qrData.getBytes("gb2312");  
			if(d.length > 0 && d.length <120){  
				boolean[][] s = qrcode.calQrcode(d);  
				for(int i=0;i<s.length;i++){  
					for(int j=0;j<s.length;j++){  
						if(s[j][i]){  
							gs.fillRect(j*3+pixoff, i*3+pixoff, 3, 3);  
						}  
					}  
				}  
			}  
			gs.dispose();  
			bufferedImage.flush();  
			ImageIO.write(bufferedImage, "png", file);  
		}
		JSONObject jsonobject = new JSONObject();
		jsonobject.put("url",  url);
		jsonobject.put("img", "http://cdn.companyclub.cn/tongshiquan/qrcode/company_"+companyid+".png");
		return_json(200, "生成二维码成功", jsonobject);
	}
}
