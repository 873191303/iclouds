package com.h3c.iclouds.po;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
* @author  zKF7420
* @date 2017年1月7日 下午8:59:41
*/
public class GwRoute implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "name(路由器名称)")
	private String name;
	
	@ApiModelProperty(value = "name(ip地址)")
	private String floatingIp;
	
	public GwRoute() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFloatingIp() {
		return floatingIp;
	}

	public void setFloatingIp(String floatingIp) {
		this.floatingIp = floatingIp;
	}
	
	

}
