package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Script extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long scriptId;

	private Long userId;

	private String name;

	private String command;

	private Integer hostAccess;

	private Long usrGrpId;

	private Long PftGroupId;

	private String description;

	private String confirmation;

	private Integer type;

	private Integer executeOn;

	private String tenantId;
	
	public Long getScriptId () {
		return scriptId;
	}
	
	public void setScriptId (Long scriptId) {
		this.scriptId = scriptId;
	}
	
	public Long getUserId () {
		return userId;
	}
	
	public void setUserId (Long userId) {
		this.userId = userId;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getCommand () {
		return command;
	}
	
	public void setCommand (String command) {
		this.command = command;
	}
	
	public Integer getHostAccess () {
		return hostAccess;
	}
	
	public void setHostAccess (Integer hostAccess) {
		this.hostAccess = hostAccess;
	}
	
	public Long getUsrGrpId () {
		return usrGrpId;
	}
	
	public void setUsrGrpId (Long usrGrpId) {
		this.usrGrpId = usrGrpId;
	}
	
	public Long getPftGroupId () {
		return PftGroupId;
	}
	
	public void setPftGroupId (Long pftGroupId) {
		PftGroupId = pftGroupId;
	}
	
	public String getDescription () {
		return description;
	}
	
	public void setDescription (String description) {
		this.description = description;
	}
	
	public String getConfirmation () {
		return confirmation;
	}
	
	public void setConfirmation (String confirmation) {
		this.confirmation = confirmation;
	}
	
	public Integer getType () {
		return type;
	}
	
	public void setType (Integer type) {
		this.type = type;
	}
	
	public Integer getExecuteOn () {
		return executeOn;
	}
	
	public void setExecuteOn (Integer executeOn) {
		this.executeOn = executeOn;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}
}
