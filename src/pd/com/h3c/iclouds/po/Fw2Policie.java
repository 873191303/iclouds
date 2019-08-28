package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/22.
 */
@ApiModel(value = "云管理防火墙与规则集映射表", description = "云管理防火墙与规则集映射表")
public class Fw2Policie extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "映射ID")
    private String id;

    @NotNull
    @ApiModelProperty(value = "防火墙(NotNull)")
    @Length(max = 36)
    private String firewallId;

    @NotNull
    @ApiModelProperty(value = "规则集(NotNull)")
    @Length(max = 36)
    private String policieId;

    @ApiModelProperty(value = "显示顺序")
    private Integer sequence;

    public Fw2Policie() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirewallId() {
        return firewallId;
    }

    public void setFirewallId(String firewallId) {
        this.firewallId = firewallId;
    }

    public String getPolicieId() {
        return policieId;
    }

    public void setPolicieId(String policieId) {
        this.policieId = policieId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
