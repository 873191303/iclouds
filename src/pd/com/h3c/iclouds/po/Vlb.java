package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/21.
 */
@ApiModel(value = "云管理负载均衡", description = "云管理负载均衡")
public class Vlb extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @NotNull
    @ApiModelProperty(value = "名称(NotNull)")
    @Length(max = 50)
    private String name;

    @ApiModelProperty(value = "描述")
    @Length(max = 500)
    private String description;

    @ApiModelProperty(value = "状态")
    private Integer status = 0;

    @NotNull
    @Max(100)
    @ApiModelProperty(value = "流量(NotNull)")
    private Integer throughPut;

    @ApiModelProperty(value = "扩展信息")
    @Length(max = 2048)
    private String extra;

    @ApiModelProperty(value = "租户id")
    @Length(max = 64)
    private String projectId;

    @ApiModelProperty(value = "用户")
    @Length(max = 36)
    private String owner;

    @ApiModelProperty(value = "删除标志", notes = "0-正常使用，1-删除状态")
    @Length(max = 32)
    @CheckPattern(type = PatternType.CONTAINS, values = {"0","1"})
    private String deleted = ConfigProperty.YES;//默认正常使用

    @ApiModelProperty(value = "删除人")
    @Length(max = 36)
    private String deleteBy;

    @ApiModelProperty(value = "VDCid")
    @Length(max = 50)
    private String vdcId;

    @ApiModelProperty(value = "cloudos回传的真实id")
    private String cloudosId;

    public Vlb() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getThroughPut() {
        return throughPut;
    }

    public void setThroughPut(Integer throughPut) {
        this.throughPut = throughPut;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getVdcId() {
        return vdcId;
    }

    public void setVdcId(String vdcId) {
        this.vdcId = vdcId;
    }

    public String getCloudosId() {
        return cloudosId;
    }

    public void setCloudosId(String cloudosId) {
        this.cloudosId = cloudosId;
    }
}
