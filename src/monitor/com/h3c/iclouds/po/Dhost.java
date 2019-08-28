package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Dhost extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long dhostId;
	
	private Long druleId;
	
	private Integer status;
	
	private Integer lastUp;
	
	private Integer lastDown;
	
	private String tenantId;
	
	public Long getDhostId () {
		return dhostId;
	}
	
	public void setDhostId (Long dhostId) {
		this.dhostId = dhostId;
	}
	
	public Long getDruleId () {
		return druleId;
	}
	
	public void setDruleId (Long druleId) {
		this.druleId = druleId;
	}
	
	public Integer getStatus () {
		return status;
	}
	
	public void setStatus (Integer status) {
		this.status = status;
	}
	
	public Integer getLastUp () {
		return lastUp;
	}
	
	public void setLastUp (Integer lastUp) {
		this.lastUp = lastUp;
	}
	
	public Integer getLastDown () {
		return lastDown;
	}
	
	public void setLastDown (Integer lastDown) {
		this.lastDown = lastDown;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}
}
