package com.h3c.iclouds.po;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by yKF7317 on 2016/11/9.
 */
@ApiModel(value = "资源配置云主机配置", description = "资源配置云主机配置")
public class Server2Vm extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @Length(max = 50)
    @ApiModelProperty(value = "宿主机id")
    private String hostId;

    @Length(max = 50)
    @ApiModelProperty(value = "宿主机序列号")
    private String serilNo;

    @NotNull
    @Length(max = 50)
    @ApiModelProperty(value = "名称")
    private String vmName;

    @Length(max = 50)
    @NotNull
    @ApiModelProperty(value = "识别码")
    private String uuid;

    @ApiModelProperty(value = "运行状态")
    private String status;

    private String os;

    @ApiModelProperty(value = "虚拟cpu")
    private Integer cpu;

    @ApiModelProperty(value = "内存")
    private Integer memory;

    @ApiModelProperty(value = "存储")
    private Integer storage;

    @Length(max = 36)
    @ApiModelProperty(value = "系统管理员")
    private String sysUser;

    @Length(max = 36)
    @ApiModelProperty(value = "当前使用人")
    private String userId;

    @Length(max = 100)
    @ApiModelProperty(value = "描述")
    private String note;

    @Length(max = 50)
    @ApiModelProperty(value = "同步类型")
    private String syncType;

    @ApiModelProperty(value = "最后同步时间")
    private Date lastSyncDate;

    @ApiModelProperty(value = "ip地址")
    private String ips;

    @ApiModelProperty(value = "名称")
    private String poolName;

    private String poolType = "4";

    @Length(max = 36)
    @NotNull
    @ApiModelProperty(value = "所属cas (NotNull)")
    private String belongCas;

    @Length(max = 36)
    @NotNull
    @ApiModelProperty(value = "casId (NotNull)")
    private String casId;

    public Server2Vm() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getSerilNo() {
        return serilNo;
    }

    public void setSerilNo(String serilNo) {
        this.serilNo = serilNo;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
        this.poolName = vmName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public Integer getCpu() {
        return cpu;
    }

    public void setCpu(Integer cpu) {
        this.cpu = cpu;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    public Integer getStorage() {
        return storage;
    }

    public void setStorage(Integer storage) {
        this.storage = storage;
    }

    public String getSysUser() {
        return sysUser;
    }

    public void setSysUser(String sysUser) {
        this.sysUser = sysUser;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    public Date getLastSyncDate() {
        return lastSyncDate;
    }

    public void setLastSyncDate(Date lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getPoolType() {
        return poolType;
    }

    public void setPoolType(String poolType) {
        this.poolType = poolType;
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
}
