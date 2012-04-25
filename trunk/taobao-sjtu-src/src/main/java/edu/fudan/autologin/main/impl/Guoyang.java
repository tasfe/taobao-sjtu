package edu.fudan.autologin.main.impl;

import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import edu.fudan.autologin.formfields.FormFields;
import edu.fudan.autologin.formfields.PostMethod;
import edu.fudan.autologin.formfields.impl.GuoyangFormFields;
import edu.fudan.autologin.formfields.impl.GuoyangLoginFormFields;
import edu.fudan.autologin.main.AutoLogin;
import edu.fudan.autologin.threads.GuoyangVote;
import edu.fudan.autologin.utils.PrintUtils;
import edu.fudan.autologin.utils.RandomUtils;

public class Guoyang implements AutoLogin {
	DefaultHttpClient httpClient;

	public Guoyang() {
		// 设置动态代理机制
		httpClient = new DefaultHttpClient();
		// 设置代理对象 ip/代理名称,端口
		HttpHost proxy = new HttpHost("10.141.251.173", 3128);
		// HttpHost proxy = new HttpHost("proxy.fudan.edu.cn", 8080);
		// // 实例化验证
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		// // 设定验证内容
		// UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
		// "10210240089", "fudan123");
		// // 创建验证
		// credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST,
		// AuthScope.ANY_PORT), creds);
		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
		((DefaultHttpClient) httpClient).setCredentialsProvider(credsProvider);
	}

	public void setCookie() {

		// Cookie: pqrF_267c_sodateline=1333676226;
		// pqrF_267c_sohash=9cc0f09f995e909137bdac2f515d86f9;
		// pqrF_267c_soid=xfIlKuOF; cnzz_a3990314=4; sin3990314=; rtime=0;
		// ltime=1333680221837; cnzz_eid=54969145-1333671149-;
		// pqrF_267c_lastvisit=1333672623; pqrF_267c_sid=9649Sx;
		// pqrF_267c_lastact=1333680213%09misc.php%09so;
		// pqrF_267c_voted_1=1333680209;
		// z38h_267c_saltkey=3Ad0GD72; z38h_267c_lastvisit=1333676287;
		// z38h_267c_sid=lJK424; z38h_267c_lastact=1333680184%09index.php%09;
		// z38h_267c_connect_last_report_time=2012-04-06;
		// z38h_267c_connect_report_times=5;
		// CNZZDATA3850662=cnzz_eid=55121650-1333679907-&cnzz_a=0&retime=1333679900769&sin=&ltime=1333679900769&rtime=0;
		// z38h_267c_connect_check_token=1; z38h_267c_fid42=1333676810;
		// z38h_267c_secqaaSK9oPnV0=9522j4rB8jx%2B81wfQtnBWXu8anF1AcZtYwqn6DejPncSwssfvMlmxq%2FEV3KQ1%2FEvq2ilvQkk3WzdovprffrHDwb%2FvC8Ln4HHVhgGS9Lq9f6txXuRygp5q7X1;
		// z38h_267c_seccodeSK9oPnV0=b71eVE14AbHHka%2BP207DUCkSF2bz8kvtUC591SJ%2FXM101wFVzp8QMeoiNYMCO%2FLXshkzkD%2BRhSvOmxJthrc;
		// z38h_267c_auth=1d4bij9%2BKYr3kVOSMuFywwZGrici7E6JGbGxl5O1zDKIbH%2FAobbeBf%2BdwljS66%2FyPNuVVtQwaINJe26Nn3FGsks8UA;
		// z38h_267c_connect_is_bind=0;
		// z38h_267c_ulastactivity=23a8jWzOe%2FG02iuypvtDA6nqMrhWzOI4M3NL%2FJy7POkvnmWgwDgU;
		// z38h_267c_noticeTitle=1; z38h_267c_onlineusernum=1076;
		// pqrF_267c_repeat_cookie_1=a%3A1%3A%7Bi%3A1333680209%3Bi%3A15%3B%7D;
		// pqrF_267c_repeat_username_0_1=a%3A1%3A%7Bi%3A1333680209%3Bi%3A15%3B%7D;
		// pqrF_267c_repeat_ip_146.185.21.14_1=a%3A1%3A%7Bi%3A1333680209%3Bi%3A15%3B%7D;
		// pqrF_267c_repeat_so_xfIlKuOF_1=a%3A1%3A%7Bi%3A1333680209%3Bi%3A15%3B%7D
		// Cookie: pqrF_267c_sodateline=1333676226;
		// pqrF_267c_sohash=9cc0f09f995e909137bdac2f515d86f9;
		// pqrF_267c_soid=xfIlKuOF;
		// cnzz_a3990314=6; sin3990314=; rtime=0; ltime=1333680985813;
		// cnzz_eid=54969145-1333671149-;
		// pqrF_267c_lastvisit=1333672623; pqrF_267c_sid=00vvYv;
		// pqrF_267c_lastact=1333680995%09misc.php%09so;
		// pqrF_267c_voted_1=1333680244;
		// CNZZDATA3850662=cnzz_eid=55121650-1333679907-&cnzz_a=0&retime=1333679900769&sin=&ltime=1333679900769&rtime=0;
		// pqrF_267c_repeat_cookie_1=a%3A2%3A%7Bi%3A1333680209%3Bi%3A15%3Bi%3A1333680244%3Bi%3A4%3B%7D;
		// pqrF_267c_repeat_username_0_1=a%3A2%3A%7Bi%3A1333680209%3Bi%3A15%3Bi%3A1333680244%3Bi%3A4%3B%7D;
		// pqrF_267c_repeat_ip_146.185.21.14_1=a%3A2%3A%7Bi%3A1333680209%3Bi%3A15%3Bi%3A1333680244%3Bi%3A4%3B%7D;
		// pqrF_267c_repeat_so_xfIlKuOF_1=a%3A2%3A%7Bi%3A1333680209%3Bi%3A15%3Bi%3A1333680244%3Bi%3A4%3B%7D;
		// *z38h_267c_sid=RLrsWI;
		// *z38h_267c_saltkey=aU2tT5Mh;
		// *z38h_267c_lastvisit=1333676947;
		// *z38h_267c_lastact=1333680587%09home.php%09spacecp;
		// z38h_267c_noticeTitle=1;
		// z38h_267c_connect_last_report_time=2012-04-06;
		// z38h_267c_connect_report_times=5;
		// z38h_267c_connect_check_token=1;
		// *z38h_267c_auth=c916wQ8gJWDygdNRzi%2FnPIX8Vr4ESe%2B8k8K32APcSvP%2FbM3%2F92x4nkHScSfTH6B9CBYBS5GJ5B0KjymG2jjsS%2FTQdw;
		// z38h_267c_connect_is_bind=0;
		// z38h_267c_ulastactivity=b078oiwMFEukF%2BvNDQFW1Bf3cc32pjS9MQ3TqVwgDfnXKPNB9kN%2B

		/* 设置post表单之前的cookie信息开始 */
		String soid = RandomUtils.getRandomString(8);// 根据soid和vote_value来唯一的标识一次投票
		BasicCookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie cookie = new BasicClientCookie(
				"pqrF_267c_sodateline", "1333676685");
		BasicClientCookie cookie1 = new BasicClientCookie("pqrF_267c_sohash",
				"b371007876675265f8e426b340069dd0");
		BasicClientCookie cookie2 = new BasicClientCookie("pqrF_267c_soid",
				soid);
//		BasicClientCookie cookie3 = new BasicClientCookie(
//				"z38h_267c_ulastactivity",
//				"b078oiwMFEukF%2BvNDQFW1Bf3cc32pjS9MQ3TqVwgDfnXKPNB9kN%2B");
//		BasicClientCookie cookie4 = new BasicClientCookie(
//				"z38h_267c_sendmail","1");
//		BasicClientCookie cookie5 = new BasicClientCookie(
//				"z38h_267c_connect_is_bind", "0");
//		BasicClientCookie cookie6 = new BasicClientCookie(
//				"z38h_267c_connect_check_token", "1");
//		BasicClientCookie cookie7 = new BasicClientCookie(
//				"z38h_267c_connect_report_times", "5");
//		BasicClientCookie cookie8 = new BasicClientCookie(
//				"z38h_267c_connect_last_report_time", "2012-04-06");
//		BasicClientCookie cookie9 = new BasicClientCookie(
//				"z38h_267c_noticeTitle", "1");
//		BasicClientCookie cookie10 = new BasicClientCookie("pqrF_267c_loginuser",
//				"deleted");
//		BasicClientCookie cookie11 = new BasicClientCookie("pqrF_267c_activationauth",
//				"deleted");
//		BasicClientCookie cookie12 = new BasicClientCookie("pqrF_267c_pmnum",
//		"deleted");
		cookieStore.addCookie(cookie);
		cookieStore.addCookie(cookie1);
		cookieStore.addCookie(cookie2);
//		cookieStore.addCookie(cookie3);
//		cookieStore.addCookie(cookie4);
//		cookieStore.addCookie(cookie5);
//		cookieStore.addCookie(cookie6);
//		cookieStore.addCookie(cookie11);
//		cookieStore.addCookie(cookie12);
//		cookieStore.addCookie(cookie10);
		httpClient.setCookieStore(cookieStore);
		/* 设置post表单之前的cookie信息结束 */
	}

