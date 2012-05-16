package edu.fudan.autologin.pageparser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.constants.SheetNames;
import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.excel.TopTenPageExcel;
import edu.fudan.autologin.pojos.CategoryInfo;
import edu.fudan.autologin.pojos.TaobaoDataSet;
import edu.fudan.autologin.pojos.TopTenItemInfo;

public class TopTenPageParser extends BasePageParser {
	
	private static final Logger log = Logger.getLogger(TopTenPageParser.class);
	
	
	private List<TopTenItemInfo> topTenItemInfos;
	private CategoryInfo categoryInfo;
	public CategoryInfo getCategoryInfo() {
		return categoryInfo;
	}

	public void setCategoryInfo(CategoryInfo categoryInfo) {
		this.categoryInfo = categoryInfo;
	}

	@Override
	public void writeExcel() {
		ExcelUtil.writeTopTenSheet( topTenItemInfos);
	}

	public TopTenPageParser(HttpClient httpClient, String pageUrl) {
		super(httpClient, pageUrl);
		topTenItemInfos = new ArrayList<TopTenItemInfo>();
		categoryInfo = new CategoryInfo();
	}

	public List<TopTenItemInfo> getTopTenItemInfos() {
		return topTenItemInfos;
	}

	public void setTopTenItemInfos(List<TopTenItemInfo> topTenItemInfos) {
		this.topTenItemInfos = topTenItemInfos;
	}


	@Override
	public void parsePage(){
		this.getLog().info("start to parse page "+TopTenPageParser.class);
		this.getPage(this.getPageUrl());
		
		Document doc = this.getDoc();
		/* parse top ten page start */
		try {
			Elements itemDiv = doc.select("div.items, div.imagelists");
			Elements itemList = itemDiv.select("ol > li");
			
			for (int i = 0; i < itemList.size() && i < 10; i++) {
				Element item = itemList.get(i);

				String rankStr = item.child(0).text();
				int rank = Integer.valueOf(rankStr);
				Element link = item.select("a.name").get(0);
				String href = link.attr("href");
				href = href.substring(href.indexOf("url=") + 4);
				String itemName = link.text();

				TopTenItemInfo itemInfo = new TopTenItemInfo();
				itemInfo.setTopRank(rank);
				itemInfo.setHref(href);
				itemInfo.setItemName(itemName);
				itemInfo.setCategoryName(this.categoryInfo.getCategoryName());
				
				this.topTenItemInfos.add(itemInfo);
				TaobaoDataSet.topTenItemInfos.add(itemInfo);
				
				this.getLog().info("Top 10 page - add item:");
				this.getLog().info("Top 10 page - rank: "+itemInfo.getTopRank());
				this.getLog().info("Top 10 page - href:" + itemInfo.getHref());
				this.getLog().info("Top 10 page - itemName:" + itemInfo.getItemName());
			}
			
			//setTopTenItemInfos(topTenItemInfos);
		} catch (Exception e) {
			this.getLog().info("Taobao Page Paser Exception:" + e.getMessage());
			e.printStackTrace();
		}
		/* parse top ten page end */
	}

	@Override
	public void doNext(){
		
		log.info("Parsing specified top ten page complete - " + categoryInfo.getCategoryName());
		log.info("--------------------------------------------------------------------------------------------------------------");
		log.info("Start to parse search result page.");
		for(TopTenItemInfo ttii : topTenItemInfos){
			SearchResultPageParser searchResultPageParser = new SearchResultPageParser(this.getHttpClient(), ttii.getHref());
			searchResultPageParser.setTopTenItemInfo(ttii);
			log.info("--------------------------------------------------------------------------------------------------------------");
			log.info("Start to process (TopTenItem, Rank) : " + "("+ttii.getItemName() + ", "+ttii.getTopRank()+")");
			searchResultPageParser.execute();
			try {
				Thread.sleep(1000000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
