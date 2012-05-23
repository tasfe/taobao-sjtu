package edu.fudan.autologin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import jxl.write.WritableSheet;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.constants.SexEnum;
import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pageparser.ItaobaoPageParser;
import edu.fudan.autologin.pojos.BuyerInfo;
import edu.fudan.autologin.utils.DosCmdUtils;
import edu.fudan.autologin.utils.PrintUtils;
import edu.fudan.autologin.utils.RandomUtils;
import edu.fudan.autologin.utils.XmlConfUtil;

public class BuyerListService {
	private static final Logger log = Logger.getLogger(BuyerListService.class);
	private String itemPageUrl;
	
	private String sellerId;
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	private WritableSheet sheet;

	public void setSheet(WritableSheet sheet) {
		this.sheet = sheet;
	}

	private HttpClient httpClient;
	private List<BuyerInfo> buyerInfos;
	private int buyerSum = 0;
	private String pageStr;

	
	public class BuyerListThread implements Runnable{

		private String showBuyerListUrl;
		private int pageNum;
		
		public BuyerListThread(String url, int page){
			this.pageNum = page;
			this.showBuyerListUrl = url;
		}
		public void run() {
			log.info("This is buyers of Page NO: " + pageNum);
			String constructedShowBuyerListUrl = constructShowBuyerListUrl(
					showBuyerListUrl, pageNum);
			parseBuyerListTable(getShowBuyerListDoc(constructedShowBuyerListUrl));
		}
	}

	/**
	 * 1.get ItemDetailPage; <br/>
	 * 2.get showBuyerListUrl from ItemDetailPage; 3.according to taobao rules,
	 * construct our showBuyerListUrl list; 4.according to construted
	 * showBuyerListUrl, get json data from server; 5.parsing json data from
	 * server and get our desired data;
	 * 
	 */
	public void execute() {
		String showBuyerListUrl = getShowBuyerListUrl(itemPageUrl);
		log.info("ShowBuyerList url is: " + showBuyerListUrl);
		if (showBuyerListUrl == null) {
			log.info("There is no showBuyerList url in the page.");
			assert (buyerInfos.size() == 0);
		} else {
			int pageSize = 15;
			int pageSum = (buyerSum % pageSize == 0) ? buyerSum / pageSize
					: (buyerSum / pageSize + 1);

			if(pageSum >= 100){
				pageSum = 100;
			}
			log.info("Total page num is: " + pageSum);
			for (int pageNum = 1; pageNum <= pageSum; ++pageNum) {
				log.info("-----------------------------------------------------");
				log.info("This is buyers of Page NO: " + pageNum);
				String constructedShowBuyerListUrl = constructShowBuyerListUrl(
						showBuyerListUrl, pageNum);
				
				Document doc = getShowBuyerListDoc(constructedShowBuyerListUrl);
				while( parseBuyerListTable(doc) == false){
					constructedShowBuyerListUrl = constructShowBuyerListUrl(
							showBuyerListUrl, pageNum);
					
					doc = getShowBuyerListDoc(constructedShowBuyerListUrl+"&&code="+verifyCode);
				}
				
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//因为触发了验证码机制，所以无法请求后面的页面
				//什么样的机制会触发验证码？
				/*
				 *1. 请求太频繁；
				 *2. 一定时间段内请求数的限制？ 
				 * 
				 */
				
			}
		}

	}
	
	public void testUrl(String url){
		parseBuyerListTable(getShowBuyerListDoc(url));

	}

	public String getItemPageUrl() {
		return itemPageUrl;
	}

