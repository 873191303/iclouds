package com.h3c.iclouds.dao;

import java.util.List;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.bean.model.AzoneBean;
import com.h3c.iclouds.po.Project2Azone;

public interface Project2AzoneDao extends BaseDAO<Project2Azone> {

	List<Project2Azone> getProject2Azone(Project2Azone project2Azone);

	List<Project2Azone> getProject2Azone(String projectId);

	void save(List<AzoneBean> ids, String id);

	void update(List<AzoneBean> ids, String id);

}
