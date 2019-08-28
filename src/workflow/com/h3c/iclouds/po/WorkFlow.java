package com.h3c.iclouds.po;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.utils.InvokeAnnotate;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "工作流程定义表", description = "工作流程定义表")
public class WorkFlow extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "流程id")
	private String id;

	@Length(max = 50)
	@NotNull
	@ApiModelProperty(value = "工作流名称")
	private String name;

	@Length(max = 50)
	@NotNull
	@ApiModelProperty(value = "key值")
	private String key;

	@Length(max = 500)
	@ApiModelProperty(value = "文件名")
	private String fileName;

	//@Max(16)
	@NotNull
	@ApiModelProperty(value = "版本")
	private Integer version;

	@ApiModelProperty(value = "上传时间")
	private Date uploadDate;

	@ApiModelProperty(value = "部署时间")
	private Date deployDate;

	@ApiModelProperty(value = "启用时间")
	private Date startDate;

	@ApiModelProperty(value = "状态", notes = " 默认为0； 1:未部署 2:已部署 3:挂起")
	@CheckPattern(type = PatternType.CONTAINS, values = {"0", "1", "2", "3"})
	private String status = ConfigProperty.WORKFLOW_STATUS1_UNDEPLOY;

	@Length(max = 600)
	@ApiModelProperty(value = "备注")
	private String remark;

	private String deployer;

	@Length(max = 50)
	@ApiModelProperty(value = "部署id")
	private String deploymentId;

	public WorkFlow() {
		
	}

	@InvokeAnnotate(type = PatternType.UNCOPY)
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getDeployDate() {
		return deployDate;
	}

	public void setDeployDate(Date deployDate) {
		this.deployDate = deployDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDeployer() {
		return deployer;
	}

	public void setDeployer(String deployer) {
		this.deployer = deployer;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	
}
