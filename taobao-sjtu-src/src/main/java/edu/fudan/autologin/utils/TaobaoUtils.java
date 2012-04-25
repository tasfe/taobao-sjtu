package edu.fudan.autologin.utils;

import java.io.IOException;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.fudan.autologin.formfields.GetMethod;

public class TaobaoUtils {
	/**
	 * 
	 * 在浏览器中打开指定的url，获得验证码
	 * @param httpClient
	 * @return
	 */
	public final static String getCheckCode(HttpClient httpClient){
		String sessionID = TaobaoUtils.getSessionID(httpClient);
		String checkCodeUrl = "https://regcheckcode.taobao.com/auction/checkcode?sessionID="+sessionID;
		DosCmdUtils.open(checkCodeUrl);
		Scanner scanner = new Scanner(System.in);
		System.out.print("请您输入验证码：");
		String verifyCode = scanner.next();
		return verifyCode;
	}
	
	/**
	 * 
	 * 辅助函数，获得sessionID
	 * @param httpClient
	 * @return
	 */
	public final static String getSessionID(HttpClient httpClient){
		String getUrl = "https://login.taobao.com/member/login.jhtml?f=top&redirectURL=http%3A%2F%2Fwww.taobao.com%2F";
		GetMethod getMethod = new GetMethod(httpClient,getUrl);
		getMethod.doGet();
		String sessionID = parseLoginPage(getMethod.getResponse());
		getMethod.shutDown();
		return sessionID;
	}
	
	public final static String parseLoginPage(HttpResponse response){
		Document doc;
		String url = null;
		
		String imgId = "J_StandardCode_m";
		try {
			doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
			Elements eles = doc.select("img#" + imgId);
			
			for (Element e : eles) {
				url = e.attr("data-src");
			}
			
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("------------"+url);
		
		System.out.println("INFO: session id is :"+getSessionID(url));
		return getSessionID(url);
	}
	
	public final static String getSessionID(String str){
		String[] array = str.split("[?*=]");
		return array[2];
	}
}
