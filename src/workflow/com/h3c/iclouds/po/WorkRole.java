package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "工作流角色2系统角色映射表", description = "工作流角色2系统角色映射表")
public class WorkRole extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 36)
	@ApiModelProperty(value = "流程id")
	private String workFlowId;

	@Length(max = 50)
	@ApiModelProperty(value = "流程角色key")
	private String roleKey;

	@Length(max = 50)
	@ApiModelProperty(value = "流程环节id")
	private String processSegment;

	@Length(max = 50)
	@ApiModelProperty(value = "流程环节名称")
	private String processName;

	@ApiModelProperty(value = "流程环节级别")
	private Integer level;

	@Length(max = 50)
	@ApiModelProperty(value = "系统角色")
	private String roleId;

	@Length(max = 50)
	@ApiModelProperty(value = "是否要部门匹配")
	private String isSameDept;

	@Length(max = 500)
	@ApiModelProperty(value = "备注")
	private String remark;

	private String workFlowName;

	private String roleName;
	
	public WorkRole() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleKey() {
		return roleKey;
	}

	public void setRoleKey(String roleKey) {
		this.roleKey = roleKey;
	}

	public String getProcessSegment() {
		return processSegment;
	}

	public void setProcessSegment(String processSegment) {
		this.processSegment = processSegment;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getIsSameDept() {
		return isSameDept;
	}

	public void setIsSameDept(String isSameDept) {
		this.isSameDept = isSameDept;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getWorkFlowName() {
		return workFlowName;
	}

	public void setWorkFlowName(String workFlowName) {
		this.workFlowName = workFlowName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getWorkFlowId() {
		return workFlowId;
	}

	public void setWorkFlowId(String workFlowId) {
		this.workFlowId = workFlowId;
	}

}
