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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pojos.FeedRateComment;
import edu.fudan.autologin.service.BuyerListService;
import edu.fudan.autologin.service.ItemReviewService;
import edu.fudan.autologin.service.MonthService;
import edu.fudan.autologin.service.PostageService;

public class ItemDetailPageParserTest {

	private static final Logger log = Logger
			.getLogger(ItemDetailPageParserTest.class);
	private HttpClient httpClient;

	@Before
	public void setUp() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
		}

		beforeWriteExcel();
		PropertyConfigurator.configure("log4j.properties");
		log.setLevel(Level.DEBUG);

	}

	@After
	public void tearDown() {
		log.info("--------------------------------------------------------------------------------------------------------------");
		log.info("COMPLETE ALL TASKS!");
		ExcelUtil.closeWorkbook();
		httpClient.getConnectionManager().shutdown();
	}

	@Test
	public void testDoc() {
		String url = "http://item.taobao.com/item.htm?id=14568205276";
		GetMethod get = new GetMethod(httpClient, url);
		get.doGet();
		Document doc = Jsoup.parse(get.getResponseAsString());
		log.info("shop rank is: "+doc.select("a#shop-rank"));
		if (doc.select("a#shop-rank").size() == 0) {
			log.info("There is no shop rank href.");
		} else {
			Element shopRankEle = doc.select("a#shop-rank").get(0);
		}
	}

	public void beforeWriteExcel() {
		ExcelUtil.createWorkbook();
		ExcelUtil.createSheets();
	}

	public void testMonthServie() {
		MonthService monthService = new MonthService();
		monthService.setHttpClient(httpClient);
		monthService
				.setUserRatePageUrl("http://rate.taobao.com/user-rate-2cd40cbdf1fbaa2fbdc9bb2fff2aaa4d.htm");
		monthService.execute();
	}

	public void testReview() {
		ItemReviewService itemReviewService = new ItemReviewService();
		itemReviewService
				.setItemPageUrl("http://item.taobao.com/item.htm?id=13619790834");
		itemReviewService.setHttpClient(httpClient);
		itemReviewService.parseReviews();
	}

	public void testBuyerListService() {
		BuyerListService buyerListService = new BuyerListService();
		buyerListService.setHttpClient(httpClient);
		buyerListService
				.setItemPageUrl("http://item.taobao.com/item.htm?id=10203414733");
		buyerListService.setBuyerSum(1902);
		buyerListService.parseShowBuyerListDoc();
	}

	public void testPostageService() {
		PostageService postageService = new PostageService();
		postageService.setHttpClient(httpClient);
		postageService
				.setItemPageUrl("http://wt.taobao.com/detail.htm?id=14010379194");
		postageService.parsePostage();
	}
}
