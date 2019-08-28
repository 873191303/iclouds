package com.h3c.iclouds.po.business;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/1/12.
 */
@ApiModel(value = "云管理产品目录定价镜像表")
public class ListPrice2Imag extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "规格id（NotNull）")
    @NotNull
    private String specId;

    @Length(max = 36)
    @ApiModelProperty(value = "归属产品大类（NotNull）")
    @NotNull
    private String classId;

    @Length(max = 128)
    @ApiModelProperty(value = "可用域ID")
    private String azoneId;

    @Length(max = 100)
    @ApiModelProperty(value = "规格名称（NotNull）")
    @NotNull
    private String name;

    @ApiModelProperty(value = "计量规格（NotNull）")
    @NotNull
    private String spec;

    @ApiModelProperty(value = "规格单价")
    @NotNull
    private Double specPrice;

    @Length(max = 36)
    @ApiModelProperty(value = "计量单位（NotNull）")
    private String unit;

    @ApiModelProperty(value = "目录价")
    private Double listPrice;

    @ApiModelProperty(value = "步长单价")
    private Double stepPrice;

    @ApiModelProperty(value = "优惠折扣")
    private Double discount;

    @Length(max = 36)
    @ApiModelProperty(value = "flavorid")
    private String flavorId;
    
    @ApiModelProperty(value = "扣费周期-年 月 日 天")
    private String measureType;
    
    @ApiModelProperty(value = "基准值")
    private Integer minValue;
    
    @ApiModelProperty(value = "步长")
    private Integer step;
    
    @Length(max = 50)
    @ApiModelProperty(value = "可用域名称(不需要传递)")
    private String azoneName;
    
    @NotNull
    @ApiModelProperty(value = "属性键值map(NotNull)")
    private Map<String, String> keyMap = new HashMap<>();

    public ListPrice2Imag() {
    }

    public ListPrice2Imag(ListPrice listPrice) {
        this.specId = listPrice.getId();
        this.classId = listPrice.getClassId();
        this.azoneId = listPrice.getAzoneId();
        this.name = listPrice.getName();
        this.spec = listPrice.getSpec();
        this.specPrice = listPrice.getSpecPrice();
        this.unit = listPrice.getUnit();
        this.listPrice = listPrice.getSpecPrice();
        this.stepPrice = listPrice.getStepPrice();
        this.discount = listPrice.getDiscount();
        this.flavorId = listPrice.getFlavorId();
        this.measureType = listPrice.getMeasureType();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpecId() {
        return specId;
    }

    public void setSpecId(String specId) {
        this.specId = specId;
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

    public Double getSpecPrice() {
        return specPrice;
    }

    public void setSpecPrice(Double specPrice) {
        this.specPrice = specPrice;
    }
    
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getListPrice() {
        return listPrice;
    }

    public void setListPrice(Double listPrice) {
        this.listPrice = listPrice;
    }
    
    public Double getStepPrice() {
        return stepPrice;
    }

    public void setStepPrice(Double stepPrice) {
        this.stepPrice = stepPrice;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getFlavorId() {
        return flavorId;
    }

    public void setFlavorId(String flavorId) {
        this.flavorId = flavorId;
    }

    public String getAzoneName() {
        return azoneName;
    }

    public void setAzoneName(String azoneName) {
        this.azoneName = azoneName;
    }

    public Map<String, String> getKeyMap() {
        return keyMap;
    }

    public void setKeyMap(Map<String, String> keyMap) {
        this.keyMap = keyMap;
    }
    
    public String getMeasureType () {
        return measureType;
    }
    
    public void setMeasureType (String measureType) {
        this.measureType = measureType;
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
