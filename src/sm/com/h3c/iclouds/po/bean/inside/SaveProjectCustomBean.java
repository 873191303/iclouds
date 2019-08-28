package com.h3c.iclouds.po.bean.inside;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.po.bean.model.AzoneBean;
import com.h3c.iclouds.po.bean.model.TenantBean;
import com.h3c.iclouds.validate.NotNull;

public class SaveProjectCustomBean implements Serializable {

	private static final long serialVersionUID = -929498480587020475L;
	
	private TenantBean project;
	
	private List<AzoneBean> azone;
	
	@NotNull
	private String password;
	
	@NotNull
	@Pattern(regexp = "^[a-zA-Z][0-9a-zA-Z_@-]{3,31}$")
	private String userName;

	private JSONObject cloudosError;
	
	private boolean []flag;
	
	private String projectId;
	
	private String userId;
	
	private String issoUserId;
	
	public TenantBean getProject() {
		return project;
	}

	public void setProject(TenantBean project) {
		this.project = project;
	}

	public List<AzoneBean> getAzone() {
		return azone;
	}

	public void setAzone(List<AzoneBean> azone) {
		this.azone = azone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean [] getFlag() {
		return flag;
	}

	public void setFlag(boolean [] flag) {
		this.flag = flag;
	}
	@JsonIgnore
	public String getProjectId() {
		return projectId;
	}

	
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	@JsonIgnore
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@JsonIgnore
	public JSONObject getCloudosError() {
		return cloudosError;
	}

	public void setCloudosError(JSONObject cloudosError) {
		this.cloudosError = cloudosError;
	}
	
	public String getIssoUserId () {
		return issoUserId;
	}
	
	public void setIssoUserId (String issoUserId) {
		this.issoUserId = issoUserId;
	}
}
