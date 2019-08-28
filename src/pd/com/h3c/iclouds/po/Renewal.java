package com.h3c.iclouds.po;
/**
 * 租期时间表
 * @author mayn
 *
 */

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;

@ApiModel(value = "租期时间", description = "租期时间")
public class Renewal extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private String uuid;
	private String userUuid;
	private String userName;
	private String email;
	private String serviceUuid;
	private String resourceUuid;
	private String resourceName;
	private Date startTime;
	private Date endTime;
	private boolean isdue;
	private String tenantId;
	private Integer tenancyDay;
	private boolean deleted;
	private String description;
	private String dueAction;
	private Integer myhash;
	private String strStartTime;
	private String strEndTime;
	
	private String dateToStr(Date d) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy年mm月dd日 HH:mm:ss:SSS");
			return formatter.format(d);
		}catch(Exception e) {
			
		}
		return "";
	}
	 
	public String getStrStartTime() {
		return strStartTime;
	}
	public void setStrStartTime(String strStartTime) {
		this.strStartTime = strStartTime;
	}
	public String getStrEndTime() {
		return strEndTime;
	}
	public void setStrEndTime(String strEndTime) {
		this.strEndTime = strEndTime;
	}
	public Integer getMyhash() {
		return myhash;
	}
	public void setMyhash(Integer myhash) {
		this.myhash = myhash;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getUserUuid() {
		return userUuid;
	}
	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getServiceUuid() {
		return serviceUuid;
	}
	public void setServiceUuid(String serviceUuid) {
		this.serviceUuid = serviceUuid;
	}
	public String getResourceUuid() {
		return resourceUuid;
	}
	public void setResourceUuid(String resourceUuid) {
		this.resourceUuid = resourceUuid;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
		this.setStrStartTime(this.dateToStr(startTime));
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
		this.setStrEndTime(this.dateToStr(endTime));
	}
	public boolean isIsdue() {
		return isdue;
	}
	public void setIsdue(boolean isdue) {
		this.isdue = isdue;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public Integer getTenancyDay() {
		return tenancyDay;
	}
	public void setTenancyDay(Integer tenancyDay) {
		this.tenancyDay = tenancyDay;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDueAction() {
		return dueAction;
	}
	public void setDueAction(String dueAction) {
		this.dueAction = dueAction;
	}



}
