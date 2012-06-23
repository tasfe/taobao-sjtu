package edu.fudan.autologin.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.write.WritableSheet;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import edu.fudan.autologin.constants.AddrIndicator;
import edu.fudan.autologin.constants.SystemConstant;
import edu.fudan.autologin.constants.UserType;
import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pageparser.ItaobaoPageParser;
import edu.fudan.autologin.pojos.BuyerInfo;
import edu.fudan.autologin.pojos.FeedRate;
import edu.fudan.autologin.pojos.FeedRateComment;
import edu.fudan.autologin.utils.GetWaitUtil;
import edu.fudan.autologin.utils.XmlConfUtil;

/**
 * Giving you a item page url, and you can get the reviews of the item.
 * 
 * @author JustinChen
 * 
 *         评论的格式如下: { "watershed":100, "maxPage":167, "currentPageNum":166,
 *         "comments":[ { "auction":{
 *         "title":"Apple/苹果 iPhone 4S 无锁版/港版 16G 32G 64G可装软件有未激活",
 *         "aucNumId":13599064573, "link":"", "sku":"机身颜色:港版16G白色现货  手机套餐:官方标配"
 *         }, "content":"hao", "append":null, "rate":"好评！", "tag":"",
 *         "rateId":16249892723, "award":"", "reply":null, "useful":0,
 *         "date":"2012.03.08", "user":{ "vip":"", "rank":136,
 *         "nick":"771665176_44", "userId":410769781,
 *         "displayRatePic":"b_red_4.gif", "nickUrl":
 *         "http://wow.taobao.com/u/NDEwNzY5Nzgx/view/ta_taoshare_list.htm?redirect=fa"
 *         , "vipLevel":2, "avatar":
 *         "http://img.taobaocdn.com/sns_logo/i1/T1VxqHXa4rXXb1upjX.jpg_40x40.jpg"
 *         , "anony":false,
 *         "rankUrl":"http://rate.taobao.com/rate.htm?user_id=410769781&rater=1"
 *         } },
 */
public class ItemReviewService {
	private static final Logger log = Logger.getLogger(ItemReviewService.class);
	
	private String sellerId ;
	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	// First of all, fetch all infos;
	// Secondly, disclose them
	List<BuyerInfo> buyerInfos = new ArrayList<BuyerInfo>();

	private int indicator;

	public int getIndicator() {
		return indicator;
	}

	public void setIndicator(int indicator) {
		this.indicator = indicator;
	}

	private int startIndex = 0;
	private int endIndex = 0;
	
	private String itemPageUrl;

	private FeedRate feedRate = new FeedRate();

	private int reviewSum = 0;

	private String reviewUrl;

	public List<String> getDateList() {
		return dateList;
	}

	private List<String> dateList = new ArrayList<String>();

	public int getReviewSum() {
		return reviewSum;
	}

	public void setReviewSum(int reviewSum) {
		this.reviewSum = reviewSum;
	}

	public String getItemPageUrl() {
		return itemPageUrl;
	}

	public void setItemPageUrl(String itemPageUrl) {
		this.itemPageUrl = itemPageUrl;
	}

	public FeedRate getFeedRate() {
		return feedRate;
	}

