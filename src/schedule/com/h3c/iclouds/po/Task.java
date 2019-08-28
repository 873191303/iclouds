package com.h3c.iclouds.po;

import java.util.Date;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;

public class Task extends BaseEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String busId;
	
	//{url: url, parma}
	private String input;
	
	private String busType;
	
	private Date pushTime;
	
	private Date stackTime;
	
	private Date finishTime;
	
	private String stackIp;
	
	// 默认状态为等待
	private String status = ConfigProperty.TASK_STATUS1_WAIT;
	
	private String projectId;
	
	public Task() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBusId() {
		return busId;
	}

	public void setBusId(String busId) {
		this.busId = busId;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getBusType() {
		return busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public Date getPushTime() {
		return pushTime;
	}

	public void setPushTime(Date pushTime) {
		this.pushTime = pushTime;
	}

	public Date getStackTime() {
		return stackTime;
	}

	public void setStackTime(Date stackTime) {
		this.stackTime = stackTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getStackIp() {
		return stackIp;
	}

	public void setStackIp(String stackIp) {
		this.stackIp = stackIp;
	}
	
}
