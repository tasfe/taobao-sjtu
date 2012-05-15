package edu.fudan.autologin.pojos;

public class SellerRateInfo {

	
	private String sellerRateHref;
	public String getSellerRateHref() {
		return sellerRateHref;
	}
	public void setSellerRateHref(String sellerRateHref) {
		this.sellerRateHref = sellerRateHref;
	}
	private String sellerId;
	private String sellerName;
	private String mainSale;
	private String location;
	private String createShopDate;
	private String sellerRate;
	private String buyerRate;
	private String chargeNum;//卖家当前保证金金额
	private boolean isConsumerPromise;
	private boolean isSevenDayReturn;
	private String matchScore;
	private String serviceScore;
	private String consignmentScore;
	private String refundmentScore;
	private String refundmentRateScore;
	private String complaintScore;
	private String punishmentScore;
	
	private String weekSumRateOk;
	private String weekMainRateOk;
	private String weekNotmainRateOk;
	private String weekSumRateNormal;
	private String weekMainRateNormal;
	private String weekNotmainRateNormal;
	private String weekSumRateBad;
	private String weekMainRateBad;
	private String weekNotmainRateBad;

	private String monthSumRateOk;
	private String monthMainRateOk;
	private String monthNotmainRateOk;
	private String monthSumRateNormal;
	private String monthMainRateNormal;
	private String monthNotmainRateNormal;
	private String monthSumRateBad;
	private String monthMainRateBad;
	private String monthNotmainRateBad;

	private String halfYearSumRateOk;
	private String halfYearMainRateOk;
	private String halfYearNotmainRateOk;
	private String halfYearSumRateNormal;
	private String halfYearMainRateNormal;
	private String halfYearNotmainRateNormal;
	private String halfYearSumRateBad;
	private String halfYearMainRateBad;
	private String halfYearNotmainRateBad;
	
	private String beforeHalfYearSumRateOk;
	private String beforeHalfYearSumRateNormal;
	private String beforeHalfYearSumRateBad;
	
