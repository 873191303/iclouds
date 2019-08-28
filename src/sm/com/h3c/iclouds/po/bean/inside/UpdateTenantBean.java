package com.h3c.iclouds.po.bean.inside;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h3c.iclouds.po.bean.model.AzoneBean;
import com.h3c.iclouds.po.bean.model.TenantBean;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Azone;

import java.io.Serializable;
import java.util.List;

public class UpdateTenantBean implements Serializable{
	
	private static final long serialVersionUID = -5095290989963304079L;
	
	private TenantBean project;
	
	private List<AzoneBean> azones;
	
	private Project tenant;
	
	private boolean []flag;
	
	private List<Project2Azone> project2Azones;
	
	public TenantBean getProject() {
		return project;
	}
	public void setProject(TenantBean project) {
		this.project = project;
	}
	
	public List<AzoneBean> getAzones() {
		return azones;
	}
	public void setAzones(List<AzoneBean> azones) {
		this.azones = azones;
	}
	@JsonIgnore
	public boolean [] getFlag() {
		return flag;
	}
	public void setFlag(boolean [] flag) {
		this.flag = flag;
	}
	public List<Project2Azone> getProject2Azones() {
		return project2Azones;
	}
	public void setProject2Azones(List<Project2Azone> project2Azones) {
		this.project2Azones = project2Azones;
	}
	@JsonIgnore
	public Project getTenant() {
		return tenant;
	}
	public void setTenant(Project tenant) {
		this.tenant = tenant;
	}
	
}
