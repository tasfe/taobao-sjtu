package edu.fudan.autologin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.cookie.Cookie;
import org.apache.log4j.Logger;

public class PrintUtils {
	private static final Logger log = Logger.getLogger(PrintUtils.class);

	public static final void printInputStream(InputStream is){
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = "";
		System.out.println("Content is:");
		try {
			while((line = br.readLine())!= null){
				System.out.println("- "+line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static final void printCookies(List<Cookie> cookies){
		log.info("--------------- print cookies start.");
		if (cookies.isEmpty()) {
			System.out.println("INFO: none cookies.");
		} else {
			for (int i = 0; i < cookies.size(); i++) {
				// System.out.println("- " + cookies.get(i).toString());
				//log.info("- " + cookies.get(i).toString());
				System.out.println("INFO: -"+cookies.get(i).toString());
			}
		}
		log.info("---------------- print cookies end.");
	}
	
	public static final void printList(List list){
		List<NameValuePair> nvps = list;
		
		log.info("The details of the list is:");
		
		for(NameValuePair nvp : nvps){
			log.info("["+nvp.getName()+"]"+" "+nvp.getValue());
			//System.out.println("["+nvp.getName()+"]"+" "+nvp.getValue());
		}
	}
}
