package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by ykf7408 on 2017/1/10.
 */
@ApiModel(value = "云运维应用配置视图", description = "云运维应用配置视图")
public class AppViews extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = -3162324251923519081L;
	@ApiModelProperty(value = "id")
	private String id;
	@ApiModelProperty(value = "视图名称")
	private String name;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value = "显示顺序")
	private Integer sequence;
	@ApiModelProperty(value = "租户id")
	private String projectId;

	@ApiModelProperty(value = "用户id")
	private String userId;

	@ApiModelProperty(value = "锁定标识")
	private Boolean lock = false;

	@ApiModelProperty(value = "当前sessionid")
	private String sessionId;

	@ApiModelProperty(value = "当前版本号")
	private String version;

	public Boolean getLock() {
		return lock;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setLock(Boolean lock) {
		this.lock = lock;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	

}
