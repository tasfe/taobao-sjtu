package edu.fudan.autologin.formfields.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;

import edu.fudan.autologin.formfields.FormFields;

public class CdjxjyPostDataFormFields implements FormFields {

	public List<NameValuePair> getFormFields(HttpClient httpClient) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("postdata", "1"));//每次postdata的值是固定的，不能设为其他的方法
		return nvps;
	}

}
