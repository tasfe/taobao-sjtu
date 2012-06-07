package edu.fudan.autologin.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import edu.fudan.autologin.utils.PingUtil;
import edu.fudan.autologin.utils.RandomUtils;
import edu.fudan.autologin.utils.XmlConfUtil;

public class JavaBaseTest {
	private static final Logger log = Logger.getLogger(JavaBaseTest.class);
	@Before
	public void setUp(){
		PropertyConfigurator.configure("log4j.properties");
		log.setLevel(Level.DEBUG);
	}

	/**
	 * The substring begins at specified beginIndex and extends to the character at index endIndex - 1
	 */
	@Test
	public void testListErase(){
		
		List<String> myint = new ArrayList<String>();
		myint.add("123");
		myint.add("456");
		
		myint.remove(0);
		
		for(String s:myint){
			log.info(s);
		}
		
	}
	public void testString2Date(){
		Date d = new Date();
		Date d1 = new Date();
		 SimpleDateFormat df=new SimpleDateFormat("yyyy.MM.dd");
		 try {
			d = df.parse("2012.04.30");
			d1 = df.parse("2012.05.31");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		    System.out.println("今天的日期："+df.format(d));
		    
		    log.info((d1.getTime() - d.getTime())/(24*60*60*1000));
	}
	public void testDate(){
		Date d = new Date();  
        SimpleDateFormat df=new SimpleDateFormat("yyyy.MM.dd");   
        System.out.println("今天的日期："+df.format(d));   
        System.out.println("两天前的日期：" + df.format(new Date(d.getTime() - (long)2 * 24 * 60 * 60 * 1000)));   
        System.out.println("三天后的日期：" + df.format(new Date(d.getTime() + (long)30 * 24 * 60 * 60 * 1000)));
	}
	public void testPing(){
		
		URI uri = null;
		try {
			uri = new URI("http://item.taobao.com/item.htm?id=14785357817");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		log.info(PingUtil.pingServer(uri.getHost(), 100));
	}
	public void getToken(){
		String pageStr = "},\"sys\":{\"now\":1337239299737,\"tkn\":\"73e36deede3bb\"}} ;  }";
		String token = null;
		
		int base = pageStr.indexOf("sys\":{\"now\":");
		int end = pageStr.indexOf(",\"tkn\":", base);

		token = pageStr
				.substring(base + "sys\":{\"now\":".length(), end);
		log.info(token.substring(0,6)+RandomUtils.getRandomNum(token.length()-6));
	}
	public void testXmlConf(){
		
		XmlConfUtil.openXml();
		log.info(XmlConfUtil.getValueByName("browserPath"));
	}
	public void testConstructUrl(){
		String initUrl = "http://fx.taobao.com/u/Nzg1MDIxNjky/view/ta_taoshare_list.htm?redirect=fa";
		log.info(constructBuyerHref(initUrl));
	}
	
	public String constructBuyerHref(String initUrl){
		String buyerId = initUrl.split("/")[4];
		StringBuffer sb = new StringBuffer();
		String baseUrl = "http://i.taobao.com/u/";
		String appendStr = "/front.htm";
		
		sb.append(baseUrl);
		sb.append(buyerId);
		sb.append(appendStr);
		
		return sb.toString();
	}
	public void testUrl(){
			String appendStr = "/frontInfoGather.htm?viewList=";
			String baseUrl = "http://i.taobao.com/u/NjAwNjgzNDQ=/front.htm";
			String targetUrl = baseUrl.split(".htm")[0] + appendStr;
			log.info("Target url is: "+targetUrl);
	}
	public void testEncoding(){
		String str ;
		str = StringEscapeUtils.unescapeJava("\u8012\u9633\u5e02");
		String s = new String("\u4ed6");
		//  \u4ed6就是一个unicode码，就是一个字符，你可以
		System.out.println(str);
		System.out.print("\u8012\u9633\u5e02");//试试
    String url = "http://a.tbcdn.cn//apps/mytaobao/3.0/tlive/mods/??sidebar.js,follow.js?t=20111128.js"; 
    
	}
	
	public void testJson(){
		String jsonStr = "{\"test\":\"123123\"}";
		JSONObject obj = JSONObject.fromObject(jsonStr);
		try{
			log.info(obj.getString("test"));
			
		}catch (JSONException e) {
			log.error(e.getMessage());
		}
	}
	public void testDoc(){
		String url = "http://item.taobao.com/item.htm?id=14568205276";
	}
	
	public void testSubString(){
		String str = "(123123)";
		log.info(str.substring(str.indexOf("("),str.indexOf(")")));
	}
	
}
