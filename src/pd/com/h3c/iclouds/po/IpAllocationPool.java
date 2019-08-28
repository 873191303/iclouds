package com.h3c.iclouds.po;

import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/21.
 */
@ApiModel(value = "云管理子网准IP池", description = "云管理子网准IP池")
public class IpAllocationPool implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "子网id")
    @Length(max = 36)
    private String subnetId;

    @NotNull
    @Length(max = 64)
    @ApiModelProperty(value = "首ip(NotNull)")
    @CheckPattern(type = PatternType.IP)
    private String firstIp;

    @NotNull
    @Length(max = 64)
    @ApiModelProperty(value = "末ip(NotNull)")
    @CheckPattern(type = PatternType.IP)
    private String lastIp;

    public IpAllocationPool() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubnetId() {
        return subnetId;
    }

    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    public String getFirstIp() {
        return firstIp;
    }

    public void setFirstIp(String firstIp) {
        this.firstIp = firstIp;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }
}
