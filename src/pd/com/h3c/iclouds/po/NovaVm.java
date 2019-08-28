package com.h3c.iclouds.po;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.validate.NotNull;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by yKF7317 on 2016/11/21.
 */
@ApiModel(value = "云管理云主机", description = "云管理云主机")
public class NovaVm extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private String id;

    //@NotNull
    @ApiModelProperty(value = "UUID(NotNull)")
    @Length(max = 36)
    private String uuid;

    @NotNull
    @ApiModelProperty(value = "宿主机ID")
    @Length(max = 255)
    private String host;

    @ApiModelProperty(value = "主机名")
    @Length(max = 255)
    private String hostName;

    @ApiModelProperty(value = "cpu核数")
    private Integer vcpus;

    @ApiModelProperty(value = "内存")
    private Integer memory;

    @ApiModelProperty(value = "系统硬盘")
    private Integer ramdisk;

    @ApiModelProperty(value = "状态")
    @Length(max = 255)
    private String vmState;
    
    @ApiModelProperty(value = "开关状态")
    private Integer powerState;

    @ApiModelProperty(value = "配置规格")
    @Length(max = 50)
    private String flavorId;

    @ApiModelProperty(value = "操作系统类型")
    @Length(max = 64)
    private String osType;

    @ApiModelProperty(value = "镜像")
    @Length(max = 255)
    private String imageRef;

    @ApiModelProperty(value = "可用域")
    @Length(max = 128)
    private String azoneId;

    @ApiModelProperty(value = "租户ID")
    @Length(max = 64)
    private String projectId;

    @ApiModelProperty(value = "用户")
    @Length(max = 36)
    private String owner;
    
    @ApiModelProperty(value = "主机下的内部网络(不需要传值)")
    private List<Subnet> subnets = new ArrayList<Subnet>();
    
    @ApiModelProperty(value = "主机下的虚拟网卡id(不需要传值)")
    private String portCloudosId;
    
    @ApiModelProperty(value = "主机下的初始虚拟网卡（不需要传值）")
    private Port port;
    
    @ApiModelProperty(value = "owner所对应的用户名")
    private String ownerName;
    
    @ApiModelProperty(value = "归属租户（不需要传值）")
    private String projectName;
    
    @ApiModelProperty(value = "管理网ip")
    private String manageIp;

    @ApiModelProperty(value = "镜像")
    @Length(max = 255)
    private String imageName;
    
    public NovaVm() {
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getVcpus() {
        return vcpus;
    }

    public void setVcpus(Integer vcpus) {
        this.vcpus = vcpus;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    public Integer getRamdisk() {
        return ramdisk;
    }

    public void setRamdisk(Integer ramdisk) {
        this.ramdisk = ramdisk;
    }

    public String getVmState() {
        return vmState;
    }

    public void setVmState(String vmState) {
        this.vmState = vmState;
    }

    public Integer getPowerState() {
        return powerState;
    }

    public void setPowerState(Integer powerState) {
        this.powerState = powerState;
    }

    public String getFlavorId() {
        return flavorId;
    }

    public void setFlavorId(String flavorId) {
        this.flavorId = flavorId;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
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

    public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
    
	public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

	public List<Subnet> getSubnets() {
		return subnets;
	}

	public void setSubnets(List<Subnet> subnets) {
		this.subnets = subnets;
	}

	public String getPortCloudosId() {
		return portCloudosId;
	}

	public void setPortCloudosId(String portCloudosId) {
		this.portCloudosId = portCloudosId;
	}
    
	public Port getPort() {
		return port;
	}
    
	public void setPort(Port port) {
		this.port = port;
	}
    
    public String getProjectName () {
        return projectName;
    }
    
    public void setProjectName (String projectName) {
        this.projectName = projectName;
    }
    
    public String getManageIp () {
        return manageIp;
    }
    
    public void setManageIp (String manageIp) {
        this.manageIp = manageIp;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
