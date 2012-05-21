package edu.fudan.autologin.pojos;

public class TopTenItemInfo {

	private int topRank;
	private String itemName;
	private String href;
	private String categoryName;
	
	private int weekSaleNum = 0;
	private int weekSellerNum = 0;
	
	public int getWeekSaleNum() {
		return weekSaleNum;
	}
	public void setWeekSaleNum(int weekSaleNum) {
		this.weekSaleNum = weekSaleNum;
	}
	public int getWeekSellerNum() {
		return weekSellerNum;
	}
	public void setWeekSellerNum(int weekSellerNum) {
		this.weekSellerNum = weekSellerNum;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public int getTopRank() {
		return topRank;
	}
	public void setTopRank(int topRank) {
		this.topRank = topRank;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
}
