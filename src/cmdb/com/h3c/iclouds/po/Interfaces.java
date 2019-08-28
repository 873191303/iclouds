package com.h3c.iclouds.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "云运维系统参数设置2接口类", description = "云运维系统参数设置2接口类")
public class Interfaces extends BaseEntity implements java.io.Serializable{

	
	private static final long serialVersionUID = -2995280414102973440L;

	@ApiModelProperty(value = "id")
	private String id;
	
	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "类型")
	private String type;
	
	@Length(max = 36)
	@ApiModelProperty(value = "IP地址")
	private String ip;
	
	@Length(max = 36)
	@ApiModelProperty(value = "端口")
	private String port;
	
	@Length(max = 36)
	@ApiModelProperty(value = "管理员")
	private String admin;
	
	@Length(max = 36)
	@ApiModelProperty(value = "密码")
	private String passwd;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	@JsonIgnore
	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
}
