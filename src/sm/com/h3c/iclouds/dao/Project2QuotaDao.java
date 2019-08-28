package com.h3c.iclouds.dao;

import java.util.List;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Quota;

public interface Project2QuotaDao extends BaseDAO<Project2Quota> {

	void save(Project project);

	List<Project2Quota> get(String projectId);

	List<Project2Quota> get(String id, Project2Quota project2Quota);

}
