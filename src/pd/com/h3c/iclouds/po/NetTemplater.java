package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/6.
 */
public class NetTemplater extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String tenantId;
	
	private Long vsId;
	
	private String vsName;
	
	private String deviceModel;
	
	private Short isKernelAccelerated;
	
	private String mode;
	
	private Long profileId;
	
	private String profileName;
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}
	
	public Long getVsId () {
		return vsId;
	}
	
	public void setVsId (Long vsId) {
		this.vsId = vsId;
	}
	
	public String getVsName () {
		return vsName;
	}
	
	public void setVsName (String vsName) {
		this.vsName = vsName;
	}
	
	public String getDeviceModel () {
		return deviceModel;
	}
	
	public void setDeviceModel (String deviceModel) {
		this.deviceModel = deviceModel;
	}
	
	public Short getIsKernelAccelerated () {
		return isKernelAccelerated;
	}
	
	public void setIsKernelAccelerated (Short isKernelAccelerated) {
		this.isKernelAccelerated = isKernelAccelerated;
	}
	
	public String getMode () {
		return mode;
	}
	
	public void setMode (String mode) {
		this.mode = mode;
	}
	
	public long getProfileId () {
		return profileId;
	}
	
	public void setProfileId (long profileId) {
		this.profileId = profileId;
	}
	
	public String getProfileName () {
		return profileName;
	}
	
	public void setProfileName (String profileName) {
		this.profileName = profileName;
	}
}
