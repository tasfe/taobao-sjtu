package edu.fudan.autologin.formfields;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * 
 * 负责构造一次post请求
 * @author JustinChen
 *
 */
public class PostMethod {

	private HttpClient httpClient;
	private String postUrl;
	private HttpResponse response;
	private HttpPost httpPost;
	private FormFields formFields;

	public void setFormFields(FormFields formFields) {
		this.formFields = formFields;
	}

	public HttpResponse getResponse() {
		return response;
	}
	public void printResponse(String charset){
		if(charset.equals(null)){
			charset = "utf-8";
		}
		try {
			System.out.println(EntityUtils.toString(this.response.getEntity(),charset));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	public PostMethod(HttpClient httpClient, String postUrl) {
		if (httpClient == null || postUrl.equals(null)) {
			System.out.println("ERROR: httpclient is null." + PostMethod.class);
		}
		this.httpClient = httpClient;
		this.postUrl = postUrl;
	}

	 
    
	public void doPost() {
		if(httpPost == null){
			 httpPost = new HttpPost(this.postUrl);
		}
		
//		httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.142 Safari/535.19");
//		httpPost.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//		httpPost.setHeader("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
//		httpPost.setHeader("Accept-Encoding","gzip,deflate,sdch");
//		httpPost.setHeader("Accept-Language","zh-CN,zh;q=0.8");
//		httpPost.setHeader("Cache-Control","max-age=0");
//		httpPost.setHeader("Referer"," http://www.guoyang.cc/polls/poll.php?id=1&iframe=1&bgcolor=FFF");
//		httpPost.setHeader("Origin","http://www.guoyang.cc");
//		httpPost.setHeader("Host","www.guoyang.cc");
//		httpPost.setHeader("Proxy-Connection","keep-alive");
//		httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formFields.getFormFields(this.httpClient),
					HTTP.UTF_8));
			System.out.println("INFO: start to execute httppost.");
			response = httpClient.execute(httpPost);
			System.out.println("INFO: complete to execute httppost.");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void shutDown(){
		try {
			System.out.println("INFO: shutdown httppost stream.");
			//Ensures that the entity content is fully consumed, and the content stream, if exists, is closed.
			EntityUtils.consume(this.response.getEntity());
			//release connection
			httpPost.releaseConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
