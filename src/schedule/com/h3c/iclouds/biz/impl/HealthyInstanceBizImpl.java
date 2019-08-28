package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.HealthyInstanceBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.HealthyInstanceDao;
import com.h3c.iclouds.po.HealthyInstance;
import com.h3c.iclouds.po.HealthyValue;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service("healthyInstanceBiz")
public class HealthyInstanceBizImpl extends BaseBizImpl<HealthyInstance> implements HealthyInstanceBiz {
	
	@Resource
	private HealthyInstanceDao healthyInstanceDao;

	@Override
	public PageModel<HealthyInstance> findForPage(PageEntity entity) {
		return healthyInstanceDao.findForPage(entity);
	}

	@Override
	public List<HealthyValue> data(String id, Date start, Date end) {
		return healthyInstanceDao.data(id, start, end);
	}
}
