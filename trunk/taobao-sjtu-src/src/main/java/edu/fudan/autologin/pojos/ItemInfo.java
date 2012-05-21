package edu.fudan.autologin.pojos;

public class ItemInfo {

	private String sellerId;
	private String priceRange;
	private String freightPrice;
	private int saleNumIn30Days;
	private int reviews;
	private int viewCounter;
	public int getViewCounter() {
		return viewCounter;
	}
	public void setViewCounter(int viewCounter) {
		this.viewCounter = viewCounter;
	}
	private String payType;
	public String getItemDetailHref() {
		return itemDetailHref;
	}
	public void setItemDetailHref(String itemDetailHref) {
		this.itemDetailHref = itemDetailHref;
	}
	private String serviceType;
	private String spec;
	private String capacity;
	private String firstReviewDate;
	private String lastReviewDate;
	private String userRateHref;
	private String itemDetailHref;
	public String getUserRateHref() {
		return userRateHref;
	}
	public void setUserRateHref(String userRateHref) {
		this.userRateHref = userRateHref;
	}
	public String getItemReviewsHref() {
		return itemReviewsHref;
	}
	public void setItemReviewsHref(String itemReviewsHref) {
		this.itemReviewsHref = itemReviewsHref;
	}
	public String getItemBuyersHref() {
		return itemBuyersHref;
	}
	public void setItemBuyersHref(String itemBuyersHref) {
		this.itemBuyersHref = itemBuyersHref;
	}
	private String itemReviewsHref;
	private String itemBuyersHref;
	
	
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
	public String getPriceRange() {
		return priceRange;
	}
	public void setPriceRange(String priceRange) {
		this.priceRange = priceRange;
	}
	public String getFreightPrice() {
		return freightPrice;
	}
	public void setFreightPrice(String freightPrice) {
		this.freightPrice = freightPrice;
	}
	public int getSaleNumIn30Days() {
		return saleNumIn30Days;
	}
	public void setSaleNumIn30Days(int saleNumIn30Days) {
		this.saleNumIn30Days = saleNumIn30Days;
	}
	public int getReviews() {
		return reviews;
	}
	public void setReviews(int reviews) {
		this.reviews = reviews;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getSpec() {
		return spec;
	}
	public void setSpec(String spec) {
		this.spec = spec;
	}
	public String getCapacity() {
		return capacity;
	}
	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}
	public String getFirstReviewDate() {
		return firstReviewDate;
	}
	public void setFirstReviewDate(String firstReviewDate) {
		this.firstReviewDate = firstReviewDate;
	}
	public String getLastReviewDate() {
		return lastReviewDate;
	}
	public void setLastReviewDate(String lastReviewDate) {
		this.lastReviewDate = lastReviewDate;
	}
}
