package edu.fudan.autologin.pojos;

public class MonthServiceEntity {

	private String nativeValue;
	private String comparison;
	private String avgValue;
	
	private String lineString;//页面上显示的每一行的数据如0.54 天小于1.83 天
	
	
	public String getLineString() {
		return nativeValue + " " + comparison + " "+avgValue;
	}
	public String getNativeValue() {
		return nativeValue;
	}
	public void setNativeValue(String nativeValue) {
		this.nativeValue = nativeValue;
	}
	public String getComparison() {
		return comparison;
	}
	public void setComparison(String comparison) {
		this.comparison = comparison;
	}
	public String getAvgValue() {
		return avgValue;
	}
	public void setAvgValue(String avgValue) {
		this.avgValue = avgValue;
	}
}
