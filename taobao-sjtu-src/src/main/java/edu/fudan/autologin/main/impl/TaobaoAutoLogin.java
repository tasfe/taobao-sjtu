package edu.fudan.autologin.main.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.constants.SexEnum;
import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.main.AutoLogin;
import edu.fudan.autologin.pageparser.ItaobaoPageParser;
import edu.fudan.autologin.pageparser.ItemDetailPageParser;
import edu.fudan.autologin.pageparser.SearchResultPageParser;
import edu.fudan.autologin.pageparser.TopTenPageParser;
import edu.fudan.autologin.pageparser.UserRatePageParser;
import edu.fudan.autologin.pojos.BasePostInfo;
import edu.fudan.autologin.pojos.CategoryInfo;
import edu.fudan.autologin.pojos.FeedRateComment;
import edu.fudan.autologin.pojos.Postage;
import edu.fudan.autologin.utils.PostUtils;
import edu.fudan.autologin.utils.TaobaoUtils;

public class TaobaoAutoLogin implements AutoLogin {
	private static final Logger log = Logger.getLogger(TaobaoAutoLogin.class);
	private DefaultHttpClient httpClient;

	public TaobaoAutoLogin() {
		if (this.httpClient == null) {
			// 设置动态代理机制
			httpClient = new DefaultHttpClient();
//			// 设置代理对象 ip/代理名称,端口
//			HttpHost proxy = new HttpHost("10.141.251.173", 3128);
//			// HttpHost proxy = new HttpHost("proxy.fudan.edu.cn", 8080);
//			// // 实例化验证
//			CredentialsProvider credsProvider = new BasicCredentialsProvider();
//			// // 设定验证内容
//			// UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
//			// "10210240089", "fudan123");
//			// // 创建验证
//			// credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST,
//			// AuthScope.ANY_PORT), creds);
//			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
//					proxy);
//			((DefaultHttpClient) httpClient).setCredentialsProvider(credsProvider);
		}

	}

	public void beforeWriteExcel() {
		ExcelUtil.createWorkbook();
		ExcelUtil.createSheets();
	}


	

