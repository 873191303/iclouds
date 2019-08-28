package com.h3c.iclouds.po;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * cvk
 * Created by yKF7317 on 2017/2/23.
 */
@ApiModel(value = "容量管理CVK超配记录(主机)", description = "容量管理CVK超配记录(主机)")
public class Server2Ove implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "主机池id (NotNull)")
    @Length(max = 36)
    @NotNull
    private String poolId;

    @ApiModelProperty(value = "集群id")
    @Length(max = 36)
    private String custerId;

    @ApiModelProperty(value = "主机id (NotNull)")
    @Length(max = 100)
    @NotNull
    private String hostId;

    @ApiModelProperty(value = "主机名称 (NotNull)")
    @Length(max = 36)
    @NotNull
    private String hostName;

    @ApiModelProperty(value = "资产id")
    @Length(max = 36)
    private String assetId;

    @ApiModelProperty(value = "序列号")
    @Length(max = 36)
    private String serilNum;

    @ApiModelProperty(value = "归属cvm")
    @Length(max = 36)
    private String belongId;

    @ApiModelProperty(value = "年")
    @Length(max = 4)
    private String year;

    @ApiModelProperty(value = "月")
    @Length(max = 2)
    private String month;

    @ApiModelProperty(value = "日")
    @Length(max = 2)
    private String day;

    @ApiModelProperty(value = "cpu")
    private Integer cpus;

    @ApiModelProperty(value = "cpu超配值")
    private Float cpuOverSize;

    @ApiModelProperty(value = "ram")
    private Integer ram;

    @ApiModelProperty(value = "ram超配值")
    private Float ramOverSize;

    @ApiModelProperty(value = "虚拟机数")
    private Integer vms;

    @ApiModelProperty(value = "用户")
    @Length(max = 36)
    private String userId;

    @ApiModelProperty(value = "日期")
    private Date date;

    @ApiModelProperty(value = "ip")
    @Length(max = 36)
    private String ip;

    @ApiModelProperty(value = "mac")
    @Length(max = 36)
    private String mac;

    @ApiModelProperty(value = "主机池名称")
    private String poolName;

    @ApiModelProperty(value = "集群名称")
    private String custerName;

    @ApiModelProperty(value = "资产名称")
    private String assetName;

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

    @ApiModelProperty(value = "归属cvm名称")
    private String belongCvm;
    
    @ApiModelProperty(value = "租户集合")
    private List<Map<String, Object>> projectList = new ArrayList<>();
    
    @ApiModelProperty(value = "租户数量")
    private Integer projectCount;

    public Server2Ove() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPoolId() {
        return poolId;
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }

    public String getCusterId() {
        return custerId;
    }

    public void setCusterId(String custerId) {
        this.custerId = custerId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getSerilNum() {
        return serilNum;
    }

    public void setSerilNum(String serilNum) {
        this.serilNum = serilNum;
    }

    public String getBelongId() {
        return belongId;
    }

    public void setBelongId(String belongId) {
        this.belongId = belongId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Integer getCpus() {
        return cpus;
    }

    public void setCpus(Integer cpus) {
        this.cpus = cpus;
    }

    public Float getCpuOverSize() {
        return cpuOverSize;
    }

    public void setCpuOverSize(Float cpuOverSize) {
        this.cpuOverSize = cpuOverSize;
    }

    public Integer getRam() {
        return ram;
    }

    public void setRam(Integer ram) {
        this.ram = ram;
    }

    public Float getRamOverSize() {
        return ramOverSize;
    }

    public void setRamOverSize(Float ramOverSize) {
        this.ramOverSize = ramOverSize;
    }

    public Integer getVms() {
        return vms;
    }

    public void setVms(Integer vms) {
        this.vms = vms;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getCusterName() {
        return custerName;
    }

    public void setCusterName(String custerName) {
        this.custerName = custerName;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
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

    public String getBelongCvm() {
        return belongCvm;
    }

    public void setBelongCvm(String belongCvm) {
        this.belongCvm = belongCvm;
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
}
