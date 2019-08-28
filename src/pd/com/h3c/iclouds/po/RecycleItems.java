package com.h3c.iclouds.po;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "云主机回收站", description = "云主机回收站")
public class RecycleItems extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 9024977562791503019L;
	
	@Length(max = 36)
	@ApiModelProperty(value = "回收ID")
	private String id;
	
	@ApiModelProperty(value = "回收资源ID")
	@Length(max = 36)
	private String resId;
	
	@ApiModelProperty(value = "资源类别ID")
	@Length(max = 36)
	private String classId;
	
	@ApiModelProperty(value = "回收类别,0-自助回收; 1-表示到期回收)")
	private String recycleType;
	
	@ApiModelProperty(value = "进站时间")
	private Date inboundTime;
	
	
	@ApiModelProperty(value = "回收时间")
	private Date recycleTime;
	
	@ApiModelProperty(value = "回收动作")
	private String recycleAction;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRecycleType() {
		return recycleType;
	}

	public void setRecycleType(String recycleType) {
		this.recycleType = recycleType;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getInboundTime() {
		return inboundTime;
	}

	public void setInboundTime(Date inboundTime) {
		this.inboundTime = inboundTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getRecycleTime() {
		return recycleTime;
	}

	public void setRecycleTime(Date recycleTime) {
		this.recycleTime = recycleTime;
	}

	public String getRecycleAction() {
		return recycleAction;
	}

	public void setRecycleAction(String recycleAction) {
		this.recycleAction = recycleAction;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

}
