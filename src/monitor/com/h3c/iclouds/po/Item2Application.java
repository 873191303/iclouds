package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Item2Application extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long applicationId;
	
	private Long itemId;
	
	public Long getId () {
		return id;
	}
	
	public void setId (Long id) {
		this.id = id;
	}
	
	public Long getApplicationId () {
		return applicationId;
	}
	
	public void setApplicationId (Long applicationId) {
		this.applicationId = applicationId;
	}
	
	public Long getItemId () {
		return itemId;
	}
	
	public void setItemId (Long itemId) {
		this.itemId = itemId;
	}
}
