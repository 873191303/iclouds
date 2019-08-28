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
@ApiModel(value = "云管理子网可用IP范围", description = "云管理子网可用IP范围")
public class IpAvailabilityRange implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ip池ID")
    @Length(max = 36)
    private String allocationPoolId;

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

    public String getAllocationPoolId() {
        return allocationPoolId;
    }

    public void setAllocationPoolId(String allocationPoolId) {
        this.allocationPoolId = allocationPoolId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IpAvailabilityRange)) return false;

        IpAvailabilityRange that = (IpAvailabilityRange) o;

        if (!allocationPoolId.equals(that.allocationPoolId)) return false;
        if (!firstIp.equals(that.firstIp)) return false;
        return lastIp.equals(that.lastIp);

    }

    @Override
    public int hashCode() {
        int result = allocationPoolId.hashCode();
        result = 31 * result + firstIp.hashCode();
        result = 31 * result + lastIp.hashCode();
        return result;
    }
}
