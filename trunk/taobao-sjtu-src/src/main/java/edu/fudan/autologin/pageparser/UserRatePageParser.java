package edu.fudan.autologin.pageparser;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.pojos.SellerRateInfo;
import edu.fudan.autologin.service.MonthService;

/**
 * 
 * 商家信用页面解析
 * 
 * @author JustinChen
 * 
 */
public class UserRatePageParser extends BasePageParser {

	private SellerRateInfo sellerRateInfo;
	private static final Logger log = Logger
			.getLogger(UserRatePageParser.class);

	private String sellerId;

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

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
		sellerRateInfo.setSellerRateHref(pageUrl);
	}

	@Override
	public void parsePage() {
		log.info("Start to parse page " + UserRatePageParser.class);
		log.info("User rate url is: " + this.getPageUrl());
		this.getPage(this.getPageUrl());
		Document doc = this.getDoc();
		log.info("sellerId: " + sellerId);
		sellerRateInfo.setSellerId(sellerId);

		if (doc.select("div.personal-info div.left-box").size() == 0) {

		} else {
			Element sellerInfoEl = doc.select("div.personal-info div.left-box")
					.get(0);
			Element sellerServiceEl = doc.select(
					"div.personal-info div.left-box").get(1);

			// seller name
			String sellerName = null;
			if (sellerInfoEl.select("div.bd div.title > a").size() == 0) {
				sellerName = "0";
			} else {
				sellerName = sellerInfoEl.select("div.bd div.title > a").get(0)
						.ownText();
			}
			log.info("sellerName: " + sellerName);
			sellerRateInfo.setSellerName(sellerName);

			// main sale
			String mainSale = null;
			String location = null;
			if (sellerInfoEl.select("div.bd ul").size() == 0) {

			} else {
				Element upBoxEl = sellerInfoEl.select("div.bd ul").get(0);
				mainSale = upBoxEl.select("li").get(0).select("a").text();

				// location
				location = upBoxEl.select("li").get(1).ownText();
				location = location.substring(location.indexOf("：") + 1).trim();
			}
			log.info("mainSale: " + mainSale);
			sellerRateInfo.setMainSale(mainSale);
			log.info("location: " + location);
			sellerRateInfo.setLocation(location);

			// create shop date
			String createShopDate = doc.getElementById("J_showShopStartDate")
					.val();
			log.info("createShopDate: " + createShopDate);
			sellerRateInfo.setCreateShopDate(createShopDate);

			// seller rate
			Element downBoxEl = sellerInfoEl.select("div.bd ul.sep").get(0);
			String sellerRate = downBoxEl.select("li").get(0).ownText();
			log.info("sellerRate: " + sellerRate.split("：")[1]);
			sellerRateInfo.setSellerRate(sellerRate.split("：")[1]);

			// buyer rate
			String buyerRate = downBoxEl.select("li").get(1).ownText();
			log.info("buyerRate: " + buyerRate.split("：")[1]);
			sellerRateInfo.setBuyerRate(buyerRate.split("：")[1]);

			boolean isConsumerPromise = false;
			boolean isSevenDayReturn = false;

			if (sellerServiceEl.select("div.bd div.desc ul").size() == 0) {

			} else {
				Element rateEl = sellerServiceEl.select("div.bd div.desc ul")
						.get(0);
				if (rateEl.select("li span.xiaofei").size() > 0) {
					isConsumerPromise = true;
				}

				if (rateEl.select("li span.seven").size() > 0) {
					isSevenDayReturn = true;
				}
			}
			log.info("isConsumerPromise: " + isConsumerPromise);
			log.info("isSevenDayReturn: " + isSevenDayReturn);
			sellerRateInfo.setConsumerPromise(isConsumerPromise);
			sellerRateInfo.setSevenDayReturn(isSevenDayReturn);
			// charge num
			String chargeNum;
			if (sellerServiceEl.select("div.bd div.charge span").size() == 0) {
				chargeNum = "卖家尚未提交保证金";

			} else {
				chargeNum = sellerServiceEl.select("div.bd div.charge span")
						.text();
			}
			log.info("chargeNum: " + chargeNum);
			sellerRateInfo.setChargeNum(chargeNum);
		}

		String matchScore;
		String serviceScore;
		String consignmentScore;
		if (doc.select("div#dynamic-rate div#sixmonth ul li").size() == 0) {
			matchScore = "null";
			serviceScore = "null";
			consignmentScore = "null";
		} else {
			Elements dynamicRateEls = doc
					.select("div#dynamic-rate div#sixmonth ul li");

			if (dynamicRateEls.get(0).select("div.item-scrib em.count").size() == 0) {
				matchScore = "null";
			} else {
				matchScore = dynamicRateEls.get(0)
						.select("div.item-scrib em.count").text();
			}
			if (dynamicRateEls.get(1).select("div.item-scrib em.count").size() == 0) {
				serviceScore = "null";
			} else {
				serviceScore = dynamicRateEls.get(1)
						.select("div.item-scrib em.count").text();
			}
			if (dynamicRateEls.get(2).select("div.item-scrib em.count").size() == 0) {
				consignmentScore = "null";
			} else {
				consignmentScore = dynamicRateEls.get(2)
						.select("div.item-scrib em.count").text();
			}
		}
		log.info("matchScore: " + matchScore);
		log.info("serviceScore: " + serviceScore);
		log.info("consignmentScore: " + consignmentScore);
		sellerRateInfo.setMatchScore(matchScore);
		sellerRateInfo.setServiceScore(serviceScore);
		sellerRateInfo.setConsignmentScore(consignmentScore);

		/* 店铺30天内服务情况需要自己构造url请求 */
		// Elements serviceInfoEls =
		// doc.select("div.seller-rate-info div#halfmonth div.left1 div.bg30 div.each");
		// Elements refundmentScoreEls = serviceInfoEls.get(0).select("span");
		//
		// String refundmentScore = refundmentScoreEls.get(1).ownText() +
		// refundmentScoreEls.get(2).ownText() +
		// refundmentScoreEls.get(3).ownText();
		// log.info("refundmentScore: " + refundmentScore);
		//
		// Elements refundmentRateScoreEls =
		// serviceInfoEls.get(1).select("span");
		// String refundmentRateScore = refundmentRateScoreEls.get(1).ownText()
		// + refundmentRateScoreEls.get(2).ownText() +
		// refundmentRateScoreEls.get(3).ownText();
		// log.info("refundmentRateScore: " + refundmentRateScore);
		//
		// Elements complaintScoreEls = serviceInfoEls.get(1).select("span");
		// String complaintScore = complaintScoreEls.get(1).ownText() +
		// complaintScoreEls.get(2).ownText() +
		// complaintScoreEls.get(3).ownText();
		// log.info("complaintScore: " + complaintScore);
		//
		// Elements punishmentScoreEls = serviceInfoEls.get(1).select("span");
		// String punishmentScore = punishmentScoreEls.get(1).ownText() +
		// punishmentScoreEls.get(2).ownText() +
		// punishmentScoreEls.get(3).ownText();
		// log.info("punishmentScore: " + punishmentScore);
		MonthService monthService = new MonthService();
		monthService.setHttpClient(this.getHttpClient());
		monthService.setUserRatePageUrl(this.getPageUrl());
		monthService.execute();
		sellerRateInfo.setRefundmentScore(monthService.getMonthServieEntities()
				.get(0).getLineString());
		sellerRateInfo.setRefundmentRateScore(monthService
				.getMonthServieEntities().get(1).getLineString());
		sellerRateInfo.setComplaintScore(monthService.getMonthServieEntities()
				.get(2).getLineString());
		sellerRateInfo.setPunishmentScore(monthService.getMonthServieEntities()
				.get(3).getLineString());

		if (doc.select("div.show-list#J_show_list ul li").size() == 0) {

		} else {
			Elements sellerRateList = doc
					.select("div.show-list#J_show_list ul li");
			Elements weekRateEls = sellerRateList.get(0).select(
					"table tbody tr");

			Element weekSumEl = weekRateEls.get(1);
			String weekSumRateOk = weekSumEl.select("td.rateok").text();
			String weekSumRateNormal = weekSumEl.select("td.ratenormal").text();
			String weekSumRateBad = weekSumEl.select("td.ratebad").text();
			sellerRateInfo.setWeekSumRateOk(weekSumRateOk);
			sellerRateInfo.setWeekSumRateNormal(weekSumRateNormal);
			sellerRateInfo.setWeekSumRateBad(weekSumRateBad);
			log.info("Week sum rate ok is: " + weekSumRateOk);
			log.info("Week sum rate normal is: " + weekSumRateNormal);
			log.info("Week sum rate bad is: " + weekSumRateBad);

			Elements weekMainEl = weekRateEls.get(2).select("td");
			String weekMainRateOk = weekMainEl.get(1).text();
			String weekMainRateNormal = weekMainEl.get(2).text();
			String weekMainRateBad = weekMainEl.get(3).text();
			sellerRateInfo.setWeekMainRateOk(weekMainRateOk);
			sellerRateInfo.setWeekMainRateNormal(weekMainRateNormal);
			sellerRateInfo.setWeekMainRateBad(weekMainRateBad);
			log.info("Week main rate ok is: " + weekMainRateOk);
			log.info("Week main rate normal is: " + weekMainRateNormal);
			log.info("Week main rate bad is: " + weekMainRateBad);

			Elements weekNotMainEl = weekRateEls.get(3).select("td");
			String weekNotMainRateOk = weekNotMainEl.get(1).text();
			String weekNotMainRateNormal = weekNotMainEl.get(2).text();
			String weekNotMainRateBad = weekNotMainEl.get(3).text();
			sellerRateInfo.setWeekNotmainRateOk(weekNotMainRateOk);
			sellerRateInfo.setWeekNotmainRateNormal(weekNotMainRateNormal);
			sellerRateInfo.setWeekNotmainRateBad(weekNotMainRateBad);
			log.info("Week not main rate ok is: " + weekNotMainRateOk);
			log.info("Week not main rate normal is: " + weekNotMainRateNormal);
			log.info("Week not main rate bad is: " + weekNotMainRateBad);

			Elements monthRateEls = sellerRateList.get(1).select(
					"table tbody tr");

			Element monthSumEl = monthRateEls.get(1);
			String monthSumRateOk = monthSumEl.select("td.rateok").text();
			String monthSumRateNormal = monthSumEl.select("td.ratenormal")
					.text();
			String monthSumRateBad = monthSumEl.select("td.ratebad").text();
			sellerRateInfo.setMonthSumRateBad(monthSumRateBad);
			sellerRateInfo.setMonthSumRateNormal(monthSumRateNormal);
			sellerRateInfo.setMonthSumRateOk(monthSumRateOk);
			log.info("Month sum rate ok is: " + monthSumRateOk);
			log.info("Month sum rate normal is: " + monthSumRateNormal);
			log.info("Month sum rate bad is: " + monthSumRateBad);

			Elements monthMainEl = monthRateEls.get(2).select("td");
			String monthMainRateOk = monthMainEl.get(1).text();
			String monthMainRateNormal = monthMainEl.get(2).text();
			String monthMainRateBad = monthMainEl.get(3).text();
			sellerRateInfo.setMonthMainRateBad(monthMainRateBad);
			sellerRateInfo.setMonthMainRateNormal(monthMainRateNormal);
			sellerRateInfo.setMonthMainRateOk(monthMainRateOk);
			log.info("Month main rate ok is: " + monthMainRateOk);
			log.info("Month main rate normal is: " + monthMainRateNormal);
			log.info("Month main rate bad is: " + monthMainRateBad);

			Elements monthNotMainEl = monthRateEls.get(3).select("td");
			String monthNotMainRateOk = monthNotMainEl.get(1).text();
			String monthNotMainRateNormal = monthNotMainEl.get(2).text();
			String monthNotMainRateBad = monthNotMainEl.get(3).text();
			sellerRateInfo.setMonthNotmainRateBad(monthNotMainRateBad);
			sellerRateInfo.setMonthNotmainRateNormal(monthNotMainRateNormal);
			sellerRateInfo.setMonthNotmainRateOk(monthNotMainRateOk);
			log.info("Month not main rate ok: " + monthNotMainRateOk);
			log.info("Month not main rate normal: " + monthNotMainRateNormal);
			log.info("Month not main rate bad: " + monthNotMainRateBad);

			Elements halfYearRateEls = sellerRateList.get(2).select(
					"table tbody tr");

			Element halfYearSumEl = halfYearRateEls.get(1);
			String halfYearSumRateOk = halfYearSumEl.select("td.rateok").text();
			String halfYearSumRateNormal = halfYearSumEl
					.select("td.ratenormal").text();
			String halfYearSumRateBad = halfYearSumEl.select("td.ratebad")
					.text();
			sellerRateInfo.setHalfYearSumRateBad(halfYearSumRateBad);
			sellerRateInfo.setHalfYearSumRateOk(halfYearSumRateOk);
			sellerRateInfo.setHalfYearSumRateNormal(halfYearSumRateNormal);
			log.info("Half year sum rate ok: " + halfYearSumRateOk);
			log.info("Half year sum rate normal: " + halfYearSumRateNormal);
			log.info("Half year sum rate bad: " + halfYearSumRateBad);

			Elements halfYearMainEl = halfYearRateEls.get(2).select("td");
			String halfYearMainRateOk = halfYearMainEl.get(1).text();
			String halfYearMainRateNormal = halfYearMainEl.get(2).text();
			String halfYearMainRateBad = halfYearMainEl.get(3).text();
			sellerRateInfo.setHalfYearMainRateBad(halfYearMainRateBad);
			sellerRateInfo.setHalfYearMainRateNormal(halfYearMainRateNormal);
			sellerRateInfo.setHalfYearMainRateOk(halfYearMainRateOk);
			log.info("Half year main rate ok: " + halfYearMainRateOk);
			log.info("Half year main rate normal: " + halfYearMainRateNormal);
			log.info("Half year main rate bad: " + halfYearMainRateBad);

			Elements halfYearNotMainEl = halfYearRateEls.get(3).select("td");
			String halfYearNotMainRateOk = halfYearNotMainEl.get(1).text();
			String halfYearNotMainRateNormal = halfYearNotMainEl.get(2).text();
			String halfYearNotMainRateBad = halfYearNotMainEl.get(3).text();
			sellerRateInfo.setHalfYearNotmainRateBad(halfYearNotMainRateBad);
			sellerRateInfo
					.setHalfYearNotmainRateNormal(halfYearNotMainRateNormal);
			sellerRateInfo.setHalfYearNotmainRateOk(halfYearNotMainRateOk);
			log.info("Half year not main rate ok: " + halfYearNotMainRateOk);
			log.info("Half year not main rate normal: "
					+ halfYearNotMainRateNormal);
			log.info("Half year not main rate bad: " + halfYearNotMainRateBad);

			Elements beforeHalfYearRateEls = sellerRateList.get(3).select(
					"table tbody tr");

			Element beforeHalfYearSumEl = beforeHalfYearRateEls.get(1);
			String beforeHalfYearSumRateOk = beforeHalfYearSumEl.select(
					"td.rateok").text();
			String beforeHalfYearSumRateNormal = beforeHalfYearSumEl.select(
					"td.ratenormal").text();
			String beforeHalfYearSumRateBad = beforeHalfYearSumEl.select(
					"td.ratebad").text();
			sellerRateInfo
					.setBeforeHalfYearSumRateBad(beforeHalfYearSumRateBad);
			sellerRateInfo
					.setBeforeHalfYearSumRateNormal(beforeHalfYearSumRateNormal);
			sellerRateInfo.setBeforeHalfYearSumRateOk(beforeHalfYearSumRateOk);
			log.info("Before half year sum rate ok: " + beforeHalfYearSumRateOk);
			log.info("Before half year sum rate normal: "
					+ beforeHalfYearSumRateNormal);
			log.info("Before half year sum rate bad: "
					+ beforeHalfYearSumRateBad);

			Elements sellerHistoryEls = doc
					.select("div.seller-rate-info div.frame div.list");
			String mainBusiness = sellerHistoryEls.get(1).ownText();
			mainBusiness = mainBusiness
					.substring(mainBusiness.indexOf("：") + 1);
			String mainBusinessPercentage = sellerHistoryEls.get(2).ownText();
			mainBusinessPercentage = mainBusinessPercentage
					.substring(mainBusinessPercentage.indexOf("：") + 1);
			sellerRateInfo.setMainBusiness(mainBusiness);
			sellerRateInfo.setMainBusinessPercentage(mainBusinessPercentage);
			log.info("Main biz is: " + mainBusiness);
			log.info("Main biz percentage is: " + mainBusinessPercentage);
		}

	}

}
