package com.h3c.iclouds.po;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by yKF7317 on 2016/11/22.
 */
@ApiModel(value = "云管理虚拟数据中心对象表", description = "云管理虚拟数据中心对象表")
public class VdcItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "对象ID")
    private String id;

    @ApiModelProperty(value = "对象UUID")
    private String uuid;

    @NotNull
    @ApiModelProperty(value = "对象名称(NotNull)")
    private String name;

    @NotNull
    @ApiModelProperty(value = "类型(NotNull)", notes = "0-vdc,1-防火墙,2-路由器,3-网络,4-负载均衡")
    @CheckPattern(type = PatternType.CONTAINS, values = {"0","1","2","3","4"})
    @Length(max = 36)
    private String itemType;

    @ApiModelProperty(value = "状态", notes = "1-等待,2-创建中,3-创建成功,4-创建失败")
    private String status;


    public VdcItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
