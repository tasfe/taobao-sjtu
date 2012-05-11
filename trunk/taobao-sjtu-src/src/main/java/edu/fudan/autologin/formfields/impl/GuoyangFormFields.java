package edu.fudan.autologin.formfields.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.formfields.FormFields;
import edu.fudan.autologin.formfields.GetMethod;

public class GuoyangFormFields implements FormFields {

	public String getFormHash(HttpClient httpClient) {
		Document doc;

		String getUrl = "http://www.guoyang.cc/polls/poll.php?id=1&iframe=1&bgcolor=fff";
		String formId = "pollform";
		String formhash = null;
		GetMethod getMethod = new GetMethod(httpClient, getUrl);
		getMethod.doGet();
		try {
			doc = Jsoup.parse(EntityUtils.toString(getMethod.getResponse()
					.getEntity()));
			Elements eles = doc.select("form#" + formId + " input");
			for (Element e : eles) {
				if (e.attr("name").equals("formhash")) {
					formhash = e.attr("value");
					System.out.println("INFO: formhash is :" + formhash);
				}
			}
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return formhash;
	}

	public List<NameValuePair> getFormFields(HttpClient httpClient) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		// handlekey=pollresult&id=1&formhash=20ed0d14&iframe=1&bgcolor=FFF&choose_value=14
		// handlekey=pollresult&id=1&formhash=20ed0d14&iframe=1&bgcolor=FFF&choose_value=37
		// handlekey=pollresult&id=1&formhash=20ed0d14&iframe=1&bgcolor=FFF&choose_value=15
		nvps.add(new BasicNameValuePair("handlekey", "pollresult"));
		nvps.add(new BasicNameValuePair("id", "1"));
		nvps.add(new BasicNameValuePair("formhash", getFormHash(httpClient)));
		nvps.add(new BasicNameValuePair("iframe", "1"));
		nvps.add(new BasicNameValuePair("bgcolor", "FFF"));
		nvps.add(new BasicNameValuePair("choose_value", "16"));

		return nvps;
	}

}
