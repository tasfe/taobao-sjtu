package edu.fudan.autologin.pageparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.DataLine;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.constants.SexEnum;
import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.main.impl.TaobaoAutoLogin;
import edu.fudan.autologin.pojos.BuyerInfo;
import edu.fudan.autologin.pojos.FeedRateComment;
import edu.fudan.autologin.pojos.ItemInfo;
import edu.fudan.autologin.pojos.Postage;
import edu.fudan.autologin.service.BuyerListService;
import edu.fudan.autologin.service.PostageService;

public class ItemDetailPageParser extends BasePageParser {
	private static final Logger log = Logger
			.getLogger(ItemDetailPageParser.class);
	private ItemInfo itemInfo;
	private String postageUrl;
	private String saleNumUrl;
	private String reviewUrl;
	private List<BuyerInfo> buyerInfos = new ArrayList<BuyerInfo>();
	
	
	private List<String> dateList = new ArrayList<String>();

	public String getPostageUrl() {
		return postageUrl;
	}

	public void setPostageUrl(String postageUrl) {
		this.postageUrl = postageUrl;
	}

	public String getSaleNumUrl() {
		return saleNumUrl;
	}

	public void setSaleNumUrl(String saleNumUrl) {
		this.saleNumUrl = saleNumUrl;
	}

	public String getReviewUrl() {
		return reviewUrl;
	}

	public void setReviewUrl(String reviewUrl) {
		this.reviewUrl = reviewUrl;
	}

	// constructor
	public ItemDetailPageParser(HttpClient httpClient, String pageUrl) {
		super(httpClient, pageUrl);
		itemInfo = new ItemInfo();
	}

	@Override
	public void writeExcel() {
		ExcelUtil.writeItemDetailSheet(itemInfo);
	}

	@Override
	public void parsePage() {
		log.info("Start to parse page " + ItemDetailPageParser.class);
		this.getPage(this.getPageUrl());
		Document doc = this.getDoc();
		preprocessDoc();
		Element itemPro = doc.select("div.tb-property").get(0);
		String sellerId = "inherited from parent call";
		log.info("sellerId: " + sellerId);
		String priceRange = itemPro.getElementById("J_StrPrice").ownText();
		log.info("priceRange: " + priceRange);
		String freightPrice = "";
		String location = "";
		String freight = "";

		PostageService postageService = new PostageService();
		postageService.setHttpClient(this.getHttpClient());
		postageService.setItemPageUrl(this.getPageUrl());
		
		postageService.parsePostage();
		
//		Postage postage = postageService.getPostage();
		location = postageService.getPostage().getLocation();
		freight = postageService.getPostage().getCarriage();
		freightPrice = location + " : " + freight;
		log.info("freightPrice: " + freightPrice);

		int saleNumIn30Days = getSaleNum();
		//buyerSum = saleNumIn30Days;
		log.info("saleNumIn30Days: " + saleNumIn30Days);

		int reviews = getReviewsNum();
		log.info("reviews: " + reviews);

		String itemType = "";
		Element element = itemPro.select("li.tb-item-type em").get(0);
		itemType = element.ownText();
		log.info("itemType: " + itemType);

		String payType = "";
		Elements links = itemPro.select("dl.tb-paymethods a");
		for (Element link : links) {
			if (!"#".equals(link.attr("href"))) {
				payType += link.ownText() + ", ";
			}
		}
		payType = payType.substring(0, payType.lastIndexOf(","));
		log.info("payType: " + payType);
		String serviceType = "";
		links = itemPro.select("dl.tb-featured-services a");
		for (Element link : links) {
			serviceType += link.ownText();
		}
		log.info("serviceType: " + serviceType);

		String spec = "";
		String capacity = "";
		Elements elements = doc.select("div#attributes ul.attributes-list li");
		spec = elements.get(1).ownText();
		capacity = elements.get(2).ownText();
		
		//parseShowBuyerListDoc();//解析买家列表
		BuyerListService buyerListService = new BuyerListService();
		buyerListService.setHttpClient(this.getHttpClient());
		buyerListService.setItemPageUrl(this.getPageUrl());
		buyerListService.parseShowBuyerListDoc();
		buyerInfos = buyerListService.getBuyerInfos();
		
		parseReviews();
		log.info("First feed date is: "+this.getFirstReviewDate());
		log.info("Last feed date is: " + this.getLastReviewDate());
		
	}

