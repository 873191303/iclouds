package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@ApiModel(value = "云管理云存储", description = "云管理云存储")
public class Volume extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "uuid")
    @Length(max = 36)
    private String uuid;

    @ApiModelProperty(value = "硬盘ID")
    @Length(max = 50)
    private String diskId;

    @Pattern(regexp = "^[\\u4E00-\\u9FA50-9a-zA-Z_@-]{2,32}$")
    @ApiModelProperty(value = "名称")
    @Length(max = 50)
    private String name;

    @ApiModelProperty(value = "挂载状态")
    @Length(max = 250)
    private String attachStatus;

    @ApiModelProperty(value = "硬盘类型(1-普通硬盘; 2 -固态硬盘; 3 -创建云主机的系统盘)")
    @Length(max = 50)
    private String volumeType;

    @ApiModelProperty(value = "容量")
    private Integer size;

    @ApiModelProperty(value = "挂载主机")
    @Length(max = 100)
    private String host;

    @ApiModelProperty(value = "挂载点")
    @Length(max = 255)
    private String mountPoint;

    @ApiModelProperty(value = "运行状态")
    @Length(max = 50)
    private String status;

    @ApiModelProperty(value = "源硬盘快照")
    @Length(max = 50)
    private String snapShotId;

    @ApiModelProperty(value = "源硬盘ID")
    @Length(max = 50)
    private String sourceVolId;

    @ApiModelProperty(value = "源镜像")
    @Length(max = 50)
    private String imageRef;

    @ApiModelProperty(value = "描述")
    @Length(max = 500)
    private String description;

    @ApiModelProperty(value = "元信息")
    @Length(max = 500)
    private String metaData;

    @ApiModelProperty(value = "可用域")
    @Length(max = 50)
    private String azoneId;

    @ApiModelProperty(value = "租户ID")
    @Length(max = 64)
    private String projectId;

    @ApiModelProperty(value = "规格ID")
    @Length(max = 32)
    private String flavorId;

    @ApiModelProperty(value = "用户")
    @Length(max = 36)
    private String owner2;

    @ApiModelProperty(value = "删除标志")
    @Length(max = 32)
    private String deleted;

    @ApiModelProperty(value = "删除人")
    @Length(max = 36)
    private String deleteBy;
    
    @ApiModelProperty(value = "硬盘数量(不需要传值，数据表也没有该字段)")
    private Integer count;
    
    @ApiModelProperty(value = "可用域labelName(不需要传值，数据表也没有该字段)")
    private String lableName;
    
    @ApiModelProperty(value = "租期(不需要传值，数据表也没有该字段)")
    private String lease;
    
    @ApiModelProperty(value = "云硬盘规格名称(不需要传值)")
    private String flavorName;
    
    @ApiModelProperty(value = "可用域的zone(不需要传值)")
    private String zone;
    
    @ApiModelProperty(value = "主机名称")
    private String hostName;
    
    @ApiModelProperty(value = "用户名（不需要传值）")
    private String ownerName;
    
    @ApiModelProperty(value = "归属租户（不需要传值）")
    private String projectName;

	public Volume() {

	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDiskId() {
        return diskId;
    }

    public void setDiskId(String diskId) {
        this.diskId = diskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttachStatus() {
        return attachStatus;
    }

    public void setAttachStatus(String attachStatus) {
        this.attachStatus = attachStatus;
    }

    public String getVolumeType() {
        return volumeType;
    }

    public void setVolumeType(String volumeType) {
        this.volumeType = volumeType;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMountPoint() {
        return mountPoint;
    }

    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSnapShotId() {
        return snapShotId;
    }

    public void setSnapShotId(String snapShotId) {
        this.snapShotId = snapShotId;
    }

    public String getSourceVolId() {
        return sourceVolId;
    }

    public void setSourceVolId(String sourceVolId) {
        this.sourceVolId = sourceVolId;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public String getAzoneId() {
        return azoneId;
    }

    public void setAzoneId(String azoneId) {
        this.azoneId = azoneId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getFlavorId() {
        return flavorId;
    }

    public void setFlavorId(String flavorId) {
        this.flavorId = flavorId;
    }

    public String getOwner2() {
        return owner2;
    }

    public void setOwner2(String owner2) {
        this.owner2 = owner2;
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

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getLableName() {
		return lableName;
	}

	public void setLableName(String lableName) {
		this.lableName = lableName;
	}

	public String getLease() {
		return lease;
	}

	public void setLease(String lease) {
		this.lease = lease;
	}

	public String getFlavorName() {
		return flavorName;
	}

	public void setFlavorName(String flavorName) {
		this.flavorName = flavorName;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
