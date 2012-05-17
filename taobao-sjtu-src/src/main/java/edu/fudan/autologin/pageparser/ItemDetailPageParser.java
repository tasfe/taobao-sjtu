package edu.fudan.autologin.pageparser;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pojos.BuyerInfo;
import edu.fudan.autologin.pojos.ItemInfo;
import edu.fudan.autologin.service.BuyerListService;
import edu.fudan.autologin.service.ItemReviewService;
import edu.fudan.autologin.service.PostageService;
import edu.fudan.autologin.service.ReviewSumService;
import edu.fudan.autologin.service.SaleSumService;

public class ItemDetailPageParser extends BasePageParser {
	private static final Logger log = Logger
			.getLogger(ItemDetailPageParser.class);
	private ItemInfo itemInfo;
	private String postageUrl;
	private String saleNumUrl;
	private String reviewUrl;

	private String sellerId;

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	private List<BuyerInfo> buyerInfos;

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
		itemInfo.setItemDetailHref(pageUrl);
	}

	@Override
	public void writeExcel() {
		ExcelUtil.writeItemDetailSheet(itemInfo);
	}

	public String getShopRankHref(Document doc) {
		if (doc.select("a#shop-rank").size() == 0) {
			log.info("There is no shop rank href in the page and page url is: "
					+ this.getPageUrl());
			return null;
		} else {
			Element shopRankEle = doc.select("a#shop-rank").get(0);
			return shopRankEle.attr("href");
		}
	}

	public void normalPageParser(Document doc){

		if (doc.select("div.tb-property").size() == 0) {

		} else {
			Element itemPro = doc.select("div.tb-property").get(0);
			// price range
			String priceRange = itemPro.getElementById("J_StrPrice").ownText();
			log.info("priceRange: " + priceRange);
			itemInfo.setPriceRange(priceRange);

			// item type
			String itemType = "";
			Element element = itemPro.select("li.tb-item-type em").get(0);
			itemType = element.ownText();
			log.info("itemType: " + itemType);
			itemInfo.setItemType(itemType);

			// pay type
			String payType = "";
			Elements links = itemPro.select("dl.tb-paymethods a");
			for (Element link : links) {
				if (!"#".equals(link.attr("href"))) {
					payType += link.ownText() + ", ";
				}
			}
			payType = payType.substring(0, payType.lastIndexOf(","));
			log.info("payType: " + payType);
			itemInfo.setPayType(payType);

			// service type
			String serviceType = "";
			links = itemPro.select("dl.tb-featured-services a");
			for (Element link : links) {
				serviceType += link.ownText();
			}
			log.info("serviceType: " + serviceType);
			itemInfo.setServiceType(serviceType);
		}

		// seller id
		log.info("sellerId: " + sellerId);
		itemInfo.setSellerId(sellerId);

		String freightPrice = "";
		String location = "";
		String freight = "";

		itemInfo.setUserRateHref(getShopRankHref(doc));
		PostageService postageService = new PostageService();
		postageService.setHttpClient(this.getHttpClient());
		postageService.setItemPageUrl(this.getPageUrl());
		postageService.execute();
		location = postageService.getPostage().getLocation();
		freight = postageService.getPostage().getCarriage();
		freightPrice = location + " : " + freight;
		itemInfo.setFreightPrice(freightPrice);
		log.info("freightPrice: " + freightPrice);

		SaleSumService saleSumService = new SaleSumService();
		saleSumService.setHttpClient(this.getHttpClient());
		saleSumService.setItemPageUrl(this.getPageUrl());
		saleSumService.execute();
		int saleNumIn30Days = saleSumService.getSaleSum();
		log.info("saleNumIn30Days: " + saleNumIn30Days);
		itemInfo.setSaleNumIn30Days(saleNumIn30Days);

		if (doc.select("div#attributes ul.attributes-list li").size() == 0) {

		} else {
			String spec = "";
			String capacity = "";
			Elements elements = doc
					.select("div#attributes ul.attributes-list li");
			//化妆品规格: 正常规格
			spec = elements.get(1).ownText().split(":")[1].trim();
			//化妆品容量: 其它容量
			capacity = elements.get(2).ownText().split(":")[1].trim();
			itemInfo.setCapacity(capacity);
			itemInfo.setSpec(spec);
		}

		// 解析买家列表
//		BuyerListService buyerListService = new BuyerListService();
//		buyerListService.setHttpClient(this.getHttpClient());
//		buyerListService.setItemPageUrl(this.getPageUrl());
//		buyerListService.setBuyerSum(saleNumIn30Days);
//		buyerListService.execute();
//		buyerInfos = buyerListService.getBuyerInfos();
//		log.info("buyerinfo list size is: " + buyerInfos.size());

		// 获得评论总数
		ReviewSumService reviewSumService = new ReviewSumService();
		reviewSumService.setHttpClient(this.getHttpClient());
		reviewSumService.setItemPageUrl(this.getPageUrl());
		reviewSumService.execute();
		itemInfo.setReviews(reviewSumService.getReviewSum());

		// 解析評論
//		ItemReviewService itemReviewService = new ItemReviewService();
//		itemReviewService.setHttpClient(this.getHttpClient());
//		itemReviewService.setItemPageUrl(this.getPageUrl());
//		itemReviewService.setReviewSum(reviewSumService.getReviewSum());
//		itemReviewService.execute();
//		itemInfo.setFirstReviewDate(itemReviewService.getFirstReviewDate());
//		itemInfo.setLastReviewDate(itemReviewService.getLastReviewDate());

	}
	@Override
	public void parsePage() {
		log.info("Start to parse page " + ItemDetailPageParser.class);
		this.getPage(this.getPageUrl());
		Document doc = this.getDoc();
		
		/* 针对各种不同的页面进行不同的处理
		 * 1. 正常页面；
		 * 2. 宝贝下架页面；
		 * 3. 增价拍页面如：http://item.taobao.com/item.htm?id=15577727894
		 * 
		 * 如何区分不同的页面类型？
		**/
		if(doc.toString().contains("增价拍")){
			log.info("Start to parse 增价拍 page.");
			bidTypePageParser();
		}else{
			normalPageParser(doc);
		}
		
	}

	//增价拍页面处理模块
	public void bidTypePageParser() {
		
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

		log.info("Start to parse buyer info page.");
	
		//when there is no buyers.
		if(buyerInfos == null){
			log.info("The list of BuyerInfo is null. There is no buyers.");
		}else{
			log.info("The size of buyer info list is: " + buyerInfos.size());

			for (BuyerInfo buyerInfo : buyerInfos) {
				buyerInfo.setSellerId(sellerId);
				log.info("Buyer href is: "+buyerInfo.getHref());
				// 当用户匿名购买时
				if (buyerInfo.getHref() == null) {
					buyerInfo.setSex("0");
					buyerInfo.setRateScore(0);
					buyerInfo.setBuyerAddress("0");
					ExcelUtil.writeItemBuyerSheet(buyerInfo);
				} else {
					ItaobaoPageParser itaobaoPageParser = new ItaobaoPageParser(
							this.getHttpClient(), buyerInfo.getHref());
					itaobaoPageParser.setBuyerInfo(buyerInfo);
					itaobaoPageParser.execute();
				}
			}
		}
		

		if (itemInfo.getUserRateHref() == null) {
			// 如果商家頁面沒有信用頁面
		} else {
			UserRatePageParser userRatePageParser = new UserRatePageParser(
					this.getHttpClient(), itemInfo.getUserRateHref());
			userRatePageParser.setSellerId(sellerId);
			userRatePageParser.execute();
		}
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
		log.info("Sale json string from server is: " + saleNumObj.toString());
		/**
		 * {"quantity":{"quanity":0,"interval":30}}
		 */
		if (saleNumObj.toString().trim().length() == "{\"quantity\":{\"quanity\":0,\"interval\":30}}"
				.length()) {
			JSONObject quantityObj = saleNumObj.getJSONObject("quantity");
			int quanity = quantityObj.getInt("quanity");
			// int interval = quantityObj.getInt("interval");

			log.debug("Sale num string  is: " + quanity);
			saleNum = quanity;

		} else {
			saleNum = saleNumObj.getInt("quanity");
		}
		return saleNum;
	}

}
