package edu.fudan.autologin.utils;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class XmlConfUtil {

	private static final Logger log = Logger.getLogger(XmlConfUtil.class);
	private static final String path = "settings.xml";
	private static Document doc = null;
	
	public static void openXml(){
		try {
			doc = Jsoup.parse(new File("settings.xml"),"UTF-8");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	public static String getValueByName(String name){
		return doc.select(name).first().text();
	}
}
