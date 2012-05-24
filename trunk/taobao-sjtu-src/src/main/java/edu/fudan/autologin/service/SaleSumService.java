package edu.fudan.autologin.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pojos.BuyerInfo;
import edu.fudan.autologin.utils.GetWaitUtil;
import net.sf.json.JSONObject;

public class SaleSumService {
	private static final Logger log = Logger.getLogger(SaleSumService.class);
	private String itemPageUrl;
	private HttpClient httpClient = new DefaultHttpClient();

	public String getItemPageUrl() {
		return itemPageUrl;
	}

	public void setItemPageUrl(String itemPageUrl) {
		this.itemPageUrl = itemPageUrl;
	}

	public int getSaleSum() {
		return saleSum;
	}

	public void setSaleSum(int saleSum) {
		this.saleSum = saleSum;
	}

	private int saleSum = 0;

	public void execute() {
		String referer = itemPageUrl;
		String requestUrl = getSaleSumUrl();
		
		if (requestUrl == null) {
			log.info("There is no sale sum url in the page.");
			saleSum = 0;
		} else {
			JSONObject saleNumObj = getJsonFromServer(referer, requestUrl);
			log.info("Sale json string from server is: "
					+ saleNumObj.toString());
			/**
			 * {"quantity":{"quanity":0,"interval":30}}
			 */
			if (saleNumObj.toString().trim().length() == "{\"quantity\":{\"quanity\":0,\"interval\":30}}"
					.length()) {
				JSONObject quantityObj = saleNumObj.getJSONObject("quantity");
				int quanity = quantityObj.getInt("quanity");
				// int interval = quantityObj.getInt("interval");

				log.debug("Sale num string  is: " + quanity);
				saleSum = quanity;

			} else {
				saleSum = saleNumObj.getInt("quanity");
			}
		}

		this.httpClient.getConnectionManager().shutdown();
	}

	/* 从服务器端获取json数据，并解析成jsonObject，由于服务器端返回的是js，需要先获取纯json String */
	public JSONObject getJsonFromServer(String referer, String requestUrl) {

		JSONObject jsonObj = null;
		List<NameValuePair> headers = new ArrayList<NameValuePair>();
		NameValuePair nvp = new BasicNameValuePair("referer", referer);
		headers.add(nvp);
		GetMethod getRequest = new GetMethod(httpClient, requestUrl);
//		getRequest.doGet(headers);
		
		GetWaitUtil.get(getRequest, headers);
		String responseStr = getRequest.getResponseAsString();
		responseStr = responseStr.substring(responseStr.indexOf("{"),
				responseStr.lastIndexOf("}") + 1); // get the plain json string
		jsonObj = JSONObject.fromObject(responseStr);
		getRequest.shutDown();
		return jsonObj;
	}

	public String getSaleSumUrl() {
		String docString;

		GetMethod get = new GetMethod(httpClient, itemPageUrl);
		GetWaitUtil.get(get);
		docString = get.getResponseAsString();
		get.shutDown();

		int base = 0;
		int begin = 0;
		int end = 0;
		if (docString.contains("getDealQuantity")) {
			base = docString.indexOf("getDealQuantity");
			begin = docString.indexOf(":", base);
			end = docString.indexOf(",", begin + 1);
			String saleNumUrl = docString.substring(begin + 1, end).trim();
			
			//
			if(saleNumUrl.contains("\"") == true){
				saleNumUrl = saleNumUrl.replaceAll("\"", "");
			}else{
				return null;
			}
			log.info("saleNumUrl: " + saleNumUrl);
			return saleNumUrl;
		} else {
			log.info("get saleNum url error, not found");
		}

		return null;
	}
}
