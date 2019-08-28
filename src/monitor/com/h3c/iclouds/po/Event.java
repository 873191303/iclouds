package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class Event implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long eventId;
	
	private Integer source;
	
	private Integer object;
	
	private Long objectId;
	
	private Integer clock;
	
	private Integer value;
	
	private Integer acknowledged;
	
	private Integer ns;
	
	public Long getEventId () {
		return eventId;
	}
	
	public void setEventId (Long eventId) {
		this.eventId = eventId;
	}
	
	public Integer getSource () {
		return source;
	}
	
	public void setSource (Integer source) {
		this.source = source;
	}
	
	public Integer getObject () {
		return object;
	}
	
	public void setObject (Integer object) {
		this.object = object;
	}
	
	public Long getObjectId () {
		return objectId;
	}
	
	public void setObjectId (Long objectId) {
		this.objectId = objectId;
	}
	
	public Integer getClock () {
		return clock;
	}
	
	public void setClock (Integer clock) {
		this.clock = clock;
	}
	
	public Integer getValue () {
		return value;
	}
	
	public void setValue (Integer value) {
		this.value = value;
	}
	
	public Integer getAcknowledged () {
		return acknowledged;
	}
	
	public void setAcknowledged (Integer acknowledged) {
		this.acknowledged = acknowledged;
	}
	
	public Integer getNs () {
		return ns;
	}
	
	public void setNs (Integer ns) {
		this.ns = ns;
	}
}