	private String mainBusiness;
	private String mainBusinessPercentage;
	
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public String getMainSale() {
		return mainSale;
	}
	public void setMainSale(String mainSale) {
		this.mainSale = mainSale;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getCreateShopDate() {
		return createShopDate;
	}
	public void setCreateShopDate(String createShopDate) {
		this.createShopDate = createShopDate;
	}
	public String getBuyerRate() {
		return buyerRate;
	}
	public void setBuyerRate(String buyerRate) {
		this.buyerRate = buyerRate;
	}
	public String getChargeNum() {
		return chargeNum;
	}
	public void setChargeNum(String chargeNum) {
		this.chargeNum = chargeNum;
	}
	public boolean isSevenDayReturn() {
		return isSevenDayReturn;
	}
	public void setSevenDayReturn(boolean isSevenDayReturn) {
		this.isSevenDayReturn = isSevenDayReturn;
	}
	public boolean isConsumerPromise() {
		return isConsumerPromise;
	}
	public void setConsumerPromise(boolean isConsumerPromise) {
		this.isConsumerPromise = isConsumerPromise;
	}
	public String getMatchScore() {
		return matchScore;
	}
	public void setMatchScore(String matchScore) {
		this.matchScore = matchScore;
	}
	public String getServiceScore() {
		return serviceScore;
	}
	public void setServiceScore(String serviceScore) {
		this.serviceScore = serviceScore;
	}
	public String getConsignmentScore() {
		return consignmentScore;
	}
	public void setConsignmentScore(String consignmentScore) {
		this.consignmentScore = consignmentScore;
	}
	public String getRefundmentScore() {
		return refundmentScore;
	}
	public void setRefundmentScore(String refundmentScore) {
		this.refundmentScore = refundmentScore;
	}
	public String getRefundmentRateScore() {
		return refundmentRateScore;
	}
	public void setRefundmentRateScore(String refundmentRateScore) {
		this.refundmentRateScore = refundmentRateScore;
	}
	public String getComplaintScore() {
		return complaintScore;
	}
	public void setComplaintScore(String complaintScore) {
		this.complaintScore = complaintScore;
	}
	public String getPunishmentScore() {
		return punishmentScore;
	}
	public void setPunishmentScore(String punishmentScore) {
		this.punishmentScore = punishmentScore;
	}
	public String getWeekSumRateOk() {
		return weekSumRateOk;
	}
	public void setWeekSumRateOk(String weekSumRateOk) {
		this.weekSumRateOk = weekSumRateOk;
	}
	public String getWeekMainRateOk() {
		return weekMainRateOk;
	}
	public void setWeekMainRateOk(String weekMainRateOk) {
		this.weekMainRateOk = weekMainRateOk;
	}
	public String getWeekNotmainRateOk() {
		return weekNotmainRateOk;
	}
	public void setWeekNotmainRateOk(String weekNotmainRateOk) {
		this.weekNotmainRateOk = weekNotmainRateOk;
	}
	public String getWeekSumRateNormal() {
		return weekSumRateNormal;
	}
	public void setWeekSumRateNormal(String weekSumRateNormal) {
		this.weekSumRateNormal = weekSumRateNormal;
	}
	public String getWeekMainRateNormal() {
		return weekMainRateNormal;
	}
	public void setWeekMainRateNormal(String weekMainRateNormal) {
		this.weekMainRateNormal = weekMainRateNormal;
	}
	public String getWeekNotmainRateNormal() {
		return weekNotmainRateNormal;
	}
	public void setWeekNotmainRateNormal(String weekNotmainRateNormal) {
		this.weekNotmainRateNormal = weekNotmainRateNormal;
	}
	public String getWeekSumRateBad() {
		return weekSumRateBad;
	}
	public void setWeekSumRateBad(String weekSumRateBad) {
		this.weekSumRateBad = weekSumRateBad;
	}
	public String getWeekMainRateBad() {
		return weekMainRateBad;
	}
	public void setWeekMainRateBad(String weekMainRateBad) {
		this.weekMainRateBad = weekMainRateBad;
	}
	public String getWeekNotmainRateBad() {
		return weekNotmainRateBad;
	}
	public void setWeekNotmainRateBad(String weekNotmainRateBad) {
		this.weekNotmainRateBad = weekNotmainRateBad;
	}
	public String getMonthSumRateOk() {
		return monthSumRateOk;
	}
	public void setMonthSumRateOk(String monthSumRateOk) {
		this.monthSumRateOk = monthSumRateOk;
	}
	public String getMonthMainRateOk() {
		return monthMainRateOk;
	}
	public void setMonthMainRateOk(String monthMainRateOk) {
		this.monthMainRateOk = monthMainRateOk;
	}
	public String getMonthNotmainRateOk() {
		return monthNotmainRateOk;
	}
	public void setMonthNotmainRateOk(String monthNotmainRateOk) {
		this.monthNotmainRateOk = monthNotmainRateOk;
	}
	public String getMonthSumRateNormal() {
		return monthSumRateNormal;
	}
	public void setMonthSumRateNormal(String monthSumRateNormal) {
		this.monthSumRateNormal = monthSumRateNormal;
	}
	public String getMonthMainRateNormal() {
		return monthMainRateNormal;
	}
	public void setMonthMainRateNormal(String monthMainRateNormal) {
		this.monthMainRateNormal = monthMainRateNormal;
	}
	public String getMonthNotmainRateNormal() {
		return monthNotmainRateNormal;
	}
	public void setMonthNotmainRateNormal(String monthNotmainRateNormal) {
		this.monthNotmainRateNormal = monthNotmainRateNormal;
	}
	public String getMonthSumRateBad() {
		return monthSumRateBad;
	}
	public void setMonthSumRateBad(String monthSumRateBad) {
		this.monthSumRateBad = monthSumRateBad;
	}
	public String getMonthMainRateBad() {
		return monthMainRateBad;
	}
	public void setMonthMainRateBad(String monthMainRateBad) {
		this.monthMainRateBad = monthMainRateBad;
	}
	public String getMonthNotmainRateBad() {
		return monthNotmainRateBad;
	}
	public void setMonthNotmainRateBad(String monthNotmainRateBad) {
		this.monthNotmainRateBad = monthNotmainRateBad;
	}
	public String getHalfYearSumRateOk() {
		return halfYearSumRateOk;
	}
	public void setHalfYearSumRateOk(String halfYearSumRateOk) {
		this.halfYearSumRateOk = halfYearSumRateOk;
	}
	public String getHalfYearMainRateOk() {
		return halfYearMainRateOk;
	}
	public void setHalfYearMainRateOk(String halfYearMainRateOk) {
		this.halfYearMainRateOk = halfYearMainRateOk;
	}
	public String getHalfYearNotmainRateOk() {
		return halfYearNotmainRateOk;
	}
	public void setHalfYearNotmainRateOk(String halfYearNotmainRateOk) {
		this.halfYearNotmainRateOk = halfYearNotmainRateOk;
	}
	public String getHalfYearSumRateNormal() {
		return halfYearSumRateNormal;
	}
	public void setHalfYearSumRateNormal(String halfYearSumRateNormal) {
		this.halfYearSumRateNormal = halfYearSumRateNormal;
	}
	public String getHalfYearMainRateNormal() {
		return halfYearMainRateNormal;
	}
	public void setHalfYearMainRateNormal(String halfYearMainRateNormal) {
		this.halfYearMainRateNormal = halfYearMainRateNormal;
	}
	public String getHalfYearNotmainRateNormal() {
		return halfYearNotmainRateNormal;
	}
	public void setHalfYearNotmainRateNormal(String halfYearNotmainRateNormal) {
		this.halfYearNotmainRateNormal = halfYearNotmainRateNormal;
	}
	public String getHalfYearSumRateBad() {
		return halfYearSumRateBad;
	}
	public void setHalfYearSumRateBad(String halfYearSumRateBad) {
		this.halfYearSumRateBad = halfYearSumRateBad;
	}
	public String getHalfYearMainRateBad() {
		return halfYearMainRateBad;
	}
	public void setHalfYearMainRateBad(String halfYearMainRateBad) {
		this.halfYearMainRateBad = halfYearMainRateBad;
	}
	public String getHalfYearNotmainRateBad() {
		return halfYearNotmainRateBad;
	}
	public void setHalfYearNotmainRateBad(String halfYearNotmainRateBad) {
		this.halfYearNotmainRateBad = halfYearNotmainRateBad;
	}
	public String getBeforeHalfYearSumRateOk() {
		return beforeHalfYearSumRateOk;
	}
	public void setBeforeHalfYearSumRateOk(String beforeHalfYearSumRateOk) {
		this.beforeHalfYearSumRateOk = beforeHalfYearSumRateOk;
	}
	public String getBeforeHalfYearSumRateNormal() {
		return beforeHalfYearSumRateNormal;
	}
	public void setBeforeHalfYearSumRateNormal(String beforeHalfYearSumRateNormal) {
		this.beforeHalfYearSumRateNormal = beforeHalfYearSumRateNormal;
	}
	public String getBeforeHalfYearSumRateBad() {
		return beforeHalfYearSumRateBad;
	}
	public void setBeforeHalfYearSumRateBad(String beforeHalfYearSumRateBad) {
		this.beforeHalfYearSumRateBad = beforeHalfYearSumRateBad;
	}
	public String getSellerRate() {
		return sellerRate;
	}
	public void setSellerRate(String sellerRate) {
		this.sellerRate = sellerRate;
	}
	public String getMainBusiness() {
		return mainBusiness;
	}
	public void setMainBusiness(String mainBusiness) {
		this.mainBusiness = mainBusiness;
	}
	public String getMainBusinessPercentage() {
		return mainBusinessPercentage;
	}
	public void setMainBusinessPercentage(String mainBusinessPercentage) {
		this.mainBusinessPercentage = mainBusinessPercentage;
	}
}
