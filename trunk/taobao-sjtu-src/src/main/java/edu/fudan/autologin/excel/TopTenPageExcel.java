package edu.fudan.autologin.excel;

import java.util.ArrayList;
import java.util.List;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Logger;

import edu.fudan.autologin.main.impl.CdjxjyTest;
import edu.fudan.autologin.pojos.TopTenItemInfo;

public class TopTenPageExcel extends BaseWriteExcel{
	
	private static final Logger log = Logger.getLogger(CdjxjyTest.class);
	private List<TopTenItemInfo> topTenItemInfos;
	private String sheetName = "TopTenPage";
	
	
	public void openSheet(){
		if(workbook.getSheet("TopTenPage") == null){
			log.info("Create new work sheet and name is: TopTenPage");
			WritableSheet sheet = workbook.createSheet(this.sheetName,0);
		}
	}
	public List<TopTenItemInfo> getTopTenItemInfos() {
		return topTenItemInfos;
	}
	public void setTopTenItemInfos(List<TopTenItemInfo> topTenItemInfos) {
		this.topTenItemInfos = topTenItemInfos;
	}
	public TopTenPageExcel(String excelPath) {
		super(excelPath);
		topTenItemInfos = new ArrayList<TopTenItemInfo>();
		
	}
	/**
	 * 
	 * steps to write records:
	 * 1. open workbook;
	 * 2. create new sheet;
	 * 3. write sheet head;
	 * 4. write a named method and invoke it to write sheet records;
	 * 5. close workbook;
	 */
	public void execute() {
		openWorkbook();//打开workbook
//openSheet();
		createNewSheet(this.sheetName);//创建新的sheet
		List<String> header = new ArrayList<String>();
		header.add("itemName");
		header.add("rank");
		header.add("href");
		log.info("Start to write header:");
		//writeHeader(this.sheetName, header);//写入头部

		log.info("Start to write recoreds:");
		writeSheet1Records(this.sheetName);//创建新的方法，并在此方法中具体的写入数据
		log.info("Sheet row length is:"+sheets.get("TopTenPage").getRows());
		closeWorkbook();
	}
	
	
	public void writeSheet1Records(String sheetName) {
		WritableSheet sheet = sheets.get(sheetName);//根据名称获取具体的sheet对象
		log.info("Sheet row length is:"+sheet.getRows());
		//将list 中的记录写入sheet中
		for(int i = sheet.getRows(); i < this.topTenItemInfos.size(); ++i){
			TopTenItemInfo t = topTenItemInfos.get(i);
			log.info("itemName:	" + t.getItemName());
			Label l1 = new Label(0, i, t.getItemName());
			Label l2 = new Label(1, i, t.getTopRank()+"");
			Label l3 = new Label(2, i, t.getHref());
			
			try {
				sheet.addCell(l1);
				sheet.addCell(l2);
				sheet.addCell(l3);
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
	}
}
