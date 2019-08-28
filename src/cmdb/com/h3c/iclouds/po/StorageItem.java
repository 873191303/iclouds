package com.h3c.iclouds.po;

import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/2/20.
 */
@ApiModel(value = "云运维存储对象", description = "云运维存储对象")
public class StorageItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "对象ID")
    private String id;

    @NotNull
    @ApiModelProperty(value = "对象UUID")
    private String uuid;

    @ApiModelProperty(value = "对象名称")
    private String name;

    @ApiModelProperty(value = "对象属性")
    private String option;

    @NotNull
    @ApiModelProperty(value = "类型(NotNull)", notes = "0-存储视图,1-存储组,2-存储集群,3-存储设备,4-独立存储")
    @CheckPattern(type = PatternType.CONTAINS, values = {"0","1","2","3","4"})
    @Length(max = 36)
    private String itemType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
