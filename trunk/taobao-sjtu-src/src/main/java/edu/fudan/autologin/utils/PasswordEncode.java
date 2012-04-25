package edu.fudan.autologin.utils;


/*
 * 	表单密码加密接口，提供多种密码加密实现方式
 * 
 */
public interface PasswordEncode {

	public String getEncodePassword(String password);
}
