package com.h3c.iclouds.po;

import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/22.
 */
@ApiModel(value = "云管理负载均衡实例与监视器关系", description = "云管理负载均衡实例与监视器关系")
public class Pool2HealthMonitor implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "监视器(NotNull)")
    @Length(max = 36)
    private String hmonitorId;

    @NotNull
    @ApiModelProperty(value = "实例(NotNull)")
    @Length(max = 36)
    private String poolId;

    @ApiModelProperty(value = "状态")
    @Length(max = 16)
    private String status;

    @ApiModelProperty(value = "状态描述")
    @Length(max = 255)
    private String statusDescription;

    public Pool2HealthMonitor() {
    }

    public String getHmonitorId() {
        return hmonitorId;
    }

    public void setHmonitorId(String hmonitorId) {
        this.hmonitorId = hmonitorId;
    }

    public String getPoolId() {
        return poolId;
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
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

}
