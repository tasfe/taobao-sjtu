package edu.fudan.autologin.pojos;

public class Postage {
	private String location;//卖家地址
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getCarriage() {
		return carriage;
	}
	public void setCarriage(String carriage) {
		this.carriage = carriage;
	}
	private String carriage;//邮费
	
	

}
