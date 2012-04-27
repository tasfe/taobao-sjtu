package edu.fudan.autologin.pojos;

/**
 * 
 * 商品种类，目前主要抓取的为三类商品，分别是：洁面、笔记本和热门手机
 * @author JustinChen
 *
 */
public class CategoryInfo {

	@Override
	public boolean equals(Object obj) {
		if(this == obj){
			return true;
		}
		if(obj instanceof CategoryInfo){
			CategoryInfo c = (CategoryInfo)obj;
			return (c.getCategoryName().equals(this.getCategoryName()))&&(c.getCategoryHref().equals(this.getCategoryHref()));
		}else{
			return false;
		}
	}
	private String categoryName = "";
	private String categoryHref = "";
	
	
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
	
}
