package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Trigger2Discovery implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long triggerId;
	
	private Long parentTriggerId;
	
	public Long getTriggerId () {
		return triggerId;
	}
	
	public void setTriggerId (Long triggerId) {
		this.triggerId = triggerId;
	}
	
	public Long getParentTriggerId () {
		return parentTriggerId;
	}
	
	public void setParentTriggerId (Long parentTriggerId) {
		this.parentTriggerId = parentTriggerId;
	}
}
