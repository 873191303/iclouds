package com.h3c.iclouds.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.h3c.iclouds.auth.CacheSingleton;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

public class BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Length(max = 36)
	@ApiModelProperty(value = "归属群组Id(R)", required = true, position = 20)
	private String groupId = CacheSingleton.getInstance().getDefaultGroupId();

	@ApiModelProperty(value = "创建人id,不需要传递", notes = "不需要传递", position = 40)
	private String createdBy;

	@ApiModelProperty(value = "创建时间,不需要传递", notes = "不需要传递", position = 22)
	private Date createdDate;

	@ApiModelProperty(value = "修改人id,不需要传递", notes = "不需要传递", position = 40)
	private String updatedBy;

	@ApiModelProperty(value = "修改时间,不需要传递", notes = "不需要传递", position = 24)
	private Date updatedDate;

	@ApiModelProperty(value = "创建人名称,不需要传递", notes = "不需要传递", position = 21)
	private String createdByName;

	@ApiModelProperty(value = "修改人名称,不需要传递", notes = "不需要传递", position = 23)
	private String updatedByName;
	
	public void createdUser(String userId) {
		this.updatedBy = userId;
		this.createdBy = userId;
		this.createdDate = new Date();
		this.updatedDate = createdDate;
	}
	
	public void createdUser(String userId, Date date) {
		this.updatedBy = userId;
		this.createdBy = userId;
		this.createdDate = date;
		this.updatedDate = date;
	}
	
	public void updatedUser(String userId) {
		this.updatedBy = userId;
		this.updatedDate = new Date();
	}
	
	public void createdDate(Date date) {
		this.createdDate = date;
		this.updatedDate = createdDate;
	}
	
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	public String getUpdatedByName() {
		return updatedByName;
	}

	public void setUpdatedByName(String updatedByName) {
		this.updatedByName = updatedByName;
	}
	
}
