package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

/**
 * 登录日志
 * @author zkf5485
 *
 */
@ApiModel(value = "系统管理用户登陆日志表", description = "系统管理用户登陆日志表")
public class OperateLogs extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	public OperateLogs() {

	}

	@ApiModelProperty(value = "id")
	private String id;

	@NotNull
	@Length(max = 36)
	@ApiModelProperty(value = "用户id")
	private String userId;

	@Length(max = 36)
	@ApiModelProperty(value = "日志类型")
	private String logTypeId;

	@ApiModelProperty(value = "备注")
	private String remark;

	@Length(max = 128)
	@ApiModelProperty(value = "日志结果")
	private String result;

	@ApiModelProperty(value = "用户名称")
	private String userName;

	@ApiModelProperty(value = "资源id")
	private String resourceId;

	@ApiModelProperty(value = "资源名称")
	private String resourceName;

	@ApiModelProperty(value = "登录ip")
	private String ip;

	private String projectId;

	private String loginName;

	private String logTypeName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLogTypeId() {
		return logTypeId;
	}

	public void setLogTypeId(String logTypeId) {
		this.logTypeId = logTypeId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLogTypeName() {
		return logTypeName;
	}

	public void setLogTypeName(String logTypeName) {
		this.logTypeName = logTypeName;
	}
}
