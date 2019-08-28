package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yKF7408 on 2017/1/4.
 */
@ApiModel(value = "云主机初始化密码表", description = "云主机初始化密码表")
public class Metadata extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "云主机ID")
    @Length(max = 36)
    private String instanceUuid;

    @ApiModelProperty(value = "key")
    @Length(max = 255)
    private String key;

    @ApiModelProperty(value = "value")
    @Length(max = 255)
    private String value;

    @ApiModelProperty(value = "删除标志")
    @Length(max = 32)
    private String deleted;

    @ApiModelProperty(value = "删除人")
    @Length(max = 36)
    private String deleteBy;

    @ApiModelProperty(value = "删除日期")
    private Date deleteDate;

    public Metadata() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstanceUuid() {
        return instanceUuid;
    }

    public void setInstanceUuid(String instanceUuid) {
        this.instanceUuid = instanceUuid;
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

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getDeleteBy() {
        return deleteBy;
    }

    public void setDeleteBy(String deleteBy) {
        this.deleteBy = deleteBy;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }
}
