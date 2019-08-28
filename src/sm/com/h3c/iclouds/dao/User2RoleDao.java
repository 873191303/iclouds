package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.User2Role;

public interface User2RoleDao extends BaseDAO<User2Role> {

	void save(String userId);

}
