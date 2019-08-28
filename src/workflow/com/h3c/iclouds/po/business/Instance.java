package com.h3c.iclouds.po.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@ApiModel(value = "云管理云资源计量对象表", description = "云管理云资源计量对象表")
public class Instance extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;
    
    @ApiModelProperty(value = "name")
    private String name;
    
    @ApiModelProperty(value = "资源ID")
    @Length(max = 36)
    private String instance;

    @ApiModelProperty(value = "资源类型")
    @Length(max = 36)
    private String classId;

    @ApiModelProperty(value = "规格配置")
    private String flavor;

    @ApiModelProperty(value = "启用日期")
    private Date begDate;

    @ApiModelProperty(value = "截至日期")
    private Date endDate;
    
    @ApiModelProperty(value = "用户id")
    @Length(max = 36)
    private String userId;
    
    @ApiModelProperty(value = "租户id")
    @Length(max = 36)
    private String tenantId;
    
    public Instance() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    public Date getBegDate() {
        return begDate;
    }

    public void setBegDate(Date begDate) {
        this.begDate = begDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public String getUserId () {
        return userId;
    }
    
    public void setUserId (String userId) {
        this.userId = userId;
    }
    
    public String getTenantId () {
        return tenantId;
    }
    
    public void setTenantId (String tenantId) {
        this.tenantId = tenantId;
    }
    
    public String getName () {
        return name;
    }
    
    public void setName (String name) {
        this.name = name;
    }
}