	public void autoLogin() {

		String postUrl = "http://bbs.guoyang.cc/member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes&inajax=1";
		FormFields formFields = new GuoyangLoginFormFields();
		PostMethod post = new PostMethod(httpClient, postUrl);
		post.setFormFields(formFields);
		post.doPost();
		post.printResponse("utf-8");
		post.shutDown();

	}

	public void doMyWork() {
		// TODO Auto-generated method stub
		/* vote start */
		String loginPostUrl = "http://www.guoyang.cc/polls/poll.php?action=choose&inajax=1";
		FormFields formFields = new GuoyangFormFields();
		PostMethod post = new PostMethod(httpClient, loginPostUrl);
		post.setFormFields(formFields);
		post.doPost();
		post.printResponse("utf-8");
		post.shutDown();

		// PrintUtils.printCookies(httpClient.getCookieStore().getCookies());
		// System.out.println("INFO: httpclient cookies is :");
		// PrintUtils.printCookies(httpClient.getCookieStore().getCookies());
		/* vote end */
	}

	public void execute() {
		setCookie();
		autoLogin();
		doMyWork();
		 PrintUtils.printCookies(httpClient.getCookieStore().getCookies());
		shutDown();
	}

	public void shutDown() {
		httpClient.getConnectionManager().shutdown();
	}

	public static void main(String[] args) {

		int threadNum = 1;
		CountDownLatch signal = new CountDownLatch(threadNum);
		for (int i = 0; i < threadNum; ++i) {
			new Thread(new GuoyangVote(signal)).start();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			signal.await();// 等待所有的子线程都运行结束
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.println("INFO: vote finish.");
		}
	}
}
