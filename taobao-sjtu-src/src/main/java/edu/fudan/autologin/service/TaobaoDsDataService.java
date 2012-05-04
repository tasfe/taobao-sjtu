package edu.fudan.autologin.service;

import net.sf.json.JSONObject;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;

import edu.fudan.autologin.formfields.GetMethod;

public class TaobaoDsDataService {
	private static final Logger log = Logger.getLogger(TaobaoDsDataService.class);
	private HttpClient httpClient;
	private JSONObject dsData;

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public void execute(){
		getDsData();
	}
	public void getDsData() {
		String plainStr;
		String url = "http://a.tbcdn.cn//apps/mytaobao/3.0/tlive/mods/??sidebar.js,follow.js?t=20111128.js";
		GetMethod get = new GetMethod(httpClient, url);
		get.doGet();
		plainStr = get.getResponseAsString();
		//log.info("Plain str is: "+plainStr);
		get.shutDown();
		
		int base = plainStr.indexOf("var _tb_ds_data=");
		int begin = plainStr.indexOf("{",base);
		int end = plainStr.indexOf("}",begin+1);
		
//		log.info("Json is: "+plainStr.substring(begin,end+1));	
		String jsonStr = plainStr.substring(begin,end+1);
		dsData = JSONObject.fromObject(jsonStr);
	}
	
	public String getData(String str){
		if(str.equals(null)||str.equals("")||(dsData.toString().contains(str)==false)){
			return null;
		}else{
			String myStr;
			myStr = dsData.getString(str).split("\"")[1];
			return myStr;
		}
	}
}
