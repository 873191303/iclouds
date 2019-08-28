package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@ApiModel(value = "资源配置存储卷配置", description = "资源配置存储卷配置")
public class StorageVolums extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @NotNull
    @Length(max = 50)
    @ApiModelProperty(value = "宿主存储id")
    private String sid;

    @Length(max = 36)
    @ApiModelProperty(value = "宿主存储类型")
    private String storyType;

    @NotNull
    @ApiModelProperty(value = "卷名")
    private String volumeName;

    @ApiModelProperty(value = "容量大小")
    private Float size;

    @ApiModelProperty(value = "已使用容量大小")
    private Float usedSize;

    @ApiModelProperty(value = "Raid方式")
    private Integer raidMethod;

    @Length(max = 100)
    @ApiModelProperty(value = "备注")
    private String remark;

    @CheckPattern(type = PatternType.IP)
    @ApiModelProperty(value = "卷IP")
    private String ip;

    @ApiModelProperty(value = "挂载主机")
    private String hostName;

    @ApiModelProperty(value = "target Name")
    private String targetName;

    @ApiModelProperty(value = "wwn号")
    private String wwn;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "IQN")
    private String iqn;

    @ApiModelProperty(value = "VIP号")
    private String vip;

    private String targetIp;

    @ApiModelProperty(value = "hpId")
    private String hpId;

    @ApiModelProperty(value = "归属接口id")
    private String belongId;

    public StorageVolums() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getStoryType() {
        return storyType;
    }

    public void setStoryType(String storyType) {
        this.storyType = storyType;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public void setVolumeName(String volumeName) {
        this.volumeName = volumeName;
    }

    public Float getSize() {
        return size;
    }

    public void setSize(Float size) {
        this.size = size;
    }

    public Integer getRaidMethod() {
        return raidMethod;
    }

    public void setRaidMethod(Integer raidMethod) {
        this.raidMethod = raidMethod;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getWwn() {
        return wwn;
    }

    public void setWwn(String wwn) {
        this.wwn = wwn;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Float getUsedSize() {
        return usedSize;
    }

    public void setUsedSize(Float usedSize) {
        this.usedSize = usedSize;
    }

    public String getIqn() {
        return iqn;
    }

    public void setIqn(String iqn) {
        this.iqn = iqn;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public String getHpId() {
        return hpId;
    }

    public void setHpId(String hpId) {
        this.hpId = hpId;
    }

    public String getBelongId() {
        return belongId;
    }

    public void setBelongId(String belongId) {
        this.belongId = belongId;
    }
}
