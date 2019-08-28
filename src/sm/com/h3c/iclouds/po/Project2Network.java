package com.h3c.iclouds.po;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "云管理租户配额网络表", description = "云管理租户配额网络表")
public class Project2Network extends BaseEntity implements Serializable {

	
	private static final long serialVersionUID = 604822159382340095L;

	@ApiModelProperty(value = "网络ID")
	private String id;

	@ApiModelProperty(value = "租户ID")
	@Length(max = 64)
	private String tenantId;

	@ApiModelProperty(value = "cidr")
	@Length(max = 36)
	private String cidr;
	
	@ApiModelProperty(value = "创建时间")
	private Date createdAt;

	@ApiModelProperty(value = "修改时间")
	private Date updatedAt;

	@ApiModelProperty(value = "删除时间")
	private Date deletedAt;

	@ApiModelProperty(value = "删除标志")

	private Integer deleted;

	private Set<Network2Subnet> network2Subnets = new HashSet<>();
	
	public Project2Network() {
	
	}
	
	public Project2Network(String tenantId,String cidr) {
		id=UUID.randomUUID().toString();
		this.cidr=cidr;
		this.tenantId=tenantId;
		deleted=0;
		createDate();
	}
	
	public void deleteDate() {
	    this.deletedAt = new Date();
		this.deleted = Integer.parseInt(ConfigProperty.NO);
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

	public String getCidr() {
		return cidr;
	}

	public void setCidr(String cidr) {
		this.cidr = cidr;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Date getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Date deletedAt) {
		this.deletedAt = deletedAt;
	}

	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}
	public void createDate() {
    	this.updatedAt = new Date();
		this.createdAt = updatedAt;
		this.deletedAt = null;
		this.deleted = Integer.parseInt(ConfigProperty.YES);
	}
	@Override
	public int hashCode() {
		return cidr.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Project2Network) {
			Project2Network project2Network=(Project2Network) obj;
			return cidr.equals(project2Network.cidr);
		}
		return false;
		
	}
	
	public Set<Network2Subnet> getNetwork2Subnets () {
		return network2Subnets;
	}
	
	public void setNetwork2Subnets (Set<Network2Subnet> network2Subnets) {
		this.network2Subnets = network2Subnets;
	}
}
