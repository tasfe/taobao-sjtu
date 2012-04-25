package edu.fudan.autologin.pojos;

public class TopTenItemInfo {

	private int topRank;
	private String itemName;
	private String href;
	private String categoryName;
	
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
