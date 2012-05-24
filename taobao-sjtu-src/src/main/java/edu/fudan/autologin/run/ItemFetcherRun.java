package edu.fudan.autologin.run;

import java.io.File;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import edu.fudan.autologin.main.impl.YihaodianAutoLogin;
import edu.fudan.autologin.pageparser.ItemDetailPageParser;
import edu.fudan.autologin.utils.XmlConfUtil;

public class ItemFetcherRun extends BaseRun {

	@Override
	public void run() {
		itemDetailProcess(2901, 2950);
	}
	

	
	public void itemDetailProcess(int start, int end) {
		try {
			Workbook workbook = Workbook.getWorkbook(new File(XmlConfUtil
					.getValueByName("excelFilePath")));
			Sheet searchResultSheet = workbook.getSheet("SearchReaultSheet");

			WritableWorkbook wbook = Workbook.createWorkbook(new File(
					XmlConfUtil.getValueByName("excelFilePath")), workbook); // 根据book创建一个操作对象
			WritableSheet sh = wbook.getSheet("ItemDetailSheet");// 得到一个工作对象

			// sheet.getRows()返回该页的总行数
			for (int i = start; i <= end; i++) {
				HttpClient tmp = new DefaultHttpClient();
				ItemDetailPageParser itemDetailPageParser = new ItemDetailPageParser(
						tmp, searchResultSheet.getCell(18, i).getContents());
				itemDetailPageParser.setSellerId(searchResultSheet
						.getCell(0, i).getContents());
				log.info("--------------------------------------------------------------------------------------------------------------");
				log.info("This is the item process no: " + i);
				itemDetailPageParser.parsePage();
				itemDetailPageParser.writeExcel(sh);
				tmp.getConnectionManager().shutdown();

			}

			wbook.write();
			try {
				wbook.close();
			} catch (WriteException e) {
				e.printStackTrace();
				log.error("Write excel exception. " + e.getMessage());
			}
			workbook.close();

		} catch (BiffException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {

		}
	}
	
	public static void main(String[] args) {

		ItemFetcherRun run = new ItemFetcherRun();
		run.execute();
	}
}
