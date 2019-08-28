package com.h3c.iclouds.po.bean.nova;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Server implements Serializable{

	
	private static final long serialVersionUID = -3149643770941275337L;
	
	private String name;
	
	private String imageRef;
	
	private String flavorRef;
	
	private Integer max_count;
	
	private Integer min_count;
	
	private List<Map<String, String>> networks;
	
	private List<Map<String, String>> security_groups;
	
	private String availability_zone;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageRef() {
		return imageRef;
	}

	public void setImageRef(String imageRef) {
		this.imageRef = imageRef;
	}

	public String getFlavorRef() {
		return flavorRef;
	}

	public void setFlavorRef(String flavorRef) {
		this.flavorRef = flavorRef;
	}

	public Integer getMax_count() {
		return max_count;
	}

	public void setMax_count(Integer max_count) {
		this.max_count = max_count;
	}

	public Integer getMin_count() {
		return min_count;
	}

	public void setMin_count(Integer min_count) {
		this.min_count = min_count;
	}

	public List<Map<String, String>> getNetworks() {
		return networks;
	}

	public void setNetworks(List<Map<String, String>> networks) {
		this.networks = networks;
	}

	public List<Map<String, String>> getSecurity_groups() {
		return security_groups;
	}

	public void setSecurity_groups(List<Map<String, String>> security_groups) {
		this.security_groups = security_groups;
	}

	public String getAvailability_zone() {
		return availability_zone;
	}

	public void setAvailability_zone(String availability_zone) {
		this.availability_zone = availability_zone;
	}
	

}
