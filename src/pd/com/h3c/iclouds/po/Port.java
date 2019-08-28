package com.h3c.iclouds.po;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * Created by yKF7317 on 2016/11/21.
 */
@ApiModel(value = "云管理路由器端口表", description = "云管理路由器端口表")
public class Port extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "端口ID")
    private String id;

    @ApiModelProperty(value = "端口名称(新建必传)")
    @Length(max = 255)
	@NotNull
    private String name;

    @ApiModelProperty(value = "MAC地址")
    @Length(max = 32)
    private String macAddress;

    @ApiModelProperty(value = "管理网络状态")
    private Boolean adminStateUp = false;

    @ApiModelProperty(value = "状态")
    @Length(max = 50)
    private String status = ConfigProperty.RESOURCE_OPTION_STATUS_SUCCESS;

    @ApiModelProperty(value = "设备ID",notes="对应的云主机，路由设备，")
    @Length(max = 255)
    private String deviceId;

    //绑定云主机或者是负载均衡
    @ApiModelProperty(value = "设备拥有者[只有产生IP才会显示]", notes = "network:router_interface[路由连接私有网络];network:router_gateway[路由连接公网Ip];network:floatingip[新建公网IP];" +
            "compute:cas[连接云主机];neutron:LOADBALANCER[连接负载均衡]")
    @Length(max = 255)
    private String deviceOwner;
    
    @ApiModelProperty(value = "租户")
    @Length(max = 255)
    private String tenantId;

    @ApiModelProperty(value = "路由器ID")
    @Length(max = 50)
    private String routeId;
    
    @ApiModelProperty(value = "端口类型")
    @Length(max = 50)
    private String portType;

    @ApiModelProperty(value = "cloudos回传的真实id")
    private String cloudosId;

    @ApiModelProperty(value = "ip地址(新建非必传)")
    private String address;

    private Set<IpAllocation> subnets;
	
    @ApiModelProperty(value = "网络的id(新建必传)")
    private String netWorkId;
    
    @ApiModelProperty(value = "network_id")
    private String networkCloudosId;

    @ApiModelProperty(value = "isinit默认是挂载网卡，true为初始化网卡")
    private Boolean isinit = true;
    
    @ApiModelProperty(value = "关联hostName")
    private String hostName;

    @ApiModelProperty(value = "关联设备(不需要传递)")
    private String deviceName;
    
    @ApiModelProperty(value = "用户名")
    private String projectName;
    
    @ApiModelProperty(value = "网络名")
    private String netName;
    
    @ApiModelProperty(value = "网络地址")
    private String netCidr;
    
    @ApiModelProperty(value = "安全组id(新建非必传)")
    private List<String> securityGroupIds;
    
    @ApiModelProperty(value = "用户id(新建必传)")
    private String userId;
    
    @ApiModelProperty(value = "绑定主机标志")
    private String bindHost;
    
    @ApiModelProperty(value = "关联主机id")
    private String hostId;
    
    @ApiModelProperty(value = "用户名")
    private String ownerName;
	
	@ApiModelProperty(value = "子网id")
	private String subnetId;
    
    public Boolean getIsinit() {
		return isinit;
	}

	public void setIsinit(Boolean isinit) {
		this.isinit = isinit;
	}

	public Port() {
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

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Boolean getAdminStateUp() {
        return adminStateUp;
    }

    public void setAdminStateUp(Boolean adminStateUp) {
        this.adminStateUp = adminStateUp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceOwner() {
        return deviceOwner;
    }

    public void setDeviceOwner(String deviceOwner) {
        this.deviceOwner = deviceOwner;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public String getCloudosId() {
        return cloudosId;
    }

    public void setCloudosId(String cloudosId) {
        this.cloudosId = cloudosId;
    }

	public Set<IpAllocation> getSubnets() {
		return subnets;
	}

	public void setSubnets(Set<IpAllocation> subnets) {
		this.subnets = subnets;
	}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

	public String getNetworkCloudosId() {
		return networkCloudosId;
	}

	public void setNetworkCloudosId(String networkCloudosId) {
		this.networkCloudosId = networkCloudosId;
	}
	
	public String getNetWorkId () {
		return netWorkId;
	}
	
	public void setNetWorkId (String netWorkId) {
		this.netWorkId = netWorkId;
	}
	
	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getNetName() {
		return netName;
	}

	public void setNetName(String netName) {
		this.netName = netName;
	}

	public String getNetCidr() {
		return netCidr;
	}

	public void setNetCidr(String netCidr) {
		this.netCidr = netCidr;
	}

	public List<String> getSecurityGroupIds() {
		return securityGroupIds;
	}

	public void setSecurityGroupIds(List<String> securityGroupIds) {
		this.securityGroupIds = securityGroupIds;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBindHost() {
		return bindHost;
	}

	public void setBindHost(String bindHost) {
		this.bindHost = bindHost;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
	
	public String getSubnetId () {
		return subnetId;
	}
	
	public void setSubnetId (String subnetId) {
		this.subnetId = subnetId;
	}
}
