package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/6.
 */
public class SpePort extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String hostId;
	
	private String mac;
	
	private String ip;
	
	private String uuid;
	
	private String monitorId;
	
	private String portId;
	
	private String tenantId;

	private Integer port;
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	public String getHostId () {
		return hostId;
	}
	
	public void setHostId (String hostId) {
		this.hostId = hostId;
	}
	
	public String getMac () {
		return mac;
	}
	
	public void setMac (String mac) {
		this.mac = mac;
	}
	
	public String getIp () {
		return ip;
	}
	
	public void setIp (String ip) {
		this.ip = ip;
	}
	
	public String getUuid () {
		return uuid;
	}
	
	public void setUuid (String uuid) {
		this.uuid = uuid;
	}
	
	public String getMonitorId () {
		return monitorId;
	}
	
	public void setMonitorId (String monitorId) {
		this.monitorId = monitorId;
	}
	
	public String getPortId () {
		return portId;
	}
	
	public void setPortId (String portId) {
		this.portId = portId;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
}
