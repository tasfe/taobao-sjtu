package edu.fudan.autologin.main.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.main.AutoLogin;
import edu.fudan.autologin.pageparser.SearchResultPageParser;
import edu.fudan.autologin.pageparser.TopTenPageParser;
import edu.fudan.autologin.pojos.BasePostInfo;
import edu.fudan.autologin.pojos.CategoryInfo;
import edu.fudan.autologin.utils.PostUtils;
import edu.fudan.autologin.utils.TaobaoUtils;

public class TaobaoAutoLogin implements AutoLogin {
	private static final Logger log = Logger.getLogger(TaobaoAutoLogin.class);
	private DefaultHttpClient httpClient;

	public TaobaoAutoLogin() {
		if (this.httpClient == null) {
			httpClient = new DefaultHttpClient();
			// HttpHost proxy = new HttpHost("web-proxy.cup.hp.com", 8080);
			// httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
			// proxy);
		}

	}

	public void beforeWriteExcel() {
		ExcelUtil.createWorkbook();
		ExcelUtil.createSheets();
	}

	/**
	 * test get request
	 */
	public void testGet() {
		String getUrl = "http://detailskip.taobao.com/json/show_buyer_list.htm?page_size=15&is_start=false&item_type=b&ends=1335361433000&starts=1334756633000&item_id=10821226356&user_tag=471101458&old_quantity=2255&sold_total_num=16&closed=false&seller_num_id=23280614&zhichong=true&title=%D5%FD%C6%B7Cetaphil%CB%BF%CB%FE%DC%BD%CF%B4%C3%E6%C4%CC591ml+%CA%E6%CC%D8%B7%F4%BD%E0%C3%E6%C8%E9+%B1%A3%CA%AA%D0%B6%D7%B1+%C3%F4%B8%D0%BC%A1%B7%F4&bidPage=3&callback=TShop.mods.DealRecord.reload&t=1335326971619";
		GetMethod getMethod = new GetMethod(httpClient, getUrl);
		List<NameValuePair> headers = new ArrayList<NameValuePair>();
		NameValuePair nvp = new BasicNameValuePair("referer",
				"http://item.taobao.com/item.htm?id=10821226356");
		headers.add(nvp);
		getMethod.doGet(headers);// 给get请求添加httphead
		getMethod.printResponse("utf-8");
		getMethod.shutDown();

	}

	public void autoLogin() {

		// 设置基本的post信息
		BasePostInfo basePostInfo = new BasePostInfo();
		basePostInfo
				.setPostPageUrl("https://login.taobao.com/member/login.jhtml");
		basePostInfo.setPostFormId("J_StaticForm");
		basePostInfo
				.setPostFormUrl("https://login.taobao.com/member/login.jhtml");

		// 设置提交表单相关信息
		List<NameValuePair> formFieldsNvps = new ArrayList<NameValuePair>();
		formFieldsNvps.add(new BasicNameValuePair("TPL_username", "gschen163"));
		formFieldsNvps.add(new BasicNameValuePair("TPL_password",
				"3DES_2_000000000000000000000000000000_D1CE4894D9F2334C"));
		formFieldsNvps.add(new BasicNameValuePair("TPL_checkcode", TaobaoUtils
				.getCheckCode(httpClient)));
		formFieldsNvps.add(new BasicNameValuePair("need_check_code", "true"));

		PostUtils.doPost(httpClient, basePostInfo, formFieldsNvps);
	}

	public void doMyWork() {

		List<CategoryInfo> categoryInfos = new ArrayList<CategoryInfo>();

		CategoryInfo ci1 = new CategoryInfo();
		ci1.setCategoryName("洁面");
		ci1.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_MRHF&level3=50011977&up=false");
		categoryInfos.add(ci1);

		CategoryInfo ci2 = new CategoryInfo();
		ci2.setCategoryName("热门手机");
		ci2.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_SJ&level3=TR_RXSJB&up=false");
		categoryInfos.add(ci2);

		CategoryInfo ci3 = new CategoryInfo();
		ci3.setCategoryName("笔记本");
		ci3.setCategoryHref("http://top.taobao.com/level3.php?cat=TR_DNJXGPJ&level3=1101&up=false");
		categoryInfos.add(ci3);

		for (CategoryInfo c : categoryInfos) {
			TopTenPageParser topTenPageParser = new TopTenPageParser(
					httpClient, c.getCategoryHref());
			topTenPageParser.setCategoryInfo(c);
			topTenPageParser.execute();
		}
	}

	public void execute() {
		ExcelUtil.prepare();
		//autoLogin();
		//isLoginSuccess();
		// searchResultPageParser();
		 doMyWork();
		shutDown();
	}

	public void topTenPageParser() {
		String pageUrl = "http://top.taobao.com/level3.php?cat=TR_SJ&level3=TR_RXSJB";
		TopTenPageParser topTenPageParser = new TopTenPageParser(httpClient,
				pageUrl);
		topTenPageParser.execute();
	}

	public void searchResultPageParser() {
		String pageUrl = "http://s.taobao.com/search?source=top_search&q=Apple%2F%C6%BB%B9%FB+iPhone+4S&pspuid=137939469&v=product&p=detail&stp=top.toplist.tr_rxsjb.sellhot.image.1.0&ad_id=&am_id=&cm_id=&pm_id=";
		SearchResultPageParser searchResultPageParser = new SearchResultPageParser(
				httpClient, pageUrl);
		searchResultPageParser.parsePage();
	}

	public void shutDown() {
		log.info("--------------------------------------------------------------------------------------------------------------");
		log.info("COMPLETE ALL TASKS!");
		ExcelUtil.closeWorkbook();
		httpClient.getConnectionManager().shutdown();
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		log.setLevel(Level.INFO);

		TaobaoAutoLogin taobaoAutoLogin = new TaobaoAutoLogin();
		taobaoAutoLogin.execute();
	}
}
