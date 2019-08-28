package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "资产设备-路由器配置", description = "资产设备-路由器配置")
public class Master2Router extends BaseEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 50)
	@ApiModelProperty(value = "CPU")
	private String cpu;

	@Length(max = 50)
	@ApiModelProperty(value = "内存")
	private String ram;

	@Length(max = 50)
	@ApiModelProperty(value = "IPv4转发率")
	private String ipv4RRate;

	@Length(max = 50)
	@ApiModelProperty(value = "IPv6转发率")
	private String ipv6RRate;

	@Length(max = 50)
	@ApiModelProperty(value = "主控版槽位数")
	private String mpuSlots;

	@Length(max = 50)
	@ApiModelProperty(value = "业务板槽位数")
	private String baseSlots;

	@Length(max = 50)
	@ApiModelProperty(value = "交换容量")
	private String swCapacity;

	@Length(max = 50)
	@ApiModelProperty(value = "包转发率")
	private String pacRate;

	@Length(max = 600)
	@ApiModelProperty(value = "备注")
	private String remark;
	
	public Master2Router() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String getRam() {
		return ram;
	}

	public void setRam(String ram) {
		this.ram = ram;
	}

	public String getIpv4RRate() {
		return ipv4RRate;
	}

	public void setIpv4RRate(String ipv4rRate) {
		ipv4RRate = ipv4rRate;
	}

	public String getIpv6RRate() {
		return ipv6RRate;
	}

	public void setIpv6RRate(String ipv6rRate) {
		ipv6RRate = ipv6rRate;
	}

	public String getMpuSlots() {
		return mpuSlots;
	}

	public void setMpuSlots(String mpuSlots) {
		this.mpuSlots = mpuSlots;
	}

	public String getBaseSlots() {
		return baseSlots;
	}

	public void setBaseSlots(String baseSlots) {
		this.baseSlots = baseSlots;
	}

	public String getSwCapacity() {
		return swCapacity;
	}

	public void setSwCapacity(String swCapacity) {
		this.swCapacity = swCapacity;
	}

	public String getPacRate() {
		return pacRate;
	}

	public void setPacRate(String pacRate) {
		this.pacRate = pacRate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
