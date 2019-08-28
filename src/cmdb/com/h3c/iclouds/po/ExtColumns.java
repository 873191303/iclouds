package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "资产管理扩展属性列", description = "资产管理扩展属性列")
public class ExtColumns extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private String id;

	@Length(max = 36)
	@ApiModelProperty(value = "资产类型")
	private String assType;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "属性列值 (NotNull)")
	private String xcName;

	@Length(max = 36)
	@NotNull
	@ApiModelProperty(value = "属性列类型 (NotNull)")
	private String xcType;

	@ApiModelProperty(value = "属性列长度")
	private Integer xcLength;

	@Length(max = 36)
	@ApiModelProperty(value = "默认值")
	private String defaultValue;
	
	@ApiModelProperty(value = "显示顺序")
	private Integer seq;
	
	@ApiModelProperty(value = "资产类型")
	private String assTypeCode;
	
	public ExtColumns() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAssType() {
		return assType;
	}

	public void setAssType(String assType) {
		this.assType = assType;
	}

	public String getXcName() {
		return xcName;
	}

	public void setXcName(String xcName) {
		this.xcName = xcName;
	}

	public String getXcType() {
		return xcType;
	}

	public void setXcType(String xcType) {
		this.xcType = xcType;
	}

	public Integer getXcLength() {
		return xcLength;
	}

	public void setXcLength(Integer xcLength) {
		this.xcLength = xcLength;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getAssTypeCode() {
		return assTypeCode;
	}

	public void setAssTypeCode(String assTypeCode) {
		this.assTypeCode = assTypeCode;
	}

}
