package com.h3c.iclouds.po;

import com.h3c.iclouds.validate.CheckPattern;
import com.h3c.iclouds.validate.NotNull;
import com.h3c.iclouds.validate.PatternType;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

public class FloatingIp implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键")
    @Length(max = 36)
    private String id;

    @ApiModelProperty(value = "租户id")
    @Length(max = 64)
    private String tenantId;

    @CheckPattern(type = PatternType.IP)
    @ApiModelProperty(value = "公网ip")
    @Length(max = 64)
    private String floatingIp;
    
    @NotNull
    @ApiModelProperty(value = "公网")
    @Length(max = 36)
    private String networkId;
    
    @ApiModelProperty(value = "公网虚拟网卡")
    @Length(max = 36)
    private String floatingPortId;
    
    @ApiModelProperty(value = "分配资源的关联网卡id")
    @Length(max = 36)
    private String fixedPortId;
    
    @CheckPattern(type = PatternType.IP)
    @ApiModelProperty(value = "分配资源的关联ip")
    @Length(max = 64)
    private String fixedIp;
    
    @ApiModelProperty(value = "分配资源的关联网卡所属的路由器")
    @Length(max = 36)
    private String routerId;
    
    @ApiModelProperty(value = "")
    @Length(max = 36)
    private String lastRouterId;
    
    @ApiModelProperty(value = "公网状态")
    @Length(max = 36)
    private String status;
    
    @NotNull
    @ApiModelProperty(value = "公网名字")
    @Length(max = 128)
    private String name;
    
    @NotNull
    @ApiModelProperty(value = "公网带宽")
    @Length(max = 256)
    private String norm;
    
    @ApiModelProperty(value = "cloudos回写id")
    @Length(max = 50)
    private String cloudosId;
    
    @ApiModelProperty(value = "公网池名称(不传值)")
    private String networkName;
    
    @ApiModelProperty(value = "所属资源(不传值)")
    private String resource;
    
    @ApiModelProperty(value = "所属资源类型(不传值)")
    private String resourceType;
    
    @ApiModelProperty(value = "公网网卡的cloudosId(不需要传值)")
    private String portCdId;
    
    @ApiModelProperty(value = "普通修改传 0, 绑定解绑虚拟网卡传1")
    private String flag;
    
    private String owner;
    
    private String ownerName;
    
    private String subnetId;
    
    private String portName;
	
	private String projectName;
	
	public FloatingIp() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getFloatingIp() {
		return floatingIp;
	}

	public void setFloatingIp(String floatingIp) {
		this.floatingIp = floatingIp;
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	public String getFloatingPortId() {
		return floatingPortId;
	}

	public void setFloatingPortId(String floatingPortId) {
		this.floatingPortId = floatingPortId;
	}

	public String getFixedPortId() {
		return fixedPortId;
	}

	public void setFixedPortId(String fixedPortId) {
		this.fixedPortId = fixedPortId;
	}

	public String getFixedIp() {
		return fixedIp;
	}

	public void setFixedIp(String fixedIp) {
		this.fixedIp = fixedIp;
	}

	public String getRouterId() {
		return routerId;
	}

	public void setRouterId(String routerId) {
		this.routerId = routerId;
	}

	public String getLastRouterId() {
		return lastRouterId;
	}

	public void setLastRouterId(String lastRouterId) {
		this.lastRouterId = lastRouterId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNorm() {
		return norm;
	}

	public void setNorm(String norm) {
		this.norm = norm;
	}

	public String getCloudosId() {
		return cloudosId;
	}

	public void setCloudosId(String cloudosId) {
		this.cloudosId = cloudosId;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	
	public String getPortCdId () {
		return portCdId;
	}
	
	public void setPortCdId (String portCdId) {
		this.portCdId = portCdId;
	}
	
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	public String getSubnetId () {
		return subnetId;
	}
	
	public void setSubnetId (String subnetId) {
		this.subnetId = subnetId;
	}
	
	public String getPortName () {
		return portName;
	}
	
	public void setPortName (String portName) {
		this.portName = portName;
	}
	
	public String getProjectName () {
		return projectName;
	}
	
	public void setProjectName (String projectName) {
		this.projectName = projectName;
	}
}
