package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 网络实体类
 * Created by yKF7317 on 2016/11/21.
 */
@ApiModel(value = "云管理网络", description = "云管理网络")
public class Network extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "网络ID")
    private String id;

    @NotNull
    @ApiModelProperty(value = "名称(NotNull)")
    @Length(max = 50)
    private String name;

    @ApiModelProperty(value = "是否共享")
    private Boolean shared = false;

    @ApiModelProperty(value = "租户(不需要传递)")
    @Length(max = 50)
    private String tenantId;

    @ApiModelProperty(value = "管理网络状态")
    private Boolean adminStateUp = false;

    @ApiModelProperty(value = "状态(不需要传递)")
    @Length(max = 50)
    private String status = ConfigProperty.RESOURCE_OPTION_STATUS_SUCCESS;

    @ApiModelProperty(value = "mtu值")
    private Integer mtu = 0;

    @ApiModelProperty(value = "vlan转换")
    private Boolean vlanTransparent = false;

    @ApiModelProperty(value = "是否公网")
    private Boolean externalNetworks = false;

    @ApiModelProperty(value = "VDCid(不需要传递)")
    @Length(max = 50)
    private String vdcId;

    @ApiModelProperty(value = "路由器id")
    @Length(max = 50)
    private String routeId;
    
    @ApiModelProperty(value = "vdc名称(不需要传递)")
    private String vdcName;

    @ApiModelProperty(value = "路由器名称(不需要传递)")
    private String routeName;

    @ApiModelProperty(value = "cidr(NotNull)")
    private String cidr;

    @ApiModelProperty(value = "网关ip")
    @CheckPattern(type = PatternType.IP)
    private String gatewayIp;

    @ApiModelProperty(value = "ip版本(NotNull)")
    private Integer ipVersion = 4;

    @ApiModelProperty(value = "是否打开dhcp")
    private Boolean enableDhcp;

    private String ipv6RaMode;

    private String ipv6AddressMode;

    @ApiModelProperty(value = "首ip(不需要传递)")
    private String startIp;

    @ApiModelProperty(value = "末ip(不需要传递)")
    private String endIp;

    @ApiModelProperty(value = "ip段")
    private String ipPool;

    @ApiModelProperty(value = "uuid(公网可以为null)")
    private String uuid;

    @ApiModelProperty(value = "子网id(不需要传递)")
    private String subnetId;

    @ApiModelProperty(value = "已使用ip集合(不需要传递)")
    private List<String> ipUsedIps;

    @ApiModelProperty(value = "cloudos回传的真实id(不需要传递)")
    private String cloudosId;

    @ApiModelProperty(value = "cloudos回传的路由器真实id(不需要传递)")
    private String routeCloudosId;

    @ApiModelProperty(value = "cloudos回传的子网真实id(不需要传递)")
    private String subnetCloudosId;

    @ApiModelProperty(value = "dns域名")
    private String dnsAddress;

    @ApiModelProperty(value = "主机路由")
    private String hostRoute;

    private String portCloudId;
    
    private String projectName;

    private List<String> dnsList = new ArrayList<>();
    
    private List<Map<String, String>> hostRouteList = new ArrayList<>();
    
    private List<Map<String,String>> ipPoolList = new ArrayList<>();
    
    @ApiModelProperty(value = "公网ip集合(公网使用)")
    private Set<FloatingIp> floatingIps = new HashSet<>();
    
    @ApiModelProperty(value = "公网的路由器集合(公网使用)")
    private List<GwRoute> routes = new ArrayList<>();

    public Network() {
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

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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

    public Integer getMtu() {
        return mtu;
    }

    public void setMtu(Integer mtu) {
        this.mtu = mtu;
    }

    public Boolean getVlanTransparent() {
        return vlanTransparent;
    }

    public void setVlanTransparent(Boolean vlanTransparent) {
        this.vlanTransparent = vlanTransparent;
    }

    public Boolean getExternalNetworks() {
        return externalNetworks;
    }

    public void setExternalNetworks(Boolean externalNetworks) {
        this.externalNetworks = externalNetworks;
    }

    public String getVdcId() {
        return vdcId;
    }

    public void setVdcId(String vdcId) {
        this.vdcId = vdcId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getVdcName() {
        return vdcName;
    }

    public void setVdcName(String vdcName) {
        this.vdcName = vdcName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getCidr() {
        return cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public String getGatewayIp() {
        return gatewayIp;
    }

    public void setGatewayIp(String gatewayIp) {
        this.gatewayIp = gatewayIp;
    }

    public Integer getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(Integer ipVersion) {
        this.ipVersion = ipVersion;
    }

    public Boolean getEnableDhcp() {
        return enableDhcp;
    }

    public void setEnableDhcp(Boolean enableDhcp) {
        this.enableDhcp = enableDhcp;
    }

    public String getIpv6RaMode() {
        return ipv6RaMode;
    }

    public void setIpv6RaMode(String ipv6RaMode) {
        this.ipv6RaMode = ipv6RaMode;
    }

    public String getIpv6AddressMode() {
        return ipv6AddressMode;
    }

    public void setIpv6AddressMode(String ipv6AddressMode) {
        this.ipv6AddressMode = ipv6AddressMode;
    }

    public String getStartIp() {
        return startIp;
    }

    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    public String getEndIp() {
        return endIp;
    }

    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    public String getIpPool() {
        return ipPool;
    }

    public void setIpPool(String ipPool) {
        this.ipPool = ipPool;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSubnetId() {
        return subnetId;
    }

    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    public List<String> getIpUsedIps() {
        return ipUsedIps;
    }

    public void setIpUsedIps(List<String> ipUsedIps) {
        this.ipUsedIps = ipUsedIps;
    }

    public String getCloudosId() {
        return cloudosId;
    }

    public void setCloudosId(String cloudosId) {
        this.cloudosId = cloudosId;
    }

    public String getRouteCloudosId() {
        return routeCloudosId;
    }

    public void setRouteCloudosId(String routeCloudosId) {
        this.routeCloudosId = routeCloudosId;
    }

    public String getSubnetCloudosId() {
        return subnetCloudosId;
    }

    public void setSubnetCloudosId(String subnetCloudosId) {
        this.subnetCloudosId = subnetCloudosId;
    }

    public String getDnsAddress() {
        return dnsAddress;
    }

    public void setDnsAddress(String dnsAddress) {
        this.dnsAddress = dnsAddress;
    }

    public String getHostRoute() {
        return hostRoute;
    }

    public void setHostRoute(String hostRoute) {
        this.hostRoute = hostRoute;
    }

    public String getPortCloudId() {
        return portCloudId;
    }

    public void setPortCloudId(String portCloudId) {
        this.portCloudId = portCloudId;
    }

    public List<String> getDnsList() {
        return dnsList;
    }

    public void setDnsList(List<String> dnsList) {
        this.dnsList = dnsList;
    }

    public List<Map<String, String>> getHostRouteList() {
        return hostRouteList;
    }

    public void setHostRouteList(List<Map<String, String>> hostRouteList) {
        this.hostRouteList = hostRouteList;
    }

    public List<Map<String, String>> getIpPoolList() {
        return ipPoolList;
    }

    public void setIpPoolList(List<Map<String, String>> ipPoolList) {
        this.ipPoolList = ipPoolList;
    }

	public Set<FloatingIp> getFloatingIps() {
		return floatingIps;
	}

	public void setFloatingIps(Set<FloatingIp> floatingIps) {
		this.floatingIps = floatingIps;
	}

	public List<GwRoute> getRoutes() {
		return routes;
	}

	public void setRoutes(List<GwRoute> routes) {
		this.routes = routes;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
    
}
