package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/22.
 */
@ApiModel(value = "云管理负载均衡实例池", description = "云管理负载均衡实例池")
public class VlbPool extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "cloudos回传的真实id(不需要传递)")
    private String cloudosId;

    @ApiModelProperty(value = "实例名称(NotNull)")
    @NotNull
    @Length(max = 255)
    private String name;

    @ApiModelProperty(value = "实例描述")
    @Length(max = 255)
    private String description;

    @NotNull
    @CheckPattern(type = PatternType.CONTAINS, values = {"HTTP", "HTTPS", "TCP"})
    @ApiModelProperty(value = "协议(NotNull)", notes = "HTTP;HTTPS;TCP")
    @Length(max = 255)
    private String protocol;

    @NotNull
    @CheckPattern(type = PatternType.CONTAINS, values = {"ROUND_ROBIN", "SOURCE_IP", "LEAST_CONNECTIONS"})
    @ApiModelProperty(value = "lb算法(NotNull)")
    @Length(max = 255)
    private String lbMethod;

    @ApiModelProperty(value = "虚服务网络id,vip归属子网(NotNull)")
    @Length(max = 255)
    private String vainSubnetId;

    @ApiModelProperty(value = "vipId(不需要传递)")
    @Length(max = 255)
    private String vipId;

    @ApiModelProperty(value = "clouos回传的vip真实Id")
    @Length(max = 255)
    private String vipCloudId;

    @NotNull
    @ApiModelProperty(value = "实服务网络id(NotNull)")
    @Length(max = 255)
    private String factSubnetId;

    @ApiModelProperty(value = "管理网络状态")
    private Boolean adminStateUp = true;

    @ApiModelProperty(value = "状态(不需要传递)")
    @Length(max = 50)
    private String status = ConfigProperty.RESOURCE_OPTION_STATUS_WAITING;

    @ApiModelProperty(value = "状态描述")
    @Length(max = 255)
    private String statusDescription;

    @NotNull
    @ApiModelProperty(value = "负载均衡ID(NotNull)")
    @Length(max = 36)
    private String lbId;

    @ApiModelProperty(value = "租户(不需要传递)")
    @Length(max = 50)
    private String tenantId;

    @ApiModelProperty(value = "VDCid(不需要传递)")
    @Length(max = 50)
    private String vdcId;

    @ApiModelProperty(value = "VDC名称(不需要传递)")
    private String vdcName;

    @ApiModelProperty(value = "负载均衡组名称(不需要传递)")
    private String lbName;

    @ApiModelProperty(value = "协议端口(NotNull)")
    private Integer protocolPort;

    @ApiModelProperty(value = "端口名称(不需要传递)")
    private String portName;

    @ApiModelProperty(value = "连接限制(NotNull)")
    @Range(max = 999999999, min = -1)
    @CheckPattern(type = PatternType.NOTEQUAL, values = {"0"})
    private Integer connectionLimit;

    @Range(max = 600, min = 5)
    @ApiModelProperty(value = "延时(NotNull)")
    private Integer delay;

    @Range(max = 600, min = 5)
    @ApiModelProperty(value = "超时(NotNull)")
    private Integer timeout;

    @Range(max = 10, min = 1)
    @ApiModelProperty(value = "最大重试次数(NotNull)")
    private Integer maxRetries;

    @CheckPattern(type = PatternType.CONTAINS, values = {"GET","POST","PUT"})
    @ApiModelProperty(value = "请求方式", notes = "GET(只有当健康检查类型为HTTP或HTTPS时有值且NotNull)")
    private String httpMethod;

    @Pattern(regexp = "^/.*")
    @ApiModelProperty(value = "url路径", notes = "只有当健康检查类型为HTTP或HTTPS时有值且NotNull")
    private String urlPath;

    @ApiModelProperty(value = "异常码")
    private String expectedCodes;

    @ApiModelProperty(value = "端口id(不需要传递)")
    private String portId;
    
    @ApiModelProperty(value = "端口的cloudosId(不需要传递)")
    private String portCdId;
    
    @ApiModelProperty(value = "监视器id(不需要传递)")
    private String hmonitorId;

    @ApiModelProperty(value = "cloudos回传的监视器真实id(不需要传递)")
    private String hmonitorCloudId;

    @CheckPattern(type = PatternType.CONTAINS, values = {"PING","HTTP","HTTPS","TCP"})
    @ApiModelProperty(value = "健康检查类型(NotNull)", notes = "PING;HTTP;HTTPS;TCP")
    private String type;

    @CheckPattern(type = PatternType.IP)
    @ApiModelProperty(value = "vip")
    private String vip;

    @NotNull
    @ApiModelProperty(value = "uuid(NotNull)")
    private String uuid;

    @CheckPattern(type = PatternType.CONTAINS, values = {"HTTP_COOKIE","APP_COOKIE","SOURCE_IP"})
    @ApiModelProperty(value = "会话持久化类型(NotNull)", notes = "HTTP_COOKIE; APP_COOKIE; SOURCE_IP")
    private String cookieType;

    @ApiModelProperty(value = "会话持久化名称(当cookieType为APP_COOKIE时NotNull)")
    private String cookieName;

    @ApiModelProperty(value = "cloudos回传的负载均衡真实id(不需要传递)")
    private String lbCloudosId;

    @ApiModelProperty(value = "cloudos回传的实服务网络真实id(不需要传递)")
    private String fsCloudosId;

    @ApiModelProperty(value = "实服务网络cidr(不需要传递)")
    private String fsCidr;

    @ApiModelProperty(value = "虚服务网络cidr(不需要传递)")
    private String vsCidr;
    
    private String projectName;
    
    public VlbPool() {
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

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getLbMethod() {
        return lbMethod;
    }

    public void setLbMethod(String lbMethod) {
        this.lbMethod = lbMethod;
    }

    public String getVainSubnetId() {
        return vainSubnetId;
    }

    public void setVainSubnetId(String vainSubnetId) {
        this.vainSubnetId = vainSubnetId;
    }

    public String getFactSubnetId() {
        return factSubnetId;
    }

    public void setFactSubnetId(String factSubnetId) {
        this.factSubnetId = factSubnetId;
    }

    public String getHmonitorCloudId() {
        return hmonitorCloudId;
    }

    public void setHmonitorCloudId(String hmonitorCloudId) {
        this.hmonitorCloudId = hmonitorCloudId;
    }

    public String getVipId() {
        return vipId;
    }

    public void setVipId(String vipId) {
        this.vipId = vipId;
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

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getLbId() {
        return lbId;
    }

    public void setLbId(String lbId) {
        this.lbId = lbId;
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

    public String getVdcName() {
        return vdcName;
    }

    public void setVdcName(String vdcName) {
        this.vdcName = vdcName;
    }

    public String getLbName() {
        return lbName;
    }

    public void setLbName(String lbName) {
        this.lbName = lbName;
    }

    public Integer getProtocolPort() {
        return protocolPort;
    }

    public void setProtocolPort(Integer protocolPort) {
        this.protocolPort = protocolPort;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public Integer getConnectionLimit() {
        return connectionLimit;
    }

    public void setConnectionLimit(Integer connectionLimit) {
        this.connectionLimit = connectionLimit;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getExpectedCodes() {
        return expectedCodes;
    }

    public void setExpectedCodes(String expectedCodes) {
        this.expectedCodes = expectedCodes;
    }

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    public String getHmonitorId() {
        return hmonitorId;
    }

    public void setHmonitorId(String hmonitorId) {
        this.hmonitorId = hmonitorId;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getLbCloudosId() {
        return lbCloudosId;
    }

    public void setLbCloudosId(String lbCloudosId) {
        this.lbCloudosId = lbCloudosId;
    }

    public String getFsCloudosId() {
        return fsCloudosId;
    }

    public void setFsCloudosId(String fsCloudosId) {
        this.fsCloudosId = fsCloudosId;
    }

    public String getFsCidr() {
        return fsCidr;
    }

    public void setFsCidr(String fsCidr) {
        this.fsCidr = fsCidr;
    }

    public String getVsCidr() {
        return vsCidr;
    }

    public void setVsCidr(String vsCidr) {
        this.vsCidr = vsCidr;
    }

    public String getVipCloudId() {
        return vipCloudId;
    }

    public void setVipCloudId(String vipCloudId) {
        this.vipCloudId = vipCloudId;
    }
    
    public String getPortCdId () {
        return portCdId;
    }
    
    public void setPortCdId (String portCdId) {
        this.portCdId = portCdId;
    }
    
    public String getProjectName () {
        return projectName;
    }
    
    public void setProjectName (String projectName) {
        this.projectName = projectName;
    }
}
