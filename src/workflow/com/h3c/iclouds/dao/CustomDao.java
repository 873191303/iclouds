package com.h3c.iclouds.dao;

import java.util.List;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.business.Custom;

public interface CustomDao extends BaseDAO<Custom> {

	List<Custom> filterCustom(List<Project> list);
	
}
