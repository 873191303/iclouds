package com.h3c.iclouds.po;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "资源配置规则", description = "资源配置规则")
public class Rules extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "规则id")
	private String id;

	@Length(max = 36)
	@ApiModelProperty(value = "OS镜像")
	private String osMirId;

	@Length(max = 100)
	@ApiModelProperty(value = "OS镜像名称")
	private String osMirName;

	@ApiModelProperty(value = "最小vcpu")
	private Integer vcpu;

	@ApiModelProperty(value = "最小内存")
	private Integer minDisk;

	@ApiModelProperty(value = "最小系统盘大小")
	private Integer minRam;

	@ApiModelProperty(value = "最小交换内存")
	private Integer minSwap;

	@Length(max = 1)
	@ApiModelProperty(value = "启用:0-禁用:1")
	private String isDefault;

	private Date syncTime;
	
	@Length(max = 100)
	@ApiModelProperty(value = "镜像格式")
	private String format;
	
	@Length(max = 100)
	@ApiModelProperty(value = "镜像类型")
	private String osType;
	
	@Length(max = 50)
	@ApiModelProperty(value = "租户id")
	private String tenantId;
	
	@Length(max = 50)
	@ApiModelProperty(value = "用户id")
	private String userId;
	
	@Length(max = 36)
	@ApiModelProperty(value = "定义类型-0:预定义,1:自定义")
	private String definition;
	
	public Rules() {
	
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOsMirName() {
		return osMirName;
	}

	public void setOsMirName(String osMirName) {
		this.osMirName = osMirName;
	}

	public Integer getVcpu() {
		return vcpu;
	}

	public void setVcpu(Integer vcpu) {
		this.vcpu = vcpu;
	}

	public Integer getMinDisk() {
		return minDisk;
	}

	public void setMinDisk(Integer minDisk) {
		this.minDisk = minDisk;
	}

	public Integer getMinRam() {
		return minRam;
	}

	public void setMinRam(Integer minRam) {
		this.minRam = minRam;
	}

	public Integer getMinSwap() {
		return minSwap;
	}

	public void setMinSwap(Integer minSwap) {
		this.minSwap = minSwap;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public Date getSyncTime() {
		return syncTime;
	}

	public void setSyncTime(Date syncTime) {
		this.syncTime = syncTime;
	}

	public String getOsMirId() {
		return osMirId;
	}

	public void setOsMirId(String osMirId) {
		this.osMirId = osMirId;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getDefinition () {
		return definition;
	}
	
	public void setDefinition (String definition) {
		this.definition = definition;
	}
}
