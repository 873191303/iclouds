package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@ApiModel(value = "云管理云主机配置规格表", description = "云管理云主机配置规格表")
public class NovaFlavor extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "规格ID")
    private String id;
    
    @NotNull
    @ApiModelProperty(value = "规格名称(NotNull)")
    @Length(max = 100)
    private String name;

    @NotNull
    @ApiModelProperty(value = "cpu(NotNull)")
    private Integer vcpus;

    @NotNull
    @ApiModelProperty(value = "内存(NotNull)")
    private Integer ram;

    @NotNull
    @ApiModelProperty(value = "系统盘(NotNull)")
    private Integer disk;

    @NotNull
    @ApiModelProperty(value = "存储类型(NotNull):0-普通类型")
    @Length(max = 100)
    private String diskType = "0";

    @NotNull
    @ApiModelProperty(value = "虚拟内存(NotNull)")
    private Integer swap = 0;

    @ApiModelProperty(value = "是否停用")
    private Boolean disabled = false;

    @NotNull
    @ApiModelProperty(value = "是否发布(NotNull)")
    private Boolean isPublic = true;

    @ApiModelProperty(value = "删除标志")
    @Length(max = 32)
    private String deleted = "0";

    @ApiModelProperty(value = "删除人")
    @Length(max = 36)
    private String deleteBy;

    @ApiModelProperty(value = "删除日期")
    private Date deleteDate;

    public NovaFlavor() {
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

    public Integer getVcpus() {
        return vcpus;
    }

    public void setVcpus(Integer vcpus) {
        this.vcpus = vcpus;
    }

    public Integer getRam() {
        return ram;
    }

    public void setRam(Integer ram) {
        this.ram = ram;
    }

    public Integer getDisk() {
        return disk;
    }

    public void setDisk(Integer disk) {
        this.disk = disk;
    }

    public String getDiskType() {
        return diskType;
    }

    public void setDiskType(String diskType) {
        this.diskType = diskType;
    }

    public Integer getSwap() {
        return swap;
    }

    public void setSwap(Integer swap) {
        this.swap = swap;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getDeleteBy() {
        return deleteBy;
    }

    public void setDeleteBy(String deleteBy) {
        this.deleteBy = deleteBy;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }
}
