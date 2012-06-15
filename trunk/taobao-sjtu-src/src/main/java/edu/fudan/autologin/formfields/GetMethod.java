package edu.fudan.autologin.formfields;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.log4j.Logger;

import edu.fudan.autologin.utils.PingUtil;

/**
 * 
 * 负责构造一次get请求
 * 
 * @author JustinChen
 * 
 */
public class GetMethod {
	private static final Logger log = Logger.getLogger(GetMethod.class);
	private HttpClient httpclient = null;
	private String getUrl = null;
	private HttpResponse response = null;
	private HttpGet httpget = null;

	public HttpResponse getResponse() {
		return response;
	}

	public GetMethod(HttpClient httpClient, String getUrl) {
		if (httpClient == null || getUrl.equals(null)) {
			System.out.println("ERROR: httpclient is null. - "
					+ GetMethod.class);
		}
		this.httpclient = httpClient;
		this.getUrl = getUrl;
	}

	public boolean doGet() {
		return doGet(null);
	}

	public boolean doGet(List<NameValuePair> headers) {
//	//在执行get请求之前， ping 主机，如果能够ping通，再执行下面的工作	
//		URI uri = null;
//		try {
//			uri = new URI(getUrl);
//		} catch (URISyntaxException e1) {
//			e1.printStackTrace();
//			log.error(e1.getStackTrace());
//		}
//		
//		while(PingUtil.pingServer(uri.getHost(), 1000) == false);
		
		
		httpget = new HttpGet(this.getUrl);

		if (headers == null) {

		} else {
			for (NameValuePair nvp : headers) {
				httpget.setHeader(nvp.getName(), nvp.getValue());
			}
		}
		httpget.setHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.83 Safari/535.11");
		// // httpget.setHeader("Connection","keep-alive");
		// httpget.setHeader("Accept-Language","zh-CN,zh;q=0.8");
		// httpget.setHeader("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
		try {
			response = httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			log.error("Client protocol exception");
			log.error(e.getMessage());
			httpget.abort();      
			return false;
		} catch (IOException e) {
			log.error("IO exception");
			log.error(e.getMessage());
			httpget.abort();
			return false;
		} finally {
		}

		return true;
	}

	public void write2File() {

	}

	// java不支持函数参数默认值这种做法，所以我们只有通过函数重载来解决这个问题
	public void printResponse() {
		printResponse("utf-8");
	}

	public String getResponseAsString() {
		HttpEntity entity = response.getEntity();
		String rtnStr = null;
		try {
			
			if(entity != null){
				rtnStr = EntityUtils.toString(entity, "utf-8");
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return rtnStr;
	}

	public void printResponse(String charset) {

		if (this.getResponse().getEntity() == null) {
			log.error("The entity of the response is null.");
		} else {
			try {
				System.out.println(EntityUtils.toString(
						this.response.getEntity(), charset));
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void shutDown() {
		try {
			// Ensures that the entity content is fully consumed and the content
			// stream, if exists, is closed.
			EntityUtils.consume(this.response.getEntity());

			// release connection
			httpget.releaseConnection();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
