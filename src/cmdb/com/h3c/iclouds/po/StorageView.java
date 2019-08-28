package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/2/20.
 */
@ApiModel(value = "云运维存储配置视图", description = "云运维存储配置视图")
public class StorageView extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "视图名称(NotNull)")
    @Length(max = 20)
    @NotNull
    private String name;

    @ApiModelProperty(value = "描述")
    @Length(max = 250)
    private String description;

    @ApiModelProperty(value = "显示顺序")
    private Integer sequence;

    @ApiModelProperty(value = "租户")
    @Length(max = 36)
    private String projectId;

    @ApiModelProperty(value = "当前操作用户id")
    @Length(max = 36)
    private String userId;

    @ApiModelProperty(value = "当前sessionid")
    @Length(max = 64)
    private String sessionId;

    @ApiModelProperty(value = "当前版本号")
    @Length(max = 36)
    private String version;

    @ApiModelProperty(value = "锁定标识")
    private Boolean lock = false;

    public StorageView() {
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

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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
