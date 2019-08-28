package com.h3c.iclouds.po.bean.model;

import java.io.Serializable;

public class AzoneBean implements Serializable {

	private static final long serialVersionUID = 410113101425572903L;
	
	private String uuid;
	
	private String lableName;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLableName() {
		return lableName;
	}

	public void setLableName(String lableName) {
		this.lableName = lableName;
	}
}
