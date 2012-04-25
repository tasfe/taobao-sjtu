package edu.fudan.autologin.main.impl;

import org.apache.http.impl.client.DefaultHttpClient;

import edu.fudan.autologin.main.AutoLogin;

public class BaseAutoLogin implements AutoLogin{

	private DefaultHttpClient httpClient;
	
	public BaseAutoLogin(){
		if(this.httpClient.equals(null)){
			this.httpClient = new DefaultHttpClient();
		}
	}

	public void autoLogin() {
		// TODO Auto-generated method stub
		
	}

	public void doMyWork() {
		// TODO Auto-generated method stub
		
	}

	public void execute() {
		// TODO Auto-generated method stub
		
		autoLogin();
		doMyWork();
		shutDown();
		
	}

	public void shutDown() {
		// TODO Auto-generated method stub
		httpClient.getConnectionManager().shutdown();
	}
}
