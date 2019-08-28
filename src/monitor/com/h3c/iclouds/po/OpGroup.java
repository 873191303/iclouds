package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class OpGroup implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long opgroupId;
	
	private Long operationId;
	
	private Long groupId;
	
	public Long getOpgroupId () {
		return opgroupId;
	}
	
	public void setOpgroupId (Long opgroupId) {
		this.opgroupId = opgroupId;
	}
	
	public Long getOperationId () {
		return operationId;
	}
	
	public void setOperationId (Long operationId) {
		this.operationId = operationId;
	}
	
	public Long getGroupId () {
		return groupId;
	}
	
	public void setGroupId (Long groupId) {
		this.groupId = groupId;
	}
}
