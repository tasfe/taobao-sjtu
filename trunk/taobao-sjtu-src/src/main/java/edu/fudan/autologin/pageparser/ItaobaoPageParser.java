package edu.fudan.autologin.pageparser;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import edu.fudan.autologin.constants.UserType;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pojos.BuyerInfo;
import edu.fudan.autologin.service.TaobaoDsDataService;
import edu.fudan.autologin.utils.GetWaitUtil;

/**
 * 买家信息页面解析
 * 
 * @author JustinChen
 * 
 */
public class ItaobaoPageParser extends BasePageParser {

	private static final Logger log = Logger.getLogger(ItaobaoPageParser.class);
	private BuyerInfo buyerInfo;

	public void setBuyerInfo(BuyerInfo buyerInfo) {
		this.buyerInfo = buyerInfo;
	}

	
	public void parsePage(String targetUrl){
		GetMethod get = new GetMethod(httpClient, targetUrl);
		GetWaitUtil.get(get);
		String getStr = get.getResponseAsString();
		get.shutDown();
		
		Document doc = Jsoup.parse(getStr);
		get.shutDown();
		
		//buyer gender
		if(doc.select("h3").size() == 0){
			buyerInfo.setSex("0");
		}else{
			Element gender = doc.select("h3").get(0);
			log.info("Text in h3 is: "+gender.ownText());
			if(gender.ownText().contains("他")){
				buyerInfo.setSex("男");
			}else if(gender.ownText().contains("她")){
				buyerInfo.setSex("女");
			}else{
				buyerInfo.setSex("0");
			}
		}
		log.info("Buyer gender is: "+buyerInfo.getSex());
		
		//buyer rank
		if(doc.select("span.profile-rank").size() == 0){
			buyerInfo.setRateScore(0);
		}else{
			if(doc.select("span.profile-rank img").size() == 0){
				buyerInfo.setRateScore(0);
			}else{
				Element span = doc.select("span.profile-rank img").get(0);
				
				String title = span.attr("title");
				log.info("Title is: "+title);
				//568个买家信用积分，请点击查看详情
				
				int rank=0;
				rank = Integer.parseInt(title.substring(0,title.indexOf("个")));
//				log.info(title.substring(0,title.indexOf("个")));
				buyerInfo.setRateScore(rank);
			}
			
		}
		log.info("Buyer rank is: "+buyerInfo.getRateScore());
		
		//buyer location
		if(doc.select("span.J_district").size() == 0){
			buyerInfo.setBuyerAddress("0");
		}else{
			Element e = doc.select("span.J_district").get(0);
			
			TaobaoDsDataService taobaoDsDataService  = new TaobaoDsDataService();
			taobaoDsDataService.setHttpClient(httpClient);
			taobaoDsDataService.execute();
			
			String prov = taobaoDsDataService.getData(e.attr("data-prov"));
			String city = taobaoDsDataService.getData(e.attr("data-city"));
			String area = taobaoDsDataService.getData(e.attr("data-area"));
			
			StringBuffer sb = new StringBuffer();
			String delimeter = "-";
			sb.append(prov);
			sb.append(delimeter);
			sb.append(city);
			sb.append(delimeter);
			sb.append(area);
			
			buyerInfo.setBuyerAddress(sb.toString());
			
			log.info("proc is: "+prov);
			log.info("city is: "+city);
			log.info("area is: "+area);
			
		}
		log.info("Buyer location is: "+buyerInfo.getBuyerAddress());
	}
	//执行解析前确保pageUrl 不为空
	@Override
	public void parsePage() {
		
		log.info("Start to parse Itaobao page.");
		String targetUrl = getAjaxUrl();
		
		GetMethod get = new GetMethod(httpClient, targetUrl);
//		get.doGet();
		
		GetWaitUtil.get(get);
		String getStr = get.getResponseAsString();
		get.shutDown();
		
		Document doc = Jsoup.parse(getStr);
		get.shutDown();
		
		//log.info(getStr);
		
		
		//buyer gender
		if(doc.select("h3").size() == 0){
			buyerInfo.setSex("0");
		}else{
			Element gender = doc.select("h3").get(0);
			log.info("Text in h3 is: "+gender.ownText());
			if(gender.ownText().contains("他")){
				buyerInfo.setSex("男");
			}else if(gender.ownText().contains("她")){
				buyerInfo.setSex("女");
			}else{
				buyerInfo.setSex("0");
			}
		}
		log.info("Buyer gender is: "+buyerInfo.getSex());
		
		//buyer rank
		if(doc.select("span.profile-rank").size() == 0){
			buyerInfo.setRateScore(0);
		}else{
			if(doc.select("span.profile-rank img").size() == 0){
				buyerInfo.setRateScore(0);
			}else{
				Element span = doc.select("span.profile-rank img").get(0);
				
				String title = span.attr("title");
				log.info("Title is: "+title);
				//568个买家信用积分，请点击查看详情
				
				int rank=0;
				rank = Integer.parseInt(title.substring(0,title.indexOf("个")));
//				log.info(title.substring(0,title.indexOf("个")));
				buyerInfo.setRateScore(rank);
			}
			
		}
		log.info("Buyer rank is: "+buyerInfo.getRateScore());
		
		//buyer location
		if(doc.select("span.J_district").size() == 0){
			buyerInfo.setBuyerAddress("0");
			buyerInfo.setIndicator(UserType.NO_ANONYMOUS_NO_ADDRESS);
		}else{
			Element e = doc.select("span.J_district").get(0);
			
			TaobaoDsDataService taobaoDsDataService  = new TaobaoDsDataService();
			taobaoDsDataService.setHttpClient(httpClient);
			taobaoDsDataService.execute();
			
			String prov = taobaoDsDataService.getData(e.attr("data-prov"));
			String city = taobaoDsDataService.getData(e.attr("data-city"));
			String area = taobaoDsDataService.getData(e.attr("data-area"));
			
			StringBuffer sb = new StringBuffer();
			String delimeter = "-";
			sb.append(prov);
			sb.append(delimeter);
			sb.append(city);
			sb.append(delimeter);
			sb.append(area);
			
			buyerInfo.setBuyerAddress(sb.toString());
			buyerInfo.setIndicator(UserType.NO_ANONYMOUS_ADDRESS);
			
			log.info("proc is: "+prov);
			log.info("city is: "+city);
			log.info("area is: "+area);
			
		}
		log.info("Buyer location is: "+buyerInfo.getBuyerAddress());
	}
	
	//http://i.taobao.com/u/NjAwNjgzNDQ=/front/frontInfoGather.htm?viewList=
	//http://i.taobao.com/u/NjAwNjgzNDQ=/front.htm
	public String getAjaxUrl(){
		String appendStr = "/frontInfoGather.htm?viewList=";
		String baseUrl = this.getPageUrl();
		String targetUrl = baseUrl.split(".htm")[0] + appendStr;
		log.info("Target url is: "+targetUrl);
		return targetUrl;
	}

	@Override
	public void writeExcel() {
		///ExcelUtil.writeItemBuyerSheet(buyerInfo);
	}

	public ItaobaoPageParser(HttpClient httpClient, String pageUrl) {
		super(httpClient, pageUrl);
	}

}
