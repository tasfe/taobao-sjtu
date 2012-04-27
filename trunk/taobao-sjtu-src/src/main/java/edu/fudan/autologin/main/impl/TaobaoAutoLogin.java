package edu.fudan.autologin.main.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.spec.PSource;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.main.AutoLogin;
import edu.fudan.autologin.pageparser.SearchResultPageParser;
import edu.fudan.autologin.pageparser.TopTenPageParser;
import edu.fudan.autologin.pojos.BasePostInfo;
import edu.fudan.autologin.pojos.CategoryInfo;
import edu.fudan.autologin.pojos.Postage;
import edu.fudan.autologin.utils.PostUtils;
import edu.fudan.autologin.utils.TaobaoUtils;

public class TaobaoAutoLogin implements AutoLogin {
	private static final Logger log = Logger.getLogger(TaobaoAutoLogin.class);
	private DefaultHttpClient httpClient;

	public TaobaoAutoLogin() {
		if (this.httpClient == null) {
			httpClient = new DefaultHttpClient();
			// HttpHost proxy = new HttpHost("web-proxy.cup.hp.com", 8080);
			// httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
			// proxy);
		}

	}

	public void beforeWriteExcel() {
		ExcelUtil.createWorkbook();
		ExcelUtil.createSheets();
	}