	/* 对页面进行预处理，获取动态请求的url */
	public void preprocessDoc() {
		GetMethod getMethod = new GetMethod(this.getHttpClient(),
				this.getPageUrl());
		getMethod.doGet();
		String docString = getMethod.getResponseAsString();
		int base, begin, end;
		if (docString.contains("getShippingInfo")) {
			base = docString.indexOf("getShippingInfo");
			begin = docString.indexOf("\"", base);
			end = docString.indexOf("\"", begin + 1);
			String postageUrl = docString.substring(begin + 1, end).trim();
			log.info("postageUrl: " + postageUrl);
			setPostageUrl(postageUrl);
		} else {
			log.info("get postage url error, not found");
		}
		if (docString.contains("getDealQuantity")) {
			base = docString.indexOf("getDealQuantity");
			begin = docString.indexOf("\"", base);
			end = docString.indexOf("\"", begin + 1);
			String saleNumUrl = docString.substring(begin + 1, end).trim();
			log.info("saleNumUrl: " + saleNumUrl);
			setSaleNumUrl(saleNumUrl);
		} else {
			log.info("get saleNum url error, not found");
		}

		if (docString.contains("counterApi")) {
			base = docString.indexOf("counterApi");
			begin = docString.indexOf("\"", base);
			end = docString.indexOf("\"", begin + 1);
			String reviewsUrl = docString.substring(begin + 1, end).trim();
			log.info("reviewsUrl: " + reviewsUrl);
			setReviewUrl(reviewsUrl);
		} else {
			log.info("get reviews url error, not found");
		}
	}

	@Override
	public void doNext() {

		//assert (itemInfo.getItemBuyersHref() != null);
		
		log.info("do next------------------------------------------------");
//		for(BuyerInfo buyerInfo: buyerInfos){
//			ItemBuyersPageParser itemBuyersPageParser = new ItemBuyersPageParser(this.getHttpClient(), buyerInfo.getHref());
//			itemBuyersPageParser.setBuyInfo(buyerInfo);
//			itemBuyersPageParser.execute();
//		}
//
//		assert (itemInfo.getUserRateHref() != null);
//		UserRatePageParser userRatePageParser = new UserRatePageParser(
//				this.getHttpClient(), itemInfo.getUserRateHref());
//		userRatePageParser.execute();
	}

	

	/* 从服务器端获取json数据，并解析成jsonObject，由于服务器端返回的是js，需要先获取纯json String */
	public JSONObject getJsonFromServer(String referer, String requestUrl) {

		JSONObject jsonObj = null;
		List<NameValuePair> headers = new ArrayList<NameValuePair>();
		NameValuePair nvp = new BasicNameValuePair("referer", referer);
		headers.add(nvp);
		GetMethod getRequest = new GetMethod(this.getHttpClient(), requestUrl);
		getRequest.doGet(headers);
		String responseStr = getRequest.getResponseAsString();
		responseStr = responseStr.substring(responseStr.indexOf("{"),
				responseStr.lastIndexOf("}") + 1); // get the plain json string
		jsonObj = JSONObject.fromObject(responseStr);
		getRequest.shutDown();
		return jsonObj;
	}

	public int getSaleNum() {
		int saleNum = 0;
		String referer = getPageUrl();
		String requestUrl = getSaleNumUrl();
		JSONObject saleNumObj = getJsonFromServer(referer, requestUrl);
		log.info("Sale json string from server is: "+saleNumObj.toString());
		/**
		 * {"quantity":{"quanity":0,"interval":30}}
		 */
		if(saleNumObj.toString().trim().length() == "{\"quantity\":{\"quanity\":0,\"interval\":30}}".length()){
			JSONObject quantityObj = saleNumObj.getJSONObject("quantity");
			int quanity = quantityObj.getInt("quanity");
			int interval = quantityObj.getInt("interval");
		
			log.debug("Sale num string  is: "+quanity);
			saleNum = quanity;
			
		}else{
			saleNum = saleNumObj.getInt("quanity");
		}
		return saleNum;
	}

