package com.h3c.iclouds.po.business;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "基础数据产品申请条目属性模板", description = "基础数据产品申请条目属性模板")
public class Prd2Templates extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "模板id")
	private String id;

	@Length(max = 36)
	@ApiModelProperty(value = "产品类别id")
	private String classId;

	@Length(max = 36)
	@ApiModelProperty(value = "key")
	private String key;

	@Length(max = 100)
	@ApiModelProperty(value = "keyName")
	private String keyName;

	@ApiModelProperty(value = "显示排序")
	private Integer orderBy;

	@Length(max = 200)
	@ApiModelProperty(value = "检验规则")
	private String validate;

	@Length(max = 1)
	@ApiModelProperty(value = "数据类型")
	private String dataType;

	@Length(max = 1)
	@ApiModelProperty(value = "是否显示")
	private String isShow;

	@Length(max = 1)
	@ApiModelProperty(value = "是否必须")
	private String isMust;

	@Length(max = 16)
	@ApiModelProperty(value = "单位")
	private String units;
	
	public String getValidate() {
		return validate;
	}

	public void setValidate(String validate) {
		this.validate = validate;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	public String getIsMust() {
		return isMust;
	}

	public void setIsMust(String isMust) {
		this.isMust = isMust;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public Prd2Templates() {
	
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public Integer getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}
	
}
