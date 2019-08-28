package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/22.
 */
@ApiModel(value = "云管理防火墙策略集表", description = "云管理防火墙策略集表")
public class Policie extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "规则集id")
    private String id;

    @NotNull
    @ApiModelProperty(value = "名字(NotNull)")
    @Length(max = 50)
    private String name;

    @ApiModelProperty(value = "描述")
    @Length(max = 50)
    private String description;

    @ApiModelProperty(value = "租户")
    @Length(max = 50)
    private String tenantId;

    @ApiModelProperty(value = "状态(不需要传递)")
    @Length(max = 50)
    private String status = ConfigProperty.RESOURCE_OPTION_STATUS_SUCCESS;

    @ApiModelProperty(value = "是否共享")
    private Boolean shared;

    @ApiModelProperty(value = "是否审核")
    private Boolean audited;

    @ApiModelProperty(value = "删除标志", notes = "0-正常使用，1-删除状态")
    @Length(max = 32)
    @CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})
    private String deleted = ConfigProperty.YES;//默认正常使用

    @ApiModelProperty(value = "删除人")
    @Length(max = 36)
    private String deleteBy;

    @ApiModelProperty(value = "cloudos回传的真实id")
    private String cloudosId;

    private String fwId;

    public Policie(Firewall firewall) {
        this.name = firewall.getName();
        this.tenantId = firewall.getTenantId();
        this.status = firewall.getStatus();
        this.createdUser(firewall.getCreatedBy());
        this.updatedUser(firewall.getUpdatedBy());
    }

    public Policie() {
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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public Boolean getAudited() {
        return audited;
    }

    public void setAudited(Boolean audited) {
        this.audited = audited;
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

    public String getCloudosId() {
        return cloudosId;
    }

    public void setCloudosId(String cloudosId) {
        this.cloudosId = cloudosId;
    }

    public String getFwId() {
        return fwId;
    }

    public void setFwId(String fwId) {
        this.fwId = fwId;
    }
}
