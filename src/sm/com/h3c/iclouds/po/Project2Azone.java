package com.h3c.iclouds.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/19.
 */
@ApiModel(value = "云管理租户与可用域关系", description = "云管理租户与可用域关系")
public class Project2Azone implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "UUID")
    private String uuid;

    @ApiModelProperty(value = "租户ID")
    @Length(max = 64)
    private String id;

    @ApiModelProperty(value = "系统管理_可用域ID")
    @Length(max = 128)
    private String iyuUuid;

    @ApiModelProperty(value = "删除标志")
    @Length(max = 32)
    private String deleted;

    public Project2Azone() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIyuUuid() {
        return iyuUuid;
    }

    public void setIyuUuid(String iyuUuid) {
        this.iyuUuid = iyuUuid;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
}
