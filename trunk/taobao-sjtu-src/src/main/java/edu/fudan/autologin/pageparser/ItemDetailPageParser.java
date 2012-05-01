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
import edu.fudan.autologin.service.PostageService;

public class ItemDetailPageParser extends BasePageParser {
	private static final Logger log = Logger
			.getLogger(ItemDetailPageParser.class);
	private ItemInfo itemInfo;
	private String postageUrl;
	private String saleNumUrl;
	private String reviewUrl;
	
	private int buyerSum = 0;
	
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
		buyerSum = saleNumIn30Days;
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
		
		parseShowBuyerListDoc();//解析买家列表
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

	/**
	 * 1. get ItemDetailPage; 2. get showBuyerListUrl from ItemDetailPage;
	 * 3.according to taobao rules, construct our showBuyerListUrl list;
	 * 4.according to construted showBuyerListUrl, get json data from server;
	 * 5.parsing json data from server and get our desired data;
	 * 
	 */
	public void parseShowBuyerListDoc() {
		String itemDetailPageUrl = this.getPageUrl();
		String showBuyerListUrl = getShowBuyerListUrl(itemDetailPageUrl);
		log.debug("ShowBuyerList url is: " + showBuyerListUrl);
//		int pageNum = 1;
		int pageSize = 15;
		int pageSum = (buyerSum%pageSize == 0) ? buyerSum/pageSize : (buyerSum/pageSize + 1);
		
		for(int pageNum = 1; pageNum <= pageSum; ++pageNum){
//		while (true) {
			log.info("This is buyers of Page NO: "+pageNum);
			String constructedShowBuyerListUrl = constructShowBuyerListUrl(
					showBuyerListUrl, pageNum);

			parseConstructedShowBuyerListDoc(getShowBuyerListDoc(constructedShowBuyerListUrl));
//			if (parseConstructedShowBuyerListDoc(getShowBuyerListDoc(constructedShowBuyerListUrl)) == false) {
//				log.info("Total page NO is: "+(pageNum-1));
//				break;// 最后一个页面，跳出循环
//			}
			
			++pageNum;
		}
	}

	/**
	 * 没有买家时返回的是这个 <div class="msg msg-attention-shortcut"
	 * server-num="detailskip185108.cm4">
	 * <p class="attention naked">
	 * 暂时还没有买家购买此宝贝，最近30天成交0件。
	 * </p>
	 * </div>
	 */
	public void parseBuyerListTable(Document doc){
		Elements buyerListEls = doc.select("table.tb-list > tbody > tr");
		for(int i = 0; i < buyerListEls.size(); i ++){
			Element buyerEl = buyerListEls.get(i);
			Elements buyerInfo = buyerEl.select("td.tb-buyer");
			if(0 == buyerInfo.size()){
				continue;
			}
			String priceStr = buyerEl.select("td.tb-price").get(0).ownText();
			float price = Float.valueOf(priceStr);
			String numStr = buyerEl.select("td.tb-amount").get(0).ownText();
			int num = Integer.valueOf(numStr);
			String payTime = buyerEl.select("td.tb-time").get(0).ownText();
			String size = buyerEl.select("td.tb-sku").text();
			String sex = SexEnum.unknown;
			
			
			
			BuyerInfo bi = new BuyerInfo();
			bi.setPayTime(payTime);
			bi.setNum(num);
			bi.setPrice(price);
			bi.setSize(size);
			
			buyerInfos.add(bi);
			
			
			log.info("price: " + price);
			log.info("num: " + num);
			log.info("payTime: " + payTime);
			log.info("size: " + size);
		}
	}
	/**
	 * 
	 * 当解析到最后一个页面时返回false，其余页面返回true
	 * 
	 * @param doc
	 * @return
	 */
	public void parseConstructedShowBuyerListDoc(Document doc) {

		parseBuyerListTable(doc);
//		if (doc.toString().contains("暂时还没有买家购买此宝贝")) {
//			log.info("There is no buyers.");
//			return false;
//		} else {
//			parseBuyerListTable(doc);
//			return true;
//		}
	}

	public Document getShowBuyerListDoc(String getUrl) {
		assert(getUrl != null);
		GetMethod get = new GetMethod(this.getHttpClient(), getUrl);

		List<NameValuePair> headers1 = new ArrayList<NameValuePair>();
		NameValuePair nvp1 = new BasicNameValuePair("referer",
				"http://item.taobao.com/item.htm?id=13048366752");
		headers1.add(nvp1);
		get.doGet(headers1);
		Document doc = getHtmlDocFromJson(get.getResponseAsString());
		get.shutDown();
		return doc;
	}

	public String getShowBuyerListUrl(String itemDetailPageUrl) {
		String showBuyerListUrl = "";

		GetMethod getMethod = new GetMethod(this.getHttpClient(), itemDetailPageUrl);
		getMethod.doGet();
		String tmpStr = getMethod.getResponseAsString();
		getMethod.shutDown();

		int base = tmpStr.indexOf("detail:params=\"http");
//		int begin = tmpStr.indexOf("\"", base);
		int end = tmpStr.indexOf(",showBuyerList",base);

		String myStr = tmpStr.substring(base+"detail:params=\"".length(), end);

		showBuyerListUrl = myStr;
		return showBuyerListUrl;
	}

	public String constructShowBuyerListUrl(String showBuyerListUrl, int pageNum) {
		String delims = "[?&]+";
		String[] tokens = showBuyerListUrl.split(delims);
//		System.out.println(tokens.length);
//		for (int i = 0; i < tokens.length; i++)
//			System.out.println(tokens[i]);

		StringBuffer sb = new StringBuffer();
		sb.append(tokens[0] + "?");
		for (int i = 2; i <= 12; ++i) {
			sb.append(tokens[i] + "&");
		}
		sb.append(tokens[14]);

		String append = "&bidPage="
				+ pageNum
				+ "&callback=TShop.mods.DealRecord.reload&closed=false&t=1335495514388";

		sb.append(append);
//		System.out.println(sb);

		return sb.toString();
	}

	public Document getHtmlDocFromJson(String jsonStr) {
		String tmp = new String((jsonStr.trim()));
		JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(tmp.substring(
				"TShop.mods.DealRecord.reload(".length(), tmp.length() - 1));
//		System.out.println(jsonObj.getString("html"));
		Document doc = Jsoup.parse(jsonObj.getString("html"));
		return doc;
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
