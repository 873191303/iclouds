package com.h3c.iclouds.po;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "云管理第三方爱数任务节点", description = "云管理第三方爱数任务节点")
public class Backup2Nodes extends BaseEntity implements Serializable  {

private static final long serialVersionUID = 1L;

	@NotNull
    @ApiModelProperty(value = "ID")
    @Length(max = 36)
    private String id;

    @ApiModelProperty(value = "任务节点ID")
    private Integer backupNodeId;
    
	@NotNull
    @ApiModelProperty(value = "节点名称")
	@Length(max = 255)
    private String backupNodeName;

    @ApiModelProperty(value = "备份ID")
    @Length(max = 36)
    private String backupId;
    
    @ApiModelProperty(value = "租户ID")
    @Length(max = 64)
    private String tenantId;
    
    @NotNull
    @ApiModelProperty(value = "节点mac")
	@Length(max = 64)
    private String backupNodeMac;

    @NotNull
    @ApiModelProperty(value = "节点地址")
    @Length(max = 255)
    private String backupNodeAdrr;
    
    @NotNull
    @ApiModelProperty(value = "节点UUID")
    @Length(max = 36)
    private String backupNodeUuid;
    
    @NotNull
    @ApiModelProperty(value = "版本")
    @Length(max = 36)
    private String productVersion;
    
    @ApiModelProperty(value = "系统类型")
    @Length(max = 36)
    private String os;

	public Backup2Nodes() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getBackupNodeId() {
		return backupNodeId;
	}

	public void setBackupNodeId(Integer backupNodeId) {
		this.backupNodeId = backupNodeId;
	}

	public String getBackupNodeName() {
		return backupNodeName;
	}

	public void setBackupNodeName(String backupNodeName) {
		this.backupNodeName = backupNodeName;
	}

	public String getBackupId() {
		return backupId;
	}

	public void setBackupId(String backupId) {
		this.backupId = backupId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getBackupNodeMac() {
		return backupNodeMac;
	}

	public void setBackupNodeMac(String backupNodeMac) {
		this.backupNodeMac = backupNodeMac;
	}

	public String getBackupNodeAdrr() {
		return backupNodeAdrr;
	}

	public void setBackupNodeAdrr(String backupNodeAdrr) {
		this.backupNodeAdrr = backupNodeAdrr;
	}

	public String getBackupNodeUuid() {
		return backupNodeUuid;
	}

	public void setBackupNodeUuid(String backupNodeUuid) {
		this.backupNodeUuid = backupNodeUuid;
	}

	public String getProductVersion() {
		return productVersion;
	}

	public void setProductVersion(String productVersion) {
		this.productVersion = productVersion;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

}
