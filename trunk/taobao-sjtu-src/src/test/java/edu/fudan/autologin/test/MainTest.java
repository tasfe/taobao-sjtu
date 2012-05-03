package edu.fudan.autologin.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.pageparser.ItemDetailPageParser;
import edu.fudan.autologin.pageparser.SearchResultPageParser;
import edu.fudan.autologin.pageparser.TopTenPageParser;
import edu.fudan.autologin.pojos.CategoryInfo;

public class MainTest {
	private static final Logger log = Logger
			.getLogger(MainTest.class);
	private HttpClient httpClient;
	
	
	public void initialize(){
		ExcelUtil.prepare();
	}
	
	@Before
	public void setUp() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
		}

		initialize();
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
	
	
	public void testSearchResult(){
		
		String url = "http://s.taobao.com/search?source=top_search&q=Lenovo%2F%C1%AA%CF%EB+A750&pspuid=142771461&v=product&p=detail&stp=top.toplist.tr_rxsjb.sellhot.image.1.0&ad_id=&am_id=&cm_id=&pm_id=";
		SearchResultPageParser searchResultPageParser = new SearchResultPageParser(httpClient, url);
		searchResultPageParser.execute();
	}
	public void testSellerInSearchResult(){
		ItemDetailPageParser itemDetailPageParser = new ItemDetailPageParser(httpClient, "http://item.taobao.com/item.htm?id=6042143146");
		itemDetailPageParser.execute();
	}
	public void execute(){
		doMyWork();
	}
	
	@Test
	public void doMyWork() {

		List<CategoryInfo> categoryInfos = new ArrayList<CategoryInfo>();

//		CategoryInfo ci1 = new CategoryInfo();
//		ci1.setCategoryName("洁面");
//		ci1.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_MRHF&level3=50011977&up=false");
//		categoryInfos.add(ci1);
//
//		CategoryInfo ci2 = new CategoryInfo();
//		ci2.setCategoryName("热门手机");
//		ci2.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_SJ&level3=TR_RXSJB&up=false");
//		categoryInfos.add(ci2);

		CategoryInfo ci3 = new CategoryInfo();
		ci3.setCategoryName("笔记本");
		ci3.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_DNJXGPJ&level3=1101&up=false");
		categoryInfos.add(ci3);

		for (CategoryInfo c : categoryInfos) {
			TopTenPageParser topTenPageParser = new TopTenPageParser(
					httpClient, c.getCategoryHref());
			topTenPageParser.setCategoryInfo(c);
			topTenPageParser.execute();
		}
	}
}
