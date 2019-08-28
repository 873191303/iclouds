package com.h3c.iclouds.po.bean;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/5/3.
 */
public class EiAuthBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String userId;
	
	private String tokenId;
	
	private String expires;
	
	public String getUserId () {
		return userId;
	}
	
	public void setUserId (String userId) {
		this.userId = userId;
	}
	
	public String getTokenId () {
		return tokenId;
	}
	
	public void setTokenId (String tokenId) {
		this.tokenId = tokenId;
	}
	
	public String getExpires () {
		return expires;
	}
	
	public void setExpires (String expires) {
		this.expires = expires;
	}
}
