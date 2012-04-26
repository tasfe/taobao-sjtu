package edu.fudan.autologin.main.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String str = "getShippingInfo : \"http://detailskip.taobao.com/json/postage_fee.htm?opt=&catid=50012938&ic=1&id=13871713569&il=%BA%DA%C1%FA%BD%AD%B9%FE%B6%FB%B1%F5&ap=false&ss=false&free=true&tg=false&tid=0&sid=278373237&iv=119.50&up=0.00&exp=0.00&ems=0.00&iw=0&is=\",";
		Pattern pattern = Pattern.compile("getShippingInfo : \"(.+?)\"");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()){
			System.out.println(matcher.group(1));
		}else{
			System.out.println("no match");
		}
	}
}
