package edu.fudan.autologin.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

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
import edu.fudan.autologin.pageparser.UserRatePageParser;
import edu.fudan.autologin.pojos.BasePostInfo;
import edu.fudan.autologin.pojos.BuyerInfo;
import edu.fudan.autologin.pojos.CategoryInfo;
import edu.fudan.autologin.pojos.ItemInfo;
import edu.fudan.autologin.pojos.SellerInSearchResult;
import edu.fudan.autologin.pojos.TaobaoDataSet;
import edu.fudan.autologin.pojos.TopTenItemInfo;
import edu.fudan.autologin.service.BuyerListService;
import edu.fudan.autologin.service.SaleSumService;
import edu.fudan.autologin.utils.PostUtils;
import edu.fudan.autologin.utils.TaobaoUtils;
import edu.fudan.autologin.utils.XmlConfUtil;

public class HomeClientTest {
	private static final Logger log = Logger.getLogger(MainTest.class);
	private HttpClient httpClient;

	public void initialize() {
		XmlConfUtil.openXml();
		// ExcelUtil.prepare();

	}

	@Before
	public void setUp() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
			httpClient.getParams().setIntParameter("http.socket.timeout",
					300000);// 毫秒

			// // 设置代理对象 ip/代理名称,端口
			// HttpHost proxy = new HttpHost("10.141.251.173", 3128);
			// // // HttpHost proxy = new HttpHost("proxy.fudan.edu.cn", 8080);
			// // // // 实例化验证
			// // CredentialsProvider credsProvider = new
			// BasicCredentialsProvider();
			// // // // 设定验证内容
			// // // UsernamePasswordCredentials creds = new
			// UsernamePasswordCredentials(
			// // // "10210240089", "fudan123");
			// // // // 创建验证
			// // credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST,
			// // // AuthScope.ANY_PORT), creds);
			// httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
			// proxy);
			// // ((DefaultHttpClient)
			// httpClient).setCredentialsProvider(credsProvider);
		}

		XmlConfUtil.openXml();
		// PropertyConfigurator.configure("log4j.xml");
		DOMConfigurator.configure("log4j.xml");
		// log.setLevel(Level.DEBUG);

	}

	@After
	public void tearDown() {
		log.info("--------------------------------------------------------------------------------------------------------------");
		log.info("COMPLETE ALL TASKS!");
		// ExcelUtil.closeWorkbook();
		httpClient.getConnectionManager().shutdown();
	}

	public void testSearchResult() {
		String url = "http://s.taobao.com/search?source=top_search&q=Lenovo%2F%C1%AA%CF%EB+A750&pspuid=142771461&v=product&p=detail&stp=top.toplist.tr_rxsjb.sellhot.image.1.0&ad_id=&am_id=&cm_id=&pm_id=";
		SearchResultPageParser searchResultPageParser = new SearchResultPageParser(
				httpClient, url);
		searchResultPageParser.execute();
	}

	public void testSellerInSearchResult() {
		ItemDetailPageParser itemDetailPageParser = new ItemDetailPageParser(
				httpClient, "http://item.taobao.com/item.htm?id=6042143146");
		itemDetailPageParser.execute();
	}

	/**
	 * 1. create work book; 2. fetching top ten page items and write top ten
	 * items to excel; 3. ;
	 * 
	 * 
	 * 
	 */

	// open TopTenSheet and get toptenItemInfo
	// user rate task
	public void task4() {
		int itemSum = 0;// the sum of the items in the search result sheet
		try {
			Workbook workbook = Workbook.getWorkbook(new File(XmlConfUtil
					.getValueByName("excelFilePath")));
			Sheet searchResultSheet = workbook.getSheet("SearchReaultSheet");
			itemSum = searchResultSheet.getRows();// getRows返回的是记录行数

			workbook.close();
		} catch (BiffException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {

		}
		log.info("Item sum is: " + itemSum);
		int cnt = 10;// 每次处理的sheet记录条数

		// itemSum = 5;
		int numOfProcess = itemSum % cnt == 0 ? itemSum / cnt : itemSum / cnt
				+ 1;
		log.info("Num of processes is: " + numOfProcess);
		int start = 0;
		int end = 0;
		for (int i = 1; i <= numOfProcess; ++i) {
			start = (i - 1) * cnt + 1;
			if (i == numOfProcess) {// 如果是最后一次处理时, end就直接为记录的总数
				end = itemSum;
			} else {
				end = start + cnt;
			}
			userRateProcess(start, end);
		}
	}

	@Test
	public void task() {
		// autoLogin();
		// task1();
		// task2();
//		task3();
		// task4();
		 autoLogin();
		 task5();
	}

	public void itemDetailProcess(int start, int end) {
		try {
			Workbook workbook = Workbook.getWorkbook(new File(XmlConfUtil
					.getValueByName("excelFilePath")));
			Sheet searchResultSheet = workbook.getSheet("SearchReaultSheet");

			WritableWorkbook wbook = Workbook.createWorkbook(new File(
					XmlConfUtil.getValueByName("excelFilePath")), workbook); // 根据book创建一个操作对象
			WritableSheet sh = wbook.getSheet("ItemDetailSheet");// 得到一个工作对象

			// sheet.getRows()返回该页的总行数
			for (int i = start; i <= end; i++) {
				HttpClient tmp = new DefaultHttpClient();
				ItemDetailPageParser itemDetailPageParser = new ItemDetailPageParser(
						tmp, searchResultSheet.getCell(18, i).getContents());
				itemDetailPageParser.setSellerId(searchResultSheet
						.getCell(0, i).getContents());
				log.info("--------------------------------------------------------------------------------------------------------------");
				log.info("This is the item process no: " + i);
				itemDetailPageParser.parsePage();
				itemDetailPageParser.writeExcel(sh);
				tmp.getConnectionManager().shutdown();

				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
					log.error(e.getMessage());
				}
			}

			wbook.write();
			try {
				wbook.close();
			} catch (WriteException e) {
				e.printStackTrace();
				log.error("Write excel exception. " + e.getMessage());
			}
			workbook.close();

		} catch (BiffException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {

		}
	}

	public void itemBuyerProcess(int start, int end) {
		try {

			Workbook workbook = Workbook.getWorkbook(new File(XmlConfUtil
					.getValueByName("excelFilePath")));
			Sheet searchResultSheet = workbook.getSheet("SearchReaultSheet");

			WritableWorkbook wbook = Workbook.createWorkbook(new File(
					XmlConfUtil.getValueByName("excelFilePath")), workbook); // 根据book创建一个操作对象
			WritableSheet sh = wbook.getSheet("BuyerInfoSheet");// 得到一个工作对象

			// sheet.getRows()返回该页的总行数
			for (int i = start; i <= end; i++) {
				HttpClient tmp = new DefaultHttpClient();
				String itemDetailHref = searchResultSheet.getCell(18, i)
						.getContents();
				String sellerId = searchResultSheet.getCell(0, i).getContents();

				BuyerListService buyerListService = new BuyerListService();
				buyerListService.setHttpClient(httpClient);
				buyerListService.setItemPageUrl(itemDetailHref);
				List<BuyerInfo> buyerInfos = new ArrayList<BuyerInfo>();
				buyerListService.setBuyerInfos(buyerInfos);
				buyerListService.setSellerId(sellerId);
				buyerListService.setSheet(sh);

				SaleSumService saleSumService = new SaleSumService();
				saleSumService.setItemPageUrl(itemDetailHref);
				saleSumService.execute();

				buyerListService.setBuyerSum(saleSumService.getSaleSum());
				buyerListService.execute();

				tmp.getConnectionManager().shutdown();
			}

			wbook.write();
			try {
				wbook.close();
			} catch (WriteException e) {
				e.printStackTrace();
				log.error("Write excel exception. " + e.getMessage());
			}
			workbook.close();

		} catch (BiffException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {

		}
	}

	public void userRateProcess(int start, int end) {
		try {
			Workbook workbook = Workbook.getWorkbook(new File(XmlConfUtil
					.getValueByName("excelFilePath")));
			Sheet searchResultSheet = workbook.getSheet("ItemDetailSheet");

			WritableWorkbook wbook = Workbook.createWorkbook(new File(
					XmlConfUtil.getValueByName("excelFilePath")), workbook); // 根据book创建一个操作对象
			WritableSheet sh = wbook.getSheet("UserRateSheet");// 得到一个工作对象

			// sheet.getRows()返回该页的总行数
			for (int i = start; i <= end; i++) {
				HttpClient tmp = new DefaultHttpClient();

				String userRateHref = searchResultSheet.getCell(13, i)
						.getContents();
				String sellerId = searchResultSheet.getCell(0, i).getContents();

				UserRatePageParser userRatePageParser = new UserRatePageParser(
						tmp, userRateHref);
				userRatePageParser.setSellerId(sellerId);
				userRatePageParser.parsePage();
				userRatePageParser.writeExcel(sh);
				log.info("--------------------------------------------------------------------------------------------------------------");
				log.info("This is the item process no: " + i);
				log.info("Seller id is: " + sellerId);
				log.info("User rate href is: " + userRateHref);
				tmp.getConnectionManager().shutdown();
			}

			wbook.write();
			try {
				wbook.close();
			} catch (WriteException e) {
				e.printStackTrace();
				log.error("Write excel exception. " + e.getMessage());
			}
			workbook.close();

		} catch (BiffException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {

		}
	}

	// write item detail records
	public void task3() {
		int itemSum = 0;// the sum of the items in the search result sheet
		try {
			Workbook workbook = Workbook.getWorkbook(new File(XmlConfUtil
					.getValueByName("excelFilePath")));
			Sheet searchResultSheet = workbook.getSheet("SearchReaultSheet");
			itemSum = searchResultSheet.getRows();// getRows返回的是记录行数

			workbook.close();
		} catch (BiffException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {

		}
		log.info("Item sum is: " + itemSum);
		itemDetailProcess(1001, 1200);
		// int cnt = 10;// 每次处理的sheet记录条数
		//
		// int numOfProcess = itemSum % cnt == 0 ? itemSum / cnt : itemSum / cnt
		// + 1;
		// // 普通数码相机专业单反相机数码摄像机
		// log.info("Num of processes is: " + numOfProcess);
		// int start = 0;
		// int end = 0;
		// for (int i = 1; i <= numOfProcess; ++i) {
		// start = (i - 1) * cnt + 1;
		// if (i == numOfProcess) {// 如果是最后一次处理时, end就直接为记录的总数
		// end = itemSum;
		// } else {
		// end = start + cnt;
		// }
		// itemDetailProcess(start, end);
		// }

	}

	// buyer info task
	public void task5() {
		int itemSum = 0;// the sum of the items in the search result sheet
		try {
			Workbook workbook = Workbook.getWorkbook(new File(XmlConfUtil
					.getValueByName("excelFilePath")));
			Sheet searchResultSheet = workbook.getSheet("SearchReaultSheet");
			itemSum = searchResultSheet.getRows();// getRows返回的是记录行数

			workbook.close();
		} catch (BiffException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {

		}
		log.info("Item sum is: " + itemSum);
		itemBuyerProcess(1, 1);
		// int cnt = 10;//每次处理的sheet记录条数
		//
		// int numOfProcess = itemSum % cnt == 0 ? itemSum/cnt : itemSum/cnt +
		// 1;
		// //普通数码相机专业单反相机数码摄像机
		// log.info("Num of processes is: "+numOfProcess);
		// int start = 0;
		// int end = 0;
		// for(int i = 1; i <= numOfProcess; ++i){
		// start = (i - 1)*cnt + 1;
		// if(i == numOfProcess){//如果是最后一次处理时, end就直接为记录的总数
		// end = itemSum;
		// }else{
		// end = start + cnt;
		// }
		// itemDetailProcess(start, end);
		// }

	}

	// search result process
	public void task2() {
		try {
			Workbook workbook = Workbook.getWorkbook(new File(XmlConfUtil
					.getValueByName("excelFilePath")));
			Sheet topTenSheet = workbook.getSheet("TopTenSheet");

			WritableWorkbook wbook = Workbook.createWorkbook(new File(
					XmlConfUtil.getValueByName("excelFilePath")), workbook); // 根据book创建一个操作对象
			WritableSheet sh = wbook.getSheet("SearchReaultSheet");// 得到一个工作对象

			// sheet.getRows()返回该页的总行数
			for (int i = 1; i < topTenSheet.getRows(); i++) {
				SearchResultPageParser searchResultPageParser = new SearchResultPageParser(
						httpClient, topTenSheet.getCell(5, i).getContents());
				searchResultPageParser.setCategoryName(topTenSheet
						.getCell(0, i).getContents());
				log.info("--------------------------------------------------------------------------------------------------------------");
				searchResultPageParser.parsePage();
				searchResultPageParser.writeExcel(sh);
			}

			wbook.write();
			try {
				wbook.close();
			} catch (WriteException e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
			workbook.close();

		} catch (BiffException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {

		}
	}

	// top ten task
	public void task1() {

		ExcelUtil.prepare();
		List<CategoryInfo> categoryInfos = new ArrayList<CategoryInfo>();

		CategoryInfo ci1 = new CategoryInfo();
		ci1.setCategoryName("洁面");
		ci1.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_MRHF&level3=50011977&up=false");
		categoryInfos.add(ci1);
		//
		CategoryInfo ci2 = new CategoryInfo();
		ci2.setCategoryName("热门手机");
		ci2.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_SJ&level3=TR_RXSJB&up=false");
		categoryInfos.add(ci2);

		CategoryInfo ci3 = new CategoryInfo();
		ci3.setCategoryName("普通数码相机");
		ci3.setCategoryHref("http://top1.search.taobao.com/level3.php?cat=TR_SYQC&level3=1403&up=false");
		categoryInfos.add(ci3);

		// get top ten item info
		for (CategoryInfo c : categoryInfos) {
			TopTenPageParser topTenPageParser = new TopTenPageParser(
					httpClient, c.getCategoryHref());
			topTenPageParser.setCategoryInfo(c);
			topTenPageParser.parsePage();
			topTenPageParser.writeExcel();
		}

		ExcelUtil.closeWorkbook();
	}

	public void execute() {
		// autoLogin();
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
		// CategoryInfo ci2 = new CategoryInfo();
		// ci2.setCategoryName("热门手机");
		// ci2.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_SJ&level3=TR_RXSJB&up=false");
		// categoryInfos.add(ci2);
		//
		// CategoryInfo ci3 = new CategoryInfo();
		// ci3.setCategoryName("笔记本");
		// ci3.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_DNJXGPJ&level3=1101&up=false");
		// categoryInfos.add(ci3);

		// get top ten item info
		for (CategoryInfo c : categoryInfos) {
			TopTenPageParser topTenPageParser = new TopTenPageParser(
					httpClient, c.getCategoryHref());
			topTenPageParser.setCategoryInfo(c);
			topTenPageParser.execute();
		}

		// //get search result infoe
		// for(TopTenItemInfo ttii : TaobaoDataSet.topTenItemInfos){
		// SearchResultPageParser searchResultPageParser = new
		// SearchResultPageParser(httpClient, ttii.getHref());
		// searchResultPageParser.setTopTenItemInfo(ttii);
		// log.info("--------------------------------------------------------------------------------------------------------------");
		// log.info("Start to process (TopTenItem, Rank) : " +
		// "("+ttii.getItemName() + ", "+ttii.getTopRank()+")");
		// searchResultPageParser.execute();
		// }
		//
		// //get item detail info
		//
		// //get seller rate
		//
		// //get buyer info
		// TaobaoDataSet.printList();
	}

	public void testMyWork() {

	}
}
