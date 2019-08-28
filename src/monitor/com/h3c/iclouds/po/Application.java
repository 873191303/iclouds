package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Application extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long hostId;
	
	private String name;
	
	private String displayName;
	
	private Integer flags;
	
	private String uuid;
	
	private String tenantId;
	
	private String owner;
	
	public Long getHostId () {
		return hostId;
	}
	
	public void setHostId (Long hostId) {
		this.hostId = hostId;
	}
	
	public String getDisplayName () {
		return displayName;
	}
	
	public void setDisplayName (String displayName) {
		this.displayName = displayName;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}
	
	public Long getId () {
		return id;
	}
	
	public void setId (Long id) {
		this.id = id;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public Integer getFlags () {
		return flags;
	}
	
	public void setFlags (Integer flags) {
		this.flags = flags;
	}
	
	public String getUuid () {
		return uuid;
	}
	
	public void setUuid (String uuid) {
		this.uuid = uuid;
	}
	
	public String getOwner () {
		return owner;
	}
	
	public void setOwner (String owner) {
		this.owner = owner;
	}
}
