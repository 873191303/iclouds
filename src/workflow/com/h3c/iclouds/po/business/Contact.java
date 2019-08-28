package com.h3c.iclouds.po.business;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "联系人对象")
public class Contact extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "联系人id")
	private String id;

	@Length(max = 36)
	@ApiModelProperty(value = "客户id （NotNull）", required = true)
	private String cusId;

	@Length(max = 50)
	@NotNull
	@ApiModelProperty(value = "姓名 （NotNull）", required = true)
	private String cname;

	@CheckPattern(type = PatternType.CONTAINS, values = {"1", "2"})
	@ApiModelProperty(value = "性别", notes = " 1:男, 2:女")
	private String sex;

	@ApiModelProperty(value = "年龄")
	private Integer age;

	@ApiModelProperty(value = "生日")
	private Date birthday;

	@Length(max = 50)
	@ApiModelProperty(value = "电话")
	private String tel;

	@Length(max = 50)
	@ApiModelProperty(value = "mobile")
	private String mobile;

	@Length(max = 50)
	@ApiModelProperty(value = "微信号")
	private String wxh;

	@Length(max = 50)
	@ApiModelProperty(value = "email")
	private String email;

	@Length(max = 500)
	@ApiModelProperty(value = "职位")
	private String position;

	@Length(max = 500)
	@ApiModelProperty(value = "主管业务")
	private String busiScope;

	@Length(max = 500)
	@ApiModelProperty(value = "背景与性格")
	private String disposition;

	@Length(max = 500)
	@ApiModelProperty(value = "个人爱好")
	private String interest;

	@Length(max = 36)
	@ApiModelProperty(value = "责任人")
	private String owner;
	
	@ApiModelProperty(value = "客户名称")
	private Custom custom;
	
	public Contact() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCusId() {
		return cusId;
	}

	public void setCusId(String cusId) {
		this.cusId = cusId;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getWxh() {
		return wxh;
	}

	public void setWxh(String wxh) {
		this.wxh = wxh;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getBusiScope() {
		return busiScope;
	}

	public void setBusiScope(String busiScope) {
		this.busiScope = busiScope;
	}

	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}

	public String getInterest() {
		return interest;
	}

	public void setInterest(String interest) {
		this.interest = interest;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@JsonIgnore
	public Custom getCustom() {
		return custom;
	}

	public void setCustom(Custom custom) {
		this.custom = custom;
	}

}