	public void autoLogin() {

		// 设置基本的post信息
		BasePostInfo basePostInfo = new BasePostInfo();
		basePostInfo
				.setPostPageUrl("https://login.taobao.com/member/login.jhtml");
		basePostInfo.setPostFormId("J_StaticForm");
		basePostInfo
				.setPostFormUrl("https://login.taobao.com/member/login.jhtml");

		// 设置提交表单相关信息
		List<NameValuePair> formFieldsNvps = new ArrayList<NameValuePair>();
		formFieldsNvps.add(new BasicNameValuePair("TPL_username", "gschen163"));
		formFieldsNvps.add(new BasicNameValuePair("TPL_password",
				"3DES_2_000000000000000000000000000000_D1CE4894D9F2334C"));
		formFieldsNvps.add(new BasicNameValuePair("TPL_checkcode", TaobaoUtils
				.getCheckCode(httpClient)));
		formFieldsNvps.add(new BasicNameValuePair("need_check_code", "true"));

		PostUtils.doPost(httpClient, basePostInfo, formFieldsNvps);
	}
	public void testItaobaoPageParser(){
		
		String pageUrl =  "http://i.taobao.com/u/NjAwNjgzNDQ=/front.htm";
		ItaobaoPageParser itaobaoPageParser = new ItaobaoPageParser(httpClient, pageUrl);
		itaobaoPageParser.execute();
	}
	/**
	 * 1. get ItemDetailPage; 2. get showBuyerListUrl from ItemDetailPage;
	 * 3.according to taobao rules, construct our showBuyerListUrl list;
	 * 4.according to construted showBuyerListUrl, get json data from server;
	 * 5.parsing json data from server and get our desired data;
	 * 
	 */
	public void parseShowBuyerListDoc() {
		String itemDetailPageUrl = "http://item.taobao.com/item.htm?id=10203414733";
		String showBuyerListUrl = getShowBuyerListUrl(itemDetailPageUrl);
		log.debug("ShowBuyerList url is: " + showBuyerListUrl);
		int pageNum = 1;
		while (true) {
			log.info("This is buyers of Page NO: "+pageNum);
			String constructedShowBuyerListUrl = constructShowBuyerListUrl(
					showBuyerListUrl, pageNum);

			if (parseConstructedShowBuyerListDoc(getShowBuyerListDoc(constructedShowBuyerListUrl)) == false) {
				log.info("Total page NO is: "+(pageNum-1));
				break;// 最后一个页面，跳出循环
			}
			
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
	public boolean parseConstructedShowBuyerListDoc(Document doc) {

		if (doc.toString().contains("暂时还没有买家购买此宝贝")) {
			log.info("There is no buyers.");
			return false;
		} else {
			parseBuyerListTable(doc);
			return true;
		}
	}

	public void doMyWork() {

		List<CategoryInfo> categoryInfos = new ArrayList<CategoryInfo>();

		CategoryInfo ci1 = new CategoryInfo();
		ci1.setCategoryName("洁面");
		ci1.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_MRHF&level3=50011977&up=false");
		categoryInfos.add(ci1);

		CategoryInfo ci2 = new CategoryInfo();
		ci2.setCategoryName("热门手机");
		ci2.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_SJ&level3=TR_RXSJB&up=false");
		categoryInfos.add(ci2);

		CategoryInfo ci3 = new CategoryInfo();
		ci3.setCategoryName("笔记本");
		ci3.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_DNJXGPJ&level3=1101&up=false");
		categoryInfos.add(ci3);

		for (CategoryInfo c : categoryInfos) {
			TopTenPageParser topTenPageParser = new TopTenPageParser(
					httpClient, c.getCategoryHref());
			topTenPageParser.setCategoryInfo(c);
			topTenPageParser.execute();
		}
	}

	public Document getShowBuyerListDoc(String getUrl) {
		GetMethod get = new GetMethod(httpClient, getUrl);

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

		GetMethod getMethod = new GetMethod(httpClient, itemDetailPageUrl);
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
		//System.out.println(jsonObj.getString("html"));
		Document doc = Jsoup.parse(jsonObj.getString("html"));
		return doc;
	}

	public List<Document> getBuyerDocList() {
		List<Document> buyerDocList = new ArrayList<Document>();
		return buyerDocList;
	}
	
	public void execute() {
		beforeWriteExcel();
		// testDealRecord(getShowBuyerListUrl());
		// testGet();
		// isLoginSuccess();
		// searchResultPageParser();
		// parseReviews();
//		parseReviews();
		// itemDetailPageParser();
		autoLogin();
		
//	testItaobaoPageParser();
//		doMyWork();
		shutDown();
		// 
		//parseShowBuyerListDoc();
//		userRatePageParser();
	}
	
	

	public void topTenPageParser() {
		String pageUrl = "http://top.taobao.com/level3.php?cat=TR_SJ&level3=TR_RXSJB";
		TopTenPageParser topTenPageParser = new TopTenPageParser(httpClient,
				pageUrl);
		topTenPageParser.execute();
	}

	public void searchResultPageParser() {
		String pageUrl = "http://s.taobao.com/search?source=top_search&q=Apple%2F%C6%BB%B9%FB+iPhone+4S&pspuid=137939469&v=product&p=detail&stp=top.toplist.tr_rxsjb.sellhot.image.1.0&ad_id=&am_id=&cm_id=&pm_id=";
		SearchResultPageParser searchResultPageParser = new SearchResultPageParser(
				httpClient, pageUrl);
		searchResultPageParser.parsePage();
	}

	public void itemDetailPageParser() {
		String pageUrl = "http://item.taobao.com/item.htm?id=10425980787";
		ItemDetailPageParser itemDetailPageParser = new ItemDetailPageParser(
				httpClient, pageUrl);
		itemDetailPageParser.parsePage();
	}

	public void userRatePageParser(){
		String pageUrl = "http://rate.taobao.com/user-rate-1fb32e41d7bc59591c5e4de73f3a6f6a.htm";
		UserRatePageParser userRatePageParser = new UserRatePageParser(httpClient, pageUrl);
		userRatePageParser.parsePage();
	}
	public void shutDown() {
		log.info("--------------------------------------------------------------------------------------------------------------");
		log.info("COMPLETE ALL TASKS!");
		ExcelUtil.closeWorkbook();
		httpClient.getConnectionManager().shutdown();
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		log.setLevel(Level.DEBUG);

		TaobaoAutoLogin taobaoAutoLogin = new TaobaoAutoLogin();
		taobaoAutoLogin.execute();
	}

	public void parseReviews() {
		int pageNum = 0;
		while (true) {
			log.info("--------------------------------------------------------------------------------------");
			log.info("This review of page num is: " + (++pageNum));
			GetMethod get = new GetMethod(httpClient, constructFeedRateListUrl(
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
		String itemDetailUrl = "http://item.taobao.com/item.htm?id=13117536191";

		String baseFeedRateListUrl = "";

		String tmpStr = "";
		GetMethod getMethod = new GetMethod(httpClient, itemDetailUrl);
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
				log.info("Content is: " + j.getString("content"));
				log.info("Auction title is: "+j.getJSONObject("auction").getString("title"));
				log.info("Comment NO is: " + i++);
			}
			return true;
		}
	}
	
	/**
	 * 
	 * 评论的格式如下:
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
}
