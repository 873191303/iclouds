package com.h3c.iclouds.po;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@ApiModel(value = "资源配置存储集群配置", description = "资源配置存储集群配置")
public class StorageClusters extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @NotNull
    @Length(max = 50)
    @ApiModelProperty(value = "名称")
    private String name;

    @Length(max = 50)
    @ApiModelProperty(value = "存储管理组id")
    private String gid;

    @CheckPattern(type = PatternType.IP)
    @ApiModelProperty(value = "管理ip")
    private String ip;

    @Length(max = 100)
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "归属管理组")
    private String groupName;

    @ApiModelProperty(value = "子存储数")
    private Integer items;

    @ApiModelProperty(value = "挂载数")
    private Integer volums;

    @ApiModelProperty(value = "总容量")
    private Float size;

    @ApiModelProperty(value = "已使用容量")
    private Float usedSize;

    @ApiModelProperty(value = "存储类型", notes = "0-独立存储,1-集群存储")
    @CheckPattern(type = PatternType.CONTAINS, values = {"0", "1"})
    private String type;

    @ApiModelProperty(value = "hpId")
    private String hpId;

    @ApiModelProperty(value = "归属接口id")
    private String belongId;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "序列号")
    private String serial;

    public StorageClusters() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getVolums() {
        return volums;
    }

    public void setVolums(Integer volums) {
        this.volums = volums;
    }

    public Integer getItems() {
        return items;
    }

    public void setItems(Integer items) {
        this.items = items;
    }

    public Float getSize() {
        return size;
    }

    public void setSize(Float size) {
        this.size = size;
    }

    public Float getUsedSize() {
        return usedSize;
    }

    public void setUsedSize(Float usedSize) {
        this.usedSize = usedSize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
