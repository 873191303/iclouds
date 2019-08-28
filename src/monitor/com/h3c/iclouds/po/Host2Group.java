package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Host2Group extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long hostGroupId;
	
	private Long pftGroupId;
	
	private Long hostId;
	
	private String tenantId;
	
	public Long getHostGroupId () {
		return hostGroupId;
	}
	
	public void setHostGroupId (Long hostGroupId) {
		this.hostGroupId = hostGroupId;
	}
	
	public Long getPftGroupId () {
		return pftGroupId;
	}
	
	public void setPftGroupId (Long pftGroupId) {
		this.pftGroupId = pftGroupId;
	}
	
	public Long getHostId () {
		return hostId;
	}
	
	public void setHostId (Long hostId) {
		this.hostId = hostId;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}
}
