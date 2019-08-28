package com.h3c.iclouds.po.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.po.User;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yKF7317 on 2017/5/17.
 */
public class AuthUserBean extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String projectId;
	
	private String projectName;
	
	private String deptId;
	
	private String deptName;
	
	@NotNull
	private String loginName;
	
	@NotNull
	private String userName;
	
	private String isAdmin;
	
	private String idCard;
	
	private String password;
	
	private String remark;
	
	private String email;
	
	private String telephone;
	
	private String status;
	
	private Date effectiveDate;
	
	private Date expireDate;
	
	@NotNull
	private String authType;
	
	private String comeFrom;
	
	private String passwordCode;
	
	private List<String> roleIds = new ArrayList<>();
	
	public AuthUserBean (User user) {
		this.loginName = user.getLoginName();
		this.projectId = user.getProjectId();
		this.projectName = user.getProjectName();
		this.deptId = user.getDeptId();
		this.deptName = user.getDeptName();
		this.userName = user.getUserName();
		this.isAdmin = user.getIsAdmin();
		this.idCard = user.getIdCard();
		this.passwordCode = user.getPassword();
		this.remark = user.getRemark();
		this.email = user.getEmail();
		this.telephone = user.getTelephone();
		this.status = user.getStatus();
		this.effectiveDate = user.getEffectiveDate();
		this.expireDate = user.getExpireDate();
		this.authType = "0";
	}
	
	public AuthUserBean () {
	}
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	@JsonIgnore
	public String getPassword () {
		return password;
	}
	
	public void setPassword (String password) {
		this.password = password;
	}
	
	public String getRemark () {
		return remark;
	}
	
	public void setRemark (String remark) {
		this.remark = remark;
	}
	
	public String getEmail () {
		return email;
	}
	
	public void setEmail (String email) {
		this.email = email;
	}
	
	public String getTelephone () {
		return telephone;
	}
	
	public void setTelephone (String telephone) {
		this.telephone = telephone;
	}
	
	public String getStatus () {
		return status;
	}
	
	public void setStatus (String status) {
		this.status = status;
	}
	
	public String getProjectId () {
		return projectId;
	}
	
	public void setProjectId (String projectId) {
		this.projectId = projectId;
	}
	
	public String getProjectName () {
		return projectName;
	}
	
	public void setProjectName (String projectName) {
		this.projectName = projectName;
	}
	
	public String getDeptId () {
		return deptId;
	}
	
	public void setDeptId (String deptId) {
		this.deptId = deptId;
	}
	
	public String getDeptName () {
		return deptName;
	}
	
	public void setDeptName (String deptName) {
		this.deptName = deptName;
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
	
	public String getIsAdmin () {
		return isAdmin;
	}
	
	public void setIsAdmin (String isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	public String getIdCard () {
		return idCard;
	}
	
	public void setIdCard (String idCard) {
		this.idCard = idCard;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getEffectiveDate () {
		return effectiveDate;
	}
	
	public void setEffectiveDate (Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getExpireDate () {
		return expireDate;
	}
	
	public void setExpireDate (Date expireDate) {
		this.expireDate = expireDate;
	}
	
	public String getAuthType () {
		return authType;
	}
	
	public void setAuthType (String authType) {
		this.authType = authType;
	}
	
	public String getComeFrom () {
		return comeFrom;
	}
	
	public void setComeFrom (String comeFrom) {
		this.comeFrom = comeFrom;
	}
	
	public List<String> getRoleIds () {
		return roleIds;
	}
	
	public void setRoleIds (List<String> roleIds) {
		this.roleIds = roleIds;
	}
	
	public String getPasswordCode () {
		return passwordCode;
	}
	
	public void setPasswordCode (String passwordCode) {
		this.passwordCode = passwordCode;
	}
}
