package com.h3c.iclouds.po;

import java.io.Serializable;
import java.util.Date;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "云资源策略管理", description = "云资源策略管理")
public class Strategy extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	@ApiModelProperty(value = "主键id")
	private Integer id;

	@ApiModelProperty(value = "提醒日志")
	private String day;
	
	@ApiModelProperty(value = "提醒次数")
	private String count;
	
	@ApiModelProperty(value = "创建时间")
	private Date time;
	
	@ApiModelProperty(value = "备注信息")
	private String other;
	
	@ApiModelProperty(value = "备注字段1")
	private String col1;
	
	@ApiModelProperty(value = "备注字段2")
	private String col2;
	
	@ApiModelProperty(value = "类型")
	private String type;
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getCol1() {
		return col1;
	}

	public void setCol1(String col1) {
		this.col1 = col1;
	}

	public String getCol2() {
		return col2;
	}

	public void setCol2(String col2) {
		this.col2 = col2;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


}
