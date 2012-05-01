package edu.fudan.autologin.test;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pojos.FeedRateComment;
import edu.fudan.autologin.service.ItemReviewService;

public class ItemDetailPageParserTest {

	private static final Logger log = Logger.getLogger(ItemDetailPageParserTest.class);
	private HttpClient httpClient;
	
	@Before
	public void setUp(){
		if(httpClient == null){
			httpClient = new DefaultHttpClient();
		}
		
		beforeWriteExcel();
		PropertyConfigurator.configure("log4j.properties");
		log.setLevel(Level.DEBUG);

	}
	
	@After
	public void tearDown(){
		log.info("--------------------------------------------------------------------------------------------------------------");
		log.info("COMPLETE ALL TASKS!");
		ExcelUtil.closeWorkbook();
		httpClient.getConnectionManager().shutdown();
	}
	
	public void beforeWriteExcel() {
		ExcelUtil.createWorkbook();
		ExcelUtil.createSheets();
	}
	
	
	
	@Test
	public void testReview(){
		ItemReviewService itemReviewService = new ItemReviewService();
		itemReviewService.setItemPageUrl("http://item.taobao.com/item.htm?id=13619790834");
		itemReviewService.setHttpClient(httpClient);
		itemReviewService.parseReviews();
		
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
		String itemDetailUrl = "http://item.taobao.com/item.htm?id=13619790834";

		String baseFeedRateListUrl = "";

		String tmpStr = "";
		GetMethod getMethod = new GetMethod(httpClient, itemDetailUrl);
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
	public String getFeedRateListJsonString(String str) {
		return str.substring("jsonp_reviews_list(".length(), str.length() - 1);
	}

	public boolean parseFeedRateListJson(String str) {

		JSONObject jsonObj = JSONObject.fromObject(str);

		if (jsonObj.get("comments").equals(null)) {
			log.info("There is no comment.");
			return false;
		} else {

			JSONArray comments = jsonObj.getJSONArray("comments");

			List list = (List) JSONSerializer.toJava(comments);

			List<FeedRateComment> cmts = new ArrayList<FeedRateComment>();
			int i = 1;
			for (Object o : list) {
				JSONObject j = JSONObject.fromObject(o);
				log.info("Date is: " + j.getString("date"));
				log.info("Content is: " + j.getString("content"));
				log.info("Auction title is: "+j.getJSONObject("auction").getString("title"));
				log.info("Comment NO is: " + i++);
			}
			return true;
		}
	}
	
	
}
