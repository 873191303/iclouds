package com.h3c.iclouds.po.business;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/1/12.
 */
@ApiModel(value = "云管理产品规格组成属性值预备值表")
public class Specs2KeyValue extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @Length(max = 36)
    @ApiModelProperty(value = "规格属性id")
    @NotNull
    private String key;

    @Length(max = 500)
    @ApiModelProperty(value = "属性值")
    private String value;
    
    @Length(max = 100)
    @ApiModelProperty(value = "属性单位")
    private String unit;
    
    @NotNull
    @ApiModelProperty(value = "属性值类型:0-离散,1-连续")
    private String valueType;
    
    @ApiModelProperty(value = "最小值")
    private Integer minValue;
    
    @ApiModelProperty(value = "最大值")
    private Integer maxValue;
    
    @ApiModelProperty(value = "步长")
    private Integer step;
    
    @ApiModelProperty(value = "规格名称")
    private String keyName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
    
    public String getValueType () {
        return valueType;
    }
    
    public void setValueType (String valueType) {
        this.valueType = valueType;
    }
    
    public Integer getMinValue () {
        return minValue;
    }
    
    public void setMinValue (Integer minValue) {
        this.minValue = minValue;
    }
    
    public Integer getMaxValue () {
        return maxValue;
    }
    
    public void setMaxValue (Integer maxValue) {
        this.maxValue = maxValue;
    }
    
    public Integer getStep () {
        return step;
    }
    
    public void setStep (Integer step) {
        this.step = step;
    }
}
