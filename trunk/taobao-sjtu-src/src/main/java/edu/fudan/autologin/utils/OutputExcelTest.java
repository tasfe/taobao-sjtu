package edu.fudan.autologin.utils;


import java.io.File;
import java.io.IOException;
import jxl.*;
import jxl.write.*; 
import jxl.write.biff.RowsExceededException;

public class OutputExcelTest {

public class User{
	private String name;
	private String age;
	private String sex;
	
	public User(String name, String age, String sex){
		this.name = name;
		this.age = age;
		this.sex = sex;
	}
}
	public OutputExcelTest(){
		
	}
	
	public void run(){
		String path = "D:\\output.xls";
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(new File(path));
			WritableSheet sheet = workbook.createSheet("First Sheet", 0); 
			
			try {
				// write headers
				Label label1 = new Label(0, 0, "姓名");
				Label label2 = new Label(1, 0, "年龄");
				Label label3 = new Label(2, 0, "性别");
				
				
				sheet.addCell(label1);
				sheet.addCell(label2);
				sheet.addCell(label3);
				
				
				// write records 
				for(int i = 1; i <= 1000; ++i){
					User u = new User("name_"+i,"age_"+i,"sex_"+i);
					Label l1 = new Label(0,i,u.name);
					Label l2 = new Label(1,i,u.age);
					Label l3 = new Label(2,i,u.sex);
					
					sheet.addCell(l1);
					sheet.addCell(l2);
					sheet.addCell(l3);
					
				}
				
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
			workbook.write();
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		} 
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		OutputExcelTest outputExcelUtils = new OutputExcelTest();
		outputExcelUtils.run();
	}

}
