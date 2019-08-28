package com.h3c.iclouds.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.ResourceDao;
import com.h3c.iclouds.po.Resource;

@Repository("resourceDao")
public class ResourceDaoImpl extends BaseDAOImpl<Resource> implements ResourceDao {

	@Override
	public List<Resource> getResourceByUserId(String userId) {
		String sql = this.getSession().getNamedQuery(SqlQueryProperty.QUERY_RESOURCE_BY_USERID).getQueryString();
		sql = sql.replace(":USERID", "'" + userId + "'");
		return this.list(sql);
	}

}
