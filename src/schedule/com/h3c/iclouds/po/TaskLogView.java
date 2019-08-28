package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by yKF7317 on 2017/4/20.
 */
public class TaskLogView extends BaseEntity implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "任务id")
	private String id;
	
	@ApiModelProperty(value = "业务id")
	private String busId;
	
	@ApiModelProperty(value = "入口参数")
	private String input;
	
	@ApiModelProperty(value = "任务对象及操作类型")
	private String busType;
	
	@ApiModelProperty(value = "入栈时间")
	private Date pushTime;
	
	@ApiModelProperty(value = "出栈时间")
	private Date stackTime;
	
	@ApiModelProperty(value = "完成时间")
	private Date finishTime;
	
	@ApiModelProperty(value = "状态")
	private String status;
	
	@ApiModelProperty(value = "租户id")
	private String projectId;
	
	@ApiModelProperty(value = "租户名称")
	private String projectName;
	
	@ApiModelProperty(value = "执行服务器ip")
	private String stackIp;
	
	public TaskLogView() {
		
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
	
	public String getProjectName () {
		return projectName;
	}
	
	public void setProjectName (String projectName) {
		this.projectName = projectName;
	}
}
