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
@ApiModel(value = "云管理负载均衡监视器", description = "云管理负载均衡监视器")
public class HealthMonitor extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "类型")
    @Length(max = 255)
    private String type;

    @NotNull
    @ApiModelProperty(value = "延时(NotNull)")
    private Integer delay;

    @NotNull
    @ApiModelProperty(value = "超时(NotNull)")
    private Integer timeout;

    @NotNull
    @ApiModelProperty(value = "最大重试次数(NotNull)")
    private Integer maxRetries;

    @ApiModelProperty(value = "请求方式(NotNull)")
    private String httpMethod;

    @ApiModelProperty(value = "url路径")
    @Length(max = 255)
    private String urlPath;

    @ApiModelProperty(value = "异常码")
    @Length(max = 64)
    private String expectedCodes;

    @NotNull
    @ApiModelProperty(value = "管理网络状态(NotNull)")
    private Boolean adminStateUp = true;

    @NotNull
    @ApiModelProperty(value = "负载均衡ID(NotNull)")
    @Length(max = 36)
    private String lbId;

    @ApiModelProperty(value = "租户(不需要传递)")
    @Length(max = 50)
    private String tenantId;

    @ApiModelProperty(value = "cloudos回传的真实id(不需要传递)")
    private String cloudosId;

    @ApiModelProperty(value = "cloudos回传的负载均衡真实id(不需要传递)")
    private String lbCloudosId;

    @ApiModelProperty(value = "资源池id(不需要传递)")
    private String poolId;

    @ApiModelProperty(value = "cloudos回传的资源池真实id(不需要传递)")
    private String poolCloudosId;

    public HealthMonitor() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getLbId() {
        return lbId;
    }

    public void setLbId(String lbId) {
        this.lbId = lbId;
    }

    public String getCloudosId() {
        return cloudosId;
    }

    public void setCloudosId(String cloudosId) {
        this.cloudosId = cloudosId;
    }

    public String getLbCloudosId() {
        return lbCloudosId;
    }

    public void setLbCloudosId(String lbCloudosId) {
        this.lbCloudosId = lbCloudosId;
    }

    public String getPoolId() {
        return poolId;
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }

    public String getPoolCloudosId() {
        return poolCloudosId;
    }

    public void setPoolCloudosId(String poolCloudosId) {
        this.poolCloudosId = poolCloudosId;
    }

    public HealthMonitor(VlbPool vlbPool) {
        this.id = vlbPool.getHmonitorId();
        this.type = vlbPool.getType();
        this.delay = vlbPool.getDelay();
        this.timeout = vlbPool.getTimeout();
        this.maxRetries = vlbPool.getMaxRetries();
        this.httpMethod = vlbPool.getHttpMethod();
        this.urlPath = vlbPool.getUrlPath();
        this.expectedCodes = vlbPool.getExpectedCodes();
        this.adminStateUp = vlbPool.getAdminStateUp();
        this.tenantId = vlbPool.getTenantId();
        this.lbId = vlbPool.getLbId();
        this.cloudosId = vlbPool.getHmonitorCloudId();
        this.updatedUser(vlbPool.getUpdatedBy());
        this.createdUser(vlbPool.getCreatedBy());
    }
}
