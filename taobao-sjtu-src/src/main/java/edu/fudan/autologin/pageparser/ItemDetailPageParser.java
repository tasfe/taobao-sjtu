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

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.main.impl.TaobaoAutoLogin;
import edu.fudan.autologin.pojos.BuyerInfo;
import edu.fudan.autologin.pojos.FeedRateComment;
import edu.fudan.autologin.pojos.ItemInfo;
import edu.fudan.autologin.pojos.Postage;

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

		Postage postage = getPostage();
		location = postage.getLocation();
		freight = postage.getCarriage();
		freightPrice = location + " : " + freight;
		log.info("freightPrice: " + freightPrice);

		int saleNumIn30Days = getSaleNum();
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

		assert (itemInfo.getItemBuyersHref() != null);
		
		for(BuyerInfo buyerInfo: buyerInfos){
			ItemBuyersPageParser itemBuyersPageParser = new ItemBuyersPageParser(this.getHttpClient(), buyerInfo.getHref());
			itemBuyersPageParser.setBuyInfo(buyerInfo);
			itemBuyersPageParser.execute();
		}
//		ItemBuyersPageParser itemBuyersPageParser = new ItemBuyersPageParser(
//				this.getHttpClient(), itemInfo.getItemBuyersHref());
//		itemBuyersPageParser.execute();

		assert (itemInfo.getUserRateHref() != null);
		UserRatePageParser userRatePageParser = new UserRatePageParser(
				this.getHttpClient(), itemInfo.getUserRateHref());
		userRatePageParser.execute();
	}

	public String getPostageUrl(String str) {// 获得邮费get请求的url地址
		Pattern pattern = Pattern.compile("getShippingInfo:\"(.+?)\"");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			System.out.println(matcher.group(1));
			return matcher.group(1);
		} else {
			System.out.println("no match");
		}
		return null;
	}

	/**
	 * 
	 * 根据postage url地址，获得id
	 * 
	 * @param str
	 * @return
	 */
	public String getIdFromPostageUrl(String str) {
		Pattern pattern = Pattern.compile("&id=(.+?)&");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			System.out.println(matcher.group(1));
			return matcher.group(1);
		} else {
			System.out.println("no match");
		}

		return null;
	}

	/**
	 * 
	 * parse json string in order to get location,carriage and other info
	 * 
	 * @param rtnStr
	 * @return
	 */
	public Postage getPostageFromJson(String jsonStr) {
		Postage postage = new Postage();

		log.debug("The json string of postage from server is: " + jsonStr);

		String delimeters = "[()]+";
		String[] tokens = jsonStr.split(delimeters);// split the string to get
													// json data

		/**
		 * { type:'buyerPayPostfee', location:'浙江宁波', carriage:'快递:22.00元
		 * EMS:25.00元 平邮:100.00元 ' }
		 */

		JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(tokens[1]);

		postage.setCarriage(jsonObj.getString("carriage"));
		postage.setLocation(jsonObj.getString("location"));
		return postage;
	}

	/**
	 * 
	 * 调用此函数后可以得到邮费的起始地址和邮费
	 * 
	 * @param getUrl
	 * @return
	 */
	public Postage getPostage() {

		Postage postage = new Postage();

		GetMethod getMethod = new GetMethod(this.getHttpClient(),
				this.getPageUrl());
		getMethod.doGet();// 给get请求添加httpheader
		String postageUrl = null;
		postageUrl = getPostageUrl(getMethod.getResponseAsString());
		getMethod.shutDown();

		List<NameValuePair> headers1 = new ArrayList<NameValuePair>();
		NameValuePair nvp1 = new BasicNameValuePair("referer",
				"http://item.taobao.com/item.htm?id="
						+ getIdFromPostageUrl(postageUrl));
		headers1.add(nvp1);
		GetMethod postageGet = new GetMethod(this.getHttpClient(), postageUrl);
		postageGet.doGet(headers1);
		postage = getPostageFromJson(postageGet.getResponseAsString());

		postageGet.shutDown();
		return postage;
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
		saleNum = saleNumObj.getInt("quanity");
		return saleNum;
	}

	public int getReviewsNum() {
		int reviewsNum = 0;
		String referer = getPageUrl();
		String requestUrl = getReviewUrl();
		GetMethod getRequest = new GetMethod(this.getHttpClient(), requestUrl);
		getRequest.doGet();
		String responseStr = getRequest.getResponseAsString();
		System.out.println("reviews:");
		System.out.println(responseStr);

		return reviewsNum;
	}

	/**
	 * 1. get ItemDetailPage; 2. get showBuyerListUrl from ItemDetailPage; 3.
	 * according to taobao rules, construct our showBuyerListUrl list; 4.
	 * according to construted showBuyerListUrl, get json data from server; 5.
	 * parsing json data from server and get our desired data;
	 * 
	 */
	public void parseShowBuyerListDoc() {
		String itemDetailPageUrl = this.getPageUrl();
		String showBuyerListUrl = getShowBuyerListUrl(itemDetailPageUrl);
		log.debug("ShowBuyerList url is: ");
		log.debug(showBuyerListUrl);
		int pageNum = 1;
		while (true) {
			String constructedShowBuyerListUrl = constructShowBuyerListUrl(
					showBuyerListUrl, pageNum++);

			if (parseConstructedShowBuyerListDoc(getShowBuyerListDoc(constructedShowBuyerListUrl)) == false) {
				break;// 最后一个页面，跳出循环
			}
		}
	}

	/**
	 * 
	 * 当解析到最后一个页面时返回false，其余页面返回true
	 * 
	 * @param doc
	 * @return
	 */
	public boolean parseConstructedShowBuyerListDoc(Document doc) {

		return false;
	}

	public Document getShowBuyerListDoc(String getUrl) {
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

		int base = tmpStr.indexOf("detail:params=\"");
		int begin = tmpStr.indexOf("\"", base);
		int end = tmpStr.indexOf(",showBuyerList");

		String myStr = tmpStr.substring(begin + 1, end);

		showBuyerListUrl = myStr;
		return showBuyerListUrl;
	}

	public String constructShowBuyerListUrl(String showBuyerListUrl, int pageNum) {
		String delims = "[?&]+";
		String[] tokens = showBuyerListUrl.split(delims);
		System.out.println(tokens.length);
		for (int i = 0; i < tokens.length; i++)
			System.out.println(tokens[i]);

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
		System.out.println(sb);

		return sb.toString();
	}

	public Document getHtmlDocFromJson(String jsonStr) {
		String tmp = new String((jsonStr.trim()));
		JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(tmp.substring(
				"TShop.mods.DealRecord.reload(".length(), tmp.length() - 1));
		System.out.println(jsonObj.getString("html"));
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
				log.info("Date is: " + j.getString("date"));
				dateList.add(j.getString("date"));
				log.info("Content is: " + j.getString("content"));
				log.info("Comment NO is: "+i++);
			}
			return true;
		}
	}
	
	public String getFirstReviewDate(){
		return dateList.get(0);
	}
	
	public String getLastReviewDate(){
		return dateList.get(dateList.size()-1);	
	}

}
