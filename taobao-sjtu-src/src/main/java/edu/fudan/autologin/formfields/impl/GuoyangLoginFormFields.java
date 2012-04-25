package edu.fudan.autologin.formfields.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;

import edu.fudan.autologin.formfields.FormFields;

public class GuoyangLoginFormFields implements FormFields {

	public List<NameValuePair> getFormFields(HttpClient httpClient) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//fastloginfield=username&username=testtest123&password=test123&quickforward=yes&handlekey=ls
		// handlekey=pollresult&id=1&formhash=20ed0d14&iframe=1&bgcolor=FFF&choose_value=14
		// handlekey=pollresult&id=1&formhash=20ed0d14&iframe=1&bgcolor=FFF&choose_value=37
		// handlekey=pollresult&id=1&formhash=20ed0d14&iframe=1&bgcolor=FFF&choose_value=15
		nvps.add(new BasicNameValuePair("fastloginfield", "username"));
		nvps.add(new BasicNameValuePair("username", "testtest123"));
		nvps.add(new BasicNameValuePair("password", "test123"));
		nvps.add(new BasicNameValuePair("quickforward", "yes"));
		nvps.add(new BasicNameValuePair("handlekey", "ls"));

		return nvps;
	}

}
