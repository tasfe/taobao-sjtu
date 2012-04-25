package edu.fudan.autologin.pageparser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.pojos.BuyerInfo;

/**
 * 
 * 关于某商品的所有买家页面解析
 * @author JustinChen
 *
 */
public class ItemBuyersPageParser extends BasePageParser {

	private List<BuyerInfo> buyerInfos;
	
	
	@Override
	public void parsePage() {
	}

	@Override
	public void doNext() {
	}

	@Override
	public void writeExcel() {
		ExcelUtil.writeItemBuyerSheet(buyerInfos);
	}

	public ItemBuyersPageParser(HttpClient httpClient, String pageUrl) {
		super(httpClient, pageUrl);
		buyerInfos = new ArrayList<BuyerInfo>();
	}

}
