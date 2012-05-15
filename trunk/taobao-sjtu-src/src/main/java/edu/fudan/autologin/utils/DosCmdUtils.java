package edu.fudan.autologin.utils;

public class DosCmdUtils {
	/**
	 * 	在浏览器中打开指定的url，浏览器的执行路径可配置
	 */
	public static void open(String url) {
//		String browserPath = "C:\\Users\\JustinChen\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe   ";
		String browserPath = XmlConfUtil.getValueByName("browserPath");
		Runtime rt = Runtime.getRuntime();
		try {
			rt.exec(browserPath + "		"+url);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rt.gc();// 强制回收
		}
	}
}
