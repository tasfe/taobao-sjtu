package edu.fudan.autologin.excel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.fudan.autologin.main.impl.CdjxjyTest;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class TestWriteExcel extends BaseWriteExcel {

	private static final Logger log = Logger.getLogger(CdjxjyTest.class);

	public class User {
		private String name;
		private String age;
		private String sex;

		public User(String name, String age, String sex) {
			this.name = name;
			this.age = age;
			this.sex = sex;
		}
	}

	public void writeSheet1Records(String sheetName) {
		WritableSheet sheet = sheets.get(sheetName);//根据名称获取具体的sheet对象

		// construct user records 
		List<User> users = new ArrayList<User>();
		for (int i = 1; i <= 1000; ++i) {
			User u = new User("name_" + i, "age_" + i, "sex_" + i);
			users.add(u);
		}
		
		
		//将list 中的记录写入sheet中
		for(int i = 0; i < users.size(); ++i){
			User u = users.get(i);
			
			// Label(column, row, value)第0行用来做头部了
			Label l1 = new Label(0, i+1, u.name);
			Label l2 = new Label(1, i+1, u.age);
			Label l3 = new Label(2, i+1, u.sex);
			
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
	
	public void writeSheet2Records(String sheetName) {
		WritableSheet sheet = sheets.get(sheetName);

		// construct user records 
		List<User> users = new ArrayList<User>();
		for (int i = 1; i <= 5; ++i) {
			User u = new User("123name_" + i, "123age_" + i, "123sex_" + i);
			users.add(u);
		}
		
		for(int i = 0; i < users.size(); ++i){
			User u = users.get(i);
			Label l1 = new Label(0, i+1, u.name);
			Label l2 = new Label(1, i+1, u.age);
			Label l3 = new Label(2, i+1, u.sex);
			
			try {
				sheet.addCell(l1);
				sheet.addCell(l2);
				sheet.addCell(l3);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public TestWriteExcel(String excelPath) {
		super(excelPath);
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

		createNewSheet("sheet1");//创建新的sheet
		List<String> header = new ArrayList<String>();
		header.add("姓名");
		header.add("年龄");
		header.add("性别");
		writeHeader("sheet1", header);//写入头部
		writeSheet1Records("sheet1");//创建新的方法，并在此方法中具体的写入数据
		
		createNewSheet("sheet2");
		List<String> header1 = new ArrayList<String>();
		header1.add("姓名sdf");
		header1.add("年龄11");
		header1.add("性别sdfd");
		writeHeader("sheet2", header1);
		writeSheet2Records("sheet2");

		closeWorkbook();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		log.setLevel(Level.INFO);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd_hh-mm-ss");

		TestWriteExcel test = new TestWriteExcel("D:\\test1_"
				+ format.format(new Date()) + ".xls");
		test.execute();
	}
}
