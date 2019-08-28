package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Host2Template extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long hostId;
	
	private Long templateId;
	
	public Long getId () {
		return id;
	}
	
	public void setId (Long id) {
		this.id = id;
	}
	
	public Long getHostId () {
		return hostId;
	}
	
	public void setHostId (Long hostId) {
		this.hostId = hostId;
	}
	
	public Long getTemplateId () {
		return templateId;
	}
	
	public void setTemplateId (Long templateId) {
		this.templateId = templateId;
	}
}
