package edu.fudan.autologin.pageparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.pojos.SellerRateInfo;
/**
 * 
 * 商家信用页面解析
 * @author JustinChen
 *
 */
public class UserRatePageParser extends BasePageParser {

	private SellerRateInfo sellerRateInfo;
	private static final Logger log = Logger.getLogger(UserRatePageParser.class);

	@Override
	public void doNext() {
		super.doNext();
	}

	@Override
	public void writeExcel() {
		ExcelUtil.writeUserRateSheet(sellerRateInfo);
	}

	public UserRatePageParser(HttpClient httpClient, String pageUrl) {
		super(httpClient, pageUrl);
		sellerRateInfo = new SellerRateInfo();
	}
	
	@Override
	public void parsePage() {
		log.info("Start to parse page " + UserRatePageParser.class);
		this.getPage(this.getPageUrl());
		Document doc = this.getDoc();
		String sellerId = "Get From Parent";
		log.info("sellerId: " + sellerId);
		Element sellerInfoEl = doc.select("div.personal-info div.left-box").get(0);
		Element sellerServiceEl = doc.select("div.personal-info div.left-box").get(1);
		
		String sellerName = sellerInfoEl.select("div.bd div.title > a").get(0).ownText();
		log.info("sellerName: " + sellerName);
		Element upBoxEl = sellerInfoEl.select("div.bd ul").get(0);
		String mainSale = upBoxEl.select("li").get(0).select("a").text();
		log.info("mainSale: " + mainSale);
		String location = upBoxEl.select("li").get(1).ownText();
		location = location.substring(location.indexOf("：")).trim();
		log.info("location: " + location);
		String createShopDate = doc.getElementById("J_showShopStartDate").val();
		log.info("createShopDate: " + createShopDate);
		Element downBoxEl = sellerInfoEl.select("div.bd ul.sep").get(0);
		String sellerRate = downBoxEl.select("li").get(0).ownText();
		log.info("sellerRate: " + sellerRate);
		String buyerRate = downBoxEl.select("li").get(1).ownText();
		log.info("buyerRate: " + buyerRate);
		
		boolean isConsumerPromise = false;
		boolean isSevenDayReturn = false;
		Element rateEl = sellerServiceEl.select("div.bd div.desc ul").get(0);
		if(rateEl.select("li span.xiaofei").size() > 0){
			isConsumerPromise = true;
		}
		log.info("isConsumerPromise: " + isConsumerPromise);
		if(rateEl.select("li span.seven").size() > 0){
			isSevenDayReturn = true;
		}
		log.info("isSevenDayReturn: " + isSevenDayReturn);
		String chargeNum = sellerServiceEl.select("div.bd div.charge span").text();
		log.info("chargeNum: " + chargeNum);
		
		Elements dynamicRateEls = doc.select("div#dynamic-rate div#sixmonth ul li");
		String matchScore = dynamicRateEls.get(0).select("div.item-scrib em.count").text();
		String serviceScore = dynamicRateEls.get(1).select("div.item-scrib em.count").text();
		String consignmentScore = dynamicRateEls.get(2).select("div.item-scrib em.count").text();
		log.info("matchScore: " + matchScore);
		log.info("serviceScore: " + serviceScore);
		log.info("consignmentScore: " + consignmentScore);
		
		/*店铺30天内服务情况需要自己构造url请求*/
//		Elements serviceInfoEls = doc.select("div.seller-rate-info div#halfmonth div.left1 div.bg30 div.each");
//		Elements refundmentScoreEls = serviceInfoEls.get(0).select("span");
//		
//		String refundmentScore = refundmentScoreEls.get(1).ownText() + refundmentScoreEls.get(2).ownText() + refundmentScoreEls.get(3).ownText();
//		log.info("refundmentScore: " + refundmentScore);
//		
//		Elements refundmentRateScoreEls = serviceInfoEls.get(1).select("span");
//		String refundmentRateScore = refundmentRateScoreEls.get(1).ownText() + refundmentRateScoreEls.get(2).ownText() + refundmentRateScoreEls.get(3).ownText();
//		log.info("refundmentRateScore: " + refundmentRateScore);
//		
//		Elements complaintScoreEls = serviceInfoEls.get(1).select("span");
//		String complaintScore = complaintScoreEls.get(1).ownText() + complaintScoreEls.get(2).ownText() + complaintScoreEls.get(3).ownText();
//		log.info("complaintScore: " + complaintScore);
//		
//		Elements punishmentScoreEls = serviceInfoEls.get(1).select("span");
//		String punishmentScore = punishmentScoreEls.get(1).ownText() + punishmentScoreEls.get(2).ownText() + punishmentScoreEls.get(3).ownText();
//		log.info("punishmentScore: " + punishmentScore);
		
		Elements sellerRateList = doc.select("div.show-list#J_show_list ul li");
		Elements weekRateEls = sellerRateList.get(0).select("table tbody tr");
		
		Element weekSumEl = weekRateEls.get(1);
		String weekSumRateOk = weekSumEl.select("td.rateok").text();
		String weekSumRateNormal = weekSumEl.select("td.ratenormal").text();
		String weekSumRateBad = weekSumEl.select("td.ratebad").text();

		Element weekMainEl = weekRateEls.get(2);
		String weekMainRateOk = weekMainEl.select("td.rateok").text();
		String weekMainRateNormal = weekMainEl.select("td.ratenormal").text();
		String weekMainRateBad = weekMainEl.select("td.ratebad").text();

		Element weekNotMainEl = weekRateEls.get(3);
		String weekNotMainRateOk = weekNotMainEl.select("td.rateok").text();
		String weekNotMainRateNormal = weekNotMainEl.select("td.ratenormal").text();
		String weekNotMainRateBad = weekNotMainEl.select("td.ratebad").text();
		
		
		Elements monthRateEls = sellerRateList.get(1).select("table tbody tr");
		
		Element monthSumEl = monthRateEls.get(1);
		String monthSumRateOk = monthSumEl.select("td.rateok").text();
		String monthSumRateNormal = monthSumEl.select("td.ratenormal").text();
		String monthSumRateBad = monthSumEl.select("td.ratebad").text();
		
		Element monthMainEl = monthRateEls.get(2);
		String monthMainRateOk = monthMainEl.select("td.rateok").text();
		String monthMainRateNormal = monthMainEl.select("td.ratenormal").text();
		String monthMainRateBad = monthMainEl.select("td.ratebad").text();
		
		Element monthNotMainEl = monthRateEls.get(3);
		String monthNotMainRateOk = monthNotMainEl.select("td.rateok").text();
		String monthNotMainRateNormal = monthNotMainEl.select("td.ratenormal").text();
		String monthNotMainRateBad = monthNotMainEl.select("td.ratebad").text();
		
		Elements halfYearRateEls = sellerRateList.get(2).select("table tbody tr");
	
		Element halfYearSumEl =halfYearRateEls.get(1);
		String halfYearSumRateOk = halfYearSumEl.select("td.rateok").text();
		String halfYearSumRateNormal = halfYearSumEl.select("td.ratenormal").text();
		String halfYearSumRateBad = halfYearSumEl.select("td.ratebad").text();
		
		Element halfYearMainEl = halfYearRateEls.get(2);
		String halfYearMainRateOk = halfYearMainEl.select("td.rateok").text();
		String halfYearMainRateNormal =halfYearMainEl.select("td.ratenormal").text();
		String halfYearMainRateBad = halfYearMainEl.select("td.ratebad").text();
		
		Element halfYearNotMainEl = halfYearRateEls.get(3);
		String halfYearNotMainRateOk = halfYearNotMainEl.select("td.rateok").text();
		String halfYearNotMainRateNormal = halfYearNotMainEl.select("td.ratenormal").text();
		String halfYearNotMainRateBad = halfYearNotMainEl.select("td.ratebad").text();
		
		Elements beforeHalfYearRateEls = sellerRateList.get(3).select("table tbody tr");
		Element beforeHalfYearSumEl = beforeHalfYearRateEls.get(1);
		String beforeHalfYearSumRateOk = beforeHalfYearSumEl.select("td.rateok").text();
		String beforeHalfYearSumRateNormal = beforeHalfYearSumEl.select("td.ratenormal").text();
		String beforeHalfYearSumRateBad = beforeHalfYearSumEl.select("td.ratebad").text();
		
		Elements sellerHistoryEls = doc.select("div.seller-rate-info div.frame div.list");
		String mainBusiness = sellerHistoryEls.get(1).ownText();
		mainBusiness = mainBusiness.substring(mainBusiness.indexOf("：") + 1);
		String mainBusinessPercentage = sellerHistoryEls.get(2).ownText();
		mainBusinessPercentage = mainBusinessPercentage.substring(mainBusinessPercentage.indexOf("：") + 1);
	}
	

}
