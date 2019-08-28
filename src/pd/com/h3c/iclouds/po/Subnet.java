package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by yKF7317 on 2016/11/21.
 */
@ApiModel(value = "云管理子网", description = "云管理子网")
public class Subnet extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "子网ID")
    private String id;

    @NotNull
    @ApiModelProperty(value = "名称(NotNull)")
    @Length(max = 255)
    private String name;

    @NotNull
    @ApiModelProperty(value = "网络ID(NotNull)")
    @Length(max = 36)
    private String networkId;

    @NotNull
    @ApiModelProperty(value = "ipversion")
    private Integer ipVersion = 4;

    @NotNull
    @ApiModelProperty(value = "cidr(NotNull)")
    @Length(max = 64)
    private String cidr;

    @ApiModelProperty(value = "网关IP")
    @Length(max = 64)
    @CheckPattern(type = PatternType.IP)
    private String gatewayIp;

    @ApiModelProperty(value = "是否打开dhcp")
    private Boolean enableDhcp;

    @ApiModelProperty(value = "是否可跨租户共享")
    private Boolean shared;

    @ApiModelProperty(value = "ipv6_ra_mode")
    @Length(max = 50)
    private String ipv6RaMode;

    @ApiModelProperty(value = "ipv6_address_mode")
    @Length(max = 50)
    private String ipv6AddressMode;

    @ApiModelProperty(value = "子网池id")
    @Length(max = 50)
    private String subnetPoolId;

    @ApiModelProperty(value = "租户id")
    @Length(max = 50)
    private String tenantId;

    @ApiModelProperty(value = "cloudos回传的真实id")
    private String cloudosId;

    public Subnet(Network network) {
        this.name = network.getName();
        this.ipVersion = network.getIpVersion();
        this.cidr = network.getCidr();
        this.gatewayIp = network.getGatewayIp();
        this.enableDhcp = network.getEnableDhcp();
        this.shared = network.getShared();
        this.ipv6RaMode = network.getIpv6RaMode();
        this.ipv6AddressMode = network.getIpv6AddressMode();
        this.tenantId = network.getTenantId();
        this.createdUser(network.getCreatedBy());
        this.updatedUser(network.getUpdatedBy());
    }

    private Set<IpAllocation> ipUseds;

    public Subnet() {
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

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public Integer getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(Integer ipVersion) {
        this.ipVersion = ipVersion;
    }

    public String getCidr() {
        return cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public String getGatewayIp() {
        return gatewayIp;
    }

    public void setGatewayIp(String gatewayIp) {
        this.gatewayIp = gatewayIp;
    }

    public Boolean getEnableDhcp() {
        return enableDhcp;
    }

    public void setEnableDhcp(Boolean enableDhcp) {
        this.enableDhcp = enableDhcp;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public String getIpv6RaMode() {
        return ipv6RaMode;
    }

    public void setIpv6RaMode(String ipv6RaMode) {
        this.ipv6RaMode = ipv6RaMode;
    }

    public String getIpv6AddressMode() {
        return ipv6AddressMode;
    }

    public void setIpv6AddressMode(String ipv6AddressMode) {
        this.ipv6AddressMode = ipv6AddressMode;
    }

    public String getSubnetPoolId() {
        return subnetPoolId;
    }

    public void setSubnetPoolId(String subnetPoolId) {
        this.subnetPoolId = subnetPoolId;
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

    public Subnet(String id, String name, String networkId, Integer ipVersion, String cidr, String gatewayIp, Boolean enableDhcp, Boolean shared, String ipv6RaMode, String ipv6AddressMode, String subnetPoolId, String tenantId) {
        this.id = id;
        this.name = name;
        this.networkId = networkId;
        this.ipVersion = ipVersion;
        this.cidr = cidr;
        this.gatewayIp = gatewayIp;
        this.enableDhcp = enableDhcp;
        this.shared = shared;
        this.ipv6RaMode = ipv6RaMode;
        this.ipv6AddressMode = ipv6AddressMode;
        this.subnetPoolId = subnetPoolId;
        this.tenantId = tenantId;
    }

	public Set<IpAllocation> getIpUseds() {
		return ipUseds;
	}

	public void setIpUseds(Set<IpAllocation> ipUseds) {
		this.ipUseds = ipUseds;
	}


}
