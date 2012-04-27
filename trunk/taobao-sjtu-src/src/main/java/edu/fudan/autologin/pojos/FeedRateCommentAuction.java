package edu.fudan.autologin.pojos;
//{"auction":
//{"title":"Apple/苹果 iPhone 4S 无锁版/港版 16G 32G 64G可装软件有未激活",
//"aucNumId":13599064573,
//"link":"",
//"sku":"机身颜色:港版16G白色现货  手机套餐:官方标配"},
public class FeedRateCommentAuction {

	private String title;
	private long aucNumId;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public long getAucNumId() {
		return aucNumId;
	}
	public void setAucNumId(long aucNumId) {
		this.aucNumId = aucNumId;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	private String link;
	private String sku;
}
