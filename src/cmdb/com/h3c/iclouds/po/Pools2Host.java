package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 主机池
 * Created by yKF7317 on 2016/11/8.
 */
@ApiModel(value = "资源配置服务器主机池", description = "资源配置服务器主机池")
public class Pools2Host extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主机池id")
    private String id;

    @Length(max = 100)
    @NotNull
    @ApiModelProperty(value = "主机池名称 (NotNull)")
    private String poolName;

    @ApiModelProperty(value = "集群数量")
    private Integer clusters;

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
    
    @ApiModelProperty(value = "租户集合")
    private List<Map<String, Object>> projectList = new ArrayList<>();
    
    @ApiModelProperty(value = "租户数量")
    private Integer projectCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public Integer getClusters() {
        return clusters;
    }

    public void setClusters(Integer clusters) {
        this.clusters = clusters;
    }

    public Integer getHosts() {
        return hosts;
    }

    public void setHosts(Integer hosts) {
        this.hosts = hosts;
    }

    public Integer getVms() {
        return vms;
    }

    public void setVms(Integer vms) {
        this.vms = vms;
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
    
    public List<Map<String, Object>> getProjectList () {
        return projectList;
    }
    
    public void setProjectList (List<Map<String, Object>> projectList) {
        this.projectList = projectList;
    }
    
    public Integer getProjectCount () {
        return projectCount;
    }
    
    public void setProjectCount (Integer projectCount) {
        this.projectCount = projectCount;
    }
    
    public Pools2Host() {
    }
}
