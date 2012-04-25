package edu.fudan.autologin.pageparser;

import org.apache.http.client.HttpClient;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.pojos.SellerRateInfo;
/**
 * 
 * 商家信用页面解析
 * @author JustinChen
 *
 */
public class UserRatePageParser extends BasePageParser {

	private SellerRateInfo sellerRateInfo;
	@Override
	public void parsePage() {
		super.parsePage();
	}

	@Override
	public void doNext() {
		super.doNext();
	}

	@Override
	public void writeExcel() {
		ExcelUtil.writeUserRateSheet(sellerRateInfo);
	}

	public UserRatePageParser(HttpClient httpClient, String pageUrl) {
		super(httpClient, pageUrl);
		
		sellerRateInfo = new SellerRateInfo();
	}

}
