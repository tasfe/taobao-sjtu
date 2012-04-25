package edu.fudan.autologin.main.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.main.AutoLogin;
import edu.fudan.autologin.pojos.BasePostInfo;
import edu.fudan.autologin.utils.PostUtils;
import edu.fudan.autologin.utils.PrintUtils;
import edu.fudan.autologin.utils.TaobaoUtils;

public class YihaodianAutoLogin implements AutoLogin {

	private DefaultHttpClient httpClient;

	public YihaodianAutoLogin() {
		if (this.httpClient == null) {
			httpClient = new DefaultHttpClient();
		}

	}

	public void isLoginSuccess() {
		String getUrl = "http://i.taobao.com/my_taobao.htm?sns=true&single=true";
		GetMethod getMethod = new GetMethod(httpClient, getUrl);
		getMethod.doGet();
		getMethod.printResponse("utf-8");
		getMethod.shutDown();

	}

	public void autoLogin() {
		
		//设置基本的post信息
		BasePostInfo basePostInfo = new BasePostInfo();
		basePostInfo.setPostPageUrl("https://passport.yihaodian.com/passport/login_input.do?returnUrl=http%3A%2F%2Fwww.yihaodian.com%2F1%2F");
		basePostInfo.setPostFormId("loginform");
		basePostInfo.setPostFormUrl("https://passport.yihaodian.com/passport/login_input.do?returnUrl=http%3A%2F%2Fwww.yihaodian.com%2F1%2F");
		
		//设置提交表单相关信息
		List<NameValuePair> formFieldsNvps = new ArrayList<NameValuePair>();
		formFieldsNvps.add(new BasicNameValuePair("credentials.username", "10210240089@fudan.edu.cn"));
		formFieldsNvps.add(new BasicNameValuePair("credentials.password", "fudan123"));
		
		PostUtils.doPost(httpClient, basePostInfo, formFieldsNvps);
	}

	public void doMyWork() {

		String getUrl = "http://www.yihaodian.com/usermanager/order/myOrder.do";
		GetMethod getMethod = new GetMethod(httpClient, getUrl);
		getMethod.doGet();
		getMethod.printResponse("utf-8");
		getMethod.shutDown();
	}

	public void execute() {
		autoLogin();
		PrintUtils.printCookies(this.httpClient.getCookieStore().getCookies());
		//isLoginSuccess();
		doMyWork();
		shutDown();
	}

	public void shutDown() {
		httpClient.getConnectionManager().shutdown();
	}

	public static void main(String[] args) {

		YihaodianAutoLogin taobaoAutoLogin = new YihaodianAutoLogin();
		taobaoAutoLogin.execute();
		
	}
}
