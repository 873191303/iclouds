package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Created by ykf7317 on 2017/9/5.
 */
@ApiModel(value = "配置管理堆叠虚拟设备信息")
public class Vdevice extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "id")
	private String id;
	
	@ApiModelProperty(value = "堆叠id")
	private String stackId;
	
	@ApiModelProperty(value = "虚拟设备名称")
	private String vassetName;
	
	@ApiModelProperty(value = "cpu")
	private Integer ncore;
	
	@ApiModelProperty(value = "总内存")
	private Integer memTotal;
	
	@ApiModelProperty(value = "ip")
	private String ip;
	
	@ApiModelProperty(value = "mac")
	private String mac;
	
	@ApiModelProperty(value = "用途")
	private String utility;
	
	@ApiModelProperty(value = "承载设备id")
	private String masterId;
	
	@ApiModelProperty(value = "备注")
	private String remark;
	
	@ApiModelProperty(value = "租户")
	private String tenant;
	
	@ApiModelProperty(value = "团体字")
	private String community;
	
	@ApiModelProperty(value = "端口名称")
	private String portName;
	
	@ApiModelProperty(value = "堆叠名称")
	private String stackName;
	
	@ApiModelProperty(value = "资产名称")
	private String masterName;
	
	@ApiModelProperty(value = "租户名称")
	private String tenantName;
	
	@ApiModelProperty(value = "资产类型id")
	private String assetTypeId;
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	public Integer getNcore () {
		return ncore;
	}
	
	public void setNcore (Integer ncore) {
		this.ncore = ncore;
	}
	
	public String getIp () {
		return ip;
	}
	
	public void setIp (String ip) {
		this.ip = ip;
	}
	
	public String getMac () {
		return mac;
	}
	
	public void setMac (String mac) {
		this.mac = mac;
	}
	
	public String getUtility () {
		return utility;
	}
	
	public void setUtility (String utility) {
		this.utility = utility;
	}
	
	public String getRemark () {
		return remark;
	}
	
	public void setRemark (String remark) {
		this.remark = remark;
	}
	
	public String getTenant () {
		return tenant;
	}
	
	public void setTenant (String tenant) {
		this.tenant = tenant;
	}
	
	public String getStackId () {
		return stackId;
	}
	
	public void setStackId (String stackId) {
		this.stackId = stackId;
	}
	
	public String getVassetName () {
		return vassetName;
	}
	
	public void setVassetName (String vassetName) {
		this.vassetName = vassetName;
	}
	
	public Integer getMemTotal () {
		return memTotal;
	}
	
	public void setMemTotal (Integer memTotal) {
		this.memTotal = memTotal;
	}
	
	public String getMasterId () {
		return masterId;
	}
	
	public void setMasterId (String masterId) {
		this.masterId = masterId;
	}
	
	public String getStackName () {
		return stackName;
	}
	
	public void setStackName (String stackName) {
		this.stackName = stackName;
	}
	
	public String getMasterName () {
		return masterName;
	}
	
	public void setMasterName (String masterName) {
		this.masterName = masterName;
	}
	
	public String getTenantName () {
		return tenantName;
	}
	
	public void setTenantName (String tenantName) {
		this.tenantName = tenantName;
	}
	
	public String getCommunity () {
		return community;
	}
	
	public void setCommunity (String community) {
		this.community = community;
	}
	
	public String getPortName () {
		return portName;
	}
	
	public void setPortName (String portName) {
		this.portName = portName;
	}
	
	public String getAssetTypeId () {
		return assetTypeId;
	}
	
	public void setAssetTypeId (String assetTypeId) {
		this.assetTypeId = assetTypeId;
	}
}
