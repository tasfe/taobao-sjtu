package edu.fudan.autologin.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pojos.FeedRate;
import edu.fudan.autologin.pojos.FeedRateComment;

/**
 * Giving you a item page url, and you can get the reviews of the item.
 * @author JustinChen
 *
 * 评论的格式如下:
 * {
		"watershed":100,
		"maxPage":167,
		"currentPageNum":166,
		"comments":[
			{			"auction":{
										"title":"Apple/苹果 iPhone 4S 无锁版/港版 16G 32G 64G可装软件有未激活",
										"aucNumId":13599064573,
										"link":"",
										"sku":"机身颜色:港版16G白色现货  手机套餐:官方标配"
										},
						"content":"hao",
						"append":null,
						"rate":"好评！",
						"tag":"",
						"rateId":16249892723,
						"award":"",
						"reply":null,
						"useful":0,
						"date":"2012.03.08",
						"user":{
										"vip":"",
										"rank":136,
										"nick":"771665176_44",
										"userId":410769781,
										"displayRatePic":"b_red_4.gif",
										"nickUrl":"http://wow.taobao.com/u/NDEwNzY5Nzgx/view/ta_taoshare_list.htm?redirect=fa",
										"vipLevel":2,
										"avatar":"http://img.taobaocdn.com/sns_logo/i1/T1VxqHXa4rXXb1upjX.jpg_40x40.jpg",
										"anony":false,
										"rankUrl":"http://rate.taobao.com/rate.htm?user_id=410769781&rater=1"}
						},
 */
public class ItemReviewService {
	private static final Logger log = Logger.getLogger(ItemReviewService.class);
	private String itemPageUrl;
	private FeedRate feedRate = new FeedRate();
	public String getItemPageUrl() {
		return itemPageUrl;
	}

	public void setItemPageUrl(String itemPageUrl) {
		this.itemPageUrl = itemPageUrl;
	}

	public FeedRate getFeedRate() {
		return feedRate;
	}

	public void setFeedRate(FeedRate feedRate) {
		this.feedRate = feedRate;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	private HttpClient httpClient;
	
	
	public ItemReviewService(){
		
	}
	
	public void parseReviews() {
		int pageNum = 0;
		while (true) {
			log.info("--------------------------------------------------------------------------------------");
			log.info("This review of page num is: " + (++pageNum));
			GetMethod get = new GetMethod(httpClient, constructFeedRateListUrl(
					getFeedRateListUrl(), pageNum));
			get.doGet();
			String jsonStr = getFeedRateListJsonString(get
					.getResponseAsString().trim());
			if (parseFeedRateListJson(jsonStr) == false) {
				break;
			}

			get.shutDown();
		}
	}

	public String getFeedRateListUrl() {
		String baseFeedRateListUrl = "";

		String tmpStr = "";
		GetMethod getMethod = new GetMethod(httpClient, itemPageUrl);
		getMethod.doGet();
		tmpStr = getMethod.getResponseAsString();
		getMethod.shutDown();

		int base = tmpStr.indexOf("data-listApi=");
		int begin = tmpStr.indexOf("\"", base);
		int end = tmpStr.indexOf("\"", begin + 1);
		baseFeedRateListUrl = tmpStr.substring(begin + 1, end);
		log.info("Base feed url is: " + baseFeedRateListUrl);

		return baseFeedRateListUrl;

	}

	public String constructFeedRateListUrl(String baseFeedRateListUrl,
			int currentPageNum) {
		String append = "&currentPageNum="
				+ currentPageNum
				+ "&rateType=&orderType=feedbackdate&showContent=1&attribute=&callback=jsonp_reviews_list";
		StringBuffer sb = new StringBuffer();
		sb.append(baseFeedRateListUrl);
		sb.append(append);

		return sb.toString();
	}

	/**
	 * 将从服务器端返回的字符串转化为json字符串
	 * 
	 * @return
	 */
	/*
	 *
	 * 评论的格式如下:
	 * {
			"watershed":100,
			"maxPage":167,
			"currentPageNum":166,
			"comments":[
				{			"auction":{
											"title":"Apple/苹果 iPhone 4S 无锁版/港版 16G 32G 64G可装软件有未激活",
											"aucNumId":13599064573,
											"link":"",
											"sku":"机身颜色:港版16G白色现货  手机套餐:官方标配"
											},
							"content":"hao",
							"append":null,
							"rate":"好评！",
							"tag":"",
							"rateId":16249892723,
							"award":"",
							"reply":null,
							"useful":0,
							"date":"2012.03.08",
							"user":{
											"vip":"",
											"rank":136,
											"nick":"771665176_44",
											"userId":410769781,
											"displayRatePic":"b_red_4.gif",
											"nickUrl":"http://wow.taobao.com/u/NDEwNzY5Nzgx/view/ta_taoshare_list.htm?redirect=fa",
											"vipLevel":2,
											"avatar":"http://img.taobaocdn.com/sns_logo/i1/T1VxqHXa4rXXb1upjX.jpg_40x40.jpg",
											"anony":false,
											"rankUrl":"http://rate.taobao.com/rate.htm?user_id=410769781&rater=1"}
							},
	 */
	public String getFeedRateListJsonString(String str) {
		return str.substring("jsonp_reviews_list(".length(), str.length() - 1);
	}

	public boolean parseFeedRateListJson(String str) {

		JSONObject jsonObj = JSONObject.fromObject(str);

		feedRate.setWatershed(jsonObj.getInt("watershed"));
		feedRate.setMaxPage(jsonObj.getInt("maxPage"));
		feedRate.setCurrentPageNum(jsonObj.getInt("currentPageNum"));
		
		if (jsonObj.get("comments").equals(null)) {
			log.info("There is no comment.");
			return false;
		} else {

			JSONArray comments = jsonObj.getJSONArray("comments");

			List list = (List) JSONSerializer.toJava(comments);

			List<FeedRateComment> cmts = new ArrayList<FeedRateComment>();
			int i = 1;
			for (Object o : list) {
				//feedRate.getComments().add((FeedRateComment) o);
				
				
				JSONObject j = JSONObject.fromObject(o);
				FeedRateComment cmt = new FeedRateComment();
				cmt.setDate(j.getString("date"));
				cmt.setContent(j.getString("content"));
				log.info("Comment NO is: " + i++);
				log.info("Date is: " + j.getString("date"));
				log.info("Auction title is: "+j.getJSONObject("auction").getString("title"));
				log.info("Content is: " + j.getString("content"));
				
				
			}
			return true;
		}
	}
	
}
