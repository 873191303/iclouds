package com.h3c.iclouds.po;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "资产管理-存储资产配置信息", description = "资产管理-存储资产配置信息")
public class Master2Stock extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;
	
	@ApiModelProperty(value = "裸容量")
	private Double diskTotal;

	@Length(max = 50)
	@ApiModelProperty(value = "硬盘构成")
	private String disks;

	@Length(max = 50)
	@ApiModelProperty(value = "硬盘类型")
	private String diskType;

	@ApiModelProperty(value = "硬盘槽数")
	private Integer pinboard;

	@ApiModelProperty(value = "交换机连接口数")
	private Integer switchPort;

	@Length(max = 50)
	@ApiModelProperty(value = "电源线")
	private String lineType;

	@ApiModelProperty(value = "电源模块数")
	private Integer powers;

	@ApiModelProperty(value = "存储控制器个数")
	private Integer controls;

	@Length(max = 100)
	@ApiModelProperty(value = "存储控制器备注")
	private String contRemark;

	@Length(max = 600)
	@ApiModelProperty(value = "备注")
	private String remark;

	public Master2Stock() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getDiskTotal() {
		return diskTotal;
	}

	public void setDiskTotal(Double diskTotal) {
		this.diskTotal = diskTotal;
	}

	public String getDisks() {
		return disks;
	}

	public void setDisks(String disks) {
		this.disks = disks;
	}

	public String getDiskType() {
		return diskType;
	}

	public void setDiskType(String diskType) {
		this.diskType = diskType;
	}

	public Integer getPinboard() {
		return pinboard;
	}

	public void setPinboard(Integer pinboard) {
		this.pinboard = pinboard;
	}

	public Integer getSwitchPort() {
		return switchPort;
	}

	public void setSwitchPort(Integer switchPort) {
		this.switchPort = switchPort;
	}

	public String getLineType() {
		return lineType;
	}

	public void setLineType(String lineType) {
		this.lineType = lineType;
	}

	public Integer getPowers() {
		return powers;
	}

	public void setPowers(Integer powers) {
		this.powers = powers;
	}

	public Integer getControls() {
		return controls;
	}

	public void setControls(Integer controls) {
		this.controls = controls;
	}

	public String getContRemark() {
		return contRemark;
	}

	public void setContRemark(String contRemark) {
		this.contRemark = contRemark;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}