package edu.fudan.autologin.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.fudan.autologin.constants.SheetNames;
import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.pageparser.ItemDetailPageParser;
import edu.fudan.autologin.pageparser.SearchResultPageParser;
import edu.fudan.autologin.pageparser.TopTenPageParser;
import edu.fudan.autologin.pageparser.UserRatePageParser;
import edu.fudan.autologin.pojos.BasePostInfo;
import edu.fudan.autologin.pojos.BuyerInfo;
import edu.fudan.autologin.pojos.CategoryInfo;
import edu.fudan.autologin.service.BuyerListService;
import edu.fudan.autologin.service.DetailCommonService;
import edu.fudan.autologin.service.ItemReviewService;
import edu.fudan.autologin.service.ReviewSumService;
import edu.fudan.autologin.service.SaleSumService;
import edu.fudan.autologin.utils.PostUtils;
import edu.fudan.autologin.utils.TaobaoUtils;
import edu.fudan.autologin.utils.XmlConfUtil;

public class LabClientTest {
	private static final Logger log = Logger.getLogger(LabClientTest.class);
	private HttpClient httpClient;

	public void initialize() {
		XmlConfUtil.openXml();
		// ExcelUtil.prepare();

	}

	@Before
	public void setUp() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
		}

		XmlConfUtil.openXml();
		DOMConfigurator.configure("log4j.xml");
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

	public void appendImpress() {
		Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(new File(XmlConfUtil
					.getValueByName("excelFilePath")));
		} catch (BiffException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		WritableWorkbook wbook = null;
		try {
			wbook = Workbook.createWorkbook(
					new File(XmlConfUtil.getValueByName("excelFilePath")),
					workbook);
		} catch (IOException e1) {
			e1.printStackTrace();
		} // 根据book创建一个操作对象
		WritableSheet sh = wbook.getSheet("ItemDetailSheet");// 得到一个工作对象

		int itemSum = sh.getRows() - 1;
		// sheet.getRows()返回该页的总行数
		for (int i = 3; i <= itemSum; i++) {
			log.info("This is process no: " + i);
			String id = sh.getCell(0, i).getContents();
			String pageUrl = "http://item.taobao.com/item.htm?id=" + id;

			DetailCommonService service = new DetailCommonService();
			service.setPageUrl(pageUrl);
			service.execute();

			String impress = service.getImpress();

			try {
				sh.addCell(new Label(14, i, impress));
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}

		}

		try {
			wbook.write();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			try {
				wbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (WriteException e) {
			e.printStackTrace();
			log.error("Write excel exception. " + e.getMessage());
		}
		workbook.close();

	}

	public void task6() {

		String prefix = "http://item.taobao.com/item.htm?id=";
		String itemId = null;
		int itemSum = 0;

		Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(new File(XmlConfUtil
					.getValueByName("excelFilePath")));
			Sheet searchResultSheet = workbook.getSheet("SearchReaultSheet");

			itemSum = searchResultSheet.getRows() - 1;

			for (int i = 1; i <= itemSum; ++i) {
				StringBuilder pageUrl = new StringBuilder();
				pageUrl.append(prefix);
				itemId = searchResultSheet.getCell(0, i).getContents();
				pageUrl.append(itemId);

				ReviewSumService reviewSumService = new ReviewSumService();
				reviewSumService.setItemPageUrl(pageUrl.toString());
				reviewSumService.execute();

				ItemReviewService itemReviewService = new ItemReviewService();
				itemReviewService.setItemPageUrl(pageUrl.toString());
				itemReviewService.setReviewSum(reviewSumService.getReviewSum());
				itemReviewService.execute();

				pageUrl = null;
			}
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		workbook.close();

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
			itemSum = searchResultSheet.getRows() - 1;// getRows返回的是记录行数

			workbook.close();
		} catch (BiffException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {

		}
		// itemSum - 1为总的记录总数
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
				end = start + cnt - 1;
			}
			// userRateProcess(start, end);
		}
	}

	@Test
	public void task() {
//		 topTenProcess();
//		 searchResultProcess();
		 itemDetailProcess();
		 userRateProcess();
	}

	public void itemDetailProcess() {
		ExcelUtil.openWorkbook();
		Sheet searchResultSheet = ExcelUtil.getSheetMap().get(
				SheetNames.SEARCH_RESULT_SHEET);

		log.info("Total item is: " + (searchResultSheet.getRows() - 1));

//		for (int i = 1; i < searchResultSheet.getRows(); i++) {
		for (int i = 1; i < 2; i++) {
			HttpClient tmp = new DefaultHttpClient();
			ItemDetailPageParser itemDetailPageParser = new ItemDetailPageParser(
					tmp, searchResultSheet.getCell(18, i).getContents());
			itemDetailPageParser.setSellerId(searchResultSheet.getCell(0, i)
					.getContents());
			log.info("--------------------------------------------------------------------------------------------------------------");
			log.info("This is the item process no: " + i);
			itemDetailPageParser.parsePage();
			itemDetailPageParser.writeExcel();
			tmp.getConnectionManager().shutdown();
		}
		ExcelUtil.closeWBook();
	}

	public void itemReviews() {
		String prefix = "http://item.taobao.com/item.htm?id=";
		try {
			Workbook workbook = Workbook.getWorkbook(new File(XmlConfUtil
					.getValueByName("excelFilePath")));
			Sheet searchResultSheet = workbook.getSheet("SearchReaultSheet");
			int itemSum = searchResultSheet.getRows() - 1;

			WritableWorkbook wbook = Workbook.createWorkbook(new File(
					XmlConfUtil.getValueByName("excelFilePath")), workbook); // 根据book创建一个操作对象
			WritableSheet sh = wbook.getSheet("ReviewsSheet");// 得到一个工作对象

			// sheet.getRows()返回该页的总行数
			for (int i = 1; i <= 500; i++) {
				log.info("--------------------------------------------------------------------------------------------------------------");
				log.info("This is the item process no: " + i);
				String id = searchResultSheet.getCell(0, i).getContents();
				StringBuilder pageUrl = new StringBuilder();
				pageUrl.append(prefix);
				pageUrl.append(id);

				HttpClient tmp = new DefaultHttpClient();

				ReviewSumService reviewSumService = new ReviewSumService();
				reviewSumService.setItemPageUrl(pageUrl.toString());
				reviewSumService.execute();

				ItemReviewService itemReviewService = new ItemReviewService();
				itemReviewService.setItemPageUrl(pageUrl.toString());
				itemReviewService.setReviewSum(reviewSumService.getReviewSum());
				itemReviewService.setSheet(sh);
				itemReviewService.execute();

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

	public void itemBuyerProcess(int start, int end) {
		try {

			Workbook workbook = Workbook.getWorkbook(new File(XmlConfUtil
					.getValueByName("excelFilePath")));
			Sheet itemDetailSheet = workbook.getSheet("ItemDetailSheet");

			WritableWorkbook wbook = Workbook.createWorkbook(new File(
					XmlConfUtil.getValueByName("excelFilePath")), workbook); // 根据book创建一个操作对象
			WritableSheet sh = wbook.getSheet("BuyerInfoSheet2");// 得到一个工作对象

			// sheet.getRows()返回该页的总行数
			for (int i = start; i <= end; i++) {

				log.info("--------------------------------------------------------------------------------------------------------------");
				log.info("This is the item process no: " + i);

				HttpClient tmp = new DefaultHttpClient();
				String itemDetailHref = itemDetailSheet.getCell(12, i)
						.getContents();
				String sellerId = itemDetailSheet.getCell(0, i).getContents();
				int saleSum = Integer.parseInt(itemDetailSheet.getCell(3, i)
						.getContents());

				BuyerListService buyerListService = new BuyerListService();
				buyerListService.setHttpClient(httpClient);
				buyerListService.setItemPageUrl(itemDetailHref);
				List<BuyerInfo> buyerInfos = new ArrayList<BuyerInfo>();
				buyerListService.setBuyerInfos(buyerInfos);
				buyerListService.setSellerId(sellerId);
				buyerListService.setSheet(sh);

				// SaleSumService saleSumService = new SaleSumService();
				// saleSumService.setItemPageUrl(itemDetailHref);
				// saleSumService.execute();

				buyerListService.setBuyerSum(saleSum);
				buyerListService.execute();

				tmp.getConnectionManager().shutdown();

				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
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

	public void userRateProcess() {
		ExcelUtil.openWorkbook();
		Sheet itemDetailSheet = ExcelUtil.getSheetMap().get(
				SheetNames.ITEM_DETAIL_SHEET);

		log.info("Start to process user rate.");
		for (int i = 1; i < itemDetailSheet.getRows(); i++) {
			HttpClient tmp = new DefaultHttpClient();
			String userRateHref = itemDetailSheet.getCell(14, i).getContents();
			String sellerId = itemDetailSheet.getCell(0, i).getContents();
			log.info("--------------------------------------------------------------------------------------------------------------");
			log.info("This is the item process no: " + i);
			log.info("Seller id is: " + sellerId);
			log.info("User rate href is: " + userRateHref);
			UserRatePageParser userRatePageParser = new UserRatePageParser(tmp,
					userRateHref);
			userRatePageParser.setSellerId(sellerId);
			userRatePageParser.parsePage();
			userRatePageParser.writeExcel();
			tmp.getConnectionManager().shutdown();
		}
		ExcelUtil.closeWBook();
	}

	// write item detail records
	public void task3() {
		int itemSum = 0;// the sum of the items in the search result sheet
		try {
			Workbook workbook = Workbook.getWorkbook(new File(XmlConfUtil
					.getValueByName("excelFilePath")));
			Sheet searchResultSheet = workbook.getSheet("SearchReaultSheet");
			itemSum = searchResultSheet.getRows() - 1;// getRows返回的是记录行数

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

		int numOfProcess = itemSum % cnt == 0 ? itemSum / cnt : itemSum / cnt
				+ 1;
		// 普通数码相机专业单反相机数码摄像机
		log.info("Num of processes is: " + numOfProcess);
		int start = 0;
		int end = 0;
		for (int i = 1; i <= numOfProcess; ++i) {
			start = (i - 1) * cnt + 1;
			if (i == numOfProcess) {// 如果是最后一次处理时, end就直接为记录的总数
				end = itemSum;
			} else {
				end = start + cnt - 1;
			}
			// itemDetailProcess(start, end);
		}
	}

	// buyer info task
	public void task5() {
		int itemSum = 0;// the sum of the items in the search result sheet
		try {
			Workbook workbook = Workbook.getWorkbook(new File(XmlConfUtil
					.getValueByName("excelFilePath")));
			Sheet searchResultSheet = workbook.getSheet("SearchReaultSheet");
			itemSum = searchResultSheet.getRows() - 1;// getRows返回的是记录行数

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
		itemBuyerProcess(2251, 2300);
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
		// end = start + cnt - 1;
		// }
		// itemDetailProcess(start, end);
		// }

	}

	// search result process
	public void searchResultProcess() {

		ExcelUtil.openWorkbook();
		Sheet topTenSheet = ExcelUtil.getSheetMap().get(
				SheetNames.TOP_TEN_SHEET);

		// sheet.getRows()返回该页的总行数
		for (int i = 1; i < topTenSheet.getRows(); i++) {
			SearchResultPageParser searchResultPageParser = new SearchResultPageParser(
					httpClient, topTenSheet.getCell(5, i).getContents());
			searchResultPageParser.setCategoryName(topTenSheet.getCell(0, i)
					.getContents());
			log.info("--------------------------------------------------------------------------------------------------------------");
			searchResultPageParser.parsePage();
			searchResultPageParser.writeExcel();
		}

		ExcelUtil.closeWBook();
	}

	// top ten task
	public void topTenProcess() {
		ExcelUtil.prepare();
		List<CategoryInfo> categoryInfos = new ArrayList<CategoryInfo>();

		CategoryInfo ci1 = new CategoryInfo();
		ci1.setCategoryName("洁面");
		ci1.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_MRHF&level3=50011977&up=false");
		categoryInfos.add(ci1);

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
