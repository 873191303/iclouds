package com.h3c.iclouds.po.bean.nova;

import com.alibaba.fastjson.JSONObject;

public class ServerDetail {

	private String accessIPv4;
	
	private String accessIPv6;
	
	private JSONObject addresses;
	
	private String created;
	
	private JSONObject flavor;
	
	private String hostId;
	
	private String id;
	
	private JSONObject image;
	
	private JSONObject metadata;
	
	private String name;
	
	private Integer progress;
	
	private String status;
	
	private String tenant_id;
	
	private String updated;
	
	private String user_id;
	
	public String getAccessIPv4() {
		return accessIPv4;
	}

	public void setAccessIPv4(String accessIPv4) {
		this.accessIPv4 = accessIPv4;
	}

	public String getAccessIPv6() {
		return accessIPv6;
	}

	public void setAccessIPv6(String accessIPv6) {
		this.accessIPv6 = accessIPv6;
	}

	public JSONObject getAddresses() {
		return addresses;
	}

	public void setAddresses(JSONObject addresses) {
		this.addresses = addresses;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public JSONObject getFlavor() {
		return flavor;
	}

	public void setFlavor(JSONObject flavor) {
		this.flavor = flavor;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public JSONObject getImage() {
		return image;
	}

	public void setImage(JSONObject image) {
		this.image = image;
	}

	public JSONObject getMetadata() {
		return metadata;
	}

	public void setMetadata(JSONObject metadata) {
		this.metadata = metadata;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTenant_id() {
		return tenant_id;
	}

	public void setTenant_id(String tenant_id) {
		this.tenant_id = tenant_id;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
}
