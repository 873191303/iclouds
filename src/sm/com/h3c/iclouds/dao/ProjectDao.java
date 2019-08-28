package com.h3c.iclouds.dao;

import java.util.List;
import java.util.Map;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.Project;

/**
 * Created by yKF7317 on 2016/11/17.
 */
public interface ProjectDao extends BaseDAO<Project> {

	Project getExistProject(String  projectId);

	List<Project> getExistInProject();
	
	List<Map<String, Object>> findMacAddressByProjectId(String projectId);

	List<Project> getChildParent(String tenantId);
	
	int monthCount();
}
