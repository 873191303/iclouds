package com.h3c.iclouds.po.bean.outside;

import java.io.Serializable;

public class TenantNetworkBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2719041292401664997L;
	
	private String id;
	
	private String name;
	
	private String cidr;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCidr() {
		return cidr;
	}

	public void setCidr(String cidr) {
		this.cidr = cidr;
	}
	
	

}
