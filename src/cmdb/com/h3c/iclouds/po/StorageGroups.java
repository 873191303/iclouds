package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "资源配置存储管理组", description = "资源配置存储管理组")
public class StorageGroups extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;
	
	@NotNull
	@Length(max = 50)
	@ApiModelProperty(value = "名称 （NotNull）")
	private String name;

	@NotNull
	@Length(max = 50)
	@ApiModelProperty(value = "管理员")
	private String manager;

	@ApiModelProperty(value = "状态")
	private Integer status;

	@Length(max = 100)
	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "集群数")
	private Integer clusters;

	@ApiModelProperty(value = "子存储数")
	private Integer volums;

	@ApiModelProperty(value = "存储管理器数")
	private Integer managers;
	
	public StorageGroups() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getClusters() {
		return clusters;
	}

	public void setClusters(Integer clusters) {
		this.clusters = clusters;
	}

	public Integer getVolums() {
		return volums;
	}

	public void setVolums(Integer volums) {
		this.volums = volums;
	}

	public Integer getManagers() {
		return managers;
	}

	public void setManagers(Integer managers) {
		this.managers = managers;
	}
}
