package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/19.
 */
@ApiModel(value = "云管理可用域", description = "云管理可用域")
public class Azone extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "可用域ID")
    private String uuid;

    @NotNull
    @Length(max = 128)
    @ApiModelProperty(value = "标签名称")
    private String lableName;

    @Length(max = 128)
    @ApiModelProperty(value = "描述")
    private String description;

    @Length(max = 32)
    @ApiModelProperty(value = "归属资源域")
    private String zone;

    @Length(max = 32)
    @ApiModelProperty(value = "虚拟化类型")
    private String virtType;

    @NotNull
    @Length(max = 32)
    @ApiModelProperty(value = "资源类型")
    private String resourceType;

    @Length(max = 32)
    @ApiModelProperty(value = "删除标志")
    private String deleted;

    public Azone() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLableName() {
        return lableName;
    }

    public void setLableName(String lableName) {
        this.lableName = lableName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getVirtType() {
        return virtType;
    }

    public void setVirtType(String virtType) {
        this.virtType = virtType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
}
