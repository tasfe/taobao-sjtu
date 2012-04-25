package edu.fudan.autologin.pojos;

/**
 * 
 * 商品种类，目前主要抓取的为三类商品，分别是：洁面、笔记本和热门手机
 * @author JustinChen
 *
 */
public class CategoryInfo {

	private String categoryName;
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getCategoryHref() {
		return categoryHref;
	}
	public void setCategoryHref(String categoryHref) {
		this.categoryHref = categoryHref;
	}
	private String categoryHref;
}
