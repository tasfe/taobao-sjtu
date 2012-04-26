package edu.fudan.autologin.pageparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pojos.ItemInfo;
import edu.fudan.autologin.pojos.Postage;

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
	
	
	public String getPostageUrl(String str) {//获得邮费get请求的url地址
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
	 * @param str
	 * @return
	 */
	public String getIdFromPostageUrl(String str){
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

	
	public Postage getPostageFromJson(String rtnStr){
		Postage postage = new Postage();
		System.out.println(rtnStr);
		Pattern pattern = Pattern.compile("location:'(.+?)'");
		Matcher matcher = pattern.matcher(rtnStr);
		if (matcher.find()) {
			System.out.println(matcher.group(1));
			postage.setLocation(matcher.group(1));
			
		} else {
			System.out.println("no match");
			return null;
		}
		
		Pattern pattern1 = Pattern.compile("carriage:'(.+?)'");
		Matcher matcher1 = pattern1.matcher(rtnStr);
		if (matcher1.find()) {
			System.out.println(matcher1.group(1));
			postage.setCarriage(matcher1.group(1));
		} else {
			System.out.println("no match");
			return null;
		}
		return postage;
	}
	
	
	/**
	 * 
	 * 调用此函数后可以得到邮费的起始地址和邮费
	 * @param getUrl
	 * @return
	 */
	public Postage getPostage() {
		
		Postage postage = new Postage();
		
		GetMethod getMethod = new GetMethod(this.getHttpClient(), this.getPageUrl());
		getMethod.doGet();// 给get请求添加httpheader
		String postageUrl = null;
		postageUrl = getPostageUrl(getMethod.getResponseAsString());
		getMethod.shutDown();

		List<NameValuePair> headers1 = new ArrayList<NameValuePair>();
		NameValuePair nvp1 = new BasicNameValuePair("referer",
				"http://item.taobao.com/item.htm?id="+getIdFromPostageUrl(postageUrl));
		headers1.add(nvp1);
		GetMethod postageGet = new GetMethod(this.getHttpClient(), postageUrl);
		postageGet.doGet(headers1);
		postage = getPostageFromJson(postageGet.getResponseAsString());
		
		postageGet.shutDown();
		return postage;
	}
	
}
