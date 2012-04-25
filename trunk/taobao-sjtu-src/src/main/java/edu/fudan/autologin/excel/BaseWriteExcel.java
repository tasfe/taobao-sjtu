package edu.fudan.autologin.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class BaseWriteExcel implements WriteExcel {
	private static final Logger log = Logger.getLogger(BaseWriteExcel.class.getName());
	private static  int sheetNum = 0;
	protected String path;
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	protected WritableWorkbook workbook;
	protected Map<String,WritableSheet> sheets;
	
	
	public BaseWriteExcel(String excelPath){
		if(excelPath.equals(null)){
			log.debug("The path of the opened excel is:"+this.path);
			log.error("The path of the opened excel is null.");
		}
		this.path = excelPath;
		
		sheets = new HashMap<String,WritableSheet>();
	}

	public void openWorkbook() {
			try {
				workbook = Workbook.createWorkbook(new File(this.path));
				if(workbook == null){
					log.error("Create workbook error.");
				}
				log.info("Open workbook success.");
			} catch (IOException e) {
				e.printStackTrace();
			} 
	}

	public void createNewSheet(String sheetName) {
		// judge the sheetName is in the map
		WritableSheet sheet = null;
		if(workbook.getSheet("TopTenPage") == null){
			sheet = workbook.createSheet("TopTenPage",sheetNum);
		}
		
		if(sheet == null){
			log.error("Create sheet error");
		}
		//WritableSheet sheet = workbook.createSheet("First Sheet",sheetNum);
		sheetNum++;
		sheets.put(sheetName, sheet);
	}

	public void writeHeader(WritableSheet sheet, List<String> header) {
		
		List<Label> labels = new ArrayList<Label>();
		
		for(int i = 0; i < header.size(); ++i){
			Label label = new Label(i,0,header.get(i));
			labels.add(label);
		}
		
		for(Label label : labels){
			try {
				sheet.addCell(label);
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
	}


	
	// write records into spreadsheet before close workbook
	public void closeWorkbook() {
		try {
			this.workbook.write();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			this.workbook.close();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeHeader(String sheetName, List<String> header) {
		writeHeader(sheets.get(sheetName),header);
	}

}
