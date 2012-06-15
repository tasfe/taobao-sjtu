package edu.fudan.autologin.service;

import net.sf.json.JSONObject;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import edu.fudan.autologin.constants.SystemConstant;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.utils.GetWaitUtil;

public class ReviewSumService {
	private static final Logger log = Logger.getLogger(ReviewSumService.class);
	private String itemPageUrl;
	private HttpClient httpClient;

	private int reviewSum = 0;

	public ReviewSumService() {

		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, SystemConstant.CONNECTION_TIMEOUT);
		httpClient = new DefaultHttpClient(httpParams);
	}

	public void execute() {
		String ajaxUrl;
		ajaxUrl = getAjaxUrl();
		if (ajaxUrl == null) {
			log.info("There is no review sum url.");
		} else {
			String strFromServer = getPlainJson(ajaxUrl);
			String json = null;
			json = getJsonStr(strFromServer);

			if (json != null) {
				parseJson(json);
			}
		}

		this.httpClient.getConnectionManager().shutdown();
		httpClient = null;
	}

	public String getAjaxUrl() {

		String ajaxUrl = "";
		GetMethod get = new GetMethod(httpClient, itemPageUrl);

		GetWaitUtil.get(get);

		Document doc = Jsoup.parse(get.getResponseAsString());
		String baseUrl = "";
		String appendStr = "&callback=jsonp_reviews_summary";

		if (doc.select("em#J_RateStar").size() == 0) {
			log.error("There is no review sum url in the page.");
			ajaxUrl = null;
		} else {
			Element rateStar = doc.select("em#J_RateStar").get(0);
			baseUrl = rateStar.attr("data-commonApi");
			ajaxUrl = baseUrl + appendStr;
		}

		get.shutDown();
		return ajaxUrl;
	}

	public String getPlainJson(String ajaxUrl) {
		String strFromServer = null;
		GetMethod get = new GetMethod(httpClient, ajaxUrl);
		GetWaitUtil.get(get);

		strFromServer = get.getResponseAsString();
		get.shutDown();
		return strFromServer;

	}

	public String getJsonStr(String plainJson) {
		String jsonStr = null;
		if (plainJson.contains("{") && plainJson.contains("}")) {
			int begin = plainJson.indexOf("{");
			int end = plainJson.lastIndexOf("}");
			jsonStr = plainJson.substring(begin, end + 1);
		}

		return jsonStr;
	}

	/**
	 * jsonp_reviews_summary( { "watershed":100, "data":{ "count":
	 * {"total":12662
	 * ,"goodFull":31563,"additional":233,"correspond":0,"normal":59
	 * ,"hascontent":0,"good":12584,"bad":19}, "correspond":"4.8", "impress":[
	 * {"title":"温和不刺激","count":1055,"value":1,"attribute":"2324-11"},
	 * {"title":"质量很好","count":580,"value":1,"attribute":"620-11"}
	 * ,{"title":"正品","count":459,"value":1,"attribute":"1020-11"},
	 * {"title":"保湿滋润","count":390,"value":1,"attribute":"824-11"},
	 * {"title":"卖家很好","count":364,"value":1,"attribute":"10120-11"},
	 * {"title":"到货很快","count":326,"value":1,"attribute":"420-11"},
	 * {"title":"清洁度强","count":241,"value":1,"attribute":"2524-11"},
	 * {"title":"清洁度不好","count":137,"value":-1,"attribute":"2524-13"} ],
	 * "links":null, "correspondCount":427714,
	 * "correspondList":["85.83","12.09","1.58","0.16","0.34"], "refundTime":0 }
	 * })
	 * 
	 * @param jsonStr
	 */
	public void parseJson(String jsonStr) {
		// log.info("Json str from server is: "+jsonStr);

		JSONObject jsonObj = JSONObject.fromObject(jsonStr);

		if (jsonStr.contains("data") && jsonStr.contains("count")
				&& jsonStr.contains("total")) {
			reviewSum = jsonObj.getJSONObject("data").getJSONObject("count")
					.getInt("total");
		}

		log.info("The sum of reviews is: " + reviewSum);
	}

	public String getItemPageUrl() {
		return itemPageUrl;
	}

	public void setItemPageUrl(String itemPageUrl) {
		this.itemPageUrl = itemPageUrl;
	}

	public int getReviewSum() {
		return reviewSum;
	}

	public void setReviewSum(int reviewSum) {
		this.reviewSum = reviewSum;
	}
}
