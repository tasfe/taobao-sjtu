package edu.fudan.autologin.service;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.utils.GetWaitUtil;


/**
 * 
 * Search result page 页面上的周销量和周销量的卖家数量
 * @author JustinChen
 *
 */
public class WeekSaleService {
	private static final Logger log = Logger
			.getLogger(WeekSaleService.class);
	private int weekSaleNum = 0;
	private int weekSellerNum = 0;
	private HttpClient httpClient = new DefaultHttpClient();
	private String pageUrl;
	
	
	public int getWeekSaleNum() {
		return weekSaleNum;
	}


	public int getWeekSellerNum() {
		return weekSellerNum;
	}


	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}


	public void execute(){
		String docStr ;
		GetMethod get = new GetMethod(httpClient, pageUrl);
		
//		get.doGet();
		GetWaitUtil.get(get);
		docStr = get.getResponseAsString();
		get.shutDown();
		
		
		Document doc = Jsoup.parse(docStr);
		if( doc.select("dl.product-shortcut dd").size() < 2){
			log.info("There is no product shotcut.");
		}else{
			if( doc.select("dl.product-shortcut dd").get(1).select("strong").size() < 2){
				log.info("There is no week sale num and week seller num");
			}else{
				weekSaleNum = Integer.parseInt(doc.select("dl.product-shortcut dd").get(1).select("strong").get(0).ownText());
				weekSellerNum = Integer.parseInt(doc.select("dl.product-shortcut dd").get(1).select("strong").get(1).ownText());

			}
		}
		
		log.info("Week sale num is: " + weekSaleNum);
		log.info("Week seller num is: "+weekSellerNum);
		httpClient.getConnectionManager().shutdown();
	}
}
