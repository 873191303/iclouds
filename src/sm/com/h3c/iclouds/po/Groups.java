package com.h3c.iclouds.po;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.utils.InvokeAnnotate;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

/**
 * 用户群组
 * 
 * @author zkf5485
 *
 */
@SuppressWarnings("rawtypes")
@ApiModel(value = "群组", description = "群组", discriminator = "群组")
public class Groups extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "群组id")
	private String id;

	@Length(max = 50)
	@NotNull
	@ApiModelProperty(value = "群组名称 (NotNull)", required = true)
	private String groupName;

	@CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})
	@ApiModelProperty(value = "是否系统初始数据 0:是，不允许删除 1：用户定义数据，允许删除")
	private String flag;

	@Length(max = 36)
	private String blongToId = "";

	@Length(max = 100)
	@ApiModelProperty(value = "群组描述")
	private String description;

	private Set groupToUserSet = new HashSet();

	public Groups() {

	}

	public Groups(String id, String groupName) {
		this.id = id;
		this.groupName = groupName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getBlongToId() {
		return blongToId;
	}

	public void setBlongToId(String blongToId) {
		this.blongToId = blongToId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonIgnore
	public Set getGroupToUserSet() {
		return groupToUserSet;
	}

	public void setGroupToUserSet(Set groupToUserSet) {
		this.groupToUserSet = groupToUserSet;
	}

	@InvokeAnnotate(type = PatternType.UNCOPY)
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
