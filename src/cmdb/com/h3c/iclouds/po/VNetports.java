package com.h3c.iclouds.po;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by yKF7317 on 2016/11/9.
 */
@ApiModel(value = "资产配置虚拟机网口配置信息", description = "资产配置虚拟机网口配置信息")
public class VNetports extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "网口id")
    private String id;

    @Length(max = 50)
    @ApiModelProperty(value = "虚拟机编号")
    private String vmId;

    //@Max(16)
    @ApiModelProperty(value = "网口序号")
    private Integer seq;

    @Length(max = 50)
    @ApiModelProperty(value = "MAC地址")
    private String mac;

    @Length(max = 50)
    @ApiModelProperty(value = "网口类型")
    private String portType;

    @Length(max = 600)
    @ApiModelProperty(value = "备注")
    private String remark;

    @Length(max = 36)
    @ApiModelProperty(value = "宿主网口")
    private String peth;

    @ApiModelProperty(value = "设备名称")
    private String assetName;

    @ApiModelProperty(value = "网口mac")
    private String netMac;

    public VNetports() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVmId() {
        return vmId;
    }

    public void setVmId(String vmId) {
        this.vmId = vmId;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPeth() {
        return peth;
    }

    public void setPeth(String peth) {
        this.peth = peth;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getNetMac() {
        return netMac;
    }

    public void setNetMac(String netMac) {
        this.netMac = netMac;
    }
}
