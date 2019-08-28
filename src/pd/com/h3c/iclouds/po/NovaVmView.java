package com.h3c.iclouds.po;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yKF7317 on 2017/6/15.
 */
public class NovaVmView implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String uuid;
	
	private String vmstate;
	
	private Integer powerstate;
	
	private String hostname;
	
	private Date createdate;
	
	private Date updatedDate;
	
	private String userId;
	
	private String manageIp;
	
	private String projectId;
	
	private String ipAddress;
	
	private String privateIp;
	
	private String name;
	
	private String cidr;
	
	private String projectname;
	
	private String owner;
	
	private String publicIp;
	
	private String monitorId;
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	public String getUuid () {
		return uuid;
	}
	
	public void setUuid (String uuid) {
		this.uuid = uuid;
	}
	
	public String getVmstate () {
		return vmstate;
	}
	
	public void setVmstate (String vmstate) {
		this.vmstate = vmstate;
	}
	
	public Integer getPowerstate () {
		return powerstate;
	}
	
	public void setPowerstate (Integer powerstate) {
		this.powerstate = powerstate;
	}
	
	public String getHostname () {
		return hostname;
	}
	
	public void setHostname (String hostname) {
		this.hostname = hostname;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getCreatedate () {
		return createdate;
	}
	
	public void setCreatedate (Date createdate) {
		this.createdate = createdate;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getUpdatedDate () {
		return updatedDate;
	}
	
	public void setUpdatedDate (Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	public String getPrivateIp () {
		this.privateIp = this.ipAddress;
		if(this.privateIp == null) {
			return null;
		}
		this.privateIp = privateIp.substring(0, privateIp.length() - 1).replace("F", ",");
		if (privateIp.equals(manageIp)) {
			privateIp = null;
		} else {
			if (privateIp.contains(manageIp + ",")) {
				privateIp = privateIp.replace(manageIp + ",", "");
			} else if (privateIp.contains("," + manageIp)) {
				privateIp = privateIp.replace("," + manageIp, "");
			}
		}
		return privateIp;
	}
	
	public void setPrivateIp (String privateIp) {
		this.privateIp = privateIp;
	}
	
	public String getIpAddress () {
		return ipAddress;
	}
	
	public void setIpAddress (String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getCidr () {
		return cidr;
	}
	
	public void setCidr (String cidr) {
		this.cidr = cidr;
	}
	
	public String getProjectname () {
		return projectname;
	}
	
	public void setProjectname (String projectname) {
		this.projectname = projectname;
	}
	
	public String getOwner () {
		return owner;
	}
	
	public void setOwner (String owner) {
		this.owner = owner;
	}
	
	public String getUserId () {
		return userId;
	}
	
	public void setUserId (String userId) {
		this.userId = userId;
	}
	
	public String getManageIp () {
		return manageIp;
	}
	
	public void setManageIp (String manageIp) {
		this.manageIp = manageIp;
	}
	
	public String getProjectId () {
		return projectId;
	}
	
	public void setProjectId (String projectId) {
		this.projectId = projectId;
	}
	
	public String getPublicIp () {
		return publicIp;
	}
	
	public void setPublicIp (String publicIp) {
		this.publicIp = publicIp;
	}
	
	public String getMonitorId () {
		return monitorId;
	}
	
	public void setMonitorId (String monitorId) {
		this.monitorId = monitorId;
	}
}
