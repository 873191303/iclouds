package com.h3c.iclouds.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

/**
 * 用户群组关联
 * @author zkf5485
 *
 */
@ApiModel(value = "系统管理用户与群组映射表", description = "系统管理用户与群组映射表")
public class User2Group extends BaseEntity implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;
	
	@NotNull
	@Length(max = 36)
	@ApiModelProperty(value = "用户id")
	private String userId;
	
	@NotNull
	@Length(max = 36)
	@ApiModelProperty(value = "群组id")
	private String gid;

	@ApiModelProperty(value = "是否默认")
	@CheckPattern(type = PatternType.CONTAINS, values = {"0","1"})
	private String isDefault = ConfigProperty.NO;
	
	private User user;
	
	private Groups groups;

	public User2Group() {

	}
	
	public User2Group(String userId, String createBy) {
		this.userId = userId;
		this.createdUser(createBy);
		this.isDefault = ConfigProperty.YES;
		this.gid = CacheSingleton.getInstance().getDefaultGroupId();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	@JsonIgnore
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@JsonIgnore
	public Groups getGroups() {
		return groups;
	}

	public void setGroups(Groups groups) {
		this.groups = groups;
	}
	
}
