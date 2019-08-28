package com.h3c.iclouds.po.business;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@ApiModel(value = "云管理产品计量单元表", description = "云管理产品计量单元表")
public class MeasureUnits extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "单元编码")
    @Length(max = 36)
    private String code;

    @ApiModelProperty(value = "单元名称")
    @Length(max = 100)
    private String name;

    @ApiModelProperty(value = "最小值")
    @Length(max = 4)
    private int minValue;

    @ApiModelProperty(value = "最大值")
    @Length(max = 4)
    private int maxValue;

    @ApiModelProperty(value = "默认值")
    @Length(max = 4)
    private int defaultValue;

    @ApiModelProperty(value = "单位")
    @Length(max = 100)
    private String unit;

    @ApiModelProperty(value = " 描述")
    @Length(max = 100)
    private String description;

    @ApiModelProperty(value = "归属类别")
    @Length(max = 36)
    private String classId;

    public MeasureUnits() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }
}
