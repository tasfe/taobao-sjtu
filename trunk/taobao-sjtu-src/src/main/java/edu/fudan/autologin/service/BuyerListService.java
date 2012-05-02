package edu.fudan.autologin.service;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.constants.SexEnum;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pojos.BuyerInfo;

public class BuyerListService {
	private static final Logger log = Logger.getLogger(BuyerListService.class);
	private String itemPageUrl;
	private HttpClient httpClient;
	private List<BuyerInfo> buyerInfos = new ArrayList<BuyerInfo>();
	private int buyerSum = 0;
	
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
	 * 2.get showBuyerListUrl from ItemDetailPage;
	 * 3.according to taobao rules, construct our showBuyerListUrl list;
	 * 4.according to construted showBuyerListUrl, get json data from server;
	 * 5.parsing json data from server and get our desired data;
	 * 
	 */
	public void execute() {
		String showBuyerListUrl = getShowBuyerListUrl(itemPageUrl);
		log.debug("ShowBuyerList url is: " + showBuyerListUrl);
		int pageSize = 15;
		int pageSum = (buyerSum % pageSize == 0) ? buyerSum / pageSize
				: (buyerSum / pageSize + 1);

		log.info("Total page num is: "+pageSum);
		for (int pageNum = 1; pageNum <= pageSum; ++pageNum) {
			log.info("-----------------------------------------------------");
			new Thread(new BuyerListThread(showBuyerListUrl,pageNum)).run();
//			log.info("This is buyers of Page NO: " + pageNum);
//			String constructedShowBuyerListUrl = constructShowBuyerListUrl(
//					showBuyerListUrl, pageNum);
//			parseBuyerListTable(getShowBuyerListDoc(constructedShowBuyerListUrl));
		}
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

	/**
	 * 没有买家时返回的是这个 <div class="msg msg-attention-shortcut"
	 * server-num="detailskip185108.cm4">
	 * <p class="attention naked">
	 * 暂时还没有买家购买此宝贝，最近30天成交0件。
	 * </p>
	 * </div>
	 */
	public void parseBuyerListTable(Document doc) {
		Elements buyerListEls = doc.select("table.tb-list > tbody > tr");
//		buyerCounter += buyerListEls.size();
		for (int i = 0; i < buyerListEls.size(); i++) {

			Element buyerEl = buyerListEls.get(i);
			Elements buyerInfo = buyerEl.select("td.tb-buyer");
			if (0 == buyerInfo.size()) {
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

	public Document getShowBuyerListDoc(String getUrl) {
		assert (getUrl != null);
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

		GetMethod getMethod = new GetMethod(this.getHttpClient(),
				itemDetailPageUrl);
		getMethod.doGet();
		String tmpStr = getMethod.getResponseAsString();
		getMethod.shutDown();

		int base = tmpStr.indexOf("detail:params=\"http");
		int end = tmpStr.indexOf(",showBuyerList", base);

		String myStr = tmpStr
				.substring(base + "detail:params=\"".length(), end);

		showBuyerListUrl = myStr;
		return showBuyerListUrl;
	}

	public String constructShowBuyerListUrl(String showBuyerListUrl, int pageNum) {
		String delims = "[?&]+";
		String[] tokens = showBuyerListUrl.split(delims);
		// System.out.println(tokens.length);
		// for (int i = 0; i < tokens.length; i++)
		// System.out.println(tokens[i]);

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
		// System.out.println(sb);

		return sb.toString();
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
