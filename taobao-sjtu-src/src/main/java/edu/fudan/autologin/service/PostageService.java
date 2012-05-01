package edu.fudan.autologin.service;

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

import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pojos.Postage;

public class PostageService {
	private static final Logger log = Logger.getLogger(PostageService.class);
	
	private Postage postage;
	public Postage getPostage() {
		return postage;
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
		this.httpClient = httpClient;
	}

	private HttpClient httpClient;
	private String itemPageUrl;
	
	public void dispose(){
		log.info("The item detail page url is: "+ this.itemPageUrl);
		if(this.itemPageUrl.contains("item.taobao.com") == true){
			itemDomainPostageParser();
		}else{
			wtDomainPostageParser();
		}
	}
	
	public void itemDomainPostageParser(){
		GetMethod getMethod = new GetMethod(httpClient, itemPageUrl);
		getMethod.doGet();// 给get请求添加httpheader
		String postageUrl = null;
		postageUrl = getPostageUrl(getMethod.getResponseAsString());
		getMethod.shutDown();

		List<NameValuePair> headers = new ArrayList<NameValuePair>();
		NameValuePair nvp1 = new BasicNameValuePair("referer",
				"http://item.taobao.com/item.htm?id="
						+ getIdFromPostageUrl(postageUrl));
		headers.add(nvp1);
		GetMethod postageGet = new GetMethod(httpClient, postageUrl);
		postageGet.doGet(headers);
		getPostageFromJson(postageGet.getResponseAsString());

		postageGet.shutDown();
	}
	
	public void wtDomainPostageParser(){
		this.postage.setCarriage("none");
		this.postage.setLocation("none");
		log.info("This is wt.taobao.com domain process.");
	}
	/**
	 * 
	 * 调用此函数后可以得到邮费的起始地址和邮费
	 * 
	 * @param getUrl
	 * @return
	 */
	public void parsePostage() {

		dispose();
		
		
	}
	public String getPostageUrl(String str) {// 获得邮费get请求的url地址
		Pattern pattern = Pattern.compile("getShippingInfo:\"(.+?)\"");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			//System.out.println(matcher.group(1));
			return matcher.group(1);
		} else {
			log.error("There is no postage url in the item detail page.");
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
			//System.out.println(matcher.group(1));
			return matcher.group(1);
		} else {
			log.error("There is no id in the postage url.");
			log.info("The postage url is: "+str);
		}

		return null;
	}

	public void getPostageFromJson(String json) {
		//System.out.println(json);
		//System.out.println(json);

		String delimeters = "[()]+";
		String[] tokens = json.split(delimeters);

		//System.out.println(tokens.length);
//		for (int i = 0; i < tokens.length; i++)
//			System.out.println(tokens[i]);

		JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(tokens[1]);

		
		log.info("Type: "+jsonObj.getString("type"));
		log.info("Location: "+jsonObj.getString("location"));
		log.info("Carriage: "+jsonObj.getString("carriage"));
		
		this.postage = new Postage();
		postage.setCarriage(jsonObj.getString("carriage"));
		postage.setLocation(jsonObj.getString("location"));

	}
	
}