	public void setItemPageUrl(String itemPageUrl) {
		this.itemPageUrl = itemPageUrl;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public List<BuyerInfo> getBuyerInfos() {
		return buyerInfos;
	}

	public void setBuyerInfos(List<BuyerInfo> buyerInfos) {
		this.buyerInfos = buyerInfos;
	}

	
	private String verifyCode = null;
	/**
	 * 没有买家时返回的是这个 <div class="msg msg-attention-shortcut"
	 * server-num="detailskip185108.cm4">
	 * <p class="attention naked">
	 * 暂时还没有买家购买此宝贝，最近30天成交0件。
	 * </p>
	 * </div>
	 */
	public boolean parseBuyerListTable(Document doc) {
		/*
		 * 
		 * TShop.mods.DealRecord.reload(
		 * {html:"<div id=\"J_DealCode\" class=\"tb-code\"
		 *  data-apicode=\"http://detailskip.taobao.com/json/change_code.htm\"> 
		 *  <em>请输入校验码后继续翻页：</em> <a class=\"tb-code-pic\" href=\"#\"> 
		 *  <img id=\"J_DealCodePic\" src=\"http://checkcode.taobao.com/auction/checkcode?sessionID=5c071b726c34dbfcb86b9f89ec708a8f&r=1337684398007\" width=\"56\" height=\"20\"/> <span>换一张</span> </a> <span class=\"tb-code-split\">-</span> <input id=\"J_DealCodeInput\" class=\"tb-code-input\" type=\"text\" maxlength=\"4\"/> <button id=\"J_DealCodeBtn\" class=\"tb-code-btn\" type=\"button\">确定</button> <a id=\"J_CloseDealCode\" href=\"#\">取消 </a> </div>",
		 *  type:"code"})	
	            
		 * 
		 */
		
		//先检查是否需要输入验证码
		//如果请求某页面出现验证码，则加上验证码重新请求该链接
		
		if(doc.select("img#J_DealCodePic").size() != 0){
			String checkcodeUrl = doc.select("img#J_DealCodePic").get(0).attr("src");
			DosCmdUtils.open(checkcodeUrl);
			Scanner scanner = new Scanner(System.in);
			System.out.print("请您输入验证码：");
			verifyCode = scanner.next();
			return false;
		}
		
		//选择class=tb-list的table下的tbody下的所有tr标签(除了class=tb-change-info)
		Elements buyerListEls = doc.select("table.tb-list > tbody > tr");
		log.info("Element list size is: "+buyerListEls.size());
		
		if(buyerListEls.size() == 0){
			return false;
		}
		for (int i = 0; i < buyerListEls.size(); i++) {
			//获得第i行
			Element buyerEl = buyerListEls.get(i);

			//当tr的class=tb-change-info时，跳过
			if(buyerEl.attr("class").equals("tb-change-info")){
				continue;
			}
			//tb-buyer 
			Elements buyerInfo = buyerEl.select("td.tb-buyer");
			String buyerHref = null;
			if (0 == buyerInfo.size()) {
				continue;
			}else{
				if(buyerInfo.get(0).select("a.tb-sellnick").size() == 0){
					
				}else{
					Element href = buyerInfo.get(0).select("a.tb-sellnick").get(0);
					buyerHref = constructBuyerHref(href.attr("href"));
				}
			}
			
			//tb-price
			String priceStr = buyerEl.select("td.tb-price").get(0).ownText();
			float price = Float.valueOf(priceStr);
			
			//tb-amount
			String numStr = buyerEl.select("td.tb-amount").get(0).ownText();
			int num = Integer.valueOf(numStr);
			
			//tb-time
			String payTime = buyerEl.select("td.tb-time").get(0).ownText();
			
			//tb-sku
			String size = buyerEl.select("td.tb-sku").text();

			BuyerInfo bi = new BuyerInfo();
			bi.setPayTime(payTime);
			bi.setNum(num);
			bi.setPrice(price);
			bi.setSize(size);
			bi.setHref(buyerHref);
			bi.setSellerId(sellerId);
			
			if(buyerHref != null){
				ItaobaoPageParser itaobaoPageParser = new ItaobaoPageParser(
						httpClient, buyerHref);
				itaobaoPageParser.setBuyerInfo(bi);
				itaobaoPageParser.parsePage();
			}

			buyerInfos.add(bi);

			log.info("-----------------------------");
			
			log.info("price: " + price);
			log.info("num: " + num);
			log.info("payTime: " + payTime);
			log.info("size: " + size);
			log.info("buyer href is: "+buyerHref);
			log.info("Gender is: "+bi.getSex());
			log.info("Buyer location is: "+bi.getBuyerAddress());
			log.info("Buyer rate scoare is: "+bi.getRateScore());
			
			ExcelUtil.writeItemBuyerSheet(sheet, bi);
		}
		return true;
	}
	
//	href="http://fx.taobao.com/u/Nzg1MDIxNjky/view/ta_taoshare_list.htm?redirect=fa"
	public String constructBuyerHref(String initUrl){
		String buyerId = initUrl.split("/")[4];
		StringBuffer sb = new StringBuffer();
		String baseUrl = "http://i.taobao.com/u/";
		String appendStr = "/front.htm";
		
		sb.append(baseUrl);
		sb.append(buyerId);
		sb.append(appendStr);
		
		return sb.toString();
	}

	public Document getShowBuyerListDoc(String getUrl) {
		assert (getUrl != null);
		GetMethod get = new GetMethod(this.getHttpClient(), getUrl);

		List<NameValuePair> headers1 = new ArrayList<NameValuePair>();
		NameValuePair nvp1 = new BasicNameValuePair("referer",
				"http://item.taobao.com/item.htm?id="+getSellerIdFromItemDetailPageUrl());
		NameValuePair nvp2 = new BasicNameValuePair("Host", "detailskip.taobao.com");
		headers1.add(nvp1);
		headers1.add(nvp2);
		get.doGet(headers1);
		
		String page = get.getResponseAsString();
//		log.info("The response from server is: "+page);
		Document doc = getHtmlDocFromJson(page);
		get.shutDown();
		return doc;
	}
	
	

	//get seller id from item detail page url
	public String getSellerIdFromItemDetailPageUrl(){
		return itemPageUrl.split("=")[1];
	}
	public String getShowBuyerListUrl(String itemDetailPageUrl) {
		String showBuyerListUrl = null;

		GetMethod getMethod = new GetMethod(this.getHttpClient(),
				itemDetailPageUrl);
		getMethod.doGet();
		String tmpStr = getMethod.getResponseAsString();
		getMethod.shutDown();
		pageStr = tmpStr;
		
		if(tmpStr.contains("showBuyerList") == false){
			showBuyerListUrl = null;
		}else{
			int base = tmpStr.indexOf("detail:params=\"http");
			int end = tmpStr.indexOf(",showBuyerList", base);

			String myStr = tmpStr
					.substring(base + "detail:params=\"".length(), end);

			showBuyerListUrl = myStr;
		}
		
		return showBuyerListUrl;
	}

	public String constructShowBuyerListUrl(String showBuyerListUrl, int pageNum) {
		String delims = "[?&]+";
		String[] tokens = showBuyerListUrl.split(delims);
//		 System.out.println(tokens.length);
//		 for (int i = 0; i < tokens.length; i++)
//		 System.out.println(tokens[i]);

		StringBuffer sb = new StringBuffer();
		sb.append(tokens[0] + "?");
		for (int i = 2; i <= 12; ++i) {
			sb.append(tokens[i] + "&");
		}
		sb.append(tokens[13]);
		String token = getToken();
		String append = "&bidPage="
				+ pageNum
				+ "&callback=TShop.mods.DealRecord.reload&closed=false&t="+token.substring(0,6)+RandomUtils.getRandomNum(token.length()-6);
//				+ "&callback=TShop.mods.DealRecord.reload&closed=false&t="+getToken();

		sb.append(append);

		return sb.toString();
	}
	
	public String getToken(){
		String token = null;
		
		int base = pageStr.indexOf("sys\":{\"now\":");
		int end = pageStr.indexOf(",\"tkn\":", base);

		token = pageStr
				.substring(base + "sys\":{\"now\":".length(), end);
//		log.info(token);
		return token;
	}

	public Document getHtmlDocFromJson(String jsonStr) {
		String tmp = new String((jsonStr.trim()));
		JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(tmp.substring(
				"TShop.mods.DealRecord.reload(".length(), tmp.length() - 1));
		// System.out.println(jsonObj.getString("html"));
		Document doc = Jsoup.parse(jsonObj.getString("html"));
		return doc;
	}

	public int getBuyerSum() {
		return buyerSum;
	}

	public void setBuyerSum(int buyerSum) {
		this.buyerSum = buyerSum;
	}
}
