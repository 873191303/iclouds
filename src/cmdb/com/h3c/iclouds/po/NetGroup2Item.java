package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Created by ykf7317 on 2017/9/5.
 */
@ApiModel(value = "配置管理堆叠子设备信息")
public class NetGroup2Item extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "id")
	private String id;
	
	@ApiModelProperty(value = "堆叠id")
	private String stackId;
	
	@ApiModelProperty(value = "资产序列号")
	private String serial;
	
	@ApiModelProperty(value = "cpu")
	private Integer ncore;
	
	@ApiModelProperty(value = "总内存")
	private Integer memTotal;
	
	@ApiModelProperty(value = "资产id")
	private String masterId;
	
	@ApiModelProperty(value = "备注")
	private String remark;
	
	@ApiModelProperty(value = "资产类型id")
	private String assetTypeId;
	
	@ApiModelProperty(value = "堆叠名称")
	private String stackName;
	
	@ApiModelProperty(value = "资产名称")
	private String masterName;
	
	@ApiModelProperty(value = "资产类型名称")
	private String assetType;
	
	@ApiModelProperty(value = "是否独立设备标识")
	private String isAlone;
	
	@ApiModelProperty(value = "资产类型编码")
	private String assetTypeCode;
	
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
	
	public String getRemark () {
		return remark;
	}
	
	public void setRemark (String remark) {
		this.remark = remark;
	}
	
	public String getStackId () {
		return stackId;
	}
	
	public void setStackId (String stackId) {
		this.stackId = stackId;
	}
	
	public String getSerial () {
		return serial;
	}
	
	public void setSerial (String serial) {
		this.serial = serial;
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
	
	public String getAssetTypeId () {
		return assetTypeId;
	}
	
	public void setAssetTypeId (String assetTypeId) {
		this.assetTypeId = assetTypeId;
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
	
	public String getAssetType () {
		return assetType;
	}
	
	public void setAssetType (String assetType) {
		this.assetType = assetType;
	}
	
	public String getIsAlone () {
		return isAlone;
	}
	
	public void setIsAlone (String isAlone) {
		this.isAlone = isAlone;
	}
	
	public String getAssetTypeCode () {
		return assetTypeCode;
	}
	
	public void setAssetTypeCode (String assetTypeCode) {
		this.assetTypeCode = assetTypeCode;
	}
}
