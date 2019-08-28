package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.PfmValue2HistoryBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.PfmValue2HistoryDao;
import com.h3c.iclouds.po.PfmValue2History;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("pfmValue2HistoryBiz")
public class PfmValue2HistoryBizImpl extends BaseBizImpl<PfmValue2History> implements PfmValue2HistoryBiz{

    @Resource
    private PfmValue2HistoryDao pfmValue2HistoryDao;

    @Override
    public PageModel<PfmValue2History> findForPage(PageEntity entity) {
        return pfmValue2HistoryDao.findForPage(entity);
    }

	@SuppressWarnings("rawtypes")
	@Override
	public PageModel findHistoryCondense(PageEntity entity, int condenseType) {
		return pfmValue2HistoryDao.findHistoryCondense(entity, condenseType);
	}
}
