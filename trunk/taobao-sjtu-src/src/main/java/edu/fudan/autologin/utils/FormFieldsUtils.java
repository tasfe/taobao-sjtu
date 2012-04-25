package edu.fudan.autologin.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.pageparser.LoginPageParser;
import edu.fudan.autologin.pojos.BaseLoginInfo;
import edu.fudan.autologin.pojos.BasePostInfo;

/**
 * 
 * post表单字段构造工具类
 * 
 * @author JustinChen
 * 
 */
public class FormFieldsUtils {

	private static final Logger log = Logger.getLogger(FormFieldsUtils.class);

	/**
	 * 	1. 根据loginPage 页面，获取指定表单id的表单字段，构造一个map; 
	 * 	2. 指定上述构造的map的某些字段的值；
	 *  3. 将上述map转化为list,并返回；
	 * 
	 * @param httpClient
	 * @param baseLoginInfo
	 * @return
	 */
	public final static List<NameValuePair> getNameValuePairList(
			HttpClient httpClient, BasePostInfo baseLoginInfo, List<NameValuePair> formFieldsNvps) {

		Map<String, String> formFieldsMap = getFormFieldsMap(httpClient,
				baseLoginInfo.getPostPageUrl(), baseLoginInfo.getPostFormId());// 获取指定登陆页面的表单字段
		    
		setFormFieldsMap(formFieldsMap, formFieldsNvps);//设置表单登录相关信息
		return convertMap2List(formFieldsMap);
	}
	
	/**
	 * 
	 * 设置登录表单信息
	 * @param formFieldsMap
	 * @param formFields
	 */
	public final static void setFormFieldsMap(Map<String,String> formFieldsMap,List<NameValuePair> formFields){
		for(NameValuePair nvp : formFields){
			formFieldsMap.put(nvp.getName(),nvp.getValue());
		}
	}
	

	/**
	 * 
	 * loginPage页面根据指定的formId获得表单字段并构造map
	 * 
	 * @param response
	 * @param formId
	 * @param formFieldMap
	 */
	public static Map<String, String> getFormFieldsMap(HttpClient httpClient,
			String loginPageUrl, String formId) {
		LoginPageParser loginPageParser = new LoginPageParser(httpClient, loginPageUrl);
		return loginPageParser.getFormFieldsMap(loginPageUrl, formId);
//		Map<String, String> formFieldMap = new HashMap<String, String>();
//		Document doc;
//
//		GetMethod getMethod = new GetMethod(httpClient, loginPageUrl);
//		getMethod.doGet();
//		try {
//			doc = Jsoup.parse(EntityUtils.toString(getMethod.getResponse()
//					.getEntity()));
//			Elements eles = doc.select("form#" + formId + " input");
//			log.info("The size of input tag in the specified form is:"
//					+ eles.size());
//			log.info("The details of the input tag [name] [value] is:");
//			for (Element e : eles) {
//				formFieldMap.put(
//						e.attr("name") == "" ? e.attr("id") : e.attr("name"),
//						e.attr("value"));
//				log.info("[id]" + e.attr("id") + "			[name]" + e.attr("name")
//						+ "				[value]" + e.attr("value"));
//			}
//		} catch (IllegalStateException e1) {
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		return formFieldMap;
	}

	/**
	 * 将Map<NameValuePair>转化为List<NameValuePair>
	 * 
	 * @param formFieldMap
	 * @return
	 */
	public static List<NameValuePair> convertMap2List(
			Map<String, String> formFieldMap) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		// System.out.println("Map is:");
		for (Map.Entry<String, String> entry : formFieldMap.entrySet()) {

			// System.out.println(entry.getKey() + "		" + entry.getValue());
			NameValuePair nvp = new BasicNameValuePair(entry.getKey(),
					entry.getValue());
			nvps.add(nvp);
		}
		return nvps;
	}

}
