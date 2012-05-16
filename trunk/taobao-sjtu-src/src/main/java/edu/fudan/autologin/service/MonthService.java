package edu.fudan.autologin.service;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pojos.MonthServiceEntity;

public class MonthService {

	private static final Logger log = Logger.getLogger(MonthService.class);
	private String userRatePageUrl;

	private List<MonthServiceEntity> monthServieEntities = new ArrayList<MonthServiceEntity>();

	public List<MonthServiceEntity> getMonthServieEntities() {
		return monthServieEntities;
	}

	public String getUserRatePageUrl() {
		return userRatePageUrl;
	}

	public void setUserRatePageUrl(String userRatePageUrl) {
		this.userRatePageUrl = userRatePageUrl;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
//		this.httpClient = httpClient;
		this.httpClient = new DefaultHttpClient();
	}

	private HttpClient httpClient;

	private String monthuserid;
	private String userTag;
	private String isB2C;

	public void execute() {
		log.info("User rate page url is: " + this.userRatePageUrl);
		getFieldsFromPage();
		String json = getJsonString(getPlainJson());
		parseJson(json);
		

		this.httpClient.getConnectionManager().shutdown();
	}

	public String getPlainJson() {
		GetMethod get = new GetMethod(httpClient,
				constructMonthServiceAjaxUrl());
		
		List<NameValuePair> headers1 = new ArrayList<NameValuePair>();
		NameValuePair nvp1 = new BasicNameValuePair("Accept", "application/json");
		headers1.add(nvp1);
		get.doGet(headers1);

		
		String plainJson = get.getResponseAsString().trim();
		log.info("Plain json string of month service from server is: "
				+ plainJson);
		get.shutDown();

		return plainJson;
	}

	public String getJsonString(String str) {
		int begin = str.indexOf("{");
		int end = str.lastIndexOf("}");

		log.info("Json string is: "+str);
		return str.substring(begin, end + 1);
	}

	public void getFieldsFromPage() {
		GetMethod get = new GetMethod(httpClient, userRatePageUrl);
		get.doGet();

		String tmp = get.getResponseAsString();
		get.shutDown();
		
		Document doc = Jsoup.parse(tmp);

		Element monthuseridEle = doc.select("input#monthuserid").get(0);
		monthuserid = monthuseridEle.attr("value");
		log.info("Monthuserid is: " + monthuserid);

		// Elements monthuseridEles = doc.select("input#monthuserid");
		//
		// for(Element e : monthuseridEles){
		// monthuserid = e.attr("value");
		// log.info("Monthuserid is: "+monthuserid);
		// }

		Elements userTagEles = doc.select("input#userTag");

		for (Element e : userTagEles) {
			userTag = e.attr("value");
			log.info("UserTag is: " + userTag);
		}

		Elements isB2CEles = doc.select("input#isB2C");

		for (Element e : isB2CEles) {
			isB2C = e.attr("value").toString();
			log.info("IsB2C is: " + isB2C);
		}

		get.shutDown();
	}

	public String constructMonthServiceAjaxUrl() {
		String ajaxUrl = null;
		String baseUrl = "http://ratehis.taobao.com/monthServiceAjax.htm?";

		ajaxUrl = baseUrl + "monthuserid=" + monthuserid + "&userTag="
				+ userTag + "&isB2C=" + isB2C;

		log.info("Month service ajax url is: "+ajaxUrl);
		return ajaxUrl;
	}

	/**
	 * Json format from server is as following: { "alldata": [
	 * {"days":"1.02","native":"1.02","avg":"2.85","comparison":"小于"},
	 * {"sevenRefund"
	 * :"62","native":"6.61%","refund":184,"Other":"72","quality":"18"
	 * ,"avg":"3.50%","noReceiving":"32","comparison":"大于"},
	 * {"native":"0.04%","Other"
	 * :"1","avg":"0.02%","noReceiving":"0","complaints"
	 * :1,"comparison":"大于","sale":"0"},
	 * {"knowledge":"0","native":0,"Prohibited"
	 * :"0","trade":"0","punished":0,"avg":"0.08","comparison":"小于"} ]
	 * ,"tips":""
	 * ,"monthServiceData":"uri: /monthServiceAjax.vm type: pageNoCache" }
	 */
	public void parseJson(String jsonStr) {
		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		log.info("tips is: " + jsonObj.getString("tips"));
		log.info("monthServiceData is: "
				+ jsonObj.getString("monthServiceData"));

		JSONArray array = jsonObj.getJSONArray("alldata");

		List alldata = (List) JSONSerializer.toJava(array);

		if (alldata.size() == 0) {
			log.info("Alldata list size is 0");
		} else {
			for (Object o : alldata) {
				JSONObject jo = JSONObject.fromObject(o);

				MonthServiceEntity entity = new MonthServiceEntity();
				entity.setNativeValue(jo.getString("native"));
				entity.setComparison(jo.getString("comparison"));
				entity.setAvgValue(jo.getString("avg"));

				log.info(entity.getLineString());
				monthServieEntities.add(entity);
				// log.info("native is: "+entity.getNativeValue()+" "+entity.getComparison()+" "+entity.getAvgValue());
			}
		}
	}
}
