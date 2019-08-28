package com.h3c.iclouds.po;

import java.io.Serializable;

import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.PatternType;
import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by yKF7317 on 2016/11/22.
 */
@ApiModel(value = "云管理虚拟数据中心", description = "云管理虚拟数据中心")
public class Vdc extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "vdc名字(NotNull)")
    @Length(max = 100)
    @NotNull
    private String name;

    @ApiModelProperty(value = "描述")
    @Length(max = 255)
    private String description;

    @ApiModelProperty(value = "显示顺序")
    private Integer sequence;

    @ApiModelProperty(value = "vdc类型", notes = "1-数据，2-模板")
    @Length(max = 64)
    @CheckPattern(type = PatternType.CONTAINS, values = {"1","2"})
    private String vdcType = "1";

    @ApiModelProperty(value = "租户")
    @Length(max = 50)
    private String projectId;

    @NotNull
    @ApiModelProperty(value = "uuid(NotNull)")
    private String uuid;

    @ApiModelProperty(value = "当前操作用户id")
    @Length(max = 50)
    private String userId;

    @ApiModelProperty(value = "当前sessionid")
    @Length(max = 50)
    private String sessionId;

    @ApiModelProperty(value = "当前版本号")
    @Length(max = 50)
    private String version;

    @ApiModelProperty(value = "锁定标识")
    private Boolean lock = false;

    public Vdc() {
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

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getVdcType() {
        return vdcType;
    }

    public void setVdcType(String vdcType) {
        this.vdcType = vdcType;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getLock() {
        return lock;
    }

    public void setLock(Boolean lock) {
        this.lock = lock;
    }
}
