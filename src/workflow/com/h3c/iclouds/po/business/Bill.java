package com.h3c.iclouds.po.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 计费流水账单
 * Created by yKF7317 on 2017/8/10.
 */
@ApiModel(value = "云管理云资源计量流水账单")
public class Bill extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "流水id")
	private String id;
	
	@ApiModelProperty(value = "资源实例id")
	private String instanceId;
	
	@ApiModelProperty(value = "明细id")
	private String measureId;
	
	@ApiModelProperty(value = "扣费年")
	private Integer myear;
	
	@ApiModelProperty(value = "扣费月")
	private Integer mmonth;
	
	@ApiModelProperty(value = "扣费日")
	private Integer mday;
	
	@ApiModelProperty(value = "计费数量")
	private Long num;
	
	@ApiModelProperty(value = "费用整理")
	private Double amount;
	
	@ApiModelProperty(value = "扣费日期")
	private Date billDate;
	
	@ApiModelProperty(value = "扣费类型")
	private String billType;
	
	@ApiModelProperty(value = "产品id")
	private String classId;
	
	@ApiModelProperty(value = "资源名称")
	private String instanceName;
	
	@ApiModelProperty(value = "资源类型")
	private String instanceType;
	
	@ApiModelProperty(value = "用户id")
	private String userId;
	
	@ApiModelProperty(value = "租户id")
	private String tenantId;
	
	@ApiModelProperty(value = "用户名称")
	private String userName;
	
	@ApiModelProperty(value = "租户名称")
	private String projectName;
	
	@ApiModelProperty(value = "资源开始计费时间")
	private Date beginDate;
	
	@ApiModelProperty(value = "资源结束计费时间")
	private Date endDate;
	
	@ApiModelProperty(value = "上次扣费时间")
	private Date lastBillDate;
	
	private String billTime;
	
	@ApiModelProperty(value = "单价")
	private Double price;
	
	@ApiModelProperty(value = "步长")
	private Integer step;
	
	@ApiModelProperty(value = "步长单价")
	private Double stepPrice;
	
	@ApiModelProperty(value = "计费单位")
	private String unit;
	
	@ApiModelProperty(value = "扣费周期")
	private String measureType;
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	public String getInstanceId () {
		return instanceId;
	}
	
	public void setInstanceId (String instanceId) {
		this.instanceId = instanceId;
	}
	
	public String getMeasureId () {
		return measureId;
	}
	
	public void setMeasureId (String measureId) {
		this.measureId = measureId;
	}
	
	public Integer getMyear () {
		return myear;
	}
	
	public void setMyear (Integer myear) {
		this.myear = myear;
	}
	
	public Integer getMmonth () {
		return mmonth;
	}
	
	public void setMmonth (Integer mmonth) {
		this.mmonth = mmonth;
	}
	
	public Integer getMday () {
		return mday;
	}
	
	public void setMday (Integer mday) {
		this.mday = mday;
	}
	
	public Long getNum () {
		return num;
	}
	
	public void setNum (Long num) {
		this.num = num;
	}
	
	public Double getAmount () {
		return amount;
	}
	
	public void setAmount (Double amount) {
		this.amount = amount;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getBillDate () {
		return billDate;
	}
	
	public void setBillDate (Date billDate) {
		this.billDate = billDate;
	}
	
	public String getBillType () {
		return billType;
	}
	
	public void setBillType (String billType) {
		this.billType = billType;
	}
	
	public String getClassId () {
		return classId;
	}
	
	public void setClassId (String classId) {
		this.classId = classId;
	}
	
	public String getInstanceName () {
		return instanceName;
	}
	
	public void setInstanceName (String instanceName) {
		this.instanceName = instanceName;
	}
	
	public String getInstanceType () {
		return instanceType;
	}
	
	public void setInstanceType (String instanceType) {
		this.instanceType = instanceType;
	}
	
	public String getUserId () {
		return userId;
	}
	
	public void setUserId (String userId) {
		this.userId = userId;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}
	
	public String getUserName () {
		return userName;
	}
	
	public void setUserName (String userName) {
		this.userName = userName;
	}
	
	public String getProjectName () {
		return projectName;
	}
	
	public void setProjectName (String projectName) {
		this.projectName = projectName;
	}
	
	public String getBillTime () {
		return billTime;
	}
	
	public void setBillTime (String billTime) {
		this.billTime = billTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getBeginDate () {
		return beginDate;
	}
	
	public void setBeginDate (Date beginDate) {
		this.beginDate = beginDate;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getEndDate () {
		return endDate;
	}
	
	public void setEndDate (Date endDate) {
		this.endDate = endDate;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getLastBillDate () {
		return lastBillDate;
	}
	
	public void setLastBillDate (Date lastBillDate) {
		this.lastBillDate = lastBillDate;
	}
	
	public Double getPrice () {
		return price;
	}
	
	public void setPrice (Double price) {
		this.price = price;
	}
	
	public Integer getStep () {
		return step;
	}
	
	public void setStep (Integer step) {
		this.step = step;
	}
	
	public Double getStepPrice () {
		return stepPrice;
	}
	
	public void setStepPrice (Double stepPrice) {
		this.stepPrice = stepPrice;
	}
	
	public String getUnit () {
		return unit;
	}
	
	public void setUnit (String unit) {
		this.unit = unit;
	}
	
	public String getMeasureType () {
		return measureType;
	}
	
	public void setMeasureType (String measureType) {
		this.measureType = measureType;
	}
}
