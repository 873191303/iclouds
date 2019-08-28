package com.h3c.iclouds.po.business;

import java.util.List;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "基础数据产品大类", description = "基础数据产品大类")
public class PrdClass extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "类别id")
	private String id;

	@Length(max = 100)
	@NotNull
	@ApiModelProperty(value = "类别名称 (NotNull)")
	private String className;

	@CheckPattern(type = PatternType.CONTAINS, values = {"1", "0"})
	@ApiModelProperty(value = "标识", notes = "0:正常, 1:停用")
	private String flag;
	
	@ApiModelProperty(value = "是否关联规格表:true-关联规格表,false-不关联规格表")
	private Boolean flavorFlag = false;
	
	@ApiModelProperty(value = "模板")
	private List<Prd2Templates> templates;

	@NotNull
	private String type;
	
	public PrdClass() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public Boolean getFlavorFlag () {
		return flavorFlag;
	}
	
	public void setFlavorFlag (Boolean flavorFlag) {
		this.flavorFlag = flavorFlag;
	}
	
	public List<Prd2Templates> getTemplates() {
		return templates;
	}

	public void setTemplates(List<Prd2Templates> templates) {
		this.templates = templates;
	}
	
	public String getType () {
		return type;
	}
	
	public void setType (String type) {
		this.type = type;
	}
}
