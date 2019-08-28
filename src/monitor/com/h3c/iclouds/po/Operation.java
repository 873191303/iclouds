package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Operation extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long operationId;
	
	private Long actionId;
	
	private Integer operationType;
	
	private Integer escPeriod;
	
	private Integer escStepFrom;
	
	private Integer escStepTo;
	
	private Integer evalType;
	
	private String tenantId;
	
	public Long getOperationId () {
		return operationId;
	}
	
	public void setOperationId (Long operationId) {
		this.operationId = operationId;
	}
	
	public Long getActionId () {
		return actionId;
	}
	
	public void setActionId (Long actionId) {
		this.actionId = actionId;
	}
	
	public Integer getOperationType () {
		return operationType;
	}
	
	public void setOperationType (Integer operationType) {
		this.operationType = operationType;
	}
	
	public Integer getEscPeriod () {
		return escPeriod;
	}
	
	public void setEscPeriod (Integer escPeriod) {
		this.escPeriod = escPeriod;
	}
	
	public Integer getEscStepFrom () {
		return escStepFrom;
	}
	
	public void setEscStepFrom (Integer escStepFrom) {
		this.escStepFrom = escStepFrom;
	}
	
	public Integer getEscStepTo () {
		return escStepTo;
	}
	
	public void setEscStepTo (Integer escStepTo) {
		this.escStepTo = escStepTo;
	}
	
	public Integer getEvalType () {
		return evalType;
	}
	
	public void setEvalType (Integer evalType) {
		this.evalType = evalType;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}
}
