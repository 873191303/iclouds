package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.po.PfmValue2History;

public interface PfmValue2HistoryBiz extends BaseBiz<PfmValue2History>{
	
	@SuppressWarnings("rawtypes")
	public PageModel findHistoryCondense(PageEntity entity, int condenseType);

}
