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
import edu.fudan.autologin.pageparser.UserRatePageParser;
import edu.fudan.autologin.pojos.FeedRateComment;
import edu.fudan.autologin.service.BuyerListService;
import edu.fudan.autologin.service.ItemReviewService;
import edu.fudan.autologin.service.MonthService;
import edu.fudan.autologin.service.PostageService;
import edu.fudan.autologin.service.ReviewSumService;
import edu.fudan.autologin.service.SaleSumService;

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

	
	@Test
	public void testNoItem(){
		
		String pageUrl = "http://item.taobao.com/item.htm?id=14614561158";
		
		GetMethod get =new GetMethod(httpClient, pageUrl);
		get.doGet();
		get.printResponse();
		get.shutDown();
	}
	public void testMonthServie() {
		MonthService monthService = new MonthService();
		monthService.setHttpClient(httpClient);
		monthService
				.setUserRatePageUrl("http://rate.taobao.com/user-rate-2cd40cbdf1fbaa2fbdc9bb2fff2aaa4d.htm");
		monthService.execute();
	}

	public void testReviewSum(){
		ReviewSumService reviewSumService = new ReviewSumService();
		reviewSumService.setHttpClient(httpClient);
		reviewSumService.setItemPageUrl("http://item.taobao.com/item.htm?id=10203414733");
		reviewSumService.execute();
	}
	
	
	public void testUserRate(){
		
		String pageUrl = "http://rate.taobao.com/user-rate-20c69a05f4c7b64896614a8b08d83fee.htm";
		UserRatePageParser userRatePageParser = new UserRatePageParser(httpClient, pageUrl);
		userRatePageParser.setSellerId("55600035");
		userRatePageParser.execute();
	}
	public void testReview() {
		
		String url = "http://item.taobao.com/item.htm?id=10203414733";
		ReviewSumService reviewSumService = new ReviewSumService();
		reviewSumService.setHttpClient(httpClient);
		reviewSumService.setItemPageUrl(url);
		reviewSumService.execute();
		
		
		ItemReviewService itemReviewService = new ItemReviewService();
		itemReviewService
				.setItemPageUrl(url);
		itemReviewService.setHttpClient(httpClient);
		itemReviewService.setReviewSum(reviewSumService.getReviewSum());
		itemReviewService.execute();
	}
	
	public void testSaleSumService(){
		SaleSumService saleSumService = new SaleSumService();
		saleSumService.setHttpClient(httpClient);
		saleSumService.setItemPageUrl("http://item.taobao.com/item.htm?id=10203414733");
		saleSumService.execute();
		log.info("Sale sum is: "+saleSumService.getSaleSum());	
	}
	
	
	
	public void testBuyerListService() {
		String itemPageUrl = "http://item.taobao.com/item.htm?id=10203414733&_u=2fhiru823e4";
		BuyerListService buyerListService = new BuyerListService();
		buyerListService.setHttpClient(httpClient);
		buyerListService
				.setItemPageUrl(itemPageUrl);
		
		SaleSumService saleSumService = new SaleSumService();
		saleSumService.setHttpClient(httpClient);
		saleSumService.setItemPageUrl(itemPageUrl);
		saleSumService.execute();
		log.info("Sale sum is: "+saleSumService.getSaleSum());	
		
		
		buyerListService.setBuyerSum(saleSumService.getSaleSum());
		buyerListService.execute();
	}

	public void testPostageService() {
		PostageService postageService = new PostageService();
		postageService.setHttpClient(httpClient);
		postageService
				.setItemPageUrl("http://wt.taobao.com/detail.htm?id=14010379194");
		postageService.execute();
	}
}
