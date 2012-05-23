package edu.fudan.autologin.formfields.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;

import edu.fudan.autologin.formfields.FormFields;

public class MonthServicePost implements FormFields {

	public List<NameValuePair> getFormFields(HttpClient httpClient) {
		List<NameValuePair> formFields = new ArrayList<NameValuePair>();
		
		//http://ratehis.taobao.com/monthServiceAjax.htm?monthuserid=12305085&userTag=403798032&isB2C=&data=%22%22
		NameValuePair monthuserid = new BasicNameValuePair("monthuserid","12305085"); 
		NameValuePair userTag = new BasicNameValuePair("userTag","403798032");
		NameValuePair isB2C = new BasicNameValuePair("isB2C","");
		NameValuePair data = new BasicNameValuePair("data","");
		
		
		formFields.add(monthuserid);
		formFields.add(userTag);
		formFields.add(isB2C);
		formFields.add(data);
		
		return formFields;
	}

}
