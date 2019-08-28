package com.h3c.iclouds.po.bean.outside;

import com.h3c.iclouds.po.bean.BaseBean;
import com.h3c.iclouds.po.bean.model.NetworksBean;
import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Quota;
import com.h3c.iclouds.validate.ValidatorUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ProjectDetailBean extends BaseBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6165681624912104249L;

	private Project project;

	private boolean root;

	private List<Project2Quota> computer_resource;

	private List<Project2Quota> storage_resource;

	private List<Project2Quota> network_resource;
	
	private List<Project2Quota> third_resource;

	private List<Azone> azones;

	private List<NetworksBean> networks;

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<NetworksBean> getNetworks() {
		return networks;
	}

	public void setNetworks(List<NetworksBean> networks) {
		this.networks = networks;
	}

	@Override
	public Map<String, String> getValidatorMap() {
		for (Project2Quota project2Quota : computer_resource) {
			addValidMap(ValidatorUtils.validator(project2Quota));
		}
		for (Project2Quota project2Quota : storage_resource) {
			addValidMap(ValidatorUtils.validator(project2Quota));
		}
		for (Project2Quota project2Quota : network_resource) {
			addValidMap(ValidatorUtils.validator(project2Quota));
		}
		return validatorMap;
	}

	public List<Azone> getAzones() {
		return azones;
	}

	public void setAzones(List<Azone> azones) {
		this.azones = azones;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}
	
	public List<Project2Quota> getComputer_resource() {
		return computer_resource;
	}

	public void setComputer_resource(List<Project2Quota> computer_resource) {
		this.computer_resource = computer_resource;
	}

	public List<Project2Quota> getStorage_resource() {
		return storage_resource;
	}

	public void setStorage_resource(List<Project2Quota> storage_resource) {
		this.storage_resource = storage_resource;
	}

	public List<Project2Quota> getNetwork_resource() {
		return network_resource;
	}

	public void setNetwork_resource(List<Project2Quota> network_resource) {
		this.network_resource = network_resource;
	}
	
	public List<Project2Quota> getThird_resource () {
		return third_resource;
	}
	
	public void setThird_resource (List<Project2Quota> third_resource) {
		this.third_resource = third_resource;
	}
}
