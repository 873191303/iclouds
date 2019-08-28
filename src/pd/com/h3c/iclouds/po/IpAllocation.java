package com.h3c.iclouds.po;

import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/21.
 */
@ApiModel(value = "云管理IP使用表", description = "云管理IP使用表")
public class IpAllocation implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@NotNull
	@ApiModelProperty(value = "端口ID(NotNull)")
	@Length(max = 36)
	private String portId;
	
	@CheckPattern(type = PatternType.IP)
	@ApiModelProperty(value = "ip地址(NotNull)")
	@Length(max = 64)
	private String ipAddress;
	
	@NotNull
	@ApiModelProperty(value = "子网id(NotNull)")
	@Length(max = 36)
	private String subnetId;
	
	@ApiModelProperty(value = "网络的id(不需要传值)")
	private String networkId;
	
	@ApiModelProperty(value = "ip名称(不需要传值)")
	private String name;
	
	@ApiModelProperty(value = "公网ip的回写id(不需要传值)")
	private String cloudosId;
	
	public IpAllocation () {
	}
	
	public IpAllocation (String portId, String subnetId, String ipAddress) {
		this.ipAddress = ipAddress;
		this.subnetId = subnetId;
		this.portId = portId;
	}
	
	public String getPortId () {
		return portId;
	}
	
	public void setPortId (String portId) {
		this.portId = portId;
	}
	
	public String getIpAddress () {
		return ipAddress;
	}
	
	public void setIpAddress (String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public String getSubnetId () {
		return subnetId;
	}
	
	public void setSubnetId (String subnetId) {
		this.subnetId = subnetId;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getNetworkId () {
		return networkId;
	}
	
	public void setNetworkId (String networkId) {
		this.networkId = networkId;
	}
	
	public String getCloudosId () {
		return cloudosId;
	}
	
	public void setCloudosId (String cloudosId) {
		this.cloudosId = cloudosId;
	}
	
}
