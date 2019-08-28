package com.h3c.iclouds.po.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yKF7317 on 2017/5/2.
 */
public class EiUserBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String userId;
	
	private String loginName;//登录名称
	
	private String displayName;//显示名称
	
	private String email;//email
	
	private Integer space;//用户配额空间:默认5G
	
	private Integer type;//用户所属类似:默认1-本地用户
	
	private String password;//密码
	
	private Boolean pwdControl;//密码管控
	
	private List<String> depIds;//部门is
	
	private List<String> depNames;//部门名称
	
	private Integer csfLevel;//用户密级
	
	private Integer priority;//用户权重
	
	private String siteId;//用户站点信息
	
	public String getUserId () {
		return userId;
	}
	
	public void setUserId (String userId) {
		this.userId = userId;
	}
	
	public String getLoginName () {
		return loginName;
	}
	
	public void setLoginName (String loginName) {
		this.loginName = loginName;
	}
	
	public String getDisplayName () {
		return displayName;
	}
	
	public void setDisplayName (String displayName) {
		this.displayName = displayName;
	}
	
	public String getEmail () {
		return email;
	}
	
	public void setEmail (String email) {
		this.email = email;
	}
	
	public Integer getSpace () {
		return space;
	}
	
	public void setSpace (Integer space) {
		this.space = space;
	}
	
	public Integer getType () {
		return type;
	}
	
	public void setType (Integer type) {
		this.type = type;
	}
	
	public String getPassword () {
		return password;
	}
	
	public void setPassword (String password) {
		this.password = password;
	}
	
	public Boolean getPwdControl () {
		return pwdControl;
	}
	
	public void setPwdControl (Boolean pwdControl) {
		this.pwdControl = pwdControl;
	}
	
	public List<String> getDepIds () {
		return depIds;
	}
	
	public void setDepIds (List<String> depIds) {
		this.depIds = depIds;
	}
	
	public List<String> getDepNames () {
		return depNames;
	}
	
	public void setDepNames (List<String> depNames) {
		this.depNames = depNames;
	}
	
	public Integer getCsfLevel () {
		return csfLevel;
	}
	
	public void setCsfLevel (Integer csfLevel) {
		this.csfLevel = csfLevel;
	}
	
	public Integer getPriority () {
		return priority;
	}
	
	public void setPriority (Integer priority) {
		this.priority = priority;
	}
	
	public String getSiteId () {
		return siteId;
	}
	
	public void setSiteId (String siteId) {
		this.siteId = siteId;
	}
}
