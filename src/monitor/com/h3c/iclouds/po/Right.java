package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Right extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long rightId;
	
	private Long pftGroupId;
	
	private Integer permission;
	
	private Long usrGrpId;
	
	private String tenantId;
	
	public Long getRightId () {
		return rightId;
	}
	
	public void setRightId (Long rightId) {
		this.rightId = rightId;
	}
	
	public Long getPftGroupId () {
		return pftGroupId;
	}
	
	public void setPftGroupId (Long pftGroupId) {
		this.pftGroupId = pftGroupId;
	}
	
	public Integer getPermission () {
		return permission;
	}
	
	public void setPermission (Integer permission) {
		this.permission = permission;
	}
	
	public Long getUsrGrpId () {
		return usrGrpId;
	}
	
	public void setUsrGrpId (Long usrGrpId) {
		this.usrGrpId = usrGrpId;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}
}
