package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Dservice implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long dserviceId;
	
	private Long hostId;
	
	private Integer type;
	
	private String key;
	
	private String value;
	
	private Integer port;
	
	private Integer status;
	
	private Integer lastUp;
	
	private Integer lastDown;
	
	public Long getDserviceId () {
		return dserviceId;
	}
	
	public void setDserviceId (Long dserviceId) {
		this.dserviceId = dserviceId;
	}
	
	public Long getHostId () {
		return hostId;
	}
	
	public void setHostId (Long hostId) {
		this.hostId = hostId;
	}
	
	public Integer getType () {
		return type;
	}
	
	public void setType (Integer type) {
		this.type = type;
	}
	
	public String getKey () {
		return key;
	}
	
	public void setKey (String key) {
		this.key = key;
	}
	
	public String getValue () {
		return value;
	}
	
	public void setValue (String value) {
		this.value = value;
	}
	
	public Integer getPort () {
		return port;
	}
	
	public void setPort (Integer port) {
		this.port = port;
	}
	
	public Integer getStatus () {
		return status;
	}
	
	public void setStatus (Integer status) {
		this.status = status;
	}
	
	public Integer getLastUp () {
		return lastUp;
	}
	
	public void setLastUp (Integer lastUp) {
		this.lastUp = lastUp;
	}
	
	public Integer getLastDown () {
		return lastDown;
	}
	
	public void setLastDown (Integer lastDown) {
		this.lastDown = lastDown;
	}
}
