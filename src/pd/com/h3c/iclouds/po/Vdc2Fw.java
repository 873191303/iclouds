package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/22.
 */
@ApiModel(value = "云管理VDC与防火墙映射表", description = "云管理VDC与防火墙映射表")
public class Vdc2Fw extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "防火墙ID")
    @Length(max = 32)
    private String firewallId;

    @ApiModelProperty(value = "VDC")
    @Length(max = 32)
    private String vdcId;

    @ApiModelProperty(value = "吞吐量")
    private Integer throughPut;

    @ApiModelProperty(value = "显示顺序")
    private Integer sequence;

    public Vdc2Fw() {
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

    public String getVdcId() {
        return vdcId;
    }

    public void setVdcId(String vdcId) {
        this.vdcId = vdcId;
    }

    public Integer getThroughPut() {
        return throughPut;
    }

    public void setThroughPut(Integer throughPut) {
        this.throughPut = throughPut;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
