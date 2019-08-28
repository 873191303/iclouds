package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Created by ykf7317 on 2017/9/5.
 */
@ApiModel(value = "配置管理网络堆叠信息")
public class NetGroup extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "id")
	private String id;
	
	@ApiModelProperty(value = "堆叠名称")
	private String stackName;
	
	@ApiModelProperty(value = "ip")
	private String ip;
	
	@ApiModelProperty(value = "备注")
	private String remark;
	
	@ApiModelProperty(value = "资产类型id")
	private String assType;
	
	@ApiModelProperty(value = "资产类型名称")
	private String assTypeCode;
	
	@ApiModelProperty(value = "是否独立资产")
	private String isAlone;
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	public String getIp () {
		return ip;
	}
	
	public void setIp (String ip) {
		this.ip = ip;
	}
	
	public String getRemark () {
		return remark;
	}
	
	public void setRemark (String remark) {
		this.remark = remark;
	}
	
	public String getStackName () {
		return stackName;
	}
	
	public void setStackName (String stackName) {
		this.stackName = stackName;
	}
	
	public String getAssType () {
		return assType;
	}
	
	public void setAssType (String assType) {
		this.assType = assType;
	}
	
	public String getAssTypeCode () {
		return assTypeCode;
	}
	
	public void setAssTypeCode (String assTypeCode) {
		this.assTypeCode = assTypeCode;
	}
	
	public String getIsAlone () {
		return isAlone;
	}
	
	public void setIsAlone (String isAlone) {
		this.isAlone = isAlone;
	}
}
