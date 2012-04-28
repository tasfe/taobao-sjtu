package edu.fudan.autologin.pojos;

//{"auction":
//{"title":"Apple/苹果 iPhone 4S 无锁版/港版 16G 32G 64G可装软件有未激活",
//"aucNumId":13599064573,
//"link":"",
//"sku":"机身颜色:港版16G白色现货  手机套餐:官方标配"},
//"content":"hao",
//"append":null,
//"rate":"好评！",
//"tag":"",
//"rateId":16249892723,
//"award":"",
//"reply":null,
//"useful":0,
//"date":"2012.03.08",
//"user":{
//			"vip":"",
//			"rank":136,
//			"nick":"771665176_44",
//			"userId":410769781,
//			"displayRatePic":"b_red_4.gif",
//			"nickUrl":"http://wow.taobao.com/u/NDEwNzY5Nzgx/view/ta_taoshare_list.htm?redirect=fa",
//			"vipLevel":2,
//			"avatar":"http://img.taobaocdn.com/sns_logo/i1/T1VxqHXa4rXXb1upjX.jpg_40x40.jpg",
//			"anony":false,
//			"rankUrl":"http://rate.taobao.com/rate.htm?user_id=410769781&rater=1"}
//},
public class FeedRateComment {

	public FeedRateCommentAuction getAuction() {
		return auction;
	}
	public void setAuction(FeedRateCommentAuction auction) {
		this.auction = auction;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAppend() {
		return append;
	}
	public void setAppend(String append) {
		this.append = append;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getRateId() {
		return rateId;
	}
	public void setRateId(String rateId) {
		this.rateId = rateId;
	}
	public String getAward() {
		return award;
	}
	public void setAward(String award) {
		this.award = award;
	}
//	public String getReply() {
//		return reply;
//	}
//	public void setReply(String reply) {
//		this.reply = reply;
//	}
	public int getUserful() {
		return userful;
	}
	public void setUserful(int userful) {
		this.userful = userful;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public FeedRateUserAttr getUser() {
		return user;
	}
	public void setUser(FeedRateUserAttr user) {
		this.user = user;
	}
	private FeedRateCommentAuction auction;
	private String content;
	private String append;
	private String rate;
	private String tag;
	private String rateId;
	private String award;
//	private String reply = null;
	private int userful;
	private String date;
	private FeedRateUserAttr user;
	
}
