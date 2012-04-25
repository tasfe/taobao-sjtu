package edu.fudan.autologin.pageparser;

import org.apache.http.client.HttpClient;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.pojos.ItemInfo;

public class ItemDetailPageParser extends BasePageParser {

	private ItemInfo itemInfo;
	
	
	//constructor
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
//do parse page here		
	}

	@Override
	public void doNext() {
		
		assert(itemInfo.getItemBuyersHref() != null);
		ItemBuyersPageParser itemBuyersPageParser =  new ItemBuyersPageParser(this.getHttpClient(), itemInfo.getItemBuyersHref());
		itemBuyersPageParser.execute();
		
		assert(itemInfo.getUserRateHref() != null);
		UserRatePageParser userRatePageParser = new UserRatePageParser(this.getHttpClient(), itemInfo.getUserRateHref());
		userRatePageParser.execute();
	}
	
}
