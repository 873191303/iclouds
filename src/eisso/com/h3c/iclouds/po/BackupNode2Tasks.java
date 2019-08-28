package com.h3c.iclouds.po;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "云管理第三方爱数任务表", description = "云管理第三方爱数备份节点")
public class BackupNode2Tasks extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "任务ID(NotNull)")
    private Integer taskId;

    @ApiModelProperty(value = "备份节点ID, 注意这里应该存储对应备份节点记录的id")
    @Length(max = 36)
    private String backupNodeId;

    @ApiModelProperty(value = "任务名称")
    @Length(max = 255)
    private String taskName;
    
    @ApiModelProperty(value = "任务类型")
    private Integer taskType;

    @ApiModelProperty(value = "数据源类型")
    @Length(max = 100)
    private String dataSourceType;

    @ApiModelProperty(value = "任务状态")
    @Length(max = 50)
    private String taskStatus;
    
    @ApiModelProperty(value = "上次执行任务结果")
    @Length(max = 255)
    private String taskLastResult;
    
    @ApiModelProperty(value = "上次执行任务时间")
    @Length(max = 255)
    private String taskLastRunTime;
    
    @ApiModelProperty(value = "下次执行任务时间")
    @Length(max = 255)
    private String taskNextRunTime;
    
    public String getTaskStatusName() {
		return taskStatusName;
	}


	public void setTaskStatusName(String taskStatusName) {
		this.taskStatusName = taskStatusName;
	}


	public String getBackupNodeAddr() {
		return backupNodeAddr;
	}


	public void setBackupNodeAddr(String backupNodeAddr) {
		this.backupNodeAddr = backupNodeAddr;
	}


	@ApiModelProperty(value = "客户ID")
    private Integer clientId;
    
    @ApiModelProperty(value = "客户名称")
    @Length(max = 255)
    private String clientName;
    
    @ApiModelProperty(value = "客户地址")
    @Length(max = 255)
    private String clientAddr;
    
    @ApiModelProperty(value = "目标服务ID")
    private Integer destServerId;
    
    @ApiModelProperty(value = "目标服务名称")
    @Length(max = 255)
    private String destServerName;
    
    @ApiModelProperty(value = "目标服务类型")
    @Length(max = 100)
    private String destServerType;
    
    @ApiModelProperty(value = "目标服务地址")
    @Length(max = 255)
    private String destServerAddr;
    
    @ApiModelProperty(value = "源服务ID")
    private Integer srcServerId;
    
    @ApiModelProperty(value = "源服务名称")
    @Length(max = 255)
    private String srcServerName;
    
    @ApiModelProperty(value = "源服务类型")
    @Length(max = 100)
    private String srcServerType;
    
    @ApiModelProperty(value = "源服务地址")
    @Length(max = 255)
    private String srcServerAddr;
    
    @ApiModelProperty(value = "任务类型名")
    @Length(max = 255)
    private String taskTypeName;
    
    @ApiModelProperty(value = "数据源类型名")
    @Length(max = 255)
    private String dataSourceTypeName;
    
    @ApiModelProperty(value = "任务状态名")
    @Length(max = 255)
    private String taskStatusName;
    
    @ApiModelProperty(value = "备份节点地址（不需要传值）")
    private String backupNodeAddr;
    
    public BackupNode2Tasks() {
    	
    }


	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}




	public String getBackupNodeId() {
		return backupNodeId;
	}

	public void setBackupNodeId(String backupNodeId) {
		this.backupNodeId = backupNodeId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}


	public String getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public String getTaskLastResult() {
		return taskLastResult;
	}

	public void setTaskLastResult(String taskLastResult) {
		this.taskLastResult = taskLastResult;
	}

	public String getTaskLastRunTime() {
		return taskLastRunTime;
	}

	public void setTaskLastRunTime(String taskLastRunTime) {
		this.taskLastRunTime = taskLastRunTime;
	}

	public String getTaskNextRunTime() {
		return taskNextRunTime;
	}

	public void setTaskNextRunTime(String taskNextRunTime) {
		this.taskNextRunTime = taskNextRunTime;
	}

	

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientAddr() {
		return clientAddr;
	}

	public void setClientAddr(String clientAddr) {
		this.clientAddr = clientAddr;
	}

	public String getDestServerName() {
		return destServerName;
	}

	public void setDestServerName(String destServerName) {
		this.destServerName = destServerName;
	}

	public String getDestServerType() {
		return destServerType;
	}

	public void setDestServerType(String destServerType) {
		this.destServerType = destServerType;
	}

	public String getDestServerAddr() {
		return destServerAddr;
	}

	public void setDestServerAddr(String destServerAddr) {
		this.destServerAddr = destServerAddr;
	}

	public String getSrcServerName() {
		return srcServerName;
	}

	public void setSrcServerName(String srcServerName) {
		this.srcServerName = srcServerName;
	}

	public String getSrcServerType() {
		return srcServerType;
	}

	public void setSrcServerType(String srcServerType) {
		this.srcServerType = srcServerType;
	}

	public String getSrcServerAddr() {
		return srcServerAddr;
	}

	public void setSrcServerAddr(String srcServerAddr) {
		this.srcServerAddr = srcServerAddr;
	}
	
	public String getTaskTypeName() {
		return taskTypeName;
	}

	public void setTaskTypeName(String taskTypeName) {
		this.taskTypeName = taskTypeName;
	}

	public String getDataSourceTypeName() {
		return dataSourceTypeName;
	}

	public void setDataSourceTypeName(String dataSourceTypeName) {
		this.dataSourceTypeName = dataSourceTypeName;
	}

	
	
	public Integer getTaskType() {
		return taskType;
	}


	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}


	public Integer getClientId() {
		return clientId;
	}


	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}


	public Integer getDestServerId() {
		return destServerId;
	}


	public void setDestServerId(Integer destServerId) {
		this.destServerId = destServerId;
	}


	public Integer getSrcServerId() {
		return srcServerId;
	}


	public void setSrcServerId(Integer srcServerId) {
		this.srcServerId = srcServerId;
	}

}
