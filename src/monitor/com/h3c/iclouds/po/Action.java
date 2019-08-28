package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Action extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long actionId;
	
	private String name;
	
	private Integer eventSource;
	
	private Integer evalType;
	
	private Integer status;
	
	private Integer escPeriod;
	
	private String defShortData;
	
	private String defLongData;
	
	private Integer recoveryMsg;
	
	private String rshortData;
	
	private String rlongData;
	
	private String formula;
	
	private String tenantId;
	
	public Long getActionId () {
		return actionId;
	}
	
	public void setActionId (Long actionId) {
		this.actionId = actionId;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public Integer getEventSource () {
		return eventSource;
	}
	
	public void setEventSource (Integer eventSource) {
		this.eventSource = eventSource;
	}
	
	public Integer getEvalType () {
		return evalType;
	}
	
	public void setEvalType (Integer evalType) {
		this.evalType = evalType;
	}
	
	public Integer getStatus () {
		return status;
	}
	
	public void setStatus (Integer status) {
		this.status = status;
	}
	
	public Integer getEscPeriod () {
		return escPeriod;
	}
	
	public void setEscPeriod (Integer escPeriod) {
		this.escPeriod = escPeriod;
	}
	
	public String getDefShortData () {
		return defShortData;
	}
	
	public void setDefShortData (String defShortData) {
		this.defShortData = defShortData;
	}
	
	public String getDefLongData () {
		return defLongData;
	}
	
	public void setDefLongData (String defLongData) {
		this.defLongData = defLongData;
	}
	
	public Integer getRecoveryMsg () {
		return recoveryMsg;
	}
	
	public void setRecoveryMsg (Integer recoveryMsg) {
		this.recoveryMsg = recoveryMsg;
	}
	
	public String getRshortData () {
		return rshortData;
	}
	
	public void setRshortData (String rshortData) {
		this.rshortData = rshortData;
	}
	
	public String getRlongData () {
		return rlongData;
	}
	
	public void setRlongData (String rlongData) {
		this.rlongData = rlongData;
	}
	
	public String getFormula () {
		return formula;
	}
	
	public void setFormula (String formula) {
		this.formula = formula;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}
}
