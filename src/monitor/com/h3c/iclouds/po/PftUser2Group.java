package com.h3c.iclouds.po;

import com.h3c.iclouds.base.BaseEntity;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class PftUser2Group extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long usrGrpId;
	
	private Long userId;
	
	public Long getId () {
		return id;
	}
	
	public void setId (Long id) {
		this.id = id;
	}
	
	public Long getUsrGrpId () {
		return usrGrpId;
	}
	
	public void setUsrGrpId (Long usrGrpId) {
		this.usrGrpId = usrGrpId;
	}
	
	public Long getUserId () {
		return userId;
	}
	
	public void setUserId (Long userId) {
		this.userId = userId;
	}
	
}
