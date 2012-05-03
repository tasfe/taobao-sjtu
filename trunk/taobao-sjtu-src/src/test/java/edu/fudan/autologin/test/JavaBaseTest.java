package edu.fudan.autologin.test;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

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
