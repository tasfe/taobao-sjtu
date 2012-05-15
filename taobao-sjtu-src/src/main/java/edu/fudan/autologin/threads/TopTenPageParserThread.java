package edu.fudan.autologin.threads;

import org.apache.http.client.HttpClient;

import edu.fudan.autologin.pageparser.TopTenPageParser;
import edu.fudan.autologin.pojos.CategoryInfo;

public class TopTenPageParserThread implements Runnable {

	
	private HttpClient httpClient;
	private CategoryInfo c;
	
	public TopTenPageParserThread(HttpClient httpClient, CategoryInfo c){
		this.httpClient = httpClient;
		this.c = c;
	}
	public void run() {
		TopTenPageParser topTenPageParser = new TopTenPageParser(httpClient,
				c.getCategoryHref());
		topTenPageParser.setCategoryInfo(c);
		topTenPageParser.execute();
	}

}
