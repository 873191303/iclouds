package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/21.
 */
@ApiModel(value = "云管理安全组规则表", description = "云管理安全组规则表")
public class SecurityRule extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "规则id")
    private String id;

    @ApiModelProperty(value = "安全组ID")
    @Length(max = 36)
    private String securityGroupId;

    @ApiModelProperty(value = "远程安全组")
    @Length(max = 36)
    private String remoteGroupId;

    @ApiModelProperty(value = "方向")
    @Length(max = 50)
    private String direction;

    @NotNull
    @ApiModelProperty(value = "网卡类型(NotNull)")
    @Length(max = 40)
    private String etherType;

    @ApiModelProperty(value = "协议")
    @Length(max = 40)
    private String protocol;

    @ApiModelProperty(value = "源最小端口")
    private Integer portRangeMin;

    @ApiModelProperty(value = "源最大端口")
    private Integer portRangeMax;

    @ApiModelProperty(value = "远程IP前缀")
    @Length(max = 250)
    private String remoteIpPrefix;

    @ApiModelProperty(value = "租户")
    @Length(max = 50)
    private String tenantId;

    @ApiModelProperty(value = "cloudos回传的真实id")
    private String cloudosId;

    public SecurityRule() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecurityGroupId() {
        return securityGroupId;
    }

    public void setSecurityGroupId(String securityGroupId) {
        this.securityGroupId = securityGroupId;
    }

    public String getRemoteGroupId() {
        return remoteGroupId;
    }

    public void setRemoteGroupId(String remoteGroupId) {
        this.remoteGroupId = remoteGroupId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getEtherType() {
        return etherType;
    }

    public void setEtherType(String etherType) {
        this.etherType = etherType;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getPortRangeMin() {
        return portRangeMin;
    }

    public void setPortRangeMin(Integer portRangeMin) {
        this.portRangeMin = portRangeMin;
    }

    public Integer getPortRangeMax() {
        return portRangeMax;
    }

    public void setPortRangeMax(Integer portRangeMax) {
        this.portRangeMax = portRangeMax;
    }

    public String getRemoteIpPrefix() {
        return remoteIpPrefix;
    }

    public void setRemoteIpPrefix(String remoteIpPrefix) {
        this.remoteIpPrefix = remoteIpPrefix;
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