	/**
	 * test get request
	 */
	public void testGet() {
		String getUrl = "http://item.taobao.com/item.htm?id=13921701227";
		// String getUrl
		// ="http://detailskip.taobao.com/json/postage_fee.htm?opt=&catid=50012938&ic=1&id=13871713569&il=%BA%DA%C1%FA%BD%AD%B9%FE%B6%FB%B1%F5&ap=false&ss=false&free=true&tg=false&tid=0&sid=278373237&iv=119.50&up=0.00&exp=0.00&ems=0.00&iw=0&is=";
		// String getUrl =
		// "http://delivery.taobao.com/detail/detail.do?itemCount=1&amp;itemId=10425980787&amp;itemValue=98.00&amp;isSellerPay=false&amp;templateId=2866818&amp;userId=70492495&amp;unifiedPost=5.00&amp;unifiedExpress=5.00&amp;unifiedEms=0.00&amp;weight=0&amp;size=0";
		// String getUrl =
		// "http://detailskip.taobao.com/json/postage_fee.htm?opt=&catid=1512&ic=1&id=160168996217&il=%B9%E3%B6%AB%C9%EE%DB%DA&ap=false&ss=false&free=true&tg=false&tid=0&sid=412899893&iv=958.00&up=0.00&exp=0.00&ems=0.00&iw=0&is=&callback=TShop.mods.SKU.DefaultShippingInfo.render";
		// String getUrl = "http://detailskip.taobao.com/json/postage_fee.htm?"
		// +
		// "opt=&catid=50012938&ic=1&" +
		// "id=13871713569&" +//商品编号id
		// "il=%B9%E3%B6%AB%B9%E3%D6%DD&" +//location的一种中文编码方式
		// "ap=false&ss=false&" +
		// "free=true&" +//如果买家承担费用，则free为ture
		// "tg=false&tid=2866818&" +
		// "sid=278373237&" +// user id
		// "iv=98.00&" +//价格
		// "up=5.00&exp=5.00&ems=0.00&iw=0&is=&" +
		// "callback=TShop.mods.SKU.DefaultShippingInfo.render";//回调
		// String getUrl =
		// "http://detailskip.taobao.com/json/show_buyer_list.htm?page_size=15&is_start=false&item_type=b&ends=1335361433000&starts=1334756633000&item_id=10821226356&user_tag=471101458&old_quantity=2255&sold_total_num=16&closed=false&seller_num_id=23280614&zhichong=true&title=%D5%FD%C6%B7Cetaphil%CB%BF%CB%FE%DC%BD%CF%B4%C3%E6%C4%CC591ml+%CA%E6%CC%D8%B7%F4%BD%E0%C3%E6%C8%E9+%B1%A3%CA%AA%D0%B6%D7%B1+%C3%F4%B8%D0%BC%A1%B7%F4&bidPage=3&callback=TShop.mods.DealRecord.reload&t=1335326971619";
		// String getUrl =
		// "http://detailskip.taobao.com/json/show_buyer_list.htm?page_size=15&is_start=false&item_type=b&ends=1335454211000&starts=1334849411000&item_id=13599064573&user_tag=475887632&old_quantity=6482&sold_total_num=16&closed=false&seller_num_id=14744854&zhichong=true&title=Apple%2F%C6%BB%B9%FB+iPhone+4S+%CE%DE%CB%F8%B0%E6%2F%B8%DB%B0%E6%2F%B5%E7%D0%C5%B0%E6%2F%D3%D0%B8%DB%B0%E65.01%BF%C9%D4%BD%D3%FC%B0%E6&bidPage=2&callback=TShop.mods.DealRecord.reload&t=1335414993258";
		// String getUrl =
		// "http://item.taobao.com/item.htm?id=14312891950#deal-record";
		GetMethod getMethod = new GetMethod(httpClient, getUrl);

		getMethod.doGet();// 给get请求添加httpheader
		// getMethod.printResponse("utf-8");
		String postageUrl = null;
		postageUrl = getPostageUrl(getMethod.getResponseAsString());
		getMethod.shutDown();

		List<NameValuePair> headers1 = new ArrayList<NameValuePair>();
		NameValuePair nvp1 = new BasicNameValuePair("referer",
				"http://item.taobao.com/item.htm?id="
						+ getIdFromPostageUrl(postageUrl));
		headers1.add(nvp1);
		GetMethod postage = new GetMethod(httpClient, postageUrl);
		postage.doGet(headers1);
		// postage.printResponse();
		getPostageFromJson(postage.getResponseAsString());

		postage.shutDown();
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

	public Postage getPostageFromJson(String json) {
		System.out.println(json);
		Postage postage = new Postage();
		System.out.println(json);

		String delimeters = "[()]+";
		String[] tokens = json.split(delimeters);

		System.out.println(tokens.length);
		for (int i = 0; i < tokens.length; i++)
			System.out.println(tokens[i]);

		JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(tokens[1]);

		System.out.println(jsonObj.getString("type"));
		System.out.println(jsonObj.getString("location"));
		System.out.println(jsonObj.getString("carriage"));

		return postage;
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

	/**
	 * 1. get ItemDetailPage;
	 * 2. get showBuyerListUrl from ItemDetailPage; 
	 * 3.according to taobao rules, construct our showBuyerListUrl list;
	 * 4.according to construted showBuyerListUrl, get json data from server; 
	 * 5.parsing json data from server and get our desired data;
	 * 
	 */
	public void parseShowBuyerListDoc() {
		String itemDetailPageUrl = "http://item.taobao.com/item.htm?id=12962369261";
		String showBuyerListUrl = getShowBuyerListUrl(itemDetailPageUrl);
		log.debug("ShowBuyerList url is: ");
		log.debug( showBuyerListUrl);
		int pageNum = 1;
		while (true) {
			String constructedShowBuyerListUrl = constructShowBuyerListUrl(
					showBuyerListUrl, pageNum++);
			
			if(parseConstructedShowBuyerListDoc(getShowBuyerListDoc(constructedShowBuyerListUrl)) == false){
				break;//最后一个页面，跳出循环
			}
		}
	}

	/**
	 * 
	 * 当解析到最后一个页面时返回false，其余页面返回true
	 * @param doc
	 * @return
	 */
	public boolean parseConstructedShowBuyerListDoc(Document doc){
		
		return false;
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
		Document doc;

		GetMethod getMethod = new GetMethod(httpClient, itemDetailPageUrl);
		getMethod.doGet();
		try {
			doc = Jsoup.parse(EntityUtils.toString(getMethod.getResponse()
					.getEntity()));
			Elements eles = doc.select("button#J_listBuyerOnView");

			log.debug("Find elements's size is: "+eles.size());
			for (Element e : eles) {
				String tmp = e.attr("detail:params").trim();
				showBuyerListUrl = tmp.substring(0, tmp.length()
						- ",showBuyerList".length());
			}
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
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

	public List<Document> getBuyerDocList() {
		List<Document> buyerDocList = new ArrayList<Document>();
		return buyerDocList;
	}

	public void execute() {
		ExcelUtil.prepare();
		parseShowBuyerListDoc();
		// autoLogin();
		// testDealRecord(getShowBuyerListUrl());
		// testGet();
		// isLoginSuccess();
		// searchResultPageParser();
		// doMyWork();
		shutDown();
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
}
