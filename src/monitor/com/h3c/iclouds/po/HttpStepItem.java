package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class HttpStepItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long httpStepItemId;
	
	private Long httpStepId;
	
	private Long itemId;
	
	private Integer type;
	
	public Long getHttpStepItemId () {
		return httpStepItemId;
	}
	
	public void setHttpStepItemId (Long httpStepItemId) {
		this.httpStepItemId = httpStepItemId;
	}
	
	public Long getHttpStepId () {
		return httpStepId;
	}
	
	public void setHttpStepId (Long httpStepId) {
		this.httpStepId = httpStepId;
	}
	
	public Long getItemId () {
		return itemId;
	}
	
	public void setItemId (Long itemId) {
		this.itemId = itemId;
	}
	
	public Integer getType () {
		return type;
	}
	
	public void setType (Integer type) {
		this.type = type;
	}
}
