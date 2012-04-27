package edu.fudan.autologin.main.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import edu.fudan.autologin.pojos.CategoryInfo;

public class TestMain {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {

//		String str = "getShippingInfo : \"http://detailskip.taobao.com/json/postage_fee.htm?opt=&catid=50012938&ic=1&id=13871713569&il=%BA%DA%C1%FA%BD%AD%B9%FE%B6%FB%B1%F5&ap=false&ss=false&free=true&tg=false&tid=0&sid=278373237&iv=119.50&up=0.00&exp=0.00&ems=0.00&iw=0&is=\",";
//		Pattern pattern = Pattern.compile("getShippingInfo : \"(.+?)\"");
//		Matcher matcher = pattern.matcher(str);
//		if (matcher.find()){
//			System.out.println(matcher.group(1));
//		}else{
//			System.out.println("no match");
//		}
//		CategoryInfo c1 = new CategoryInfo();
//		CategoryInfo c2 = new CategoryInfo();
//		
////		assert(c2.equals(c1));
////		assert(c1.equals(c2));
//		System.out.println(c2.equals(c1));
		
//		
//		String str = "$callback({type:'buyerPayPostfee',location:'浙江宁波',carriage:'快递:22.00元 EMS:25.00元 平邮:100.00元 '});";
//		String delims = "[{}]+";
//		String[] tokens = str.split(delims);
//		
//		System.out.println(tokens.length);
//		for (int i = 0; i < tokens.length; i++)
//		    System.out.println(tokens[i]);
		
		String str = "$..callback({type:'buyerPayPostfee',location:'浙江宁波',carriage:'快递:22.00元 EMS:25.00元 平邮:100.00元 '});";
		System.out.println(str.substring("$..callback(".length(),str.length()-2));
	}

}
