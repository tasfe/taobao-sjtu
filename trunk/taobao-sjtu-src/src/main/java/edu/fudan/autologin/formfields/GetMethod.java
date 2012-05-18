package edu.fudan.autologin.formfields;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes.Name;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.log4j.Logger;

import edu.fudan.autologin.main.impl.TaobaoAutoLogin;

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

	public void doGet() {
		doGet(null);
	}

	public void doGet(List<NameValuePair> headers) {
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

		try {
			response = httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}finally{
			//当get请求出现错误时(一般为超时情况)，处理
			
		}
	}

	public void write2File() {

	}

	// java不支持函数参数默认值这种做法，所以我们只有通过函数重载来解决这个问题
	public void printResponse() {
		printResponse("utf-8");
	}

	public String getResponseAsString(){
		String rtnStr = null;
		try {
			rtnStr = EntityUtils.toString(this.response.getEntity(), "utf-8");
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
			//Ensures that the entity content is fully consumed and the content  stream, if exists, is closed.
			EntityUtils.consume(this.response.getEntity());
			
			//release connection
			httpget.releaseConnection();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//RAII resource acquisition is initializasion
}
