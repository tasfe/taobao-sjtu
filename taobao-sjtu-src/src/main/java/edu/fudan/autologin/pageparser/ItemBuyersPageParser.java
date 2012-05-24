package edu.fudan.autologin.pageparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.constants.SexEnum;
import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pojos.BuyerInfo;
import edu.fudan.autologin.utils.GetWaitUtil;

/**
 * 
 * 关于某商品的所有买家页面解析
 * @author JustinChen
 *
 */
public class ItemBuyersPageParser extends BasePageParser {

	private BuyerInfo buyInfo;
	

	public BuyerInfo getBuyInfo() {
		return buyInfo;
	}

	public void setBuyInfo(BuyerInfo buyInfo) {
		this.buyInfo = buyInfo;
	}

	private static final Logger log = Logger.getLogger(ItemBuyersPageParser.class);
	
	@Override
	public void parsePage() {
//		log.info("Start to parse page " + ItemDetailPageParser.class);
//		this.getPage(this.getPageUrl());
//		Document doc = this.getDoc();
//
//		Elements buyerListEls = doc.select("table.tb-list > tbody > tr");
//		for(int i = 0; i < buyerListEls.size(); i ++){
//			Element buyerEl = buyerListEls.get(i);
//			Element buyerInfo = buyerEl.select("td.tb-buyer").get(0);
//			String buyerHref = buyerInfo.select("a:has(img.rank)").get(0).attr("href");
//			Document buyInfoDoc = getBuyerInfo(buyerHref);
//			String buyerAddress = buyInfoDoc.select("div.personal-info div.bd dl dd").get(1).text();
//			if(0 == buyerAddress.length()){
//				buyerAddress = "0";   //标0处理
//			}
//			String rateInfo = buyInfoDoc.select("div.personal-info div.bd ul a#J_BuyerRate").text();
//			int rateScore = Integer.valueOf(rateInfo);
//			String priceStr = buyerEl.select("td.tb-price").get(0).ownText();
//			int price = Integer.valueOf(priceStr);
//			String numStr = buyerEl.select("td.tb-amount").get(0).ownText();
//			int num = Integer.valueOf(numStr);
//			String payTime = buyerEl.select("td.tb-time").get(0).ownText();
//			String size = buyerEl.select("td.tb-sku").text();
//			String sex = SexEnum.unknown;
//			
//			BuyerInfo buyer = new BuyerInfo();
//			buyer.setSellerId("inherate from parent");
//			buyer.setNum(num);
//			buyer.setPayTime(payTime);
//			buyer.setPrice(price);
//			buyer.setSize(size);
//			buyer.setSex(sex);
//			buyer.setRateScore(rateScore);
//			buyer.setBuyerAddress(buyerAddress);
//			
//			//buyerInfos.add(buyer);
//		}
	}

	public Document getBuyerInfo(String href){
		Document doc = null;
		GetMethod getMethod = new GetMethod(getHttpClient(), href);
//		getMethod.doGet();

		GetWaitUtil.get(getMethod);
		try {
			doc = Jsoup.parse(EntityUtils.toString(getMethod.getResponse()
					.getEntity()));
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return doc;
	}
	@Override
	public void doNext() {
	}

	@Override
	public void writeExcel() {
		ExcelUtil.writeItemBuyerSheet(buyInfo);
	}

	public ItemBuyersPageParser(HttpClient httpClient, String pageUrl) {
		super(httpClient, pageUrl);
//		buyerInfos = new ArrayList<BuyerInfo>();
	}

}
