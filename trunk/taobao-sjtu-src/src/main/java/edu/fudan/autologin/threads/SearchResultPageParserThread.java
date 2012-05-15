package edu.fudan.autologin.threads;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;

import edu.fudan.autologin.pageparser.SearchResultPageParser;
import edu.fudan.autologin.pojos.TopTenItemInfo;

public class SearchResultPageParserThread implements Runnable {
	private static final Logger log = Logger
			.getLogger(SearchResultPageParserThread.class);
	
	private HttpClient httpClient;
	private TopTenItemInfo ttii;
	
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	public void setTtii(TopTenItemInfo ttii) {
		this.ttii = ttii;
	}
	public void run() {
		SearchResultPageParser searchResultPageParser = new SearchResultPageParser(httpClient, ttii.getHref());
		searchResultPageParser.setTopTenItemInfo(ttii);
		log.info("--------------------------------------------------------------------------------------------------------------");
		log.info("Start to process (TopTenItem, Rank) : " + "("+ttii.getItemName() + ", "+ttii.getTopRank()+")");
		searchResultPageParser.execute();
	}

}
