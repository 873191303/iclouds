package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@Api(value = "防火墙堆叠")
public class FirewallGroups2Items extends BaseEntity implements java.io.Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 50)
	@NotNull
	@ApiModelProperty(value = "堆叠id")
	private String stackId;

	@Length(max = 50)
	@ApiModelProperty(value = "防火墙资产id")
	private String masterId;

	@Length(max = 500)
	@ApiModelProperty(value = "备注")
	private String remark;
	
	@ApiModelProperty(value = "堆叠名称")
	private String stackName;
	
	private FirewallGroups group;
	
	private AsmMaster asmMaster;
	
	public FirewallGroups2Items () {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStackId() {
		return stackId;
	}

	public void setStackId(String stackId) {
		this.stackId = stackId;
	}

	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public FirewallGroups getGroup () {
		return group;
	}
	
	public void setGroup (FirewallGroups group) {
		this.group = group;
	}
	
	public String getStackName() {
		return stackName;
	}

	public void setStackName(String stackName) {
		this.stackName = stackName;
	}

	public AsmMaster getAsmMaster() {
		return asmMaster;
	}

	public void setAsmMaster(AsmMaster asmMaster) {
		this.asmMaster = asmMaster;
	}
	
}
