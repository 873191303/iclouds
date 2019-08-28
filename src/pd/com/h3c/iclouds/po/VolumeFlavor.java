package com.h3c.iclouds.po;

import java.io.Serializable;
import java.util.Date;
import org.hibernate.validator.constraints.Length;
import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
* @author  zKF7420
* @date 2017年2月16日 上午11:09:35
*/
@ApiModel(value = "云存储规格", description = "云存储规格")
public class VolumeFlavor extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "uuid")
    @Length(max = 32)
    private String uuid;

    @ApiModelProperty(value = "规格名称")
    @Length(max = 100)
    private String name;

    @ApiModelProperty(value = "描述")
    @Length(max = 100)
    private String description;

    @ApiModelProperty(value = "容量")
    private int size;

    @ApiModelProperty(value = "硬盘类型(1-普通硬盘; 2 -固态硬盘;3 -创建云主机的系统盘)")
    @Length(max = 50)
    private String volumeType;
    
    @ApiModelProperty(value = "删除标志")
    @Length(max = 32)
    private String deleted;

    @ApiModelProperty(value = "删除人")
    @Length(max = 36)
    private String deleteBy;
    
    @ApiModelProperty(value = "删除时间")
	private Date deleteDate;
    
    public VolumeFlavor() {
    	
    }

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getVolumeType() {
		return volumeType;
	}

	public void setVolumeType(String volumeType) {
		this.volumeType = volumeType;
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
