package edu.fudan.autologin.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.pageparser.ItemDetailPageParser;
import edu.fudan.autologin.pageparser.SearchResultPageParser;
import edu.fudan.autologin.pageparser.TopTenPageParser;
import edu.fudan.autologin.pojos.BasePostInfo;
import edu.fudan.autologin.pojos.CategoryInfo;
import edu.fudan.autologin.pojos.TaobaoDataSet;
import edu.fudan.autologin.pojos.TopTenItemInfo;
import edu.fudan.autologin.utils.PostUtils;
import edu.fudan.autologin.utils.TaobaoUtils;
import edu.fudan.autologin.utils.XmlConfUtil;

public class MainTest {
	private static final Logger log = Logger
			.getLogger(MainTest.class);
	private HttpClient httpClient;
	
	
	public void initialize(){
		XmlConfUtil.openXml();
		ExcelUtil.prepare();
		
	}
	
	@Before
	public void setUp() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
			httpClient.getParams().setIntParameter("http.socket.timeout",300000);//毫秒 
			
//			// 设置代理对象 ip/代理名称,端口
//			HttpHost proxy = new HttpHost("10.141.251.173", 3128);
////			// HttpHost proxy = new HttpHost("proxy.fudan.edu.cn", 8080);
////			// // 实例化验证
////			CredentialsProvider credsProvider = new BasicCredentialsProvider();
////			// // 设定验证内容
////			// UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
////			// "10210240089", "fudan123");
////			// // 创建验证
//			// credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST,
////			// AuthScope.ANY_PORT), creds);
//			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
//					proxy);
////			((DefaultHttpClient) httpClient).setCredentialsProvider(credsProvider);
		}

		initialize();
//		PropertyConfigurator.configure("log4j.xml");
		DOMConfigurator.configure("log4j.xml");
//		log.setLevel(Level.DEBUG);

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
	
	@Test
	public void execute(){
//		autoLogin();
		doMyWork();
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
	
	public void doMyWork() {

		List<CategoryInfo> categoryInfos = new ArrayList<CategoryInfo>();

		CategoryInfo ci1 = new CategoryInfo();
		ci1.setCategoryName("洁面");
		ci1.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_MRHF&level3=50011977&up=false");
		categoryInfos.add(ci1);
//
//		CategoryInfo ci2 = new CategoryInfo();
//		ci2.setCategoryName("热门手机");
//		ci2.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_SJ&level3=TR_RXSJB&up=false");
//		categoryInfos.add(ci2);
//
//		CategoryInfo ci3 = new CategoryInfo();
//		ci3.setCategoryName("笔记本");
//		ci3.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_DNJXGPJ&level3=1101&up=false");
//		categoryInfos.add(ci3);

		//get top ten item info
		for (CategoryInfo c : categoryInfos) {
			TopTenPageParser topTenPageParser = new TopTenPageParser(
					httpClient, c.getCategoryHref());
			topTenPageParser.setCategoryInfo(c);
			topTenPageParser.execute();
		}

//		//get search result infoe 
//		for(TopTenItemInfo ttii : TaobaoDataSet.topTenItemInfos){
//			SearchResultPageParser searchResultPageParser = new SearchResultPageParser(httpClient, ttii.getHref());
//			searchResultPageParser.setTopTenItemInfo(ttii);
//			log.info("--------------------------------------------------------------------------------------------------------------");
//			log.info("Start to process (TopTenItem, Rank) : " + "("+ttii.getItemName() + ", "+ttii.getTopRank()+")");
//			searchResultPageParser.execute();
//		}
//		
//		//get item detail info
//		
//		//get seller rate 
//		
//		//get buyer info
//		TaobaoDataSet.printList();
	}
	
	
	public void testMyWork(){
		
	}
}
