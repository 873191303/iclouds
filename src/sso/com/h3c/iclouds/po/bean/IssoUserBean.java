package com.h3c.iclouds.po.bean;

import com.h3c.iclouds.po.User;

import javax.validation.constraints.NotNull;

/**
 * Created by yKF7317 on 2017/5/17.
 */
public class IssoUserBean {
	
	private String id;
	
	@NotNull
	private String loginName;
	
	@NotNull
	private String userName;
	
	private String password;
	
	public IssoUserBean (User user) {
		this.id = user.getId();
		this.loginName = user.getLoginName();
		this.userName = user.getUserName();
		this.password = user.getPassword();
	}
	
	public IssoUserBean () {
	}
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	public String getLoginName () {
		return loginName;
	}
	
	public void setLoginName (String loginName) {
		this.loginName = loginName;
	}
	
	public String getUserName () {
		return userName;
	}
	
	public void setUserName (String userName) {
		this.userName = userName;
	}
	
	public String getPassword () {
		return password;
	}
	
	public void setPassword (String password) {
		this.password = password;
	}
}
