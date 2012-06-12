package edu.fudan.autologin.main.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.write.WritableWorkbook;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import edu.fudan.autologin.excel.ExcelUtil;
import edu.fudan.autologin.pojos.CategoryInfo;
import edu.fudan.autologin.utils.XmlConfUtil;

public class TestMain {
	public static void main(String[] args) {

		XmlConfUtil.openXml();
		ExcelUtil.openWorkbook();
		WritableWorkbook wb = ExcelUtil.getWorkbook();
		String[] sheets = wb.getSheetNames();
		
		for(int i = 0; i < sheets.length; ++i){
			System.out.println("Name: "+sheets[i]);
		}
		ExcelUtil.closeWorkbook();	
	}

}
