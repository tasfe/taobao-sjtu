package edu.fudan.autologin.service;

import java.io.IOException;
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
import edu.fudan.autologin.utils.FileUtil;
import edu.fudan.autologin.utils.GetWaitUtil;


//item impress
public class DetailCommonService {
	private static final Logger log = Logger
			.getLogger(DetailCommonService.class);

	private HttpClient httpClient = new DefaultHttpClient();

	private String pageUrl;
	
	private List<ItemImpress> impresses = new ArrayList<ItemImpress>();

	public String getJsonString(String ajaxUrl) {
		String json;
		GetMethod get = new GetMethod(httpClient, ajaxUrl);
		GetWaitUtil.get(get);
		json = get.getResponseAsString();
		get.shutDown();

		int base = json.indexOf("(");
		int end = json.lastIndexOf(")");
		return json.substring(base + 1, end);
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getAjaxUrl() {
		
		log.info("Page url is: "+pageUrl);
		StringBuilder ajaxUrl = new StringBuilder();
		Document doc = null;
		String datacommonApi;
		GetMethod get = new GetMethod(httpClient, pageUrl);
		GetWaitUtil.get(get);
		String tmp = get.getResponseAsString();
		get.shutDown();

		doc = Jsoup.parse(tmp);

		if (doc.select("em#J_RateStar").size() != 0) {
			Element e = doc.select("em#J_RateStar").get(0);
			datacommonApi = e.attr("data-commonApi");
			log.info("data-commonApi is: " + datacommonApi);
			ajaxUrl.append(datacommonApi);
		} else {
			log.info("There is no detail section in the page.");
			return null;
		}

		String app = "&callback=jsonp_reviews_summary";

		ajaxUrl.append(app);
		log.info("Ajax url is: " + ajaxUrl.toString());
		return ajaxUrl.toString();
	}

	public void execute() {

		String ajaxUrl = getAjaxUrl();

		if (ajaxUrl == null) {

			log.info("There is no impressess.");
		} else {
			String json = getJsonString(ajaxUrl);
			log.info("Json from server is: " + json);
			parseJson(json);
		}
		httpClient.getConnectionManager().shutdown();
	}

	public void parseJson(String json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		JSONObject obj = jsonObj.getJSONObject("data");
		JSONArray array = obj.getJSONArray("impress");

		List<ItemImpress> list = (List<ItemImpress>) JSONSerializer.toJava(array);

		if(list.size() == 0){
			log.info("There is no impressess.");
		}else{
			for (Object o : list) {

				JSONObject j = JSONObject.fromObject(o);
				ItemImpress impress = new ItemImpress();
				log.info("title is: " + j.getString("title"));
				log.info("count is: " + j.getInt("count"));
				
				impress.setTitle(j.getString("title"));
				impress.setCount(j.getInt("count"));
				
				impresses.add(impress);
			}
		}
		
	}
	
	public String getImpress(){
		StringBuilder sb = new StringBuilder();
		
		
		for(ItemImpress ii : impresses){
			sb.append(ii.getTitle());
			sb.append("("+ii.getCount()+")");
		}
		return sb.toString();
	}
}