	public void setFeedRate(FeedRate feedRate) {
		this.feedRate = feedRate;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public int getAddrCounter(List<BuyerInfo> list, int start, int end) {
		int cnt = 0;
		for (int i = start; i <= end; ++i) {
			if (list.get(i).getBuyerAddress().equals("0") == false) {
				++cnt;
			}
		}
		return cnt;
	}


	private HttpClient httpClient = new DefaultHttpClient();

	public ItemReviewService() {

	}

	public class ItemReviewThread implements Runnable {

		private int pageNum;

		public ItemReviewThread(int page) {
			this.pageNum = page;
		}

		public void run() {
			GetMethod get = new GetMethod(httpClient, buildFeedRateAjaxUrl(
					getFeedRateListUrl(), pageNum));
			GetWaitUtil.get(get);
			// get.doGet();
			String jsonStr = getFeedRateListJsonString(get
					.getResponseAsString().trim());
			parseFeedRateListJson(jsonStr);
			get.shutDown();
		}

	}

	/**
	 * 
	 * 
	 * 1. get review url from page; 2. construct specified review url; 3. get
	 * specified review page; 4. parse json data;
	 */
	public void execute() {

		reviewUrl = getFeedRateListUrl();
		int pageSize = 20;

		if (reviewSum == 0) {

		} else {
			int pageSum = (reviewSum % pageSize == 0) ? reviewSum / pageSize
					: (reviewSum / pageSize + 1);
			log.info("Total page num is: " + pageSum);
			for (int pageNum = 1; pageNum <= pageSum; ++pageNum) {
				log.info("--------------------------------------------------------------------------------------");
				log.info("The review of Page NO is: " + pageNum);
				parseReview(pageNum);

			}
		}

		log.info("---------------------------------------");
		log.info("The sum of the reviews is: " + reviewSum);
		log.info("First feed rate date is: " + getFirstReviewDate());
		log.info("Last feed rate date is: " + getLastReviewDate());
		log.info("Review list size is: " + buyerInfos.size());
		this.httpClient.getConnectionManager().shutdown();

		
		parseList();
		writeBuyerInfo();
//		invokeDisclose();
//		printList();
//		log.info("Address indicator is: " + addrCounter);
//		calAddrNotNull();
	}

	private void writeBuyerInfo() {

		if(endIndex == 0){
			return;
		}
		for(int i = 0; i <= endIndex; ++i ){
			ExcelUtil.writeReviewsSheet(buyerInfos.get(i));
		}
	}

	public void parseReview(int pageNum) {
		
		String ajaxUrl = null;
		ajaxUrl = buildFeedRateAjaxUrl(reviewUrl, pageNum);
		
		if(ajaxUrl == null){
			return ;
		}
		
		GetMethod get = new GetMethod(httpClient, ajaxUrl);
		GetWaitUtil.get(get);
		String jsonStr = getFeedRateListJsonString(get.getResponseAsString()
				.trim());
		
		if(jsonStr == null){
			return ;
		}
		parseFeedRateListJson(jsonStr);
		get.shutDown();
	}

	public void calAddrNotNull() {
		int cnt = 0;
		for (BuyerInfo bi : buyerInfos) {
			if (bi.getBuyerAddress().equals("0") != true) {
				++cnt;
			}
		}

		log.info("addr cnt is: " + cnt);
	}

	public void parseList() {
		if(judgeItemTypeLT100() == true){
			indicator = AddrIndicator.THIRTY_LT_100;
		}else if(judgeItemTypeEQ100() == true){
			indicator = AddrIndicator.THIRTY_EQ_100;
		}else{
			indicator = AddrIndicator.THIRTY_GT_100;
		}
		log.info("Item type is: "+indicator);
		log.info("Start index is: "+startIndex);
		log.info("End index is: "+endIndex);
		log.info("The sum of addr not null is: "+getAddrCounter(buyerInfos, 0, buyerInfos.size()-1));
	}
	
	public boolean judgeItemTypeLT100(){
		int cnt = 0;
		cnt = getAddrCounter(buyerInfos, 0, buyerInfos.size() - 1);
		if (cnt < SystemConstant.ADDR_THRESHHOLD) {
			endIndex = buyerInfos.size() - 1;
			return true;
		}		
		return false;
	}
	public boolean judgeItemTypeEQ100(){
		int cnt = 0;// the counter of address is not null.
		Date firstDate = new Date();
		Date tmpDate = new Date();
		for (int i = 0; i < buyerInfos.size(); ++i) {
			if (buyerInfos.get(i).getBuyerAddress().equals("0") == false) {
				++cnt;
			}
			if (i == 0) {
				firstDate = string2Date(buyerInfos.get(i).getFeedDate());
			} else {

				tmpDate = string2Date(buyerInfos.get(i).getFeedDate());
				if (cnt >= SystemConstant.ADDR_THRESHHOLD) {
					endIndex = i;
					return true;
				}
			}
		}
		return false;
	}
	public boolean judgeItemTypeGT100(){
		int cnt = 0;
		int last = getThirtyLastDateIndex();
		cnt = getAddrCounter(buyerInfos, 0, last);
		
		if(cnt >= SystemConstant.ADDR_THRESHHOLD){
			
			indicator = AddrIndicator.THIRTY_GT_100;
			endIndex = last;
			return true;
		}
		return false;
	}

	public String getFeedRateListUrl() {
		String baseFeedRateListUrl = "";

		String tmpStr = "";
		GetMethod getMethod = new GetMethod(httpClient, itemPageUrl);
		GetWaitUtil.get(getMethod);
		tmpStr = getMethod.getResponseAsString();
		getMethod.shutDown();

		int base = tmpStr.indexOf("data-listApi=");
		int begin = tmpStr.indexOf("\"", base);
		int end = tmpStr.indexOf("\"", begin + 1);
		baseFeedRateListUrl = tmpStr.substring(begin + 1, end);
		log.info("Base feed url is: " + baseFeedRateListUrl);

		return baseFeedRateListUrl;

	}

	public String buildFeedRateAjaxUrl(String baseFeedRateListUrl,
			int currentPageNum) {
		String append = "&currentPageNum="
				+ currentPageNum
				+ "&rateType=&orderType=feedbackdate&showContent=1&attribute=&callback=jsonp_reviews_list";
		StringBuffer sb = new StringBuffer();
		sb.append(baseFeedRateListUrl);
		sb.append(append);

		return sb.toString();
	}

	/**
	 * 将从服务器端返回的字符串转化为json字符串
	 * 
	 * @return
	 */
	/*
	 * 
	 * 评论的格式如下: { "watershed":100, "maxPage":167, "currentPageNum":166,
	 * "comments":[ { "auction":{
	 * "title":"Apple/苹果 iPhone 4S 无锁版/港版 16G 32G 64G可装软件有未激活",
	 * "aucNumId":13599064573, "link":"", "sku":"机身颜色:港版16G白色现货  手机套餐:官方标配" },
	 * "content":"hao", "append":null, "rate":"好评！", "tag":"",
	 * "rateId":16249892723, "award":"", "reply":null, "useful":0,
	 * "date":"2012.03.08", "user":{ "vip":"", "rank":136,
	 * "nick":"771665176_44", "userId":410769781,
	 * "displayRatePic":"b_red_4.gif", "nickUrl":
	 * "http://wow.taobao.com/u/NDEwNzY5Nzgx/view/ta_taoshare_list.htm?redirect=fa"
	 * , "vipLevel":2, "avatar":
	 * "http://img.taobaocdn.com/sns_logo/i1/T1VxqHXa4rXXb1upjX.jpg_40x40.jpg",
	 * "anony":false,
	 * "rankUrl":"http://rate.taobao.com/rate.htm?user_id=410769781&rater=1"} },
	 */
	public String getFeedRateListJsonString(String str) {
		log.info("Plain json string of feed rate review from server is: " + str);
		if(str.contains("(")==false || str.contains(")")==false){
			return null;
		}
		
		int begin = str.indexOf("(");
		int end = str.lastIndexOf(")");
		log.info("Json string of feed rate review is: "
				+ str.substring(begin + 1, end));
		// return str.substring("jsonp_reviews_list(".length(), str.length() -
		// 1);//if the str has no jsonp_reviews_list, what should we can do?
		return str.substring(begin + 1, end);

		/*
		 * The substring begins at the specified beginIndex and extends to the
		 * character at the index endIndex - 1
		 */
	}

	public boolean parseFeedRateListJson(String str) {

		JSONObject jsonObj = JSONObject.fromObject(str);

		feedRate.setWatershed(jsonObj.getInt("watershed"));
		feedRate.setMaxPage(jsonObj.getInt("maxPage"));
		feedRate.setCurrentPageNum(jsonObj.getInt("currentPageNum"));

		if (jsonObj.get("comments").equals(null)) {
			log.info("There is no comment.");
			return false;
		} else {

			JSONArray comments = jsonObj.getJSONArray("comments");

			List list = (List) JSONSerializer.toJava(comments);

			for (Object o : list) {
				// feedRate.getComments().add((FeedRateComment) o);

				JSONObject j = JSONObject.fromObject(o);
//				FeedRateComment cmt = new FeedRateComment();
				String date = j.getString("date");
				// cmt.setDate(j.getString("date"));
				// cmt.setContent(j.getString("content"));
				// log.info("Comment NO is: " + i++);
				log.info("Date is: " + date);
				dateList.add(date);
				// log.info("Auction title is: "
				// + j.getJSONObject("auction").getString("title"));
				// log.info("Content is: " + j.getString("content"));

				JSONObject user = j.getJSONObject("user");
				String nick = user.getString("nick");
				String nickUrl = user.getString("nickUrl");

				log.info("nick is: " + nick);
				log.info("nick url is: " + nickUrl);

				BuyerInfo buyerInfo = new BuyerInfo();
				buyerInfo.setFeedDate(date);
				buyerInfo.setIndicator(UserType.ANONYMOUS);
				buyerInfo.setSellerId(sellerId);

				if (nickUrl != null && nickUrl != "") {
					ItaobaoPageParser parser = new ItaobaoPageParser(
							httpClient, buildItaobaoUrl(nickUrl));
					parser.setBuyerInfo(buyerInfo);
					parser.parsePage();
				} else {
					log.info("nick url is null.");
				}
				buyerInfos.add(buyerInfo);
			}
			return true;
		}
	}

	public Date string2Date(String dateStr) {
		Date date = new Date();

		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		try {
			date = df.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return date;

	}

	// invoke
	public void invokeDisclose() {
		int cnt = 0;// the counter of address is not null.
		Date firstDate = new Date();
		Date tmpDate = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		for (int i = 0; i < buyerInfos.size(); ++i) {
			if (buyerInfos.get(i).getBuyerAddress().equals("0") == false) {
				++cnt;
			}
			if (i == 0) {
				firstDate = string2Date(buyerInfos.get(i).getFeedDate());
			} else {

				tmpDate = string2Date(buyerInfos.get(i).getFeedDate());
				if ((firstDate.getTime() - tmpDate.getTime())
						/ (24 * 60 * 60 * 1000) >= 29
						&& cnt >= SystemConstant.ADDR_THRESHHOLD) {
					break;
				}
			}
		}
		log.info(buyerInfos.get(0).getFeedDate());
		log.info("first date is:" + df.format(firstDate));
		log.info("last date is: " + df.format(tmpDate));
	}


	public int getThirtyLastDateIndex() {
		int index = 0;
		Date lastDate = new Date();
		Date firstDate = getThirtyFirstDate();

		for (int i = 0; i < buyerInfos.size(); ++i) {
			lastDate = string2Date(buyerInfos.get(i).getFeedDate());
			if ((firstDate.getTime() - lastDate.getTime())
					/ (24 * 60 * 60 * 1000) >= 29) {
				index = i;
				break;
			}
		}

		return index;
	}

	public Date getThirtyFirstDate() {
		Date firstDate = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");

		try {
			firstDate = df.parse(buyerInfos.get(0).getFeedDate());
		} catch (ParseException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		return firstDate;
	}

	public void removeElement(List list, int start, int end) {
		log.info("Start is: " + start);
		log.info("End is: " + end);

		log.info("List size is: " + list.size());
		for (int i = start; i < end; ++i) {
			log.info("Remove element no: " + i);
			list.remove(i);
		}
	}

	public String buildItaobaoUrl(String url) {
		StringBuilder targetUrl = new StringBuilder();
		// http://i.taobao.com/u/OTE4MDUxOTU=/front.htm
		String prefix = "http://i.taobao.com/u/";
		String appender = "/front.htm";
		String id = null;

		// http://wow.taobao.com/u/NDkwMDQyNTc4/view/ta_taoshare_list.htm?redirect=fa
		id = url.substring(url.indexOf("/u/") + "/u/".length(),
				url.indexOf("/view/"));

		targetUrl.append(prefix);
		targetUrl.append(id);
		targetUrl.append(appender);

		log.info("Target url is: " + targetUrl.toString());
		return targetUrl.toString();
	}

	public String getFirstReviewDate() {
		// when there is no reviews, set first review date to 0
		if (dateList.size() == 0) {
			return "0";
		}
		return dateList.get(0);
	}

	public String getLastReviewDate() {
		if (dateList.size() == 0) {
			return "0";
		}
		return dateList.get(dateList.size() - 1);
	}

}
