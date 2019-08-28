package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/9.
 */
@ApiModel(value = "资源配置服务器集群配置", description = "资源配置服务器集群配置")
public class Clusters extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "集群id")
    private String id;

    @NotNull
    @Length(max = 50)
    @ApiModelProperty(value = "集群名称")
    private String cname;

    @Length(max = 36)
    @NotNull
    @ApiModelProperty(value = "主机池id")
    private String phostId;

    @ApiModelProperty(value = "主机池名称")
    private String poolsName;

    @ApiModelProperty(value = "主机数")
    private Integer hosts;

    @ApiModelProperty(value = "虚拟机数")
    private Integer vms;

    @ApiModelProperty(value = "cpu总核数")
    private Integer cpus = 0;

    @ApiModelProperty(value = "总内存数")
    private Integer memorys = 0;

    @Length(max = 36)
    @NotNull
    @ApiModelProperty(value = "所属cas (NotNull)")
    private String belongCas;

    @Length(max = 36)
    @NotNull
    @ApiModelProperty(value = "casId (NotNull)")
    private String casId;

    @ApiModelProperty(value = "已分配内存")
    private Integer assignMem;

    @ApiModelProperty(value = "内存分配率")
    private Double memUsage;

    @ApiModelProperty(value = "已分配CPU核")
    private Integer assignCpu;

    @ApiModelProperty(value = "cpu分配率")
    private Double cpuUsage;

    public Clusters() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getPhostId() {
        return phostId;
    }

    public void setPhostId(String phostId) {
        this.phostId = phostId;
    }

    public String getPoolsName() {
        return poolsName;
    }

    public void setPoolsName(String poolsName) {
        this.poolsName = poolsName;
    }

    public Integer getVms() {
        return vms;
    }

    public void setVms(Integer vms) {
        this.vms = vms;
    }

    public Integer getHosts() {
        return hosts;
    }

    public void setHosts(Integer hosts) {
        this.hosts = hosts;
    }

    public Integer getCpus() {
        return cpus;
    }

    public void setCpus(Integer cpus) {
        this.cpus = cpus;
    }

    public Integer getMemorys() {
        return memorys;
    }

    public void setMemorys(Integer memorys) {
        this.memorys = memorys;
    }

    public String getBelongCas() {
        return belongCas;
    }

    public void setBelongCas(String belongCas) {
        this.belongCas = belongCas;
    }

    public String getCasId() {
        return casId;
    }

    public void setCasId(String casId) {
        this.casId = casId;
    }

    public Integer getAssignMem() {
        return assignMem;
    }

    public void setAssignMem(Integer assignMem) {
        this.assignMem = assignMem;
    }

    public Double getMemUsage() {
        return memUsage;
    }

    public void setMemUsage(Double memUsage) {
        this.memUsage = memUsage;
    }

    public Integer getAssignCpu() {
        return assignCpu;
    }

    public void setAssignCpu(Integer assignCpu) {
        this.assignCpu = assignCpu;
    }

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }
}
