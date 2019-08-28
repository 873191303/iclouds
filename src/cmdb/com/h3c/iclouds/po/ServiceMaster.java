package com.h3c.iclouds.po;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

/**
 * Created by Administrator on 2017/1/10.
 */	
@ApiModel(value = "云运维服务配置信息", description = "云运维服务配置信息")
public class ServiceMaster extends BaseEntity implements java.io.Serializable{
	
	private static final long serialVersionUID = -6758590200728279938L;
	
	@ApiModelProperty(value = "服务ID")
    private String id;
	
	@ApiModelProperty(value = "服务类型")
	private Integer serviceType;
	
	@ApiModelProperty(value = "版本")
    private String serviceVersion;
	
	@ApiModelProperty(value = "服务名称")
	@Length(max = 20)
    private String name;
	
	@ApiModelProperty(value = "实例名称")
    private String instanceName;
	
	@ApiModelProperty(value = "主机")
    private String hosts;
	
	@ApiModelProperty(value = "主机类型")
    private String hostType;
	
	@Pattern(regexp = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))")
	@ApiModelProperty(value = "IP地址：((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))")
    private String ip;
	
	@Min(1)
	@Max(65535)
	@ApiModelProperty(value = "端口Port，整数范围1-65535")
    private String port;
	@ApiModelProperty(value = "管理控制台")
    private String adminConsole;
	
	//@Pattern(regexp = "^http:\\\\([a-zA-Z\\d][a-zA-Z\\d-_]+\\.)+[a-zA-Z\\d-_][^ ]*$")
	@ApiModelProperty(value = "访问地址，对应的正则表达式：^http:\\\\([a-zA-Z\\d][a-zA-Z\\d-_]+\\.)+[a-zA-Z\\d-_][^ ]*$")
    private String accessAddress;
	
	@ApiModelProperty(value = "支持等级")
    private String supportLevel;
	
	@ApiModelProperty(value = "用途")
    private String purpose;
	
	@ApiModelProperty(value = "备注")
    private String note;
	
	@ApiModelProperty(value = "服务管理员")
    private String serverOwner;
	
	@ApiModelProperty(value = "租户")
    private String projectId;
	
	@ApiModelProperty(value = "用户专享")
	private String isPrivate;
	
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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

	public Integer getServiceType() {
		return serviceType;
	}

	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}

	public String getServiceVersion() {
		return serviceVersion;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
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

	public String getAccessAddress() {
		return accessAddress;
	}

	public void setAccessAddress(String accessAddress) {
		this.accessAddress = accessAddress;
	}

	public String getSupportLevel() {
		return supportLevel;
	}

	public void setSupportLevel(String supportLevel) {
		this.supportLevel = supportLevel;
	}

	public String getServerOwner() {
		return serverOwner;
	}

	public void setServerOwner(String serverOwner) {
		this.serverOwner = serverOwner;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(String isPrivate) {
		this.isPrivate = isPrivate;
	}

}
