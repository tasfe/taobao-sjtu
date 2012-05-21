package edu.fudan.autologin.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.main.AutoLogin;
import edu.fudan.autologin.pageparser.ItaobaoPageParser;
import edu.fudan.autologin.pageparser.ItemBuyerParser;
import edu.fudan.autologin.pageparser.ItemDetailPageParser;
import edu.fudan.autologin.pageparser.SearchResultPageParser;
import edu.fudan.autologin.pageparser.UserRatePageParser;
import edu.fudan.autologin.pojos.BasePostInfo;
import edu.fudan.autologin.service.BuyerListService;
import edu.fudan.autologin.service.ItemReviewService;
import edu.fudan.autologin.service.ItemViewCountService;
import edu.fudan.autologin.service.MonthService;
import edu.fudan.autologin.service.PostageService;
import edu.fudan.autologin.service.ReviewSumService;
import edu.fudan.autologin.service.SaleSumService;
import edu.fudan.autologin.service.TaobaoDsDataService;
import edu.fudan.autologin.utils.PostUtils;
import edu.fudan.autologin.utils.PrintUtils;
import edu.fudan.autologin.utils.TaobaoUtils;
import edu.fudan.autologin.utils.XmlConfUtil;

public class ItemDetailPageParserTest {

	private static final Logger log = Logger
			.getLogger(ItemDetailPageParserTest.class);
	private HttpClient httpClient;
	
	@Test
	public void execute(){
		testItemDetailPage();
	}

	@Before
	public void setUp() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
			httpClient.getParams().setIntParameter("http.socket.timeout",300000);//毫秒 
		}

		initialize();
		DOMConfigurator.configure("log4j.xml");

	}

	public void initialize(){
		XmlConfUtil.openXml();
		ExcelUtil.prepare();
		
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

	
	
	public void testTaobaoDsDataService(){
		
		TaobaoDsDataService taobaoDsDataService = new TaobaoDsDataService();
		taobaoDsDataService.setHttpClient(httpClient);
		taobaoDsDataService.execute();
		log.info(taobaoDsDataService.getData("430400"));
	}
	

	
	public void testItemCounter(){
		ItemViewCountService itemViewCountService = new ItemViewCountService();
		itemViewCountService.setItemDetailPage("http://item.taobao.com/item.htm?id=13738559526");
		itemViewCountService.execute();
	}
	


	public void testItemDetailPage(){
//		autoLogin();
		String pageUrl = "http://item.taobao.com/item.htm?id=15876676995";
		ItemDetailPageParser itemDetailPageParser = new ItemDetailPageParser(httpClient, pageUrl);
		itemDetailPageParser.execute();
	}
	public void testItaobaoPageParser(){
	
		String pageUrl = "http://i.taobao.com/u/NjAwNjgzNDQ=/front/frontInfoGather.htm?viewList=";
		ItaobaoPageParser itaobaoPageParser = new ItaobaoPageParser(httpClient, pageUrl);
		itaobaoPageParser.execute();
	}
	public void testTaobaoDsData(){
		  String url = "http://i.taobao.com/u/NjAwNjgzNDQ=/front/frontInfoGather.htm?viewList="; 
		   GetMethod get = new GetMethod(httpClient, url);
		   get.doGet();
		   get.printResponse();
		   get.shutDown();
	}
	public void testNoItem(){
		
		String pageUrl = "http://item.taobao.com/item.htm?id=14614561158";
		
		GetMethod get =new GetMethod(httpClient, pageUrl);
		get.doGet();
		get.printResponse();
		get.shutDown();
	}
	
	
	
	public void testItemBuyers(){
		autoLogin();
		
//		
//		GetMethod get = new GetMethod(httpClient, "http://item.taobao.com/j/vdata.htm?skil=false&cb=i_r");
//		get.doGet();
//		get.printResponse();
//		get.shutDown();
		
//		PrintUtils.printCookies(((AbstractHttpClient) httpClient).getCookieStore().getCookies());
		ItemBuyerParser parser = new ItemBuyerParser();
		parser.setHttpClient(httpClient);
		parser.execute();
////		parser.doNext();
	}
	
	
	public void testMonthService() {
		MonthService monthService = new MonthService();
		monthService.setHttpClient(httpClient);
		monthService
				.setUserRatePageUrl("http://rate.taobao.com/user-rate-47ee874a0427f2bc729bb869d78ff8c6.htm?spm=2013.1.1000126.5");
		monthService.execute();
	}
	
	public void testReviewSum(){
		ReviewSumService reviewSumService = new ReviewSumService();
		reviewSumService.setHttpClient(httpClient);
		reviewSumService.setItemPageUrl("http://item.taobao.com/item.htm?id=14660281195");
		reviewSumService.execute();
	}
	
	
	

	public void testSearchResutlPageParser(){
		String pageUrl = "http://s.taobao.com/search?source=top_search&q=Samsung%2F%C8%FD%D0%C7+Galaxy+Note&pspuid=139682134&v=product&p=detail&stp=top.toplist.tr_rxsjb.sellhot.image.2.0&ad_id=&am_id=&cm_id=&pm_id=";
		SearchResultPageParser searchResultPageParser = new SearchResultPageParser(httpClient, pageUrl);
		searchResultPageParser.parsePage();
	}
	public void testUserRate(){
		
		String pageUrl = "http://rate.taobao.com/user-rate-88e99355c367380075597ef904df8ff3.htm";
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
		saleSumService.setItemPageUrl("http://item.taobao.com/item.htm?id=14660281195");
		saleSumService.execute();
		log.info("Sale sum is: "+saleSumService.getSaleSum());	
	}
	
	
	
	

	
	
	
	public void testBuyerListService() {
		autoLogin();
		String itemPageUrl = "http://item.taobao.com/item.htm?id=16016896217";
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
	
	public void autoLogin() {

		// 设置基本的post信息
		BasePostInfo basePostInfo = new BasePostInfo();
		basePostInfo
				.setPostPageUrl("https://login.taobao.com/member/login.jhtml");
		basePostInfo.setPostFormId("J_StaticForm");
		basePostInfo
				.setPostFormUrl("https://login.taobao.com/member/login.jhtml");

		// 设置提交表单相关信息
		List<NameValuePair> formFieldsNvps = new ArrayList<NameValuePair>();
		formFieldsNvps.add(new BasicNameValuePair("TPL_username", "gschen163"));
		formFieldsNvps.add(new BasicNameValuePair("TPL_password",
				"3DES_2_000000000000000000000000000000_D1CE4894D9F2334C"));
		formFieldsNvps.add(new BasicNameValuePair("TPL_checkcode", TaobaoUtils
				.getCheckCode(httpClient)));
		formFieldsNvps.add(new BasicNameValuePair("need_check_code", "true"));

		PostUtils.doPost(httpClient, basePostInfo, formFieldsNvps);
	}
}
