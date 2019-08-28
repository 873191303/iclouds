package com.h3c.iclouds.po;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "基础数据系统初始编码", description = "基础数据系统初始编码")
public class InitCode extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "编码类型 (NotNull)")
	private String codeTypeId;

	@Length(max = 100)
	@ApiModelProperty(value = "类型描述")
	private String typeDesc;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "编码值 (NotNull)")
	private String codeId;

	@Length(max = 100)
	@NotNull
	@ApiModelProperty(value = "编码值名称 (NotNull)")
	private String codeName;
	
	@ApiModelProperty(value = "顺序号")
	private Integer codeSeq;
	
	@NotNull
	@CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})
	@ApiModelProperty(value = "状态 (NotNull)(Contain: 0-启用; 1-禁用)", required = true, notes = "0:启用 1:禁用")
	private String status;

	@ApiModelProperty(value = "启用时间")
	private Date effectiveDate;

	@ApiModelProperty(value = "截至时间")
	private Date expiryDate;

	@Length(max = 100)
	@ApiModelProperty(value = "子系统编码")
	private String sysCode;
	
	public InitCode() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCodeTypeId() {
		return codeTypeId;
	}

	public void setCodeTypeId(String codeTypeId) {
		this.codeTypeId = codeTypeId;
	}

	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}

	public String getCodeId() {
		return codeId;
	}

	public void setCodeId(String codeId) {
		this.codeId = codeId;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public Integer getCodeSeq() {
		return codeSeq;
	}

	public void setCodeSeq(Integer codeSeq) {
		this.codeSeq = codeSeq;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getSysCode() {
		return sysCode;
	}

	public void setSysCode(String sysCode) {
		this.sysCode = sysCode;
	}
	
}
