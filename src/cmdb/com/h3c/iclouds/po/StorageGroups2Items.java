package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@ApiModel(value = "资源配置存储集群配置明细", description = "资源配置存储集群配置明细")
public class StorageGroups2Items extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @NotNull
    @Length(max = 50)
    @ApiModelProperty(value = "存储id（资产id）")
    private String masterId;

    @Length(max = 36)
    @ApiModelProperty(value = "集群id")
    private String cid;

    @Length(max = 500)
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "存储名称")
    private String name;

    @ApiModelProperty(value = "归属集群")
    private String cname;

    @ApiModelProperty(value = "系列号")
    private String serial;

    @ApiModelProperty(value = "型号")
    private String assMode;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "环境类型")
    private String useFlag;

    @ApiModelProperty(value = "总容量")
    private Integer size;

    @ApiModelProperty(value = "运行状态")
    private String status;

    @ApiModelProperty(value = "管理员")
    private String administrator;
    
    @ApiModelProperty(value = "存储类型-0 独立存储 1 集群存储")
    private String type;
    
    public StorageGroups2Items() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getAssMode() {
        return assMode;
    }

    public void setAssMode(String assMode) {
        this.assMode = assMode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUseFlag() {
        return useFlag;
    }

    public void setUseFlag(String useFlag) {
        this.useFlag = useFlag;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdministrator() {
        return administrator;
    }

    public void setAdministrator(String administrator) {
        this.administrator = administrator;
    }
    
    public String getType () {
        return type;
    }
    
    public void setType (String type) {
        this.type = type;
    }
}
