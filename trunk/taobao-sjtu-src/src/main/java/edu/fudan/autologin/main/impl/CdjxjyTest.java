package edu.fudan.autologin.main.impl;

import java.util.concurrent.CountDownLatch;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.fudan.autologin.formfields.FormFields;
import edu.fudan.autologin.formfields.GetMethod;
import edu.fudan.autologin.formfields.PostMethod;
import edu.fudan.autologin.formfields.impl.CdjxjyFormFields;
import edu.fudan.autologin.threads.CdjxjyPostThread;
import edu.fudan.autologin.utils.PrintUtils;

public class CdjxjyTest {
	private static final Logger log = Logger.getLogger(CdjxjyTest.class);
	DefaultHttpClient httpClient;

	public CdjxjyTest() {
		httpClient = new DefaultHttpClient();
		// //设置代理对象 ip/代理名称,端口
		// HttpHost proxy = new HttpHost("proxy.fudan.edu.cn", 8080);
		// //实例化验证
		// CredentialsProvider credsProvider = new BasicCredentialsProvider();
		// //设定验证内容
		// UsernamePasswordCredentials creds = new
		// UsernamePasswordCredentials("10210240089", "fudan123");
		// //创建验证
		// credsProvider.setCredentials(
		// new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
		// creds);
		// httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
		// proxy);
		// ((DefaultHttpClient)httpClient).setCredentialsProvider(credsProvider);
	}

	public void autoLogin() {
		/* login start */
		String loginPostUrl = "http://www.cdjxjy.com/Portal/index.aspx";
		FormFields formFields = new CdjxjyFormFields();
		PostMethod post = new PostMethod(httpClient, loginPostUrl);
		post.setFormFields(formFields);
		post.doPost();
		post.shutDown();
		System.out.println("INFO: httpclient cookies is :");
		PrintUtils.printCookies(httpClient.getCookieStore().getCookies());
		/* login end */
	}

	public void doMyWork() {
		/* after login success, do what you want to do */
		int threadNum = 50;
		CountDownLatch signal = new CountDownLatch(threadNum);
		for (int i = 0; i < threadNum; ++i) {
			new Thread(new CdjxjyPostThread(this.httpClient, signal)).start();
		}
		try {
			signal.await();// 等待所有的子线程都运行结束
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.shutDown();
		}
	}

	public void testCookie(){
		String getUrl="http://www.cdjxjy.com/bbs/aspx/1/webbbsservice.asmx/LoginOther?userlogins=a06020014,xip13zqg";
		GetMethod getMethod = new GetMethod(httpClient, getUrl);
		getMethod.doGet();
		
		PrintUtils.printCookies(this.httpClient.getCookieStore().getCookies());
	}
	public void execute() {
		autoLogin();
		PrintUtils.printCookies(this.httpClient.getCookieStore().getCookies());
		doMyWork();
		shutDown();
	}

	public void shutDown() {
		log.info("has been shutdown connection manager.");
		httpClient.getConnectionManager().shutdown();
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		log.setLevel(Level.INFO);

		CdjxjyTest test = new CdjxjyTest();
		test.execute();
	}
}
