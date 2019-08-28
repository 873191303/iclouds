package com.h3c.iclouds.po;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiModelProperty;

@Api(value = "资产库管理资产维保信息", description = "资产库管理资产维保信息")
public class Maintenancs extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 36)
	@ApiModelProperty(value = "资产ID")
	private String assId;

	@Length(max = 50)
	@ApiModelProperty(value = "维保合同")
	private String contract;

	@Length(max = 50)
	@ApiModelProperty(value = "维保级别")
	private String depth;

	//@Max(24)
	@ApiModelProperty(value = "维保费用")
	private float expense;

	@ApiModelProperty(value = "维保开始时间")
	private Date begTime;

	@ApiModelProperty(value = "维保截止时间")
	private Date endTime;

	@Length(max = 500)
	@ApiModelProperty(value = "维保供应商")
	private String msupId;

	@Length(max = 20)
	@ApiModelProperty(value = "联系方式")
	private String contact;

	@Length(max = 50)
	@ApiModelProperty(value = "维保负责人")
	private String owner;

	@Length(max = 100)
	@ApiModelProperty(value = "备注")
	private String remark;

	@Length(max = 50)
	@ApiModelProperty(value = "附件")
	private String attach;

	//@Max(16)
	@ApiModelProperty(value = "提前提示到期时间")
	private Integer warnTime;

	public Maintenancs() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAssId() {
		return assId;
	}

	public void setAssId(String assId) {
		this.assId = assId;
	}

	public String getContract() {
		return contract;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}

	public String getDepth() {
		return depth;
	}

	public void setDepth(String depth) {
		this.depth = depth;
	}

	public float getExpense() {
		return expense;
	}

	public void setExpense(float expense) {
		this.expense = expense;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getBegTime() {
		return begTime;
	}

	public void setBegTime(Date begTime) {
		this.begTime = begTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getMsupId() {
		return msupId;
	}

	public void setMsupId(String msupId) {
		this.msupId = msupId;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public Integer getWarnTime() {
		return warnTime;
	}

	public void setWarnTime(Integer warnTime) {
		this.warnTime = warnTime;
	}
}
