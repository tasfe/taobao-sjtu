package edu.fudan.autologin.service;

import net.sf.json.JSONObject;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.utils.GetWaitUtil;

public class ItemViewCountService {
//http://count.tbcdn.cn/counter3?inc=ICVT_7_16952524736&sign=58859bbf4830a08502bf36bee7dbac60c1683&keys=DFX_200_1_16952524736,ICVT_7_16952524736,ICCP_1_16952524736,ICE_3_feedcount-16952524736,ZAN_27_2_16952524736,SCCP_2_66467270&callback=TShop.mods.SKU.CounterCenter.saveCounts
//http://count.tbcdn.cn/counter3?inc=ICVT_7_16952524736&sign= 4255bbf4830a08502bf36bee7dbac60c1683&keys=DFX_200_1_16952524736,ICVT_7_16952524736,ICCP_1_16952524736,ICE_3_feedcount-16952524736,ZAN_27_2_16952524736,SCCP_2_66467270
	private static final Logger log = Logger.getLogger(ItemViewCountService.class);

	private int viewCount = 0;
	private HttpClient httpClient = new DefaultHttpClient();
	private String itemDetailPage;
	
	public int getViewCount() {
		return viewCount;
	}




	public void setItemDetailPage(String itemDetailPage) {
		this.itemDetailPage = itemDetailPage;
	}




	public void execute(){
		String ajaxUrl = getCounterApiUrl();
		
		if(ajaxUrl == null){
			log.info("Ajax url is null.");
		}else{
			String json = getJsonFromServer(ajaxUrl);
			parseJson(json);
		}
		
		
		httpClient.getConnectionManager().shutdown();
	}
	
		
	
	
	public  String  getItemId(){
		return itemDetailPage.split("=")[1];
	}
	
	/*
	 * {"DFX_200_1_13738559526":100,
	 * "ICVT_7_13738559526":295616,"ICCP_1_13738559526":5473,
	 * "ICE_3_feedcount-13738559526":528,
	 * "ZAN_27_2_13738559526":15,"SCCP_2_35555431":11658}
	 * 
	 */
	public void parseJson(String json){
		JSONObject jsonObj = JSONObject.fromObject(json);
		String itemViewCounterFLag = "ICVT_7_"+getItemId();
		
		
		log.info(itemViewCounterFLag+": "+jsonObj.getInt(itemViewCounterFLag));
		viewCount = jsonObj.getInt(itemViewCounterFLag);
	}
	
	
	public String getJsonFromServer(String ajaxUrl) {
		String json = null;
	
		GetMethod get = new GetMethod(httpClient, ajaxUrl);

		GetWaitUtil.get(get);
		json = get.getResponseAsString();
		get.shutDown();
		log.info("Json counter from server is: " + json);

		if (json.contains("TShop.mods.SKU.CounterCenter.saveCounts") == false) {
			log.info("There is no json counter from server.");
		} else {
			int begin = json.indexOf("(");
			int end = json.indexOf(")");

			if (begin == -1 || end == -1 || end < begin)
				log.error("Item view count json from server is error.");
			json = json.substring(begin + 1, end);
		}

		return json;
	}
	
	public String getCounterApiUrl(){
		String countApiUrl = "";
		
		StringBuffer sb = new StringBuffer();
		String docStr = "";
		
		String appendStr = "&callback=TShop.mods.SKU.CounterCenter.saveCounts";
		
		GetMethod get = new GetMethod(httpClient, itemDetailPage);
//		get.doGet();
		
		GetWaitUtil.get(get);
		docStr = get.getResponseAsString();
		get.shutDown();
		
		//counterApi:"http://count.tbcdn.cn/counter3?inc=ICVT_7_16952524736&sign=47105bbf4830a08502bf36bee7dbac60c1683&keys=DFX_200_1_16952524736,ICVT_7_16952524736,ICCP_1_16952524736,ICE_3_feedcount-16952524736,ZAN_27_2_16952524736,
		//SCCP_2_66467270",toolbar:{delay:10}};</script>
		
//		log.info("Doc  from server is: "+docStr);
		if(docStr.contains("counterApi") == false){
			log.info("There is no counterApiUrl in the page.");
			return null;
		}else{
			int base = docStr.indexOf("counterApi:\"");
			int end = docStr.indexOf("toolbar", base);
//			log.info("base is: "+base);
//			log.info("end is: "+end);
			countApiUrl = docStr.substring(base+"counterApi:\"".length(), end - 2);
		}
		sb.append(countApiUrl);
		sb.append(appendStr);
		
		log.info("Ajax url is: "+sb.toString());

		return sb.toString();
	}
}
