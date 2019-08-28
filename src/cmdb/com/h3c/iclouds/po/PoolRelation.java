package com.h3c.iclouds.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yKF7317 on 2017/2/20.
 */
@ApiModel(value = "云运维主机池关系拓扑视图", description = "云运维主机池关系拓扑视图")
public class PoolRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "viewId")
    @Length(max = 36)
    private String viewId;

    @ApiModelProperty(value = "对象id")
    @Length(max = 50)
    private String hpoolId;

    @ApiModelProperty(value = "上一个对象ID")
    @Length(max = 36)
    private String previous;

    @ApiModelProperty(value = "同级顺序")
    private Integer sequence;

    @ApiModelProperty(value = "对象名称")
    private String poolName;

    @ApiModelProperty(value = "上一级对象名称")
    private String previousName;

    @ApiModelProperty(value = "对象类型")
    private String poolType;

    @ApiModelProperty(value = "上一级对象类型")
    private String previousType;

    @ApiModelProperty(value = "对象uuid")
    private String poolUuid;

    @ApiModelProperty(value = "上一级对象uuid")
    private String previousUuid;

    @ApiModelProperty(value = "子集数量")
    private Integer childCount;

    @ApiModelProperty(value = "子集集合")
    private List<PoolRelation> children;
    
    @ApiModelProperty(value = " 租户数量")
    private Integer projectCount;
    
    public PoolRelation() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public String getHpoolId() {
        return hpoolId;
    }

    public void setHpoolId(String hpoolId) {
        this.hpoolId = hpoolId;
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

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getPreviousName() {
        return previousName;
    }

    public void setPreviousName(String previousName) {
        this.previousName = previousName;
    }

    public String getPoolType() {
        return poolType;
    }

    public void setPoolType(String poolType) {
        this.poolType = poolType;
    }

    public String getPreviousType() {
        return previousType;
    }

    public void setPreviousType(String previousType) {
        this.previousType = previousType;
    }

    public String getPoolUuid() {
        return poolUuid;
    }

    public void setPoolUuid(String poolUuid) {
        this.poolUuid = poolUuid;
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

    public List<PoolRelation> getChildren() {
        return children;
    }

    public void setChildren(List<PoolRelation> children) {
        this.children = children;
    }
    
    public Integer getProjectCount () {
        return projectCount;
    }
    
    public void setProjectCount (Integer projectCount) {
        this.projectCount = projectCount;
    }
}
