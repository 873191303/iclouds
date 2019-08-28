package com.h3c.iclouds.dao;

import java.util.Map;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.po.inc.IncMaster;

public interface IncMasterDao extends BaseDAO<IncMaster> {

	PageModel<Map<String, Object>> findCompleteForPage(PageEntity entity);

	PageModel<IncMaster> findForPageByApprover(PageEntity entity);
	
	String findLastIncNo();
	
	int count (String type);
	
}
