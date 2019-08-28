package com.h3c.iclouds.po;

import java.util.List;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.utils.InvokeAnnotate;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;


/**
 * 部门
 * @author zkf5485
 *
 */
@ApiModel(value = "部门对象", description = "部门对象")
public class Department extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "部门id")
	private String id;

	@Length(max = 50)
	@ApiModelProperty(value = "租户id")
	private String projectId;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "部门名称 (NotNull)", required = true)
	private String deptName;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "部门编码 (NotNull)", required = true)
	private String deptCode;

	@Length(max = 500)
	@ApiModelProperty(value = "部门描述")
	private String deptDesc;

	@Length(max = 36)
	@ApiModelProperty(value = "父级部门")
	private String parentId;

	@NotNull
	@ApiModelProperty(value = "部门级别 (NotNull)", required = true)
	private Integer depth;

	@ApiModelProperty(value = "子部门列表")
	private List<Department> children;

	@ApiModelProperty(value = "父级部门名称,不需要传递")
	private String parentName;
	
	@ApiModelProperty(value = "归属租户名称,不需要传递")
	private String projectName;
	
	public Department() {

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

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public String getDeptDesc() {
		return deptDesc;
	}

	public void setDeptDesc(String deptDesc) {
		this.deptDesc = deptDesc;
	}

	@InvokeAnnotate(type = PatternType.UNCOPY)
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@InvokeAnnotate(type = PatternType.UNCOPY)
	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public List<Department> getChildren() {
		return children;
	}

	public void setChildren(List<Department> children) {
		this.children = children;
	}
	
	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
}
