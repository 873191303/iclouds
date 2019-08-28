package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.po.AsmMaster;

public interface AsmMasterDao extends BaseDAO<AsmMaster> {
	
	PageModel<AsmMaster> findForPage(PageEntity entity, boolean flag);
		
	PageModel<AsmMaster> without(PageEntity entity);

	int otherUseFlag(String assetType);
	
}
