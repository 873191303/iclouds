package com.h3c.iclouds.po;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by yKF7317 on 2016/11/21.
 */
@ApiModel(value = "云管理安全组端口绑定表", description = "云管理安全组端口绑定表")
public class Group2Port extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "端口id(NotNull)")
    @Length(max = 36)
    private String portId;

    @NotNull
    @ApiModelProperty(value = "安全组id(NotNull)")
    @Length(max = 36)
    private String securityGroupId;

    public Group2Port() {
    }

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    public String getSecurityGroupId() {
        return securityGroupId;
    }

    public void setSecurityGroupId(String securityGroupId) {
        this.securityGroupId = securityGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group2Port)) return false;

        Group2Port that = (Group2Port) o;

        if (!portId.equals(that.portId)) return false;
        return securityGroupId.equals(that.securityGroupId);

    }

    @Override
    public int hashCode() {
        int result = portId.hashCode();
        result = 31 * result + securityGroupId.hashCode();
        return result;
    }
}
