package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Host extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long hostId;
	
	private Long proxyHostId;
	
	private String zhost;
	
	private String ihost;
	
	private String displayName;
	
	private String uuid;
	
	private String hostType;
	
	private String macs;
	
	private String tenantId;
	
	private String owner;
	
	public Long getHostId () {
		return hostId;
	}
	
	public void setHostId (Long hostId) {
		this.hostId = hostId;
	}
	
	public Long getProxyHostId () {
		return proxyHostId;
	}
	
	public void setProxyHostId (Long proxyHostId) {
		this.proxyHostId = proxyHostId;
	}
	
	public String getZhost () {
		return zhost;
	}
	
	public void setZhost (String zhost) {
		this.zhost = zhost;
	}
	
	public String getIhost () {
		return ihost;
	}
	
	public void setIhost (String ihost) {
		this.ihost = ihost;
	}
	
	public String getDisplayName () {
		return displayName;
	}
	
	public void setDisplayName (String displayName) {
		this.displayName = displayName;
	}
	
	public String getUuid () {
		return uuid;
	}
	
	public void setUuid (String uuid) {
		this.uuid = uuid;
	}
	
	public String getHostType () {
		return hostType;
	}
	
	public void setHostType (String hostType) {
		this.hostType = hostType;
	}
	
	public String getMacs () {
		return macs;
	}
	
	public void setMacs (String macs) {
		this.macs = macs;
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
