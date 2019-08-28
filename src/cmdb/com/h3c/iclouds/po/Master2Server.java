package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "资产管理-服务器资产配置信息", description = "资产管理-服务器资产配置信息")
public class Master2Server extends BaseEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;
	
	@ApiModelProperty(value = "CPU数量")
	private Integer processors;

	@Length(max = 100)
	@ApiModelProperty(value = "CPU构成")
	private String cpuForm;

	@ApiModelProperty(value = "内存总量")
	private Double memTotal;

	@Length(max = 100)
	@ApiModelProperty(value = "内存构成")
	private String memorys;

	@ApiModelProperty(value = "硬盘总量")
	private Integer diskTotal;

	@Length(max = 100)
	@ApiModelProperty(value = "硬盘构成")
	private String disks;

	@Length(max = 1)
	@ApiModelProperty(value = "是否有光驱")
	private String haveCdRoom;

	@Length(max = 50)
	@ApiModelProperty(value = "电源线")
	private String lineType;

	@ApiModelProperty(value = "电源模块数")
	private Integer powers;

	@ApiModelProperty(value = "电源插槽数")
	private Integer pinboard;

	@Length(max = 600)
	@ApiModelProperty(value = "备注")
	private String remark;
	
	public Master2Server() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getProcessors() {
		return processors;
	}

	public void setProcessors(Integer processors) {
		this.processors = processors;
	}

	public String getCpuForm() {
		return cpuForm;
	}

	public void setCpuForm(String cpuForm) {
		this.cpuForm = cpuForm;
	}

	public Double getMemTotal() {
		return memTotal;
	}

	public void setMemTotal(Double memTotal) {
		this.memTotal = memTotal;
	}

	public String getMemorys() {
		return memorys;
	}

	public void setMemorys(String memorys) {
		this.memorys = memorys;
	}

	public Integer getDiskTotal() {
		return diskTotal;
	}

	public void setDiskTotal(Integer diskTotal) {
		this.diskTotal = diskTotal;
	}

	public String getDisks() {
		return disks;
	}

	public void setDisks(String disks) {
		this.disks = disks;
	}

	public String getHaveCdRoom() {
		return haveCdRoom;
	}

	public void setHaveCdRoom(String haveCdRoom) {
		this.haveCdRoom = haveCdRoom;
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

	public Integer getPinboard() {
		return pinboard;
	}

	public void setPinboard(Integer pinboard) {
		this.pinboard = pinboard;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}