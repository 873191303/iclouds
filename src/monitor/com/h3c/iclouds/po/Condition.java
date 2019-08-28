package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Condition implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long conditionId;
	
	private Long actionId;
	
	private Integer conditionType;
	
	private Integer operator;
	
	private String value;
	
	public Long getConditionId () {
		return conditionId;
	}
	
	public void setConditionId (Long conditionId) {
		this.conditionId = conditionId;
	}
	
	public Long getActionId () {
		return actionId;
	}
	
	public void setActionId (Long actionId) {
		this.actionId = actionId;
	}
	
	public Integer getConditionType () {
		return conditionType;
	}
	
	public void setConditionType (Integer conditionType) {
		this.conditionType = conditionType;
	}
	
	public Integer getOperator () {
		return operator;
	}
	
	public void setOperator (Integer operator) {
		this.operator = operator;
	}
	
	public String getValue () {
		return value;
	}
	
	public void setValue (String value) {
		this.value = value;
	}
}
