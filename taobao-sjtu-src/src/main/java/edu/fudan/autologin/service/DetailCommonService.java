package edu.fudan.autologin.service;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pojos.FeedRateComment;
import edu.fudan.autologin.pojos.ItemImpress;
import edu.fudan.autologin.utils.GetWaitUtil;

public class DetailCommonService {
	private static final Logger log = Logger.getLogger(DetailCommonService.class);

	private HttpClient httpClient = new DefaultHttpClient();
	
	private String pageUrl;
	
	
	public String getJsonString(String ajaxUrl){
		String json;
		GetMethod get = new GetMethod(httpClient, ajaxUrl);
		GetWaitUtil.get(get);
		json = get.getResponseAsString();
		get.shutDown();
		
		
		
		int base = json.indexOf("(");
		int end = json.lastIndexOf(")");
		return json.substring(base+1,end);
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	public String getAjaxUrl(){
		StringBuilder ajaxUrl = new StringBuilder();
		Document doc;
		String datacommonApi;
		GetMethod get = new GetMethod(httpClient, pageUrl);
		GetWaitUtil.get(get);
		doc = Jsoup.parse(get.getResponseAsString());
		get.shutDown();

		if(doc.select("div#reviews").size() != 0){
			Element e = doc.select("div#reviews").get(0);
			datacommonApi = e.attr("data-commonApi");
			log.info("data-commonApi is: "+datacommonApi);
			ajaxUrl.append(datacommonApi);
		}else{
			log.info("There is no detail section in the page.");
		}
		
		
		String app = "&callback=jsonp_reviews_summary";
		
		ajaxUrl.append(app);
		log.info("Ajax url is: "+ajaxUrl.toString());
		return ajaxUrl.toString();
	}
	public void execute(){
		
		String ajaxUrl = getAjaxUrl();
		String json = getJsonString(ajaxUrl);
		
		log.info("Json from server is: "+json);
		
		parseJson(json);
		httpClient.getConnectionManager().shutdown();
	}
	
	public void parseJson(String json){
		JSONObject jsonObj = JSONObject.fromObject(json);
		
		JSONObject obj = jsonObj.getJSONObject("data");
		JSONArray array = obj.getJSONArray("impress");
		
		List list = (List) JSONSerializer.toJava(array);
		
		List<ItemImpress> impresses = new ArrayList<ItemImpress>();
		
		for (Object o : list) {

			JSONObject j = JSONObject.fromObject(o);
			ItemImpress impress = new ItemImpress();
			log.info("title is: "+j.getString("title"));
			log.info("count is: "+j.getInt("count"));
		}
	}
}
