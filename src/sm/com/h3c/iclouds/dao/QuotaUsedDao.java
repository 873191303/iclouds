package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Quota;
import com.h3c.iclouds.po.QuotaUsed;

public interface QuotaUsedDao extends BaseDAO<QuotaUsed> {

	void save(Project project);

	QuotaUsed get(Project project, Project2Quota project2Quota);

	

}
