package edu.fudan.autologin.pageparser;

import java.util.List;

public interface PageParser {

	public void getPage(String pageUrl);
	public void parsePage();
	public void shutdown();
	public void execute();
	public void doNext();
	
	public void writeExcel();
}
