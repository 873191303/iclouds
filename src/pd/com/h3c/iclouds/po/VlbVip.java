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
@ApiModel(value = "云管理负载均衡VIP池", description = "云管理负载均衡VIP池")
public class VlbVip extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @NotNull
    @ApiModelProperty(value = "实例名称(NotNull)")
    @Length(max = 255)
    private String name;

    @ApiModelProperty(value = "实例描述")
    @Length(max = 255)
    private String description;

    @ApiModelProperty(value = "状态")
    @Length(max = 16)
    private String status;

    @ApiModelProperty(value = "状态描述")
    @Length(max = 255)
    private String statusDescription;

    @ApiModelProperty(value = "协议端口")
    private Integer protocolPort;

    @NotNull
    @ApiModelProperty(value = "协议(NotNull)")
    @Length(max = 255)
    private String protocol;

    @ApiModelProperty(value = "端口id")
    @Length(max = 36)
    private String portId;

    @ApiModelProperty(value = "管理网络状态")
    private Boolean adminStateUp = true;

    @NotNull
    @ApiModelProperty(value = "连接限制(NotNull)")
    private Integer connectionLimit;

    @ApiModelProperty(value = "租户")
    @Length(max = 50)
    private String tenantId;

    @ApiModelProperty(value = "cloudos回传的真实id")
    private String cloudosId;

    @CheckPattern(type = PatternType.CONTAINS, values = {"HTTP_COOKIE","APP_COOKIE","SOURCE_IP"})
    @ApiModelProperty(value = "会话持久化类型(NotNull)")
    private String cookieType;

    @ApiModelProperty(value = "会话名称(当cookieType为APP_COOKIE时NotNull)")
    private String cookieName;

    @ApiModelProperty(value = "vip")
    private String vipAddress;

    @ApiModelProperty(value = "虚服务网络id,vip归属子网(NotNull)")
    @Length(max = 255)
    private String vainSubnetId;

    @ApiModelProperty(value = "cloudos回传的虚服务网络真实id(不需要传递)")
    private String vsCloudosId;

    @ApiModelProperty(value = "虚服务子网名称(不需要传递)")
    private String vainSubnetName;

    @ApiModelProperty(value = "cloudos回传的负载均衡真实id(不需要传递)")
    private String poolCloudosId;

    public VlbVip() {
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

    public Integer getProtocolPort() {
        return protocolPort;
    }

    public void setProtocolPort(Integer protocolPort) {
        this.protocolPort = protocolPort;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    public Boolean getAdminStateUp() {
        return adminStateUp;
    }

    public void setAdminStateUp(Boolean adminStateUp) {
        this.adminStateUp = adminStateUp;
    }

    public Integer getConnectionLimit() {
        return connectionLimit;
    }

    public void setConnectionLimit(Integer connectionLimit) {
        this.connectionLimit = connectionLimit;
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

    public String getCookieType() {
        return cookieType;
    }

    public void setCookieType(String cookieType) {
        this.cookieType = cookieType;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getVipAddress() {
        return vipAddress;
    }

    public void setVipAddress(String vipAddress) {
        this.vipAddress = vipAddress;
    }

    public String getVainSubnetId() {
        return vainSubnetId;
    }

    public void setVainSubnetId(String vainSubnetId) {
        this.vainSubnetId = vainSubnetId;
    }

    public String getVsCloudosId() {
        return vsCloudosId;
    }

    public void setVsCloudosId(String vsCloudosId) {
        this.vsCloudosId = vsCloudosId;
    }

    public String getVainSubnetName() {
        return vainSubnetName;
    }

    public void setVainSubnetName(String vainSubnetName) {
        this.vainSubnetName = vainSubnetName;
    }

    public String getPoolCloudosId() {
        return poolCloudosId;
    }

    public void setPoolCloudosId(String poolCloudosId) {
        this.poolCloudosId = poolCloudosId;
    }

    public VlbVip(VlbPool vlbPool) {
        this.id = vlbPool.getId();
        this.name = vlbPool.getName();
        this.description = vlbPool.getDescription();
        this.status = vlbPool.getStatus();
        this.statusDescription = vlbPool.getStatusDescription();
        this.protocolPort = vlbPool.getProtocolPort();
        this.protocol = vlbPool.getProtocol();
        this.portId = vlbPool.getPortId();
        this.adminStateUp = vlbPool.getAdminStateUp();
        this.connectionLimit = vlbPool.getConnectionLimit();
        this.tenantId = vlbPool.getTenantId();
        this.vipAddress = vlbPool.getVip();
        this.vainSubnetId = vlbPool.getVainSubnetId();
        this.cookieType = vlbPool.getCookieType();
        this.cookieName = vlbPool.getCookieName();
        this.cloudosId = vlbPool.getVipCloudId();
        this.updatedUser(vlbPool.getUpdatedBy());
        this.createdUser(vlbPool.getCreatedBy());
    }
}
