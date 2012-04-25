package edu.fudan.autologin.formfields;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;

/**
 * 
 * 通过返回List<NameValuePair>构造post请求的表单内容，提供多种构造post请求表单的内容
 * @author JustinChen
 *
 */
public interface FormFields {

	public List<NameValuePair> getFormFields(HttpClient httpClient);
}
