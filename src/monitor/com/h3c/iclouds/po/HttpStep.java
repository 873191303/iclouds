package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class HttpStep extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long httpStepId;
	
	private Long httpTestId;
	
	private String name;
	
	private Integer no;
	
	private String url;
	
	private Integer timeout;
	
	private String posts;
	
	private String required;
	
	private String statusCodes;
	
	private String variables;
	
	private Integer followRedirects;
	
	private Integer retrieveMode;
	
	private String headers;
	
	private String tenantId;
	
	private String owner;
	
	public Long getHttpStepId () {
		return httpStepId;
	}
	
	public void setHttpStepId (Long httpStepId) {
		this.httpStepId = httpStepId;
	}
	
	public Long getHttpTestId () {
		return httpTestId;
	}
	
	public void setHttpTestId (Long httpTestId) {
		this.httpTestId = httpTestId;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public Integer getNo () {
		return no;
	}
	
	public void setNo (Integer no) {
		this.no = no;
	}
	
	public String getUrl () {
		return url;
	}
	
	public void setUrl (String url) {
		this.url = url;
	}
	
	public Integer getTimeout () {
		return timeout;
	}
	
	public void setTimeout (Integer timeout) {
		this.timeout = timeout;
	}
	
	public String getPosts () {
		return posts;
	}
	
	public void setPosts (String posts) {
		this.posts = posts;
	}
	
	public String getRequired () {
		return required;
	}
	
	public void setRequired (String required) {
		this.required = required;
	}
	
	public String getStatusCodes () {
		return statusCodes;
	}
	
	public void setStatusCodes (String statusCodes) {
		this.statusCodes = statusCodes;
	}
	
	public String getVariables () {
		return variables;
	}
	
	public void setVariables (String variables) {
		this.variables = variables;
	}
	
	public Integer getFollowRedirects () {
		return followRedirects;
	}
	
	public void setFollowRedirects (Integer followRedirects) {
		this.followRedirects = followRedirects;
	}
	
	public Integer getRetrieveMode () {
		return retrieveMode;
	}
	
	public void setRetrieveMode (Integer retrieveMode) {
		this.retrieveMode = retrieveMode;
	}
	
	public String getHeaders () {
		return headers;
	}
	
	public void setHeaders (String headers) {
		this.headers = headers;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}
	
	public String getOwner () {
		return owner;
	}
	
	public void setOwner (String owner) {
		this.owner = owner;
	}
}
