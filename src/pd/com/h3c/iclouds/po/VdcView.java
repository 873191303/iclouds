package com.h3c.iclouds.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yKF7317 on 2016/11/22.
 */
@ApiModel(value = "云管理虚拟数据中心视图", description = "云管理虚拟数据中心视图")
public class VdcView implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "VDCid")
    @Length(max = 36)
    private String vdcId;

    @ApiModelProperty(value = "对象id")
    @Length(max = 50)
    private String objId;

    @ApiModelProperty(value = "上一个对象ID")
    @Length(max = 36)
    private String previous;

    @ApiModelProperty(value = "同级顺序")
    private Integer sequence;

    @ApiModelProperty(value = "对象名称")
    private String objName;

    @ApiModelProperty(value = "上一级对象名称")
    private String previousName;

    @ApiModelProperty(value = "对象类型")
    private String objType;

    @ApiModelProperty(value = "上一级对象类型")
    private String previousType;

    @ApiModelProperty(value = "对象uuid")
    private String objUuid;

    @ApiModelProperty(value = "上一级对象uuid")
    private String previousUuid;

    @ApiModelProperty(value = "子集id集合")
    private List<String> childIds;

    @ApiModelProperty(value = "子集数量")
    private Integer childCount;

    @ApiModelProperty(value = "状态")
    private String status;

    public VdcView() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVdcId() {
        return vdcId;
    }

    public void setVdcId(String vdcId) {
        this.vdcId = vdcId;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getObjName() {
        return objName;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

    public String getPreviousName() {
        return previousName;
    }

    public void setPreviousName(String previousName) {
        this.previousName = previousName;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public String getPreviousType() {
        return previousType;
    }

    public void setPreviousType(String previousType) {
        this.previousType = previousType;
    }

    public String getObjUuid() {
        return objUuid;
    }

    public void setObjUuid(String objUuid) {
        this.objUuid = objUuid;
    }

    public String getPreviousUuid() {
        return previousUuid;
    }

    public void setPreviousUuid(String previousUuid) {
        this.previousUuid = previousUuid;
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public List<String> getChildIds() {
        return childIds;
    }

    public void setChildIds(List<String> childIds) {
        this.childIds = childIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
