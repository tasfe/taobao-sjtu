package edu.fudan.autologin.pojos;


/**
 * 
 * 基本的认证post相关信息
 * @author JustinChen
 *
 */
public class BaseLoginInfo extends BasePostInfo {

	private String username;					//用户名
	private String password;					//密码
	private String loginFormFiledUsername;  	//登陆表单字段用户名名称
	private String loginFormFieldPassword;  	//登陆表单字段密码名称
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLoginFormFiledUsername() {
		return loginFormFiledUsername;
	}
	public void setLoginFormFiledUsername(String loginFormFiledUsername) {
		this.loginFormFiledUsername = loginFormFiledUsername;
	}
	public String getLoginFormFieldPassword() {
		return loginFormFieldPassword;
	}
	public void setLoginFormFieldPassword(String loginFormFieldPassword) {
		this.loginFormFieldPassword = loginFormFieldPassword;
	}
}
