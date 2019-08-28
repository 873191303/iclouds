package com.h3c.iclouds.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "系统管理用户角色映射表", description = "系统管理用户角色映射表")
public class User2Role extends BaseEntity implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "用户id")
	private String userId;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "角色id")
	private String roleId;
	
	private Role role;
	
	private User user;
	
	public User2Role() {

	}
	
	public User2Role(String userId,String roleId,String groupId) {
		this.userId=userId;
		this.roleId=roleId;
		setGroupId(groupId);
		createdUser(userId);
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

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	@JsonIgnore
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@JsonIgnore
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}
