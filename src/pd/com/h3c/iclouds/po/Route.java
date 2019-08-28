package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/21.
 */
@ApiModel(value = "云管理路由器", description = "云管理路由器")
public class Route extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "路由器id")
    private String id;

    @NotNull
    @ApiModelProperty(value = "名称(NotNull)")
    @Length(max = 50)
    private String name;

    @ApiModelProperty(value = "状态(不需要传递)")
    @Length(max = 50)
    private String status = ConfigProperty.RESOURCE_OPTION_STATUS_SUCCESS;

    @ApiModelProperty(value = "管理网络状态")
    private Boolean adminStateUp;

    @ApiModelProperty(value = "网关端口")
    @Length(max = 32)
    private String gwPortId;

    @ApiModelProperty(value = "打开SNAT(NotNull)")
    private Boolean enableSnat = true;//默认打开

    @ApiModelProperty(value = "租户id(不需要传递)")
    @Length(max = 50)
    private String tenantId;

    @ApiModelProperty(value = "VDCid(不需要传递)")
    @Length(max = 50)
    private String vdcId;

    @ApiModelProperty(value = "防火墙id")
    @Length(max = 50)
    private String fwId;

    @ApiModelProperty(value = "vdc名称(不需要传递)")
    private String vdcName;

    @ApiModelProperty(value = "防火墙名称(不需要传递)")
    private String firewallName;

    @NotNull
    @ApiModelProperty(value = "uuid(NotNull)")
    private String uuid;

    @ApiModelProperty(value = "cloudos回传的真实id(不需要传递)")
    private String cloudosId;

    @ApiModelProperty(value = "cloudos回传的防火墙真实id(不需要传递)")
    private String firewallCloudosId;

    @ApiModelProperty(value = "私网ip(不需要传递)")
    private String privateNetworkIp;

    @ApiModelProperty(value = "外部网关(不需要传递)")
    private String externalGateway;
    
    @ApiModelProperty(value = "外部网关网卡真实id")
    private String gwPortCdId;
    
    @ApiModelProperty(value = "外部网关所属网络id")
    private String gwNetworkId;
    
    @ApiModelProperty(value = "外部网关所属网络真实id")
    private String gwNetworkCdId;
    
    private String projectName;
    
    private String networkName;

    public Route() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getAdminStateUp() {
        return adminStateUp;
    }

    public void setAdminStateUp(Boolean adminStateUp) {
        this.adminStateUp = adminStateUp;
    }

    public String getGwPortId() {
        return gwPortId;
    }

    public void setGwPortId(String gwPortId) {
        this.gwPortId = gwPortId;
    }

    public Boolean getEnableSnat() {
        return enableSnat;
    }

    public void setEnableSnat(Boolean enableSnat) {
        this.enableSnat = enableSnat;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getVdcId() {
        return vdcId;
    }

    public void setVdcId(String vdcId) {
        this.vdcId = vdcId;
    }

    public String getFwId() {
        return fwId;
    }

    public void setFwId(String fwId) {
        this.fwId = fwId;
    }

    public String getVdcName() {
        return vdcName;
    }

    public void setVdcName(String vdcName) {
        this.vdcName = vdcName;
    }

    public String getFirewallName() {
        return firewallName;
    }

    public void setFirewallName(String firewallName) {
        this.firewallName = firewallName;
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

    public String getFirewallCloudosId() {
        return firewallCloudosId;
    }

    public void setFirewallCloudosId(String firewallCloudosId) {
        this.firewallCloudosId = firewallCloudosId;
    }

    public String getPrivateNetworkIp() {
        return privateNetworkIp;
    }

    public void setPrivateNetworkIp(String privateNetworkIp) {
        this.privateNetworkIp = privateNetworkIp;
    }

    public String getExternalGateway() {
        return externalGateway;
    }

    public void setExternalGateway(String externalGateway) {
        this.externalGateway = externalGateway;
    }
    
    public String getGwPortCdId () {
        return gwPortCdId;
    }
    
    public void setGwPortCdId (String gwPortCdId) {
        this.gwPortCdId = gwPortCdId;
    }
    
    public String getGwNetworkId () {
        return gwNetworkId;
    }
    
    public void setGwNetworkId (String gwNetworkId) {
        this.gwNetworkId = gwNetworkId;
    }
    
    public String getGwNetworkCdId () {
        return gwNetworkCdId;
    }
    
    public void setGwNetworkCdId (String gwNetworkCdId) {
        this.gwNetworkCdId = gwNetworkCdId;
    }

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
    
    public String getNetworkName () {
        return networkName;
    }
    
    public void setNetworkName (String networkName) {
        this.networkName = networkName;
    }
}

