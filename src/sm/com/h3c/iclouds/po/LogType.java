package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

/**
 * 日志类型
 * @author zkf5485
 *
 */
@ApiModel(value = "系统管理日志类型", description = "系统管理日志类型")
public class LogType extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	public LogType() {

	}

	@ApiModelProperty(value = "id")
	private String id;

	@NotNull
	@Length(max = 100)
	@ApiModelProperty(value = "描述")
	private String description;

	@Length(max = 100)
	@ApiModelProperty(value = "备注")
	private String remark;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
