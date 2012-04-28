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

		String str = "detail:params=\"http://detailskip.taobao.com/json/show_buyer_list.htm?bid_page=1.page_size=15.is_start=false⁢em_type=b.ends=1336193724000☆ts=1335588924000⁢em_id=13199871937.user_tag=475100160.old_quantity=2683.zhichong=true/d_total_num=16.seller_num_id=183262695.dk=0.title=Apple%2F%C6%BB%B9%FB+iPhone+4S+%B4%F3%C2%BD%D0%D0+%CE%B4%B2%F0%B7%E2%CE%B4%BC%A4%BB%EE+%D4%A4%D7%B0%C8%ED%BC%FE+%B0%FC%D3%CA,showBuyerList";
		int base = str.indexOf("detail:params=\"");
		int begin = str.indexOf("\"",base);
		int end = str.indexOf(",showBuyerList");
		
		System.out.println(str.substring(begin+1,end));
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
//		
//		String str = "$..callback({type:'buyerPayPostfee',location:'浙江宁波',carriage:'快递:22.00元 EMS:25.00元 平邮:100.00元 '});";
//		System.out.println(str.substring("$..callback(".length(),str.length()-2));
	}

}
