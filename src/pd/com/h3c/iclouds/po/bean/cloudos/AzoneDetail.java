package com.h3c.iclouds.po.bean.cloudos;

public class AzoneDetail {

	private Integer deleted;
	
	private String description;
	
	private String id;
	
	private String labelName;
	
	private String resourceType;
	
	private String virtType;
	
	private String zone;

	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getVirtType() {
		return virtType;
	}

	public void setVirtType(String virtType) {
		this.virtType = virtType;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}
	
}
