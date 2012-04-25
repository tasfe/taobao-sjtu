package edu.fudan.autologin.pageparser;

import org.apache.http.client.HttpClient;

/**
 * 买家信息页面解析
 * @author JustinChen
 *
 */
public class ItaobaoPageParser extends BasePageParser {

	public ItaobaoPageParser(HttpClient httpClient, String pageUrl) {
		super(httpClient, pageUrl);
	}

}
