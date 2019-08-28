package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class HostGroup extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long hostGroupId;
	
	private String name;
	
	private String displayName;
	
	private Integer internal;
	
	private Integer flags;
	
	private String tenantId;
	
	public Long getHostGroupId () {
		return hostGroupId;
	}
	
	public void setHostGroupId (Long hostGroupId) {
		this.hostGroupId = hostGroupId;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getDisplayName () {
		return displayName;
	}
	
	public void setDisplayName (String displayName) {
		this.displayName = displayName;
	}
	
	public Integer getInternal () {
		return internal;
	}
	
	public void setInternal (Integer internal) {
		this.internal = internal;
	}
	
	public Integer getFlags () {
		return flags;
	}
	
	public void setFlags (Integer flags) {
		this.flags = flags;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}
}
