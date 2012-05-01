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
import edu.fudan.autologin.service.PostageService;

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
	
	public void testReview(){
		ItemReviewService itemReviewService = new ItemReviewService();
		itemReviewService.setItemPageUrl("http://item.taobao.com/item.htm?id=13619790834");
		itemReviewService.setHttpClient(httpClient);
		itemReviewService.parseReviews();
		
	}
	
	@Test
	public void testPostageService(){
		PostageService postageService = new PostageService();
		postageService.setHttpClient(httpClient);
		postageService.setItemPageUrl("http://wt.taobao.com/detail.htm?id=14010379194");
		postageService.parsePostage();
	}
}
