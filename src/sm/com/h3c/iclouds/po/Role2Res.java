package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

/**
 * 角色资源表
 * 
 * @author zkf5485
 *
 */
@ApiModel(value = "系统管理角色资源映射表", description = "系统管理角色资源映射表")
public class Role2Res extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	public Role2Res() {
	
	}

	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "角色id")
	private String roleId;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "资源编号")
	private String resId;

	private Resource resource;
	
	private Role role;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}
