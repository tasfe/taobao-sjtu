package edu.fudan.autologin.threads;

import java.util.concurrent.CountDownLatch;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import edu.fudan.autologin.formfields.FormFields;
import edu.fudan.autologin.formfields.PostMethod;
import edu.fudan.autologin.formfields.impl.CdjxjyPostDataFormFields;

public class CdjxjyPostThread implements Runnable{

	private CountDownLatch signal;
	private DefaultHttpClient httpClient;
	
	
	public CdjxjyPostThread(DefaultHttpClient httpClient,CountDownLatch signal){
		this.httpClient = httpClient;
		this.signal = signal;
	}
	
	public void run() {
		String cid = "61c47c0d-1b46-4421-b334-a58c318fe0f0";
		String r = "0.9604169160946615";
		String posturl = "http://www.cdjxjy.com/Course/Background/Studenting.aspx?type1=&cid="
				+ cid + "&r=" + r;

		FormFields cdjxjyPostDataFormFields = new CdjxjyPostDataFormFields();
		PostMethod postMethod = new PostMethod(this.httpClient, posturl);
		postMethod.setFormFields(cdjxjyPostDataFormFields);//设置postMethod的表单构造方式为CdjxjyPostDataFormFields
		postMethod.doPost();
		postMethod.shutDown();
		signal.countDown();
	}
}