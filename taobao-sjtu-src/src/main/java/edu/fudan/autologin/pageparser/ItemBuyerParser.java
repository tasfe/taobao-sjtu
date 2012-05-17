package edu.fudan.autologin.pageparser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.pojos.BuyerInfo;
import edu.fudan.autologin.service.BuyerListService;
import edu.fudan.autologin.service.SaleSumService;

public class ItemBuyerParser {

	private static Logger log = Logger.getLogger(ItemBuyerParser.class);

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	private HttpClient httpClient;
	private List<BuyerInfo> buyerInfos = new ArrayList<BuyerInfo>();

	public void execute() {
		String itemPageUrl = "http://item.taobao.com/item.htm?id=16250820776";
		BuyerListService buyerListService = new BuyerListService();
		buyerListService.setHttpClient(httpClient);
		buyerListService.setItemPageUrl(itemPageUrl);
		buyerListService.setBuyerInfos(buyerInfos);

		SaleSumService saleSumService = new SaleSumService();
		saleSumService.setHttpClient(httpClient);
		saleSumService.setItemPageUrl(itemPageUrl);
		saleSumService.execute();
		log.info("Sale sum is: " + saleSumService.getSaleSum());

		buyerListService.setBuyerSum(saleSumService.getSaleSum());
		buyerListService.execute();
	}

	public void doNext() {
		log.info("Start to parse buyer info page.");

		// when there is no buyers.
		if (buyerInfos == null) {
			log.info("The list of BuyerInfo is null. There is no buyers.");
		} else {
			log.info("The size of buyer info list is: " + buyerInfos.size());

			for (BuyerInfo buyerInfo : buyerInfos) {
				// buyerInfo.setSellerId(sellerId);
				log.info("Buyer href is: " + buyerInfo.getHref());
				// 当用户匿名购买时
				if (buyerInfo.getHref() == null) {
					buyerInfo.setSex("0");
					buyerInfo.setRateScore(0);
					buyerInfo.setBuyerAddress("0");
					ExcelUtil.writeItemBuyerSheet(buyerInfo);
				} else {
					ItaobaoPageParser itaobaoPageParser = new ItaobaoPageParser(
							httpClient, buyerInfo.getHref());
					itaobaoPageParser.setBuyerInfo(buyerInfo);
					itaobaoPageParser.execute();
				}
			}
		}
	}
}
