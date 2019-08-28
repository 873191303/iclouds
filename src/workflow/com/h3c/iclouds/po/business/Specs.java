package com.h3c.iclouds.po.business;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@ApiModel(value = "云管理产品计量规格表", description = "云管理产品计量规格表")
public class Specs extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "规格ID")
    private String id;

    @ApiModelProperty(value = "归属产品")
    @Length(max = 36)
    private String classId;

    @ApiModelProperty(value = "可用域ID")
    @Length(max = 128)
    private String azoneId;

    @ApiModelProperty(value = "名称")
    @Length(max = 100)
    private String name;

    @ApiModelProperty(value = "计量规格")
    private String spec;

    @ApiModelProperty(value = "最小值")
    @Length(max = 4)
    private int minValue;

    @ApiModelProperty(value = "最大值")
    @Length(max = 4)
    private int maxValue;

    @ApiModelProperty(value = "默认值")
    @Length(max = 4)
    private int defaultValue;

    @ApiModelProperty(value = "计量单位")
    @Length(max = 36)
    private String unit;

    @ApiModelProperty(value = "目录价")
    @Length(max = 10)
    private double listPrice;

    @ApiModelProperty(value = "步长")
    @Length(max = 4)
    private int step;

    @ApiModelProperty(value = "步长单价")
    @Length(max = 10)
    private double stepPrice;

    @ApiModelProperty(value = "优惠折扣")
    @Length(max = 10)
    private double disCount;

    @ApiModelProperty(value = "flavorid")
    @Length(max = 36)
    private String flavorId;

    public Specs() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getAzoneId() {
        return azoneId;
    }

    public void setAzoneId(String azoneId) {
        this.azoneId = azoneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getListPrice() {
        return listPrice;
    }

    public void setListPrice(double listPrice) {
        this.listPrice = listPrice;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public double getStepPrice() {
        return stepPrice;
    }

    public void setStepPrice(double stepPrice) {
        this.stepPrice = stepPrice;
    }

    public double getDisCount() {
        return disCount;
    }

    public void setDisCount(double disCount) {
        this.disCount = disCount;
    }

    public String getFlavorId() {
        return flavorId;
    }

    public void setFlavorId(String flavorId) {
        this.flavorId = flavorId;
    }
}
