package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Alert implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long alertId;
	
	private Long actionId;
	
	private Long eventId;
	
	private Long userId;
	
	private Integer clock;
	
	private Long mediaTypeId;
	
	private String sendTo;
	
	private String subject;
	
	private String message;
	
	private Integer status;
	
	private Integer retries;
	
	private String error;
	
	private Integer escStep;
	
	private Integer alertType;
	
	public Long getAlertId () {
		return alertId;
	}
	
	public void setAlertId (Long alertId) {
		this.alertId = alertId;
	}
	
	public Long getActionId () {
		return actionId;
	}
	
	public void setActionId (Long actionId) {
		this.actionId = actionId;
	}
	
	public Long getEventId () {
		return eventId;
	}
	
	public void setEventId (Long eventId) {
		this.eventId = eventId;
	}
	
	public Long getUserId () {
		return userId;
	}
	
	public void setUserId (Long userId) {
		this.userId = userId;
	}
	
	public Integer getClock () {
		return clock;
	}
	
	public void setClock (Integer clock) {
		this.clock = clock;
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
	
	public String getSubject () {
		return subject;
	}
	
	public void setSubject (String subject) {
		this.subject = subject;
	}
	
	public String getMessage () {
		return message;
	}
	
	public void setMessage (String message) {
		this.message = message;
	}
	
	public Integer getStatus () {
		return status;
	}
	
	public void setStatus (Integer status) {
		this.status = status;
	}
	
	public Integer getRetries () {
		return retries;
	}
	
	public void setRetries (Integer retries) {
		this.retries = retries;
	}
	
	public String getError () {
		return error;
	}
	
	public void setError (String error) {
		this.error = error;
	}
	
	public Integer getEscStep () {
		return escStep;
	}
	
	public void setEscStep (Integer escStep) {
		this.escStep = escStep;
	}
	
	public Integer getAlertType () {
		return alertType;
	}
	
	public void setAlertType (Integer alertType) {
		this.alertType = alertType;
	}
}
