package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by zkf5485 on 2017/9/8.
 */
public class HealthyInstance extends BaseEntity implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "实例id")
    private String id;

    @ApiModelProperty(value = "实例编码")
    @NotNull
    private String instanceId;

    @ApiModelProperty(value = "实例名称")
    @NotNull
    private String instanceName;

    @ApiModelProperty(value = "连接配置值")
    @NotNull
    private String config;

    @ApiModelProperty(value = "实例状态; 0:可用，1：不可用")
    @NotNull
    @CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})
    private String status;

    @ApiModelProperty(value = "实例归类")
    @NotNull
    private String type;

    @ApiModelProperty(value = "归属租户，废弃该字段")
    private String tenantId;

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
