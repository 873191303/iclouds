package com.h3c.iclouds.po.bean.model;

import java.io.Serializable;

public class Project2NetworkBean implements Serializable {

	
	private static final long serialVersionUID = -6869875121136632922L;
	
	private String id;
	
	private String cidr;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCidr() {
		return cidr;
	}

	public void setCidr(String cidr) {
		this.cidr = cidr;
	}
}
