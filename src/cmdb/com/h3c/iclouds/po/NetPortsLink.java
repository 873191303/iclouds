package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "资产管理链路关系表")
public class NetPortsLink extends BaseEntity implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 50)
	@ApiModelProperty(value = "上联网口")
	private String trunkTo;

	@Length(max = 50)
	@ApiModelProperty(value = "下联网口")
	private String accessTo;

	@Length(max = 50)
	@ApiModelProperty(value = "VLAN")
	private String vlan;

	@Length(max = 600)
	@ApiModelProperty(value = "备注")
	private String remark;
	
	private NetPorts trunkPort;
	
	private NetPorts accessPort;
	
	private String trunkMasterId;
	
	private String accessMasterId;
	
	public NetPortsLink() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTrunkTo() {
		return trunkTo;
	}

	public void setTrunkTo(String trunkTo) {
		this.trunkTo = trunkTo;
	}

	public String getAccessTo() {
		return accessTo;
	}

	public void setAccessTo(String accessTo) {
		this.accessTo = accessTo;
	}

	public String getVlan() {
		return vlan;
	}

	public void setVlan(String vlan) {
		this.vlan = vlan;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public NetPorts getTrunkPort() {
		return trunkPort;
	}

	public void setTrunkPort(NetPorts trunkPort) {
		this.trunkPort = trunkPort;
	}

	public NetPorts getAccessPort() {
		return accessPort;
	}

	public void setAccessPort(NetPorts accessPort) {
		this.accessPort = accessPort;
	}

	public String getTrunkMasterId() {
		return trunkMasterId;
	}

	public void setTrunkMasterId(String trunkMasterId) {
		this.trunkMasterId = trunkMasterId;
	}

	public String getAccessMasterId() {
		return accessMasterId;
	}

	public void setAccessMasterId(String accessMasterId) {
		this.accessMasterId = accessMasterId;
	}

}
