package com.h3c.iclouds.dao;

import java.util.Map;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.po.business.RequestMaster;

public interface RequestMasterDao extends BaseDAO<RequestMaster> {
	
	PageModel<Map<String, Object>> findCompleteForPage(PageEntity entity, boolean needHistory);

	PageModel<RequestMaster> findForPageByApprover(PageEntity entity);
	
	String findLastReqCode();
	
	int count (String type);
}
