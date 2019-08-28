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
@ApiModel(value = "云管理云资源对象计量账单明细", description = "云管理云资源对象计量账单明细")
public class MeasureDetail extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "计量ID")
    private String id;

    @ApiModelProperty(value = "资源实例id(NotNull)")
    private String instanceId;

    @ApiModelProperty(value = "计量规格id(NotNull)")
    private String specId;

    @ApiModelProperty(value = "计费数量")
    private Integer num;

    @ApiModelProperty(value = "计费时效")
    private Boolean isEffective = true;

    @ApiModelProperty(value = "起始计费日期(NotNull)")
    private Date begDate;

    @ApiModelProperty(value = "停止计费日期")
    private Date endDate;

    @ApiModelProperty(value = "事件类型id(NotNull)")
    private String eventTypeId;

    @ApiModelProperty(value = "计费标识")
    private Boolean flag = true;

    @ApiModelProperty(value = "计费描述")
    @Length(max = 255)
    private String description;
    
    @ApiModelProperty(value = "用户id")
    @Length(max = 36)
    private String userId;
    
    @ApiModelProperty(value = "租户id")
    @Length(max = 36)
    private String tenantId;
    
    @ApiModelProperty(value = "操作类型")
    private String type;
    
    @ApiModelProperty(value = "用户名称")
    private String userName;
    
    @ApiModelProperty(value = "租户名称")
    private String projectName;
    
    @ApiModelProperty(value = "资源类型")
    private String instanceType;
    
    @ApiModelProperty(value = "规格")
    private String flavor;
    
    @ApiModelProperty(value = "产品id")
    private String classId;
    
    @ApiModelProperty(value = "资源名称")
    private String instanceName;
    
    @ApiModelProperty(value = "单价")
    private Double price;
    
    @ApiModelProperty(value = "步长")
    private Integer step;
    
    @ApiModelProperty(value = "步长单价")
    private Double stepPrice;
    
    @ApiModelProperty(value = "计费单位")
    private String unit;
    
    public MeasureDetail() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getSpecId() {
        return specId;
    }

    public void setSpecId(String specId) {
        this.specId = specId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Boolean getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(Boolean isEffective) {
        this.isEffective = isEffective;
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

    public String getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(String eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    
    public Boolean getEffective () {
        return isEffective;
    }
    
    public void setEffective (Boolean effective) {
        isEffective = effective;
    }
    
    public String getType () {
        return type;
    }
    
    public void setType (String type) {
        this.type = type;
    }
    
    public String getUserName () {
        return userName;
    }
    
    public void setUserName (String userName) {
        this.userName = userName;
    }
    
    public String getProjectName () {
        return projectName;
    }
    
    public void setProjectName (String projectName) {
        this.projectName = projectName;
    }
    
    public String getInstanceType () {
        return instanceType;
    }
    
    public void setInstanceType (String instanceType) {
        this.instanceType = instanceType;
    }
    
    public String getFlavor () {
        return flavor;
    }
    
    public void setFlavor (String flavor) {
        this.flavor = flavor;
    }
    
    public String getClassId () {
        return classId;
    }
    
    public void setClassId (String classId) {
        this.classId = classId;
    }
    
    public String getInstanceName () {
        return instanceName;
    }
    
    public void setInstanceName (String instanceName) {
        this.instanceName = instanceName;
    }
    
    public Double getPrice () {
        return price;
    }
    
    public void setPrice (Double price) {
        this.price = price;
    }
    
    public Integer getStep () {
        return step;
    }
    
    public void setStep (Integer step) {
        this.step = step;
    }
    
    public Double getStepPrice () {
        return stepPrice;
    }
    
    public void setStepPrice (Double stepPrice) {
        this.stepPrice = stepPrice;
    }
    
    public String getUnit () {
        return unit;
    }
    
    public void setUnit (String unit) {
        this.unit = unit;
    }
}
