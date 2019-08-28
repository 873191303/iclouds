package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Created by ykf7317 on 2017/9/5.
 */
@ApiModel(value = "配置管理网络端口")
public class Vport extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "id")
	private String id;
	
	@ApiModelProperty(value = "堆叠id")
	private String stackId;
	
	@ApiModelProperty(value = "网口序号")
	private Short seq;
	
	@ApiModelProperty(value = "mac")
	private String mac;
	
	@ApiModelProperty(value = "端口类型")
	private String portType;
	
	@ApiModelProperty(value = "下接网口Mac表")
	private String macs;
	
	@ApiModelProperty(value = "备注")
	private String remark;
	
	@ApiModelProperty(value = "堆叠名称")
	private String stackName;
	
	@ApiModelProperty(value = "承载物理设备")
	private String passet;
	
	@ApiModelProperty(value = "承载物理端口")
	private String pport;
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	public Short getSeq () {
		return seq;
	}
	
	public void setSeq (Short seq) {
		this.seq = seq;
	}
	
	public String getMac () {
		return mac;
	}
	
	public void setMac (String mac) {
		this.mac = mac;
	}
	
	public String getMacs () {
		return macs;
	}
	
	public void setMacs (String macs) {
		this.macs = macs;
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
	
	public String getPortType () {
		return portType;
	}
	
	public void setPortType (String portType) {
		this.portType = portType;
	}
	
	public String getStackName () {
		return stackName;
	}
	
	public void setStackName (String stackName) {
		this.stackName = stackName;
	}
	
	public String getPasset () {
		return passet;
	}
	
	public void setPasset (String passet) {
		this.passet = passet;
	}
	
	public String getPport () {
		return pport;
	}
	
	public void setPport (String pport) {
		this.pport = pport;
	}
}
