package com.h3c.iclouds.po;

import java.util.Date;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.CompareEnum;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "差异同步")
public class Differences extends BaseEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;
	
	@ApiModelProperty(value = "对应资源uuid", required = true)
	private String uuid;
	
	// 差异数据类别
	@ApiModelProperty(value = "差异数据类别", required = true)
	private String dataType;
	
	// 差异原因
	@ApiModelProperty(value = "差异原因：IN_CLOUDOS(cloudos中存在), IN_IYUN(行业云中存在), DATA_DIFF(两端的数据不一致)")
	private String diffType;
	
	@ApiModelProperty(value = "任务id")
	private String taskDispatchId;
	
	@ApiModelProperty(value = "同步时间")
	private Date syncDate;
	
	@ApiModelProperty(value = "处理类型")
	private String todoType;
	
	@ApiModelProperty(value = "处理结果")
	private String result;
	
	@ApiModelProperty(value = "处理人")
	private String todoUser;
	
	@ApiModelProperty(value = "处理时间")
	private Date todoTime;
	
	@ApiModelProperty(value = "差异描述")
	private String description;
	
	@ApiModelProperty(value = "归属租户名称")
	private String projectName;
	
	@ApiModelProperty(value = "差异数据类别名称", required = true)
	private String dataTypeValue;
	
	@ApiModelProperty(value = "差异原因名称", required = true)
	private String diffTypeValue;
	
	@ApiModelProperty(value = "处理人名称")
	private String todoUserName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
		try {
			CompareEnum en = CompareEnum.valueOf(dataType);
			this.dataTypeValue = en.getOpeValue();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String getDiffType() {
		return diffType;
	}

	public void setDiffType(String diffType) {
		this.diffType = diffType;
		try {
			CompareEnum en = CompareEnum.valueOf(diffType);
			this.diffTypeValue = en.getOpeValue();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String getTaskDispatchId() {
		return taskDispatchId;
	}

	public void setTaskDispatchId(String taskDispatchId) {
		this.taskDispatchId = taskDispatchId;
	}

	public Date getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(Date syncDate) {
		this.syncDate = syncDate;
	}

	public String getTodoType() {
		return todoType;
	}

	public void setTodoType(String todoType) {
		this.todoType = todoType;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getTodoUser() {
		return todoUser;
	}

	public void setTodoUser(String todoUser) {
		this.todoUser = todoUser;
	}

	public Date getTodoTime() {
		return todoTime;
	}

	public void setTodoTime(Date todoTime) {
		this.todoTime = todoTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getDataTypeValue() {
		return dataTypeValue;
	}

	public void setDataTypeValue(String dataTypeValue) {
		this.dataTypeValue = dataTypeValue;
	}

	public String getDiffTypeValue() {
		return diffTypeValue;
	}

	public void setDiffTypeValue(String diffTypeValue) {
		this.diffTypeValue = diffTypeValue;
	}

	public String getTodoUserName() {
		return todoUserName;
	}

	public void setTodoUserName(String todoUserName) {
		this.todoUserName = todoUserName;
	}
}
