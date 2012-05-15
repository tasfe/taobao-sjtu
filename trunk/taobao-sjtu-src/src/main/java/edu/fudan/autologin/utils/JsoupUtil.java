package edu.fudan.autologin.utils;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupUtil {

	public final static Document getDoc(String getUrl){
		
		Document doc = null;
		try {
			doc = Jsoup.connect(getUrl).get();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return doc;
	}
}
