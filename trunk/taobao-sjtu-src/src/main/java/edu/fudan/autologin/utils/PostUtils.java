package edu.fudan.autologin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import edu.fudan.autologin.formfields.FormFields;
import edu.fudan.autologin.formfields.PostMethod;
import edu.fudan.autologin.formfields.impl.BaseFormFields;
import edu.fudan.autologin.pojos.BasePostInfo;

public class PostUtils {

	private static final Logger log = Logger.getLogger(PostUtils.class);

	/**
	 * 
	 * 静态的执行post请求
	 * @param httpClient
	 * @param basePostInfo
	 * @param formFieldsNvps
	 */
	public static final void doPost(DefaultHttpClient httpClient, BasePostInfo basePostInfo, List<NameValuePair> formFieldsNvps){
		//构建post表单
		BaseFormFields fromFields = new BaseFormFields();
		fromFields.setBasePostInfo(basePostInfo);
		fromFields.setFormFieldsNvps(formFieldsNvps);
		
		//构建Post请求
		PostMethod postMethod = new PostMethod(httpClient, basePostInfo.getPostFormUrl());
		postMethod.setFormFields(fromFields);
		postMethod.doPost();
		postMethod.shutDown();
	}

}
