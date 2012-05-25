package edu.fudan.autologin.pageparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.utils.GetWaitUtil;

/**
 * Steps to parse page is as following; 1. get the specified page; 2. parse the
 * page and put the specified data to the collection; 3. shutdown opened
 * resources;
 * 
 * The derived class must to override the parsePage method in order to get the
 * specified data.
 * 
 * @author JustinChen
 * 
 */
public class BasePageParser implements PageParser {
	private static final Logger log = Logger.getLogger(BasePageParser.class);
	protected String pageUrl;
	protected Document doc;
	protected HttpClient httpClient;

	public static Logger getLog() {
		return log;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public Document getDoc() {
		return doc;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public BasePageParser(HttpClient httpClient, String pageUrl) {
		this.httpClient = httpClient;
		this.pageUrl = pageUrl;
	}

	public void getPage(String pageUrl) {
		GetMethod getMethod = new GetMethod(httpClient, pageUrl);
		GetWaitUtil.get(getMethod);
		try {
			doc = Jsoup.parse(EntityUtils.toString(getMethod.getResponse()
					.getEntity()));
			if (null == doc.baseUri() || 0 == doc.baseUri().length()) {
				String url = getPageUrl();
				String baseUri = "";
				if (url.startsWith("http://") || url.startsWith("https://")) {
					int start = url.indexOf("//") + 2;
					baseUri = url.substring(0, url.indexOf("/", start) + 1);
				} else {
					baseUri = url.substring(0, url.indexOf("/") + 1);
				}
				doc.setBaseUri(baseUri);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			getMethod.shutDown();
		}
	}

	public void shutdown() {

	}

	public void parsePage() {
	}

	public void doNext() {

	}

	public void writeExcel() {

	}

	/**
	 * 先解析页面获得List，再将这个list写入到excel中，再执行下一步工作
	 */
	public void execute() {
		parsePage();
		writeExcel();
		doNext();
	}
}
