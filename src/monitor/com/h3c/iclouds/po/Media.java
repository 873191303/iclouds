package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Media implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long mediaId;
	
	private Long userId;
	
	private Long mediaTypeId;
	
	private String sendTo;
	
	private Integer active;
	
	private Integer severity;
	
	private String period;
	
	public Long getMediaId () {
		return mediaId;
	}
	
	public void setMediaId (Long mediaId) {
		this.mediaId = mediaId;
	}
	
	public Long getUserId () {
		return userId;
	}
	
	public void setUserId (Long userId) {
		this.userId = userId;
	}
	
	public Long getMediaTypeId () {
		return mediaTypeId;
	}
	
	public void setMediaTypeId (Long mediaTypeId) {
		this.mediaTypeId = mediaTypeId;
	}
	
	public String getSendTo () {
		return sendTo;
	}
	
	public void setSendTo (String sendTo) {
		this.sendTo = sendTo;
	}
	
	public Integer getActive () {
		return active;
	}
	
	public void setActive (Integer active) {
		this.active = active;
	}
	
	public Integer getSeverity () {
		return severity;
	}
	
	public void setSeverity (Integer severity) {
		this.severity = severity;
	}
	
	public String getPeriod () {
		return period;
	}
	
	public void setPeriod (String period) {
		this.period = period;
	}
}
