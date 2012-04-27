package edu.fudan.autologin.pageparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
	
	
	/**
	 * 1. get ItemDetailPage;
	 * 2. get showBuyerListUrl from ItemDetailPage; 
	 * 3.according to taobao rules, construct our showBuyerListUrl list;
	 * 4.according to construted showBuyerListUrl, get json data from server; 
	 * 5.parsing json data from server and get our desired data;
	 * 
	 */
	public void parseShowBuyerListDoc() {
		String itemDetailPageUrl = this.getPageUrl();
		String showBuyerListUrl = getShowBuyerListUrl(itemDetailPageUrl);
		log.debug("ShowBuyerList url is: ");
		log.debug( showBuyerListUrl);
		int pageNum = 1;
		while (true) {
			String constructedShowBuyerListUrl = constructShowBuyerListUrl(
					showBuyerListUrl, pageNum++);
			
			if(parseConstructedShowBuyerListDoc(getShowBuyerListDoc(constructedShowBuyerListUrl)) == false){
				break;//最后一个页面，跳出循环
			}
		}
	}

	/**
	 * 
	 * 当解析到最后一个页面时返回false，其余页面返回true
	 * @param doc
	 * @return
	 */
	public boolean parseConstructedShowBuyerListDoc(Document doc){
		
		return false;
	}
	
	public Document getShowBuyerListDoc(String getUrl) {
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
		Document doc;

		GetMethod getMethod = new GetMethod(this.getHttpClient(), itemDetailPageUrl);
		getMethod.doGet();
		try {
			doc = Jsoup.parse(EntityUtils.toString(getMethod.getResponse()
					.getEntity()));
			Elements eles = doc.select("button#J_listBuyerOnView");

			log.debug("Find elements's size is: "+eles.size());
			for (Element e : eles) {
				String tmp = e.attr("detail:params").trim();
				showBuyerListUrl = tmp.substring(0, tmp.length()
						- ",showBuyerList".length());
			}
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return showBuyerListUrl;
	}

	public String constructShowBuyerListUrl(String showBuyerListUrl, int pageNum) {
		String delims = "[?&]+";
		String[] tokens = showBuyerListUrl.split(delims);
		System.out.println(tokens.length);
		for (int i = 0; i < tokens.length; i++)
			System.out.println(tokens[i]);

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
		System.out.println(sb);

		return sb.toString();
	}

	public Document getHtmlDocFromJson(String jsonStr) {
		String tmp = new String((jsonStr.trim()));
		JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(tmp.substring(
				"TShop.mods.DealRecord.reload(".length(), tmp.length() - 1));
		System.out.println(jsonObj.getString("html"));
		Document doc = Jsoup.parse(jsonObj.getString("html"));
		return doc;
	}
	
	
	
}
