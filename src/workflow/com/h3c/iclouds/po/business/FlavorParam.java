package com.h3c.iclouds.po.business;

import javax.validation.constraints.NotNull;

/**
 * Created by yKF7317 on 2017/1/19.
 */
public class FlavorParam {

    private String flavorId;//规格id
    
    private String azoneId;//可用域id

    @NotNull
    private String userId;//用户id
    
    @NotNull
    private String tenantId;//租户id
    
    @NotNull
    private String resourceId;//资源id
    
    private String specId;//定价规格
    
    @NotNull
    private String classId;//产品id
    
    private Integer value;//步长定价时的具体参数值
    
    private String createdBy;
    
    private Integer minValue;//步长定价时对应的最小值
    
    private Integer step;//步长定价对应的步长
    
    public FlavorParam() {
    }

    public String getFlavorId() {
        return flavorId;
    }

    public void setFlavorId(String flavorId) {
        this.flavorId = flavorId;
    }

    public String getAzoneId() {
        return azoneId;
    }

    public void setAzoneId(String azoneId) {
        this.azoneId = azoneId;
    }
    
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
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
    
    public String getResourceId () {
        return resourceId;
    }
    
    public void setResourceId (String resourceId) {
        this.resourceId = resourceId;
    }
    
    public String getCreatedBy () {
        return createdBy;
    }
    
    public void setCreatedBy (String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getSpecId () {
        return specId;
    }
    
    public void setSpecId (String specId) {
        this.specId = specId;
    }
    
    public Integer getValue () {
        return value;
    }
    
    public void setValue (Integer value) {
        this.value = value;
    }
    
    public Integer getMinValue () {
        return minValue;
    }
    
    public void setMinValue (Integer minValue) {
        this.minValue = minValue;
    }
    
    public Integer getStep () {
        return step;
    }
    
    public void setStep (Integer step) {
        this.step = step;
    }
}