	public int getReviewsNum() {
		int reviewsNum = 0;
		String referer = getPageUrl();
		String requestUrl = getReviewUrl();
		GetMethod getRequest = new GetMethod(this.getHttpClient(), requestUrl);
		getRequest.doGet();
		String responseStr = getRequest.getResponseAsString();
		//System.out.println("reviews:");
		//System.out.println(responseStr);

		return reviewsNum;
	}

	

	public void parseReviews() {
		int pageNum = 0;
		while (true) {
			log.info("--------------------------------------------------------------------------------------");
			log.info("This review of page num is: " + (++pageNum));
			GetMethod get = new GetMethod(this.getHttpClient(), constructFeedRateListUrl(
					getFeedRateListUrl(), pageNum));
			get.doGet();
			String jsonStr = getFeedRateListJsonString(get
					.getResponseAsString().trim());
			if (parseFeedRateListJson(jsonStr) == false) {
				break;
			}

			get.shutDown();
		}
	}

	public String getFeedRateListUrl() {
		String itemDetailUrl = this.getPageUrl();

		String baseFeedRateListUrl = "";

		String tmpStr = "";
		GetMethod getMethod = new GetMethod(this.getHttpClient(), itemDetailUrl);
		getMethod.doGet();
		tmpStr = getMethod.getResponseAsString();
		getMethod.shutDown();

		int base = tmpStr.indexOf("data-listApi=");
		int begin = tmpStr.indexOf("\"", base);
		int end = tmpStr.indexOf("\"", begin + 1);
		baseFeedRateListUrl = tmpStr.substring(begin + 1, end);
		log.info("Base feed url is: " + baseFeedRateListUrl);

		return baseFeedRateListUrl;

	}

	public String constructFeedRateListUrl(String baseFeedRateListUrl,
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
	public String getFeedRateListJsonString(String str) {
		return str.substring("jsonp_reviews_list(".length(), str.length() - 1);
	}

	
	/***
	 * 
	 * 解析从服务器端返回的json数据
	 * @param str
	 * @return
	 */
	public boolean parseFeedRateListJson(String str) {

		JSONObject jsonObj = JSONObject.fromObject(str);

		if (jsonObj.get("comments").equals(null)) {
			log.info("There is no comment.");
			return false;
		} else {

			JSONArray comments = jsonObj.getJSONArray("comments");

			List list = (List) JSONSerializer.toJava(comments);

			List<FeedRateComment> cmts = new ArrayList<FeedRateComment>();
			int i = 1;
			for (Object o : list) {
				JSONObject j = JSONObject.fromObject(o);
				log.info("Auction title is: "+j.getJSONObject("auction").getString("title"));
				log.info("Date is: " + j.getString("date"));
				dateList.add(j.getString("date"));
				log.info("Content is: " + j.getString("content"));
				log.info("Comment NO is: "+i++);
			}
			return true;
		}
	}
	/**
	 * 
	 * 返回的评论的格式如下:
	 * {
"watershed":100,
"maxPage":167,
"currentPageNum":166,
"comments":[
	{"auction":
		{"title":"Apple/苹果 iPhone 4S 无锁版/港版 16G 32G 64G可装软件有未激活",
		"aucNumId":13599064573,
		"link":"",
		"sku":"机身颜色:港版16G白色现货  手机套餐:官方标配"},
	"content":"hao",
	"append":null,
	"rate":"好评！",
	"tag":"",
	"rateId":16249892723,
	"award":"",
	"reply":null,
	"useful":0,
	"date":"2012.03.08",
	"user":{
					"vip":"",
					"rank":136,
					"nick":"771665176_44",
					"userId":410769781,
					"displayRatePic":"b_red_4.gif",
					"nickUrl":"http://wow.taobao.com/u/NDEwNzY5Nzgx/view/ta_taoshare_list.htm?redirect=fa",
					"vipLevel":2,
					"avatar":"http://img.taobaocdn.com/sns_logo/i1/T1VxqHXa4rXXb1upjX.jpg_40x40.jpg",
					"anony":false,
					"rankUrl":"http://rate.taobao.com/rate.htm?user_id=410769781&rater=1"}
	},
	 */
	public String getFirstReviewDate(){
		if(dateList.size() == 0){
			return null;
		}
		return dateList.get(0);
	}
	
	public String getLastReviewDate(){
		if(dateList.size() == 0){
			return null;
		}
		return dateList.get(dateList.size()-1);	
	}

}
