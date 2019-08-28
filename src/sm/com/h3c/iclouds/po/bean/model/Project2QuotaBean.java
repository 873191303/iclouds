package com.h3c.iclouds.po.bean.model;

import java.io.Serializable;

import com.h3c.iclouds.utils.StrUtils;

public class Project2QuotaBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3748082453313315761L;
	
	private String classCode;
	
	private Integer hardLimit;

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public Integer getHardLimit() {
		return hardLimit;
	}

	public void setHardLimit(Integer hardLimit) {
		this.hardLimit = hardLimit;
	}

	@Override
	public boolean equals(Object obj) {
		return classCode.equals(StrUtils.tranString(obj));
	}
	@Override
	public String toString() {
		return classCode.toString();
	}
	@Override
	public int hashCode() {
		return classCode.hashCode();
	}
	
}
