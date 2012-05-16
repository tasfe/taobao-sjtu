package edu.fudan.autologin.pageparser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.main.impl.TaobaoAutoLogin;
import edu.fudan.autologin.pojos.SellerInSearchResult;
import edu.fudan.autologin.pojos.TaobaoDataSet;
import edu.fudan.autologin.pojos.TopTenItemInfo;

public class SearchResultPageParser extends BasePageParser {

	private static final Logger log = Logger
			.getLogger(SearchResultPageParser.class);

	private List<SellerInSearchResult> sellerResultList;
	private TopTenItemInfo topTenItemInfo;

	public void setTopTenItemInfo(TopTenItemInfo topTenItemInfo) {
		this.topTenItemInfo = topTenItemInfo;
	}

	public SearchResultPageParser(HttpClient httpClient, String pageUrl) {
		super(httpClient, pageUrl);

		sellerResultList = new ArrayList<SellerInSearchResult>();
		topTenItemInfo = new TopTenItemInfo();
	}

	@Override
	public void doNext() {

		log.info("Parsing specified search result page complete - "
				+ topTenItemInfo.getItemName());
		log.info("--------------------------------------------------------------------------------------------------------------");
		log.info("Start to parse item detail page.");

		for (SellerInSearchResult sellerInSearchResult : sellerResultList) {
			ItemDetailPageParser itemDetailPageParser = new ItemDetailPageParser(
					this.getHttpClient(), sellerInSearchResult.getHref());
			itemDetailPageParser.setSellerId(sellerInSearchResult.getSellerId());
			log.info("--------------------------------------------------------------------------------------------------------------");
			log.info("Start to parse the specified item detail page (TopTenRank, ItemName, Page, PageRank): "+"("+topTenItemInfo.getTopRank()+", "+sellerInSearchResult.getSellerName()+", "+sellerInSearchResult.getPage()+", "+sellerInSearchResult.getRank()+")");
			log.info("Item href is: " + sellerInSearchResult.getHref());
			itemDetailPageParser.execute();
			try {
				Thread.sleep(1000000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void writeExcel() {
		ExcelUtil.writeSearchResultSheet(sellerResultList);
	}

	public void parsePage() {
		log.info("Start to parse page " + SearchResultPageParser.class);
		this.getPage(this.getPageUrl());
		Document doc = this.getDoc();
		int pageNum = 0;
		while (pageNum < 4) {
			log.info("Parsing page " + (pageNum + 1) + " ... ");
			Elements itemList = doc.select("form#bid-form li.list-item");
			for (int i = 0; i < itemList.size(); i++) {
				Element item = itemList.get(i);
				Elements tmall = item.select("div.icon-area > a");

				if (tmall.size() > 0 && "天猫".equals(tmall.get(0).attr("title"))) {
					continue;
				}
				String sellerHref = item.select("p.seller > a").get(0)
						.attr("href");
				String sellerId = sellerHref.substring(sellerHref
						.lastIndexOf("=") + 1);
				String itemHref = item.select("div.photo > a").get(0)
						.attr("href");
				String categoryName = topTenItemInfo.getCategoryName();
				String sellerName = item.select("p.seller > a").get(0).text();
				Elements globalBuy = item.select("p.seller > a.globalbuy");
				boolean isGlobalBuy = false;
				if (globalBuy.size() > 0) {
					isGlobalBuy = true;
				}
				boolean isGoldSeller = false;
				Elements brandSeller = item
						.select("div.icon-area > a.brand-seller");
				if (brandSeller.size() > 0) {
					isGoldSeller = true;
				}
				Elements itemAttr = item.select("ul.attribute");
				Elements priceList = itemAttr.select("li.price");
				String priceStr = priceList.select("em").get(0).text();
				double price = Double.valueOf(priceStr);
				String freightStr = priceList.select("span.shipping").get(0)
						.text();
				freightStr = freightStr.substring(3);
				double freight = Double.valueOf(freightStr);
				Elements creditcard = priceList.select("a.creditcard");
				boolean isCreditcard = false;
				if (creditcard.size() > 0) {
					isCreditcard = true;
				}
				Element placeEl = itemAttr.select("li.place").get(0);
				String sellerAddress = placeEl.ownText();
				String saleNumStr = itemAttr.select("li.sale").get(0).ownText();
				int saleNum = -1;
				if (null == saleNumStr || saleNumStr.length() == 0) {
					saleNum = 0;
				} else {
					int begin = 4;
					int end = saleNumStr.lastIndexOf("笔");
					saleNumStr = saleNumStr.substring(begin, end);
					saleNum = Integer.valueOf(saleNumStr);
				}
				String reviews = itemAttr.select("li.sale > a.reviews").text();
				int reviewsNum = -1;
				if (null == reviews || 0 == reviews.length()) {
					reviewsNum = 0;
				} else {
					int end = reviews.indexOf("条");
					reviews = reviews.substring(0, end);
					reviewsNum = Integer.valueOf(reviews);
				}

				Element legend2 = itemAttr.select("li.legend2").get(0);
				boolean isConsumerPromise = false;
				boolean isLeaveACompensableThree = false;//假一赔三
//				boolean isReBackPay = false;
				boolean isSevenDayReturn = false;
				boolean isQualityItem = false;
				boolean is30DaysMaintain = false;
				if (legend2.select("a.xb-as-fact").size() > 0) {
					isConsumerPromise = true;
				}
				if (legend2.select("a.xb-sevenday-return").size() > 0) {
					isSevenDayReturn = true;
				}
				if (legend2.select("a.xb-quality_item").size() > 0) {
					isQualityItem = true;
				}
				if (legend2.select("a.xb-jia1-pei3").size() > 0) {
					isLeaveACompensableThree = true;
				}
				if (legend2.select("a.xb-thirtyday-repair").size() > 0) {
					is30DaysMaintain = true;
				}

				int rank = i + 1;

				SellerInSearchResult sellerItemInfo = new SellerInSearchResult();
				sellerItemInfo.setSellerId(sellerId);
				sellerItemInfo.setHref(itemHref);
				sellerItemInfo.setCategoryName(categoryName);
				sellerItemInfo.setSellerName(sellerName);
				sellerItemInfo.setGlobalBuy(isGlobalBuy);
				sellerItemInfo.setGoldSeller(isGoldSeller);
				sellerItemInfo.setPrice(price);
				sellerItemInfo.setFreightPrice(freight);
				sellerItemInfo.setCreditCardPay(isCreditcard);
				sellerItemInfo.setSellerAddress(sellerAddress);
				sellerItemInfo.setSaleNum(saleNum);
				sellerItemInfo.setReviews(reviewsNum);
				sellerItemInfo.setConsumerPromise(isConsumerPromise);
//				sellerItemInfo.setReBackPay(isReBackPay);
				sellerItemInfo.setSevenDayReturn(isSevenDayReturn);
				sellerItemInfo.setQualityItem(isQualityItem);
				sellerItemInfo.setIs30DaysMaintain(is30DaysMaintain);
				sellerItemInfo.setPage(pageNum + 1);
				sellerItemInfo.setRank(rank);
				sellerItemInfo.setIsLeaveACompensableThree(isLeaveACompensableThree);
				
				log.info("----------------------------------------");
				log.info("seller name is: "+sellerName);
				log.info("isConsumerPromise "+isConsumerPromise);
				log.info("is30DaysMaintain "+is30DaysMaintain);
				log.info("isCreditcard "+isCreditcard);
				log.info("isQualityItem "+isQualityItem);
				log.info("isLeaveACompensableThree "+isLeaveACompensableThree);
				log.info("isSevenDayReturn "+isSevenDayReturn);

				sellerResultList.add(sellerItemInfo);
				TaobaoDataSet.sellerInSearchResults.add(sellerItemInfo);

			}
			Elements pageUrl = doc.select("div.page-bottom > a.page-next");
			if (pageUrl.size() > 0) {
				String href = pageUrl.get(0).attr("abs:href");
				setPageUrl(href);
			} else {
				break;
			}
			this.getPage(this.getPageUrl());
			doc = this.getDoc();
			pageNum++;
		}

		log.info("The size of all the item in the list is: "
				+ sellerResultList.size());
		// System.out.println(sellerResultList.size());
	}
}
