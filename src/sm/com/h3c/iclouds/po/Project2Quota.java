package com.h3c.iclouds.po;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by yKF7317 on 2016/11/22.
 */
@ApiModel(value = "云管理租户配额表", description = "云管理租户配额表")
public class Project2Quota extends BaseEntity implements Serializable,Comparator<Project2Quota> {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "配额ID")
    private String id;

    @ApiModelProperty(value = "租户ID")
    @Length(max = 64)
    private String tenantId;

    @ApiModelProperty(value = "资源id")
    @Length(max = 36)
    private String classId;

    @NotNull
    @ApiModelProperty(value = "编码")
    @Length(max = 64)
    private String classCode;

    
    @NotNull
    @ApiModelProperty(value = "资源配额")
    private Integer hardLimit;

    @ApiModelProperty(value = "创建时间")
    private Date createdAt;

    @ApiModelProperty(value = "修改时间")
    private Date updatedAt;

    @ApiModelProperty(value = "删除时间")
    private Date deletedAt;

    @ApiModelProperty(value = "删除标志")
    //@Max(10)
    private Integer deleted;

    public Project2Quota() {
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

    public Integer getHardLimit() {
        return hardLimit;
    }

    public void setHardLimit(Integer hardLimit) {
        this.hardLimit = hardLimit;
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

	@Override
	public int compare(Project2Quota o1, Project2Quota o2) {
		  if (o1.hardLimit<=o2.hardLimit) {
			  return 1;
		} else {
			return 0;
		}
	}
}
