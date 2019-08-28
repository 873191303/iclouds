package com.h3c.iclouds.po;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.utils.InvokeAnnotate;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

/**
 * 角色表
 * @author zkf5485
 *
 */
@SuppressWarnings("rawtypes")
@ApiModel(value = "角色", description = "角色", discriminator = "角色")
public class Role extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "角色id")
	private String id;

	@Length(max = 50)
	@ApiModelProperty(value = "租户id， 不需要传递", required = true)
	private String projectId;

	@ApiModelProperty(value = "租户名称， 不需要传递", required = true)
	private String projectName;

	@Length(max = 100)
	@NotNull
	@ApiModelProperty(value = "角色名称 (NotNull)", required = true)
	private String roleName;

	@Length(max = 100)
	@ApiModelProperty(value = "角色描述")
	private String roleDesc;

	@Length(max = 36)
	private String proleId;
	
	@ApiModelProperty(value = "角色所属类别")
	private String roleTypeValue;

	@CheckPattern(type = PatternType.CONTAINS, values = {"1", "2"})
	@ApiModelProperty(value = "角色类型,不需要传递。 1:系统角色 2:租户角色")
	private String flag = ConfigProperty.SM_ROLE_FLAG2_PROJECT;	// 新添加的角色都是租户角色

	private List<Role> children;
	
	private Set roleToUserSet = new HashSet();
	
	private Set roleToResSet = new HashSet();
	
	public Role() {

	}
	
	public Role(String id, String roleName) {
		this.id = id;
		this.roleName = roleName;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public String getProleId() {
		return proleId;
	}

	public void setProleId(String proleId) {
		this.proleId = proleId;
	}

	@InvokeAnnotate(type = PatternType.UNCOPY)
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	@JsonIgnore
	public Set getRoleToUserSet() {
		return roleToUserSet;
	}

	public void setRoleToUserSet(Set roleToUserSet) {
		this.roleToUserSet = roleToUserSet;
	}

	@JsonIgnore
	public Set getRoleToResSet() {
		return roleToResSet;
	}

	public void setRoleToResSet(Set roleToResSet) {
		this.roleToResSet = roleToResSet;
	}

	public List<Role> getChildren() {
		return children;
	}

	public void setChildren(List<Role> children) {
		this.children = children;
	}
	
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getRoleTypeValue() {
		return roleTypeValue;
	}

	public void setRoleTypeValue(String roleTypeValue) {
		this.roleTypeValue = roleTypeValue;
	}

}
