package edu.fudan.autologin.pageparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.main.impl.TaobaoAutoLogin;
import edu.fudan.autologin.pojos.ItemInfo;
import edu.fudan.autologin.pojos.Postage;

public class ItemDetailPageParser extends BasePageParser {
	private static final Logger log = Logger.getLogger(ItemDetailPageParser.class);
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

	/**
	 * 
	 * parse json string in order to get location,carriage and other info
	 * @param rtnStr
	 * @return
	 */
	public Postage getPostageFromJson(String jsonStr){
		Postage postage = new Postage();
		
		log.debug("The json string of postage from server is: "+jsonStr);
		
		String delimeters = "[()]+";
		String[] tokens = jsonStr.split(delimeters);//split the string to get json data
		
		/**
		 	{
				type:'buyerPayPostfee',
				location:'浙江宁波',
				carriage:'快递:22.00元 EMS:25.00元 平邮:100.00元 '
			}
		 */
		
		JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(tokens[1]);
		
		postage.setCarriage(jsonObj.getString("carriage"));
		postage.setLocation(jsonObj.getString("location"));
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
