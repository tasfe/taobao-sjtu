package edu.fudan.autologin.main.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import edu.fudan.autologin.main.AutoLogin;
import edu.fudan.autologin.pojos.BasePostInfo;
import edu.fudan.autologin.utils.PostUtils;
import edu.fudan.autologin.utils.PrintUtils;

public class SonicwrAutoLogin implements AutoLogin {

	private DefaultHttpClient httpClient;
	
	public SonicwrAutoLogin(){
		this.httpClient = new DefaultHttpClient();
	}
	public void autoLogin() {
		//设置基本的post信息
		BasePostInfo basePostInfo = new BasePostInfo();
		basePostInfo.setPostPageUrl("http://sonic.fudan.edu.cn/wr/Account/Login.aspx?ReturnUrl=%2fwr");
		basePostInfo.setPostFormId("ctl01");
		basePostInfo.setPostFormUrl("http://sonic.fudan.edu.cn/wr/Account/Login.aspx?ReturnUrl=%2fwr");
		
		//设置提交表单相关信息
		List<NameValuePair> formFieldsNvps = new ArrayList<NameValuePair>();
		formFieldsNvps.add(new BasicNameValuePair("ctl00$MainContent$LoginUser$UserName", "gongsuochen"));
		formFieldsNvps.add(new BasicNameValuePair("ctl00$MainContent$LoginUser$Password", "123456"));
		
		PostUtils.doPost(httpClient, basePostInfo, formFieldsNvps);
	}

	public void doMyWork() {

	}

	public void execute() {
		autoLogin();
		PrintUtils.printCookies(this.httpClient.getCookieStore().getCookies());
		doMyWork();
		shutDown();
	}

	public void shutDown() {
		httpClient.getConnectionManager().shutdown();
	}
	
	public static void main(String[] args) {
		SonicwrAutoLogin sonicwrAutoLogin = new SonicwrAutoLogin();
		sonicwrAutoLogin.execute();
	}

}
