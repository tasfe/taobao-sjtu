package edu.fudan.autologin.service;

import net.sf.json.JSONObject;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import edu.fudan.autologin.formfields.GetMethod;

public class ReviewSumService {
	private static final Logger log = Logger.getLogger(ReviewSumService.class);
	private String itemPageUrl;
	private HttpClient httpClient;
	
	private int reviewSum = 0;
	
	public void execute(){
		parseJson(getJsonStr(getPlainJson(getAjaxUrl())));
	}
	
	public String getAjaxUrl(){
		
		String ajaxUrl = "";
		GetMethod get = new GetMethod(httpClient, itemPageUrl);
		get.doGet();
		
		Document doc = Jsoup.parse(get.getResponseAsString());
		String baseUrl = "";
		String appendStr = "&callback=jsonp_reviews_summary";
		
		if(doc.select("em#J_RateStar").size() == 0){
			log.error("There is no review sum url in the page.");
		}else{
			Element rateStar = doc.select("em#J_RateStar").get(0);
			baseUrl = rateStar.attr("data-commonApi");
		}
		ajaxUrl = baseUrl + appendStr;
		
		get.shutDown();
		return ajaxUrl;
	}
	
	public String getPlainJson(String ajaxUrl){
		String plainJson = "";
		GetMethod get = new GetMethod(httpClient, ajaxUrl);
		get.doGet();
		
		plainJson = get.getResponseAsString();
		get.shutDown();
		return plainJson;
		
	}
	
	public String getJsonStr(String plainJson){
		String jsonStr = "";
		
		int begin = plainJson.indexOf("{");
		int end = plainJson.lastIndexOf("}");
		jsonStr = plainJson.substring(begin,end+1);
		return jsonStr;
	}
	/**
	 * jsonp_reviews_summary(
	 * {
	 * 				"watershed":100,
	 *				"data":{
	 * 							"count":
	 * 										{"total":12662,"goodFull":31563,"additional":233,"correspond":0,"normal":59,"hascontent":0,"good":12584,"bad":19},
					 * 			"correspond":"4.8",
					 * 			"impress":[
											 * {"title":"温和不刺激","count":1055,"value":1,"attribute":"2324-11"},
											 * {"title":"质量很好","count":580,"value":1,"attribute":"620-11"}
											 * ,{"title":"正品","count":459,"value":1,"attribute":"1020-11"},
											 * {"title":"保湿滋润","count":390,"value":1,"attribute":"824-11"},
											 * {"title":"卖家很好","count":364,"value":1,"attribute":"10120-11"},
											 * {"title":"到货很快","count":326,"value":1,"attribute":"420-11"},
											 * {"title":"清洁度强","count":241,"value":1,"attribute":"2524-11"},
											 * {"title":"清洁度不好","count":137,"value":-1,"attribute":"2524-13"}
								 * ],
	 * 							"links":null,
	 * 							"correspondCount":427714,
	 * 							"correspondList":["85.83","12.09","1.58","0.16","0.34"],
	 * 							"refundTime":0
	 * 					}
	 * })
	 * @param jsonStr
	 */
	public void parseJson(String jsonStr){
		log.info("Json str from server is: "+jsonStr);
		
		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		reviewSum = jsonObj.getJSONObject("data").getJSONObject("count").getInt("total");
		log.info("The sum of reviews is: "+ reviewSum);
	}

	public String getItemPageUrl() {
		return itemPageUrl;
	}

	public void setItemPageUrl(String itemPageUrl) {
		this.itemPageUrl = itemPageUrl;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public int getReviewSum() {
		return reviewSum;
	}

	public void setReviewSum(int reviewSum) {
		this.reviewSum = reviewSum;
	}
}
