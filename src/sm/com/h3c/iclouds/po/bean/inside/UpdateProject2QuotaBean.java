package com.h3c.iclouds.po.bean.inside;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.h3c.iclouds.po.bean.BaseBean;
import com.h3c.iclouds.po.Project2Quota;
import com.h3c.iclouds.validate.ValidatorUtils;

public class UpdateProject2QuotaBean extends BaseBean implements Serializable {

	
	private static final long serialVersionUID = -6158893314969278413L;

	private String projectId;

	private List<Project2Quota> quotas;

	public List<Project2Quota> getQuotas() {
		return quotas;
	}

	public void setQuotas(List<Project2Quota> quotas) {
		this.quotas = quotas;
	}

	public Map<String, String> getValidatorMap() {
		for (Project2Quota project2Quota : quotas) {
			addValidMap(ValidatorUtils.validator(project2Quota));
		}
		return validatorMap;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public  static Map<String, Integer>  bean2Map(List<Project2Quota> beans) {
		Map<String, Integer> map=new HashMap<String, Integer>();
		for (Project2Quota project2Quota : beans) {
			map.put(project2Quota.getClassCode(), project2Quota.getHardLimit());
		}
		return  map;
	}
}
