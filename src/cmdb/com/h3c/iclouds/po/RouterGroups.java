package com.h3c.iclouds.po;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "资源配置交换机堆叠信息", description = "资源配置交换机堆叠信息")
@SuppressWarnings("rawtypes")
public class RouterGroups extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 50)
	@NotNull
	@ApiModelProperty(value = "堆叠名称 （NotNull）")
	private String stackName;

	@Length(max = 50)
	@ApiModelProperty(value = "堆叠类型")
	private String stackType;

	@Length(max = 50)
	@ApiModelProperty(value = "父堆叠,目前暂时没有使用到")
	private String pstackId;

	@Length(max = 500)
	@ApiModelProperty(value = "备注")
	private String remark;
	
	private Set items = new HashSet();
	
	private List<AsmMaster> asmMasters;
	
	private List<NetPorts> netPorts;

	public RouterGroups() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStackName() {
		return stackName;
	}

	public void setStackName(String stackName) {
		this.stackName = stackName;
	}

	public String getStackType() {
		return stackType;
	}

	public void setStackType(String stackType) {
		this.stackType = stackType;
	}

	public String getPstackId() {
		return pstackId;
	}

	public void setPstackId(String pstackId) {
		this.pstackId = pstackId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@JsonIgnore
	public Set getItems() {
		return items;
	}

	public void setItems(Set items) {
		this.items = items;
	}

	public List<AsmMaster> getAsmMasters() {
		return asmMasters;
	}

	public void setAsmMasters(List<AsmMaster> asmMasters) {
		this.asmMasters = asmMasters;
	}

	public List<NetPorts> getNetPorts() {
		return netPorts;
	}

	public void setNetPorts(List<NetPorts> netPorts) {
		this.netPorts = netPorts;
	}

}
