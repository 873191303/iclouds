package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Item2Condition implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long itemConditionId;
	
	private Long itemId;
	
	private Integer operator;
	
	private String macro;
	
	private String value;
	
	public Long getItemConditionId () {
		return itemConditionId;
	}
	
	public void setItemConditionId (Long itemConditionId) {
		this.itemConditionId = itemConditionId;
	}
	
	public Long getItemId () {
		return itemId;
	}
	
	public void setItemId (Long itemId) {
		this.itemId = itemId;
	}
	
	public Integer getOperator () {
		return operator;
	}
	
	public void setOperator (Integer operator) {
		this.operator = operator;
	}
	
	public String getMacro () {
		return macro;
	}
	
	public void setMacro (String macro) {
		this.macro = macro;
	}
	
	public String getValue () {
		return value;
	}
	
	public void setValue (String value) {
		this.value = value;
	}
}
