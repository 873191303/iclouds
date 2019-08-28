package com.h3c.iclouds.po.business;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@ApiModel(value = "云管理云资源计量事件类型", description = "云管理云资源计量事件类型")
public class EventType implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "事件id")
    private String id;

    @ApiModelProperty(value = "事件名称")
    @Length(max = 100)
    private String name;

    @ApiModelProperty(value = "产品类别ID")
    @Length(max = 36)
    private String classId;

    public EventType() {
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

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }
}
