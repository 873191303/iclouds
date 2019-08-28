package com.h3c.iclouds.po;

import java.io.Serializable;
import java.util.Set;


import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.utils.InvokeAnnotate;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Pattern;

/**
 * 租户，与cloudos同步
 * 
 * @author zkf5485
 */
@ApiModel(value = "云管理租户表", description = "云管理租户表")
public class Project extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;

	@NotNull
	@Pattern(regexp = "^[\\u4E00-\\u9FA50-9a-zA-Z_@-]{2,32}$")
	@ApiModelProperty(value = "租户名称")
	@Length(max = 64)
	private String name;

	@ApiModelProperty(value = "扩展字段")
	private String extra;

	@ApiModelProperty(value = "描述")
	private String description;

	@ApiModelProperty(value = "是否启用")
	private Boolean enabled;

	@ApiModelProperty(value = "所属域")
	@Length(max = 64)
	private String domainId;

	@ApiModelProperty(value = "parent_id")
	@Length(max = 64)
	private String parentId;

	@ApiModelProperty(value = "删除标志", notes = "1-成功删除,0-正常使用")
	private Integer flag;

	@ApiModelProperty(value = "客户id")
	@Length(max = 50)
	private String cusId;

	@ApiModelProperty(value = "租户管理员")
	private String userName;

	private Set<Project2Network> networks;
	
	private Set<QuotaUsed> quotaUseds;
	
	@ApiModelProperty(value = "客户名称")
	private String cusName;
	
	@NotNull
	@CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})	// 0:是 1:否
	@ApiModelProperty(value = "是否允许自助 (NotNull)(Contain: 0-是; 1-否)")
	private String selfAllowed = ConfigProperty.YES;	// 默认为允许

	public Project() {
		
	}
	
	public Project(String name, String cusId, String description, String parentId) {
		this.cusId = cusId;
		this.name = name;
		this.description = description;
		this.parentId = parentId;
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

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	@InvokeAnnotate(type = PatternType.FK)
	public String getCusId() {
		return cusId;
	}

	public void setCusId(String cusId) {
		this.cusId = cusId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@JsonIgnore
	public Set<Project2Network> getNetworks() {
		return networks;
	}

	public void setNetworks(Set<Project2Network> networks) {
		this.networks = networks;
	}
	
	@JsonIgnore
	public Set<QuotaUsed> getQuotaUseds() {
		return quotaUseds;
	}

	public void setQuotaUseds(Set<QuotaUsed> quotaUseds) {
		this.quotaUseds = quotaUseds;
	}

	public String getCusName() {
		return cusName;
	}

	public void setCusName(String cusName) {
		this.cusName = cusName;
	}

	public String getSelfAllowed() {
		return selfAllowed;
	}

	public void setSelfAllowed(String selfAllowed) {
		this.selfAllowed = selfAllowed;
	}
	
}
