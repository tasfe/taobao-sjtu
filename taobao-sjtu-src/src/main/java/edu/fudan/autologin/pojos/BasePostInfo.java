package edu.fudan.autologin.pojos;


/**
 * 
 * 基本的post相关信息
 * @author JustinChen
 *
 */
public class BasePostInfo {

	private String postPageUrl; //post page url
	private String postFormUrl;// post form's action url
	private String postFormId;//post form id
	
	
	public String getPostPageUrl() {
		return postPageUrl;
	}
	public void setPostPageUrl(String postPageUrl) {
		this.postPageUrl = postPageUrl;
	}
	public String getPostFormUrl() {
		return postFormUrl;
	}
	public void setPostFormUrl(String postFormUrl) {
		this.postFormUrl = postFormUrl;
	}
	public String getPostFormId() {
		return postFormId;
	}
	public void setPostFormId(String postFormId) {
		this.postFormId = postFormId;
	}
	
}
