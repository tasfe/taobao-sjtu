package edu.fudan.autologin.run;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import edu.fudan.autologin.utils.XmlConfUtil;

public class BaseRun {
	protected static final Logger log = Logger.getLogger(BaseRun.class);
	protected HttpClient httpClient;

	
	public void setUp(){
		httpClient = new DefaultHttpClient();
		XmlConfUtil.openXml();
		DOMConfigurator.configure("log4j.xml");
	}
	
	public void tearDown(){
		log.info("--------------------------------------------------------------------------------------------------------------");
		log.info("COMPLETE ALL TASKS!");
		httpClient.getConnectionManager().shutdown();
	}

	public void run(){
		
	}
	
	
	public void execute(){
		setUp();
		run();
		tearDown();
	}
}
