package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Template extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long templateId;
	
	private String templateName;
	
	private String displayName;
	
	private String templateType;
	
	private String tenantId;
	
	private String owner;
	
	public Long getTemplateId () {
		return templateId;
	}
	
	public void setTemplateId (Long templateId) {
		this.templateId = templateId;
	}
	
	public String getTemplateName () {
		return templateName;
	}
	
	public void setTemplateName (String templateName) {
		this.templateName = templateName;
	}
	
	public String getDisplayName () {
		return displayName;
	}
	
	public void setDisplayName (String displayName) {
		this.displayName = displayName;
	}
	
	public String getTemplateType () {
		return templateType;
	}
	
	public void setTemplateType (String templateType) {
		this.templateType = templateType;
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
