package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class OpMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long operationId;
	
	private Integer defaultMsg;
	
	private String subject;
	
	private String message;
	
	private Long mediaTypeId;
	
	public Long getOperationId () {
		return operationId;
	}
	
	public void setOperationId (Long operationId) {
		this.operationId = operationId;
	}
	
	public Integer getDefaultMsg () {
		return defaultMsg;
	}
	
	public void setDefaultMsg (Integer defaultMsg) {
		this.defaultMsg = defaultMsg;
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
	
	public Long getMediaTypeId () {
		return mediaTypeId;
	}
	
	public void setMediaTypeId (Long mediaTypeId) {
		this.mediaTypeId = mediaTypeId;
	}
}
