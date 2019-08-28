package com.h3c.iclouds.po;

import java.io.Serializable;

/**
 * Created by yKF7317 on 2017/6/29.
 */
public class PftUser implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long userId;
	
	private String alias;
	
	private String name;
	
	private String surName;
	
	private String passwd;
	
	private String url;
	
	private Integer autoLogin;
	
	private Integer autoLogout;
	
	private String lang;
	
	private Integer refresh;
	
	private Integer type;
	
	private String theme;
	
	private Integer attemptFailed;
	
	private String attemptIp;
	
	private Integer attemptClock;
	
	private Integer rowsPerPage;
	
	public Long getUserId () {
		return userId;
	}
	
	public void setUserId (Long userId) {
		this.userId = userId;
	}
	
	public String getAlias () {
		return alias;
	}
	
	public void setAlias (String alias) {
		this.alias = alias;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getSurName () {
		return surName;
	}
	
	public void setSurName (String surName) {
		this.surName = surName;
	}
	
	public String getPasswd () {
		return passwd;
	}
	
	public void setPasswd (String passwd) {
		this.passwd = passwd;
	}
	
	public String getUrl () {
		return url;
	}
	
	public void setUrl (String url) {
		this.url = url;
	}
	
	public Integer getAutoLogin () {
		return autoLogin;
	}
	
	public void setAutoLogin (Integer autoLogin) {
		this.autoLogin = autoLogin;
	}
	
	public Integer getAutoLogout () {
		return autoLogout;
	}
	
	public void setAutoLogout (Integer autoLogout) {
		this.autoLogout = autoLogout;
	}
	
	public String getLang () {
		return lang;
	}
	
	public void setLang (String lang) {
		this.lang = lang;
	}
	
	public Integer getRefresh () {
		return refresh;
	}
	
	public void setRefresh (Integer refresh) {
		this.refresh = refresh;
	}
	
	public Integer getType () {
		return type;
	}
	
	public void setType (Integer type) {
		this.type = type;
	}
	
	public String getTheme () {
		return theme;
	}
	
	public void setTheme (String theme) {
		this.theme = theme;
	}
	
	public Integer getAttemptFailed () {
		return attemptFailed;
	}
	
	public void setAttemptFailed (Integer attemptFailed) {
		this.attemptFailed = attemptFailed;
	}
	
	public String getAttemptIp () {
		return attemptIp;
	}
	
	public void setAttemptIp (String attemptIp) {
		if (null == attemptIp) {
			this.attemptIp = "";
		}
		this.attemptIp = attemptIp;
	}
	
	public Integer getAttemptClock () {
		return attemptClock;
	}
	
	public void setAttemptClock (Integer attemptClock) {
		this.attemptClock = attemptClock;
	}
	
	public Integer getRowsPerPage () {
		return rowsPerPage;
	}
	
	public void setRowsPerPage (Integer rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}
}
