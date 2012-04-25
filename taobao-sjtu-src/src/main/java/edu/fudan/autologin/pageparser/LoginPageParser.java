package edu.fudan.autologin.pageparser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.formfields.GetMethod;

public class LoginPageParser extends BasePageParser {

	@Override
	public void parsePage() {
	}

	public LoginPageParser(HttpClient httpClient, String pageUrl) {
		super(httpClient, pageUrl);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * loginPage页面根据指定的formId获得表单字段并构造map
	 * 
	 * @param response
	 * @param formId
	 * @param formFieldMap
	 */
	public Map<String, String> getFormFieldsMap(String loginPageUrl,
			String formId) {
		Map<String, String> formFieldMap = new HashMap<String, String>();

		try {
			this.getPage(loginPageUrl);
			Elements eles = this.getDoc().select("form#" + formId + " input");
			this.getLog().info("The size of input tag in the specified form is:"
					+ eles.size());
			this.getLog().info("The details of the input tag [name] [value] is:");
			for (Element e : eles) {
				formFieldMap.put(
						e.attr("name") == "" ? e.attr("id") : e.attr("name"),
						e.attr("value"));
				this.getLog().info("[id]" + e.attr("id") + "			[name]" + e.attr("name")
						+ "				[value]" + e.attr("value"));
			}
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		}
		return formFieldMap;
	}

}
