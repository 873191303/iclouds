package com.h3c.iclouds.dao;

import java.util.List;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.Resource;

public interface ResourceDao extends BaseDAO<Resource> {
	
	List<Resource> getResourceByUserId(String userId);
	
}
