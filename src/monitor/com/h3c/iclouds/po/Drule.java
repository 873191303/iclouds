package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Drule extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long druleId;
	
	private Long proxyHostId;
	
	private String name;
	
	private String ipRange;
	
	private Integer delay;
	
	private Integer nextCheck;
	
	private Integer status;
	
	private String tenantId;
	
	public Long getDruleId () {
		return druleId;
	}
	
	public void setDruleId (Long druleId) {
		this.druleId = druleId;
	}
	
	public Long getProxyHostId () {
		return proxyHostId;
	}
	
	public void setProxyHostId (Long proxyHostId) {
		this.proxyHostId = proxyHostId;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getIpRange () {
		return ipRange;
	}
	
	public void setIpRange (String ipRange) {
		this.ipRange = ipRange;
	}
	
	public Integer getDelay () {
		return delay;
	}
	
	public void setDelay (Integer delay) {
		this.delay = delay;
	}
	
	public Integer getNextCheck () {
		return nextCheck;
	}
	
	public void setNextCheck (Integer nextCheck) {
		this.nextCheck = nextCheck;
	}
	
	public Integer getStatus () {
		return status;
	}
	
	public void setStatus (Integer status) {
		this.status = status;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}
}
