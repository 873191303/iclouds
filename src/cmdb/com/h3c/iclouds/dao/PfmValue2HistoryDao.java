package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.po.PfmValue2History;

public interface PfmValue2HistoryDao extends BaseDAO<PfmValue2History>{
	
	@SuppressWarnings("rawtypes")
	PageModel findHistoryCondense(PageEntity entity, int condenseType);

}
