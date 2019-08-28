package com.h3c.iclouds.po.bean;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/5/2.
 */
public class EiOrgBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String orgId;
	
	private String orgName;//登录名称
	
	private Integer priority;//用户权重
	
	private String siteId;//用户站点信息
	
	public String getOrgId () {
		return orgId;
	}
	
	public void setOrgId (String orgId) {
		this.orgId = orgId;
	}
	
	public String getOrgName () {
		return orgName;
	}
	
	public void setOrgName (String orgName) {
		this.orgName = orgName;
	}
	
	public Integer getPriority () {
		return priority;
	}
	
	public void setPriority (Integer priority) {
		this.priority = priority;
	}
	
	public String getSiteId () {
		return siteId;
	}
	
	public void setSiteId (String siteId) {
		this.siteId = siteId;
	}
}
