package com.h3c.iclouds.po;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by yKF7317 on 2016/11/22.
 */
@ApiModel(value = "云管理防火墙", description = "云管理防火墙")
public class Firewall extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "防火墙ID")
    private String id;

    @NotNull
    @ApiModelProperty(value = "名称(NotNull)")
    @Length(max = 50)
    private String name;

    @ApiModelProperty(value = "描述")
    @Length(max = 200)
    private String description;

    @ApiModelProperty(value = "是否共享")
    private Boolean shared;

    @ApiModelProperty(value = "管理网络状态")
    private Boolean adminStateUp;

//    @NotNull
//    @Max(100)
    @ApiModelProperty(value = "吞吐量(NotNull)")
    private Integer throughPut;

    @ApiModelProperty(value = "显示顺序")
    private Integer sequence;

    @NotNull
    @ApiModelProperty(value = "uuid(NotNull)")
    private String uuid;

    @ApiModelProperty(value = "状态(不需要传递)")
    @Length(max = 50)
    private String status = ConfigProperty.RESOURCE_OPTION_STATUS_SUCCESS;

    @ApiModelProperty(value = "vdcId(不需要传递)")
    private String vdcId;

    @ApiModelProperty(value = "vdc名称(不需要传递)")
    private String vdcName;

    @ApiModelProperty(value = "规则集id(不需要传递)")
    private String policyId;

    @ApiModelProperty(value = "cloudos回传的真实id(不需要传递,修改需要传递)")
    private String cloudosId;

    @ApiModelProperty(value = "cloudos回传的规则集真实id(不需要传递)")
    private String policyCloudosId;

    @ApiModelProperty(value = "租户(不需要传递)")
    private String tenantId;

    private String projectName;
    
    private String routeName;
    
    public Firewall() {
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

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getAdminStateUp() {
        return adminStateUp;
    }

    public void setAdminStateUp(Boolean adminStateUp) {
        this.adminStateUp = adminStateUp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVdcId() {
        return vdcId;
    }

    public void setVdcId(String vdcId) {
        this.vdcId = vdcId;
    }

    public String getVdcName() {
        return vdcName;
    }

    public void setVdcName(String vdcName) {
        this.vdcName = vdcName;
    }

    public Integer getThroughPut() {
        return throughPut;
    }

    public void setThroughPut(Integer throughPut) {
        this.throughPut = throughPut;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCloudosId() {
        return cloudosId;
    }

    public void setCloudosId(String cloudosId) {
        this.cloudosId = cloudosId;
    }

    public String getPolicyCloudosId() {
        return policyCloudosId;
    }

    public void setPolicyCloudosId(String policyCloudosId) {
        this.policyCloudosId = policyCloudosId;
    }

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
    
    public String getRouteName () {
        return routeName;
    }
    
    public void setRouteName (String routeName) {
        this.routeName = routeName;
    }
}
