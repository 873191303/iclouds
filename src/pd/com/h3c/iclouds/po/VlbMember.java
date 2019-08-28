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
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/22.
 */
@ApiModel(value = "云管理负载均衡成员池", description = "云管理负载均衡成员池")
public class VlbMember extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "状态(不需要传递)")
    @Length(max = 16)
    private String status = ConfigProperty.TASK_STATUS3_END_SUCCESS;

    @ApiModelProperty(value = "状态描述")
    @Length(max = 255)
    private String statusDescription;

    @ApiModelProperty(value = "实例ID")
    @Length(max = 36)
    private String poolId;

    @ApiModelProperty(value = "地址(NotNull)")
    @CheckPattern(type = PatternType.IP)
    @Length(max = 64)
    private String address;

    @NotNull
    @Min(1)
    @Max(65535)
    @ApiModelProperty(value = "协议端口(NotNull)")
    private Integer protocolPort;

    @NotNull
    @ApiModelProperty(value = "权重(NotNull)")
    private Integer weight;

    @NotNull
    @ApiModelProperty(value = "管理网络状态(NotNull)")
    private Boolean adminStateUp = false;

    @ApiModelProperty(value = "租户(不需要传递)")
    @Length(max = 50)
    private String tenantId;

    @ApiModelProperty(value = "cloudos回传的真实id(不需要传递)")
    private String cloudosId;

    @ApiModelProperty(value = "主机id")
    private String vmId;

    @ApiModelProperty(value = "主机名称(不需要传递)")
    private String vmName;

    @ApiModelProperty(value = "资源池真实id(不需要传递)")
    private String poolCloudosId;

    public VlbMember() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getPoolId() {
        return poolId;
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getProtocolPort() {
        return protocolPort;
    }

    public void setProtocolPort(Integer protocolPort) {
        this.protocolPort = protocolPort;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Boolean getAdminStateUp() {
        return adminStateUp;
    }

    public void setAdminStateUp(Boolean adminStateUp) {
        this.adminStateUp = adminStateUp;
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

    public String getVmId() {
        return vmId;
    }

    public void setVmId(String vmId) {
        this.vmId = vmId;
    }

    public String getPoolCloudosId() {
        return poolCloudosId;
    }

    public void setPoolCloudosId(String poolCloudosId) {
        this.poolCloudosId = poolCloudosId;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }
}
