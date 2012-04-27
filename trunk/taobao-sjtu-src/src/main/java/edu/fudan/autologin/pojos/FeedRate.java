package edu.fudan.autologin.pojos;

import java.util.ArrayList;
import java.util.List;


//"watershed":100,
//"maxPage":167,
//"currentPageNum":166,
//"comments":[
//	{"auction":
//		{"title":"Apple/苹果 iPhone 4S 无锁版/港版 16G 32G 64G可装软件有未激活",
//		"aucNumId":13599064573,
//		"link":"",
//		"sku":"机身颜色:港版16G白色现货  手机套餐:官方标配"},
//	"content":"hao",
//	"append":null,
//	"rate":"好评！",
//	"tag":"",
//	"rateId":16249892723,
//	"award":"",
//	"reply":null,
//	"useful":0,
//	"date":"2012.03.08",
//	"user":{
//					"vip":"",
//					"rank":136,
//					"nick":"771665176_44",
//					"userId":410769781,
//					"displayRatePic":"b_red_4.gif",
//					"nickUrl":"http://wow.taobao.com/u/NDEwNzY5Nzgx/view/ta_taoshare_list.htm?redirect=fa",
//					"vipLevel":2,
//					"avatar":"http://img.taobaocdn.com/sns_logo/i1/T1VxqHXa4rXXb1upjX.jpg_40x40.jpg",
//					"anony":false,
//					"rankUrl":"http://rate.taobao.com/rate.htm?user_id=410769781&rater=1"}
//	},
public class FeedRate {

	public FeedRate(){
		comments = new ArrayList<FeedRateComment>();
	}
	private int watershed;
	public int getWatershed() {
		return watershed;
	}
	public void setWatershed(int watershed) {
		this.watershed = watershed;
	}
	public int getMaxPage() {
		return maxPage;
	}
	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}
	public int getCurrentPageNum() {
		return currentPageNum;
	}
	public void setCurrentPageNum(int currentPageNum) {
		this.currentPageNum = currentPageNum;
	}
	public List<FeedRateComment> getComments() {
		return comments;
	}
	public void setComments(List<FeedRateComment> comments) {
		this.comments = comments;
	}
	private int maxPage;
	private int currentPageNum;
	private List<FeedRateComment>	comments;
}
