package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Acknowledge implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long acknowledgeId;
	
	private Long userId;
	
	private Long eventId;
	
	private Integer clock;
	
	private String message;
	
	public Long getAcknowledgeId () {
		return acknowledgeId;
	}
	
	public void setAcknowledgeId (Long acknowledgeId) {
		this.acknowledgeId = acknowledgeId;
	}
	
	public Long getUserId () {
		return userId;
	}
	
	public void setUserId (Long userId) {
		this.userId = userId;
	}
	
	public Long getEventId () {
		return eventId;
	}
	
	public void setEventId (Long eventId) {
		this.eventId = eventId;
	}
	
	public Integer getClock () {
		return clock;
	}
	
	public void setClock (Integer clock) {
		this.clock = clock;
	}
	
	public String getMessage () {
		return message;
	}
	
	public void setMessage (String message) {
		this.message = message;
	}
}
