package com.h3c.iclouds.po;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.common.ConfigProperty;
import com.wordnik.swagger.annotations.ApiModelProperty;
/**
 * 
 * By 
 *
 */
public class Network2Subnet extends BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5001706103462921377L;

	@ApiModelProperty(value = "网段ID")
	private String id;

	@ApiModelProperty(value = "网络ID")
	
	private String networkId;

	@ApiModelProperty(value = "起始IP")
	
	private String startIp;
	@ApiModelProperty(value = "结束IP")
	
	private String endIp;

	@ApiModelProperty(value = "创建时间")
	private Date createdAt;
	
	
	@ApiModelProperty(value = "修改时间")
	private Date updatedAt;

	@ApiModelProperty(value = "删除时间")
	private Date deletedAt;

	@ApiModelProperty(value = "删除标志")

	private Integer deleted;
	
	public Network2Subnet() {
		// TODO Auto-generated constructor stub
	}
	
	public Network2Subnet(String startIp,String endIp,String networkId) {
		this.endIp=endIp;
		this.startIp=startIp;
		this.networkId=networkId;
		id=UUID.randomUUID().toString();
		deleted=0;
		createDate();
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
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
	

	

}
