package edu.fudan.autologin.constants;

public class AddrIndicator {
/*
 * 
 * 	1：那么就是前30天，已经有100个上的地址了;
	2：那么就是前30天，没有100个以上的地址的,但是总共有100个地址;
	3：那么就是抓取了所有记录后也没有100个；
 */
	public final static int THIRTY_GT_100 = 1;
	public final static int THIRTY_EQ_100 = 2;
	public final static int THIRTY_LT_100 = 3;
}
