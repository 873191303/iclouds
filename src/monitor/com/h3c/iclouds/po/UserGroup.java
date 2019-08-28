package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class UserGroup implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long userGroupId;
	
	private String name;

	private Integer guiAccess;

	private Integer usersStatus;

	private Integer debugMode;
	
	private String tenantId;
	
	public Long getUserGroupId () {
		return userGroupId;
	}
	
	public void setUserGroupId (Long userGroupId) {
		this.userGroupId = userGroupId;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public Integer getGuiAccess () {
		return guiAccess;
	}
	
	public void setGuiAccess (Integer guiAccess) {
		this.guiAccess = guiAccess;
	}
	
	public Integer getUsersStatus () {
		return usersStatus;
	}
	
	public void setUsersStatus (Integer usersStatus) {
		this.usersStatus = usersStatus;
	}
	
	public Integer getDebugMode () {
		return debugMode;
	}
	
	public void setDebugMode (Integer debugMode) {
		this.debugMode = debugMode;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}
}
