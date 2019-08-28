package com.h3c.iclouds.po;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

/**
 * 资源
 * @author zkf5485
 *
 */
@SuppressWarnings("rawtypes")
@ApiModel(value = "资源", description = "资源", discriminator = "群组")
public class Resource extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "资源id")
	private String id;

	@Length(max = 100)
	@NotNull
	@ApiModelProperty(value = "资源名称 (NotNull)", required = true)
	private String resName;

	@Length(max = 100)
	@NotNull
	@ApiModelProperty(value = "模块名称 (NotNull)", required = true)
	private String modeName;

	@Length(max = 100)
	@NotNull
	@CheckPattern(type = PatternType.SYSTYPE)
	@ApiModelProperty(value = "系统类别 (NotNull) (LENGTH = 4) 0:运营 1:运维 2:电信 3:租户", required = true)
	private String sysType;

	@Length(max = 100)
	@NotNull
	@CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})	// 1：系统类资源 0：业务类资源
	@ApiModelProperty(value = "授权类别 (NotNull)(Contain: 0-可授权; 1-不可授权)", required = true)
	private String authType;

	@NotNull
	@CheckPattern(type = PatternType.CONTAINS, values = {"1", "2"})	// 1：系统类资源 0：业务类资源
	@ApiModelProperty(value = "功能类别 (NotNull)(Contain: 1-菜单资源; 2-操作资源)", required = true)
	private String funType;

	@Length(max = 100)
	@NotNull
	@ApiModelProperty(value = "资源路径 (NotNull)", required = true)
	private String resPath;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "父级资源")
	private String parentId;
	
	@ApiModelProperty(value = "层次 (NotNull)")
	@NotNull
	private Integer depth;

	@Length(max = 36)
	private String defaultRoleId = "";

	@Length(max = 50)
	@ApiModelProperty(value = "资源描述")
	private String description;
	
	@ApiModelProperty(value = "菜单顺序号", required = true)
	@NotNull
	private Integer itemSeq;

	private Set resToRoleSet = new HashSet();
	
	private List<Resource> children;
	
	private String parentName;
	
	public Resource() {

	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResName() {
		return resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

	public String getModeName() {
		return modeName;
	}

	public void setModeName(String modeName) {
		this.modeName = modeName;
	}

	public String getSysType() {
		return sysType;
	}

	public void setSysType(String sysType) {
		this.sysType = sysType;
	}

	public String getResPath() {
		return resPath;
	}

	public void setResPath(String resPath) {
		this.resPath = resPath;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public String getDefaultRoleId() {
		return defaultRoleId;
	}

	public void setDefaultRoleId(String defaultRoleId) {
		this.defaultRoleId = defaultRoleId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getItemSeq() {
		return itemSeq;
	}

	public void setItemSeq(Integer itemSeq) {
		this.itemSeq = itemSeq;
	}

	@JsonIgnore
	public Set getResToRoleSet() {
		return resToRoleSet;
	}

	public void setResToRoleSet(Set resToRoleSet) {
		this.resToRoleSet = resToRoleSet;
	}

	public List<Resource> getChildren() {
		return children;
	}

	public void setChildren(List<Resource> children) {
		this.children = children;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public String getFunType() {
		return funType;
	}

	public void setFunType(String funType) {
		this.funType = funType;
	}
	
}
