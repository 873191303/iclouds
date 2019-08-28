package com.h3c.iclouds.po.bean;

import java.io.Serializable;
import java.util.List;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class AppInfo implements Serializable {

	private static final long serialVersionUID = -764736011907938740L;

	@ApiModelProperty(value = "应用appId")
	private String appId;
	
	private List<ApplicationBean> data;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public List<ApplicationBean> getData() {
		return data;
	}

	public void setData(List<ApplicationBean> data) {
		this.data = data;
	}

	




}
