package edu.fudan.autologin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pojos.Postage;
import edu.fudan.autologin.utils.GetWaitUtil;

public class PostageService {
	private static final Logger log = Logger.getLogger(PostageService.class);

	private Postage postage;

	public Postage getPostage() {
		return postage;
	}

	public PostageService() {
		this.postage = new Postage();
	}

	public void setPostage(Postage postage) {
		this.postage = postage;
	}

	public String getItemPageUrl() {
		return itemPageUrl;
	}

	public void setItemPageUrl(String itemPageUrl) {
		this.itemPageUrl = itemPageUrl;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		// this.httpClient = httpClient;
		this.httpClient = new DefaultHttpClient();
	}

	private HttpClient httpClient;
	private String itemPageUrl;

	public void dispose() {
		log.info("The item detail page url is: " + this.itemPageUrl);
		if (this.itemPageUrl.contains("item.taobao.com") == true) {
			itemDomainPostageParser();
		} else {
			wtDomainPostageParser();
		}
	}

	public void itemDomainPostageParser() {
		GetMethod getMethod = new GetMethod(httpClient, itemPageUrl);
		GetWaitUtil.get(getMethod);
		// getMethod.doGet();// 给get请求添加httpheader
		String postageUrl = null;
		postageUrl = getAjaxUrl(getMethod.getResponseAsString());
		getMethod.shutDown();

		// 当页面中 没有找到
		if (postageUrl == null) {
			postage.setCarriage(null);
			postage.setLocation(null);
			log.info("There is no postage url in the page.");
		} else {
			List<NameValuePair> headers = new ArrayList<NameValuePair>();
			NameValuePair nvp1 = new BasicNameValuePair("referer",
					"http://item.taobao.com/item.htm?id="
							+ getIdFromPostageUrl(postageUrl));
			headers.add(nvp1);
			GetMethod postageGet = new GetMethod(httpClient, postageUrl);
			// postageGet.doGet(headers);
			GetWaitUtil.get(postageGet, headers);
			parseItemInfoJson(postageGet.getResponseAsString());

			postageGet.shutDown();
		}
	}

	private int saleSum = 0;

	public int getSaleSum() {
		return saleSum;
	}

	public void setSaleSum(int saleSum) {
		this.saleSum = saleSum;
	}

	public void wtDomainPostageParser() {
		this.postage.setCarriage("0");
		this.postage.setLocation("0");
		log.info("This is wt.taobao.com domain process.");
	}

	/**
	 * 
	 * 调用此函数后可以得到邮费的起始地址和邮费
	 * 
	 * @param getUrl
	 * @return
	 */
	public void execute() {

		dispose();
		this.httpClient.getConnectionManager().shutdown();

	}

	public String getAjaxUrl(String str) {// 获得邮费get请求的url地址
		String ajaxUrl;
		// 当页面中不包含postage url
		if (str.contains("apiItemInfo")) {
			// Pattern pattern = Pattern.compile("getShippingInfo:\"(.+?)\"");
			// Matcher matcher = pattern.matcher(str);
			// if (matcher.find()) {
			// return matcher.group(1);
			// } else {
			// log.error("There is no postage url in the item detail page.");
			// }

			// "apiItemInfo":"http://detailskip.taobao.com/json/ifq.htm?id=5656200607&sid=934381&p=1&ic=1&il=%B9%E3%B6%AB%C9%EE%DB%DA&ap=1&ss=0&free=0&tg=0&tid=12699&iv=39.00&up=6.00&exp=6.00&ems=21.00&iw=&is=&q=1&ex=0&exs=0&shid=&at=b&arc=&ct=1"
			// ,

			int base = str.indexOf("apiItemInfo\":\"");
			int end = str.indexOf("\"", base + "apiItemInfo\":\"".length());
			ajaxUrl = str
					.substring(base + "apiItemInfo\":\"".length(), end - 1);
			return ajaxUrl;
		} else {
			log.info("There is no apiItemInfo in the page.");
		}
		return null;
	}

	/**
	 * 
	 * 根据postage url地址，获得id
	 * 
	 * @param str
	 * @return
	 */
	public String getIdFromPostageUrl(String str) {
		Pattern pattern = Pattern.compile("&id=(.+?)&");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			// System.out.println(matcher.group(1));
			return matcher.group(1);
		} else {
			log.error("There is no id in the postage url.");
			log.info("The postage url is: " + str);
		}

		return null;
	}

	public void parseItemInfoJson(String json) {
		/*
		 * 
		 * $callback({ postage:{ type:'applyPostage', location:'广东深圳',
		 * destination:'上海', carriage:'快递:7.00元 EMS:21.00元 ', dataUrl:
		 * 'http://delivery.taobao.com/detail/detail.do?itemCount=1&amp;itemId=5656200607&amp;itemValue=39.00&amp;isSellerPay=false&amp;templateId=12699&amp;userId=934381&amp;unifiedPost=6.00&amp;unifiedExpress=6.00&amp;unifiedEms=21.00&amp;weight=0&amp;size=0',
		 * cityId:'310000' } , quantity:{ quanity: 76, interval: 30 } });
		 */
		// String delimeters = "[()]+";
		// String[] tokens = json.split(delimeters);

		String type = "0";
		String location = "0";
		String carriage = "0";

		if (json.contains("(") == true && json.contains(")") == true) {
			JSONObject tmp = JSONObject.fromObject(json.substring(
					json.indexOf("(") + 1, json.lastIndexOf(")")));

			if (json.contains("postage")) {
				JSONObject jsonObj = tmp.getJSONObject("postage");

				if (json.contains("type")) {
					type = jsonObj.getString("type");
				}
				if (json.contains("location")) {
					location = jsonObj.getString("location");
				}
				if (json.contains("carriage")) {
					carriage = jsonObj.getString("carriage");
				}
			}
			if (json.contains("quantity")) {
				JSONObject saleObj = tmp.getJSONObject("quantity");
				
				if(json.contains("quanity")){
					saleSum = saleObj.getInt("quanity");
				}
			}
		}

		log.info("Type: " + type);
		log.info("Location: " + location);
		log.info("Carriage: " + carriage);
		log.info("Sale sum is: " + saleSum);

		postage.setCarriage(carriage);
		postage.setLocation(location);
	}
}
