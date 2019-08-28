package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Item extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long itemId;

	private String type;

	private String snmpOid;

	private Long hostId;

	private String name;

	private String displayName;

	private String key;

	private String valueType;

	private Long templateId;

	private String status;

	private String tenantId;

	private String owner;
	
	public Long getItemId () {
		return itemId;
	}
	
	public void setItemId (Long itemId) {
		this.itemId = itemId;
	}
	
	public String getType () {
		return type;
	}
	
	public void setType (String type) {
		this.type = type;
	}
	
	public String getSnmpOid () {
		return snmpOid;
	}
	
	public void setSnmpOid (String snmpOid) {
		this.snmpOid = snmpOid;
	}
	
	public Long getHostId () {
		return hostId;
	}
	
	public void setHostId (Long hostId) {
		this.hostId = hostId;
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
	
	public String getKey () {
		return key;
	}
	
	public void setKey (String key) {
		this.key = key;
	}
	
	public String getValueType () {
		return valueType;
	}
	
	public void setValueType (String valueType) {
		this.valueType = valueType;
	}
	
	public Long getTemplateId () {
		return templateId;
	}
	
	public void setTemplateId (Long templateId) {
		this.templateId = templateId;
	}
	
	public String getStatus () {
		return status;
	}
	
	public void setStatus (String status) {
		this.status = status;
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
