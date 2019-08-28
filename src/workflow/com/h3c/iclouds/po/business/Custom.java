package com.h3c.iclouds.po.business;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.utils.InvokeAnnotate;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@SuppressWarnings("rawtypes")
@ApiModel(value = "客户对象")
public class Custom extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "客户id")
	private String id;

	@Length(max = 500)
	@NotNull
	@ApiModelProperty(value = "客户名称 （NotNull）", required = true)
	private String cusName;

	@Length(max = 36)
	@ApiModelProperty(value = "客户级别")
	private String lvls;

	@Length(max = 50)
	@ApiModelProperty(value = "客户背景简介")
	private String custIntroduction;

	@Length(max = 50)
	@ApiModelProperty(value = "主营业务")
	private String manBusiness;

	@Length(max = 50)
	@ApiModelProperty(value = "地址")
	private String address;

	@Length(max = 50)
	@ApiModelProperty(value = "电话")
	private String tel;

	@Length(max = 50)
	@ApiModelProperty(value = "网站URL")
	private String url;

	@Length(max = 50)
	@ApiModelProperty(value = "传真")
	private String fax;

	@Length(max = 50)
	@ApiModelProperty(value = "email")
	private String email;
	
	@ApiModelProperty(value = "营业额")
	private Double turnover;

	@Length(max = 36)
	@ApiModelProperty(value = "责任人")
	private String owner;
	
	@ApiModelProperty(value = "客户状态，不需要传递", notes = "0：正常 1：停用")
	private String status;
	
	@ApiModelProperty(value = "客户联系人数量")
	private Integer contactCount;
	
	private Set contactSet = new HashSet();
	
	public Custom() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCusName() {
		return cusName;
	}

	public void setCusName(String cusName) {
		this.cusName = cusName;
	}

	public String getLvls() {
		return lvls;
	}

	public void setLvls(String lvls) {
		this.lvls = lvls;
	}

	public String getCustIntroduction() {
		return custIntroduction;
	}

	public void setCustIntroduction(String custIntroduction) {
		this.custIntroduction = custIntroduction;
	}

	public String getManBusiness() {
		return manBusiness;
	}

	public void setManBusiness(String manBusiness) {
		this.manBusiness = manBusiness;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Double getTurnover() {
		return turnover;
	}

	public void setTurnover(Double turnover) {
		this.turnover = turnover;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@JsonIgnore
	public Set getContactSet() {
		return contactSet;
	}

	public void setContactSet(Set contactSet) {
		this.contactSet = contactSet;
	}
	
	@InvokeAnnotate(type = PatternType.UNCOPY)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JsonIgnore
	public Integer getContactCount() {
		return contactCount;
	}

	public void setContactCount(Integer contactCount) {
		this.contactCount = contactCount;
	}

}
