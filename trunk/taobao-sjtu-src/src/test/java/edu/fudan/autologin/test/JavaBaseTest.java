package edu.fudan.autologin.test;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

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
