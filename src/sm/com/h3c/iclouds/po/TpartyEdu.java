package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/22.
 */
@ApiModel(value = "教育云租户映射表", description = "教育云租户映射表")
public class TpartyEdu extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "id")
	private String id;
	
	@NotNull
	@Length(max = 36)
	@ApiModelProperty(value = "学校编码（NotNull）")
	@Pattern(regexp = "^[0-9a-zA-Z_@-]{1,36}$")
	private String eduCode;
	
	@NotNull
	@Length(max = 36)
	@ApiModelProperty(value = "学校名称（NotNull）")
	@Pattern(regexp = "^[\\u4E00-\\u9FA50-9a-zA-Z_@-]{2,32}$")
	private String eduName;
	
	@NotNull
	@ApiModelProperty(value = "映射租户id（NotNull）")
	private String projectId;
	
	@NotNull
	@ApiModelProperty(value = "状态：0-启用，1-禁用（NotNull）")
	@CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})
	private String status;
	
	@Length(max = 100)
	@ApiModelProperty(value = "备注")
	private String remark;
	
	@ApiModelProperty(value = "租户名称（不需要传递）")
	private String projectName;
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	public String getEduCode () {
		return eduCode;
	}
	
	public void setEduCode (String eduCode) {
		this.eduCode = eduCode;
	}
	
	public String getEduName () {
		return eduName;
	}
	
	public void setEduName (String eduName) {
		this.eduName = eduName;
	}
	
	public String getProjectId () {
		return projectId;
	}
	
	public void setProjectId (String projectId) {
		this.projectId = projectId;
	}
	
	public String getStatus () {
		return status;
	}
	
	public void setStatus (String status) {
		this.status = status;
	}
	
	public String getRemark () {
		return remark;
	}
	
	public void setRemark (String remark) {
		this.remark = remark;
	}
	
	public String getProjectName () {
		return projectName;
	}
	
	public void setProjectName (String projectName) {
		this.projectName = projectName;
	}
}
