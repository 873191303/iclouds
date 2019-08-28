package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/9.
 */
@ApiModel(value = "资源配置服务器集群配置明细", description = "资源配置服务器集群配置明细")
public class Cluster2Items extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "明细id")
    private String id;

    @Length(max = 36)
    @NotNull
    @ApiModelProperty(value = "资产id")
    private String assId;

    @Length(max = 50)
    @ApiModelProperty(value = "集群id")
    private String custerTId;

    @Length(max = 36)
    @ApiModelProperty(value = "主机池id")
    private String phostId;

    @Length(max = 25)
    @ApiModelProperty(value = "CVK版本")
    private String cvkVersion;

    @Length(max = 500)
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "虚拟机数")
    private Integer vms;

    @ApiModelProperty(value = "ip地址")
    private String ip;

    @ApiModelProperty(value = "序列号")
    private String serial;

    @ApiModelProperty(value = "主机池（集群组）名称")
    private String poolsName;

    @ApiModelProperty(value = "集群名称")
    private String clusName;

    @ApiModelProperty(value = "主机名称")
    private String hname;

    public Cluster2Items() {
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public Integer getVms() {
        return vms;
    }

    public void setVms(Integer vms) {
        this.vms = vms;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssId() {
        return assId;
    }

    public void setAssId(String assId) {
        this.assId = assId;
    }

    public String getCusterTId() {
        return custerTId;
    }

    public void setCusterTId(String custerTId) {
        this.custerTId = custerTId;
    }

    public String getPhostId() {
        return phostId;
    }

    public void setPhostId(String phostId) {
        this.phostId = phostId;
    }

    public String getCvkVersion() {
        return cvkVersion;
    }

    public void setCvkVersion(String cvkVersion) {
        this.cvkVersion = cvkVersion;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPoolsName() {
        return poolsName;
    }

    public void setPoolsName(String poolsName) {
        this.poolsName = poolsName;
    }

    public String getClusName() {
        return clusName;
    }

    public void setClusName(String clusName) {
        this.clusName = clusName;
    }

    public String getHname() {
        return hname;
    }

    public void setHname(String hname) {
        this.hname = hname;
    }
}
