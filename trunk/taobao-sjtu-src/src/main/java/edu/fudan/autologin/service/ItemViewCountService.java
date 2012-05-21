package edu.fudan.autologin.service;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import edu.fudan.autologin.formfields.GetMethod;

public class ItemViewCountService {
//http://count.tbcdn.cn/counter3?inc=ICVT_7_16952524736&sign=58859bbf4830a08502bf36bee7dbac60c1683&keys=DFX_200_1_16952524736,ICVT_7_16952524736,ICCP_1_16952524736,ICE_3_feedcount-16952524736,ZAN_27_2_16952524736,SCCP_2_66467270&callback=TShop.mods.SKU.CounterCenter.saveCounts
//http://count.tbcdn.cn/counter3?inc=ICVT_7_16952524736&sign= 4255bbf4830a08502bf36bee7dbac60c1683&keys=DFX_200_1_16952524736,ICVT_7_16952524736,ICCP_1_16952524736,ICE_3_feedcount-16952524736,ZAN_27_2_16952524736,SCCP_2_66467270
	private static final Logger log = Logger.getLogger(ItemViewCountService.class);

	private int viewCount = 0;
	private HttpClient httpClient = new DefaultHttpClient();
	private String itemDetailPage;
	
	public void execute(){
		
		
	}
	
	public void getViewCounter(String ajaxUrl){
		
		GetMethod get = new GetMethod(httpClient, ajaxUrl);
		get.doGet();
		get.shutDown();
	}
	
	public String getCountApiUrl(){
		String countApiUrl = "";
		
		String docStr = "";
		
		String appendStr = "&callback=TShop.mods.SKU.CounterCenter.saveCounts";
		
		GetMethod get = new GetMethod(httpClient, itemDetailPage);
		get.doGet();
		docStr = get.getResponseAsString();
		get.shutDown();
		
		//counterApi:"http://count.tbcdn.cn/counter3?inc=ICVT_7_16952524736&sign=47105bbf4830a08502bf36bee7dbac60c1683&keys=DFX_200_1_16952524736,ICVT_7_16952524736,ICCP_1_16952524736,ICE_3_feedcount-16952524736,ZAN_27_2_16952524736,
		//SCCP_2_66467270",toolbar:{delay:10}};</script>
		
		if(docStr.contains("counterApi") == false){
			log.info("There is no counterApiUrl in the page.");
		}else{
			int base = docStr.indexOf("counterApi:\"http");
			int end = docStr.indexOf("\",toolbar:\"");
			countApiUrl = docStr.substring(base+"counterApi:\"".length(), end);
		}

		return countApiUrl;
	}
}
