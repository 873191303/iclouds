package com.h3c.iclouds.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/10.
 */
@ApiModel(value = "云运维应用配置信息", description = "云运维应用配置信息")
public class ApplicationMaster extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = -4961906705886833658L;
	
	//@NotNull
	@ApiModelProperty(value = "应用id")
	private String id;
	
	@NotNull
	@Length(max = 20)
	@ApiModelProperty(value = "应用系统名称")
	private String appName;
	
	@CheckPattern(type = PatternType.CONTAINS, values = {"0","1"})
	@ApiModelProperty(value = "访问模式(BS/CS),0表示B/S,1表示C/S，必选项")
	private String mode;
	
	@Pattern(regexp = "^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$")
	@ApiModelProperty(value = "访问域名,要满其正则表达式：^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$")
	private String dns;
	
	@ApiModelProperty(value = "访问路径")
	private String url;
	
	//@Digits(fraction = 0, integer = 0)
	//@Size(min=1,max=65535)
	@ApiModelProperty(value = "端口Port，整数范围1-65535")
	private String port;
	
	//@CheckPattern(type = PatternType.CONTAINS, values = {"0","1"})
	@ApiModelProperty(value = "内外网，0表示内网，1表示外网")
	private String scope;

	@ApiModelProperty(value = "应用架构(J2ee/.Net)")
	private String apprRame;
	
	@ApiModelProperty(value = "上线时间")
	private Date trTime;
	
	@ApiModelProperty(value = "支撑领域")
	private String domains;
	
	@ApiModelProperty(value = "应用责任部门")
	private String appdept;
	
	//@CheckPattern(type = PatternType.CONTAINS, values = {"0","1"})
	@ApiModelProperty(value = "应用状态，0表示正常，1表示禁用")
	private String status;
	
	@ApiModelProperty(value = "备注")
	private String remark;
	
	//@NotNull
	@ApiModelProperty(value = "应用管理员")
	private String owner;
	
	//@NotNull
	@ApiModelProperty(value = "用户专享")
	private Boolean isPrivate;
	
	@ApiModelProperty(value = "租户")
	private String projectId;
	
	@ApiModelProperty(value = "访问协议")
	private String protocol;
	
	@ApiModelProperty(value = "图标路径")
	private String iconPath;
	
	public Map<String, Object> verifyProperties(Map<String, Object> data) {
		Map<String, Object> messageMap = new HashMap<>();
		Object appName=data.get("appName");
		if (appName instanceof String) {
			if (!StrUtils.checkParam(appName)) {
				messageMap.put("appName", ResultType.data_not_null);
			}
		}
		return messageMap;
	}
	
	public ApplicationMaster(String appName, String mode, String dns, String url, String port, String status,
			String owner, String remark) {
		this.appName = appName;
		this.mode = mode;
		this.dns = dns;
		this.url = url;
		this.port = port;
		this.status = status;
		this.owner=owner;
		this.remark=remark;
	}
	
	public ApplicationMaster(String appName, String url, String remark) {
		this.appName = appName;
		this.url = url;
		this.remark=remark;
	}
	
	public ApplicationMaster() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getDns() {
		return dns;
	}

	public void setDns(String dns) {
		this.dns = dns;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getApprRame() {
		return apprRame;
	}

	public void setApprRame(String apprRame) {
		this.apprRame = apprRame;
	}

	@JsonFormat
	public Date getTrTime() {
		return trTime;
	}

	public void setTrTime(Date trTime) {
		this.trTime = trTime;
	}

	public Boolean getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getDomains() {
		return domains;
	}

	public void setDomains(String domains) {
		this.domains = domains;
	}

	public String getAppdept() {
		return appdept;
	}

	public void setAppdept(String appdept) {
		this.appdept = appdept;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getProtocol () {
		return protocol;
	}
	
	public void setProtocol (String protocol) {
		this.protocol = protocol;
	}
	
	public String getIconPath () {
		return iconPath;
	}
	
	public void setIconPath (String iconPath) {
		this.iconPath = iconPath;
	}
}
