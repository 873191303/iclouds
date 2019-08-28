package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

@ApiModel(value = "云主机资源配置规格")
public class Flavor extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	@Length(max = 100)
	@ApiModelProperty(value = "规格名称")
	private String name;
	
	@ApiModelProperty(value = "cpu核数")
	private Integer vcpus;
	
	@ApiModelProperty(value = "内存（M）")
	private Integer ram;
	
	@ApiModelProperty(value = "系统盘（G）")
	private Integer disk;

	@Length(max = 100)
	@ApiModelProperty(value = "系统盘类型")
	private String diskType;
	
	@ApiModelProperty(value = "虚拟内存")
	private Integer swap;

	@Length(max = 1)
	@ApiModelProperty(value = "是否发布")
	private String isPublic;
	
	public Flavor() {
	
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

	public String getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(String isPublic) {
		this.isPublic = isPublic;
	}
	
}
