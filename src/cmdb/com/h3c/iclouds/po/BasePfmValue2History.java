package com.h3c.iclouds.po;

import java.io.Serializable;
import java.util.Date;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by yKF7317 on 2017/3/1.
 */
@ApiModel(value = "云资源性能历史数据")
public class BasePfmValue2History extends BaseEntity implements Serializable {
	
	public BasePfmValue2History() {
	
	}

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据id")
    private String id;

    @ApiModelProperty(value = "收集时间")
    private Date collectTime;

    @ApiModelProperty(value = "对象uuid")
    private String uuid;

    @ApiModelProperty(value = "资源类型")
    private String resType;

    @ApiModelProperty(value = "指标id")
    private String itemId;

    @ApiModelProperty(value = "指标值")
    private Float keyValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Float getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(Float keyValue) {
        this.keyValue = keyValue;
    }

}
