package edu.fudan.autologin.excel;

import java.util.List;


public interface WriteExcel {

	public void openWorkbook();
	public void createNewSheet(String sheetName);
	public void writeHeader(String sheetName, List<String> header);
	public void closeWorkbook();
}
