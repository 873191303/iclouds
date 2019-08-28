package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
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
@ApiModel(value = "云管理防火墙策略规则", description = "云管理防火墙策略规则")
public class PolicieRule extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "规则id")
    private String id;

    @NotNull
    @ApiModelProperty(value = "名称(NotNull)")
    @Length(max = 255)
    private String name;

    @ApiModelProperty(value = "描述")
    @Length(max = 1024)
    private String description;

    @ApiModelProperty(value = "是否共享")
    private Boolean shared = true;

    @ApiModelProperty(value = "协议", notes = "TCP、UDP、ICMP、ANY")
    @Length(max = 40)
    @CheckPattern(type = PatternType.CONTAINS, values = {"TCP", "UDP", "ICMP", "ANY"})
    private String protocol;

    @ApiModelProperty(value = "操作(NotNull)", notes = "allow,deny")
    @Length(max = 50)
    @NotNull
    @CheckPattern(type = PatternType.CONTAINS, values = {"allow", "deny"})
    private String action;

    @NotNull
    @ApiModelProperty(value = "ip版本(不需要传递)")
    private Integer ipVersion = 4;

    @ApiModelProperty(value = "源IP")
    @Length(max = 50)
    private String sourceIp;

    @ApiModelProperty(value = "源端口")
    private String sourcePort;

    @ApiModelProperty(value = "源最小端口(不需要传递)")
    @Min(1)
    @Max(65535)
    private Integer sourcePortRangeMin;

    @ApiModelProperty(value = "源最大端口(不需要传递)")
    @Min(1)
    @Max(65535)
    private Integer sourcePortRangeMax;

    @ApiModelProperty(value = "目标IP")
    @Length(max = 50)
    private String destinationIp;

    @ApiModelProperty(value = "目标端口")
    private String destinationPort;

    @ApiModelProperty(value = "目标最小端口(不需要传递)")
    @Min(1)
    @Max(65535)
    private Integer destinationPortRangeMin;

    @ApiModelProperty(value = "目标最大端口(不需要传递)")
    @Min(1)
    @Max(65535)
    private Integer destinationPortRangeMax;

    @ApiModelProperty(value = "是否禁用")
    private Boolean enabled = true;

    @ApiModelProperty(value = "策略id")
    @Length(max = 36)
    @NotNull
    private String policyId;

    @ApiModelProperty(value = "租户(不需要传递)")
    @Length(max = 50)
    private String tenantId;

    @ApiModelProperty(value = "规则顺序位置(不需要传递)")
    private Integer position;

    @ApiModelProperty(value = "cloudos回传的真实id(不需要传递)")
    private String cloudosId;

    @ApiModelProperty(value = "前于(规则id)")
    private String beforeId;

    @ApiModelProperty(value = "后于(规则id)")
    private String afterId;

    public PolicieRule() {
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

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(Integer ipVersion) {
        this.ipVersion = ipVersion;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public Integer getSourcePortRangeMin() {
        return sourcePortRangeMin;
    }

    public void setSourcePortRangeMin(Integer sourcePortRangeMin) {
        this.sourcePortRangeMin = sourcePortRangeMin;
    }

    public Integer getSourcePortRangeMax() {
        return sourcePortRangeMax;
    }

    public void setSourcePortRangeMax(Integer sourcePortRangeMax) {
        this.sourcePortRangeMax = sourcePortRangeMax;
    }

    public String getDestinationIp() {
        return destinationIp;
    }

    public void setDestinationIp(String destinationIp) {
        this.destinationIp = destinationIp;
    }

    public Integer getDestinationPortRangeMin() {
        return destinationPortRangeMin;
    }

    public void setDestinationPortRangeMin(Integer destinationPortRangeMin) {
        this.destinationPortRangeMin = destinationPortRangeMin;
    }

    public Integer getDestinationPortRangeMax() {
        return destinationPortRangeMax;
    }

    public void setDestinationPortRangeMax(Integer destinationPortRangeMax) {
        this.destinationPortRangeMax = destinationPortRangeMax;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getCloudosId() {
        return cloudosId;
    }

    public void setCloudosId(String cloudosId) {
        this.cloudosId = cloudosId;
    }

    public String getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }

    public String getBeforeId() {
        return beforeId;
    }

    public void setBeforeId(String beforeId) {
        this.beforeId = beforeId;
    }

    public String getAfterId() {
        return afterId;
    }

    public void setAfterId(String afterId) {
        this.afterId = afterId;
    }
}
