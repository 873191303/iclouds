package com.h3c.iclouds.dao;

import java.util.List;
import java.util.Map;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.User;

public interface UserDao extends BaseDAO<User> {
	
	List<Map<String, Object>> findAdminUsersByProjectId(String projectId);
	
}
