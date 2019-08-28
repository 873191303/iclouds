package com.h3c.iclouds.po.bean.model;

import java.io.Serializable;

public class Network2SubnetBean implements Serializable{

	
	private static final long serialVersionUID = -2726269082172729492L;

	private String id;
	
	private String startIp;
	
	private String endIp;
	
	private String networkId;
	
	
	public String getStartIp() {
		return startIp;
	}

	public void setStartIp(String startIp) {
		this.startIp = startIp;
	}

	public String getEndIp() {
		return endIp;
	}

	public void setEndIp(String endIp) {
		this.endIp = endIp;
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
