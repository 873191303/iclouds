package com.h3c.iclouds.po.business;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@ApiModel(value = "业务需求执行明细", description = "业务需求执行明细")
public class ReqItem2Exec extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "明细ID")
    private String id;

    @ApiModelProperty(value = "需求条目ID")
    @Length(max = 36)
    private String itemId;

    @ApiModelProperty(value = "产品类别ID")
    @Length(max = 36)
    private String classId;

    @ApiModelProperty(value = "执行资源ID")
    @Length(max = 36)
    private String uuid;

    public ReqItem2Exec() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
