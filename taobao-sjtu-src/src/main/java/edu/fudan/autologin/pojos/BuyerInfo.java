package edu.fudan.autologin.pojos;

import edu.fudan.autologin.constants.SexEnum;

public class BuyerInfo {

	
	private String sellerId;
	private float price;
	private int num;
	private String payTime;
	private String size;
	private int rateScore = 0;
	private String buyerAddress = "0";
	
	private String href;//买家个人信息页面地址
	
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public int getRateScore() {
		return rateScore;
	}
	public void setRateScore(int rateScore) {
		this.rateScore = rateScore;
	}
	public String getBuyerAddress() {
		return buyerAddress;
	}
	public void setBuyerAddress(String buyerAddress) {
		this.buyerAddress = buyerAddress;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	private String sex = "0";
}
