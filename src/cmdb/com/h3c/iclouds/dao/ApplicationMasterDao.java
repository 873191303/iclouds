package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.po.ApplicationMaster;

public interface ApplicationMasterDao extends BaseDAO<ApplicationMaster>{
	
	public PageModel<ApplicationMaster> findForPage(PageEntity entity);

	Object get();
}
