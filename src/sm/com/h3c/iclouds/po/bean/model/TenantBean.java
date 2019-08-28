package com.h3c.iclouds.po.bean.model;

import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class TenantBean implements Serializable {

	private static final long serialVersionUID = -1295121524905022759L;
	
	@NotNull
	@Pattern(regexp = "^[\\u4E00-\\u9FA50-9a-zA-Z_@-]{2,32}$")
	private String name;
	
	private String description;
	
	//@NotNull
	private String parentId;
	
	private String cusId;
	
	//@Pattern(regexp = "^\\w+$")
	private String cusName;
	
	@NotNull
	@CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})	// 0:是 1:否
	@ApiModelProperty(value = "是否允许自助 (NotNull)(Contain: 0-是; 1-否)")
	private String selfAllowed;	// 默认为允许

	public String getSelfAllowed() {
		return selfAllowed;
	}

	public void setSelfAllowed(String selfAllowed) {
		this.selfAllowed = selfAllowed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getCusId() {
		return cusId;
	}

	public void setCusId(String cusId) {
		this.cusId = cusId;
	}

	public String getCusName() {
		return cusName;
	}

	public void setCusName(String cusName) {
		this.cusName = cusName;
	}

	
}
