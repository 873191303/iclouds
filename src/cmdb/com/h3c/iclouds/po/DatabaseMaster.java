package com.h3c.iclouds.po;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

/**
 * Created by Administrator on 2017/1/10.
 */
@ApiModel(value = "云运维数据库配置信息", description = "云运维数据库配置信息")
public class DatabaseMaster extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = -401959634133619232L;
	@ApiModelProperty(value = "dbID")
	private String id;
	
	@ApiModelProperty(value = "DB描述")
	private String dbDesc;
	
	@ApiModelProperty(value = "版本")
	private String dbVersion;
	
	@ApiModelProperty(value = "db实例")
	private String instanceName;
	
	@NotNull
	@ApiModelProperty(value = "db名称，不可为空")
	@Length(max = 20)
	private String dbName;
	
	@Min(1)
	@Max(65535)
	@ApiModelProperty(value = "实例端口,整数范围1-65535")
	private String port;
	
	@ApiModelProperty(value = "主机")
	private String hosts;
	
	@ApiModelProperty(value = "主机类型")
	private String hostType;
	
	@Pattern(regexp = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))")
	@ApiModelProperty(value = "IP地址，需检验IP")
	private String ip;
	
	@ApiModelProperty(value = "管理控制台")
	private String adminConsole;
	
	@ApiModelProperty(value = "支持等级")
	private String supportLevel;
	
	@ApiModelProperty(value = "用途")
	private String purpose;
	
	@ApiModelProperty(value = "备注")
	private String remark;
	
	@ApiModelProperty(value = "DB管理员")
	private String dbowner;
	
	@ApiModelProperty(value = "租户")
	private String projectId;
	
	@ApiModelProperty(value = "用户专享")
	private Boolean isPrivate;
	
	@CheckPattern(type = PatternType.CONTAINS, values = {"0","1","2","3","4"})
	@ApiModelProperty(value = "DB类型，0表示oracle,1表示mysql,2表示nosql,3表示SQLServer，4表示其他数据库")
	private Integer dbType;

	public DatabaseMaster() {
		// TODO Auto-generated constructor stub
	}
	public DatabaseMaster(String dbName, String port, String hosts, String hostType, String ip, String dbowner,
			String remark,String dbVersion,String dbType) {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getDbDesc() {
		return dbDesc;
	}

	public void setDbDesc(String dbDesc) {
		this.dbDesc = dbDesc;
	}

	public String getDbVersion() {
		return dbVersion;
	}

	public void setDbVersion(String dbVersion) {
		this.dbVersion = dbVersion;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getHostType() {
		return hostType;
	}

	public void setHostType(String hostType) {
		this.hostType = hostType;
	}

	public String getAdminConsole() {
		return adminConsole;
	}

	public void setAdminConsole(String adminConsole) {
		this.adminConsole = adminConsole;
	}

	public String getSupportLevel() {
		return supportLevel;
	}

	public void setSupportLevel(String supportLevel) {
		this.supportLevel = supportLevel;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public Boolean getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public Integer getDbType() {
		return dbType;
	}

	public void setDbType(Integer dbType) {
		this.dbType = dbType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDbowner() {
		return dbowner;
	}

	public void setDbowner(String dbowner) {
		this.dbowner = dbowner;
	}

}
