package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/21.
 */
@ApiModel(value = "云管理安全组表", description = "云管理安全组表")
public class SecurityGroup extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "组id")
    private String id;

    @ApiModelProperty(value = "端口名称")
    @Length(max = 255)
    private String name;

    @ApiModelProperty(value = "描述")
    @Length(max = 1024)
    private String description;

    @ApiModelProperty(value = "租户")
    @Length(max = 50)
    private String tenantId;

    @ApiModelProperty(value = "cloudos回传的真实id")
    private String cloudosId;

    public SecurityGroup() {
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

    public String getCloudosId() {
        return cloudosId;
    }

    public void setCloudosId(String cloudosId) {
        this.cloudosId = cloudosId;
    }
}
