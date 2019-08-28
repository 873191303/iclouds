package com.h3c.iclouds.po;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by yKF7317 on 2016/11/22.
 */
@ApiModel(value = "云管理租户配额使用表", description = "云管理租户配额使用表")
public class QuotaUsed extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "配额ID")
	private String id;

	@ApiModelProperty(value = "租户ID")
	@Length(max = 64)
	private String tenantId;

	@ApiModelProperty(value = "资源id")
	@Length(max = 36)
	private String classId;

	@ApiModelProperty(value = "编码")
	@Length(max = 64)
	private String classCode;

	@ApiModelProperty(value = "资源配额")
	private Integer quotaUsed;

	@ApiModelProperty(value = "创建时间")
	private Date createdAt;

	@ApiModelProperty(value = "修改时间")
	private Date updatedAt;

	@ApiModelProperty(value = "删除时间")
	private Date deletedAt;

	@ApiModelProperty(value = "删除标志")
	private Integer deleted;
	
	private boolean lock;

	//private Integer version;
	
	public QuotaUsed(String tenantId,String classId,String classCode,Integer quotaUsed) {
		this.classId=tenantId;
		this.classId=classId;
		this.classCode=classCode;
		this.quotaUsed=quotaUsed;
		deleted=0;
		createDate();
	}
	public QuotaUsed() {
		// TODO Auto-generated constructor stub
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public Integer getQuotaUsed() {
		return quotaUsed;
	}

	public void setQuotaUsed(Integer quotaUsed) {
		this.quotaUsed = quotaUsed;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Date getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Date deletedAt) {
		this.deletedAt = deletedAt;
	}

	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	public void createDate() {
		this.updatedAt = new Date();
		this.createdAt = updatedAt;
		this.deletedAt = null;
		this.deleted = Integer.parseInt(ConfigProperty.YES);
	}
	public void deleteDate() {
	    this.deletedAt = new Date();
		this.deleted = Integer.parseInt(ConfigProperty.NO);
	}

	public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}
	

}
