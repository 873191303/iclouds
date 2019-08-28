package com.h3c.iclouds.po;

import javax.validation.constraints.Min;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "资产管理资源型号", description = "资产管理资源型号")
public class Class2Items extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "设备型号编码 (NotNull)")
	private String itemId;

	@Length(max = 500)
	@NotNull
	@ApiModelProperty(value = "设备型号名称 (NotNull)")
	private String itemName;
	
	@ApiModelProperty(value = "资源类别,不需要传递")
	private String resType;
	
	@NotNull
	@Min(1)
	@ApiModelProperty(value = "设备U数 (NotNull)(min >= 1)")
	private Integer unum;
	
	@CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})
	@ApiModelProperty(value = "状态 (NotNull)(Contain: 0-启用; 1-禁用)", required = true, notes = "0:启用 1:禁用")
	private String flag;
	
	@ApiModelProperty(value = "资源类别")
	private String resTypeCode;
	
	public Class2Items() {
		
	}

	public Class2Items(String id, String itemId, String itemName, Integer unum, String resTypeCode) {
		super();
		this.id = id;
		this.itemId = itemId;
		this.itemName = itemName;
		this.unum = unum;
		this.resTypeCode = resTypeCode;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getResType() {
		return resType;
	}

	public void setResType(String resType) {
		this.resType = resType;
	}

	public Integer getUnum() {
		return unum;
	}

	public void setUnum(Integer unum) {
		this.unum = unum;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getResTypeCode() {
		return resTypeCode;
	}

	public void setResTypeCode(String resTypeCode) {
		this.resTypeCode = resTypeCode;
	}
	
}
