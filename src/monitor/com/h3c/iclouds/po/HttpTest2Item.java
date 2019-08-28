package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class HttpTest2Item extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long httpTestId;
	
	private Long itemId;
	
	private String tenantId;
	
	private String owner;
	
	public Long getId () {
		return id;
	}
	
	public void setId (Long id) {
		this.id = id;
	}
	
	public Long getHttpTestId () {
		return httpTestId;
	}
	
	public void setHttpTestId (Long httpTestId) {
		this.httpTestId = httpTestId;
	}
	
	public Long getItemId () {
		return itemId;
	}
	
	public void setItemId (Long itemId) {
		this.itemId = itemId;
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
