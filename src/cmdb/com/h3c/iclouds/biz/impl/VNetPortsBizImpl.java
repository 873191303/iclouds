package com.h3c.iclouds.biz.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.VNetPortsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.VNetportsDao;
import com.h3c.iclouds.po.VNetports;

@Service("vNetPortsBiz")
public class VNetPortsBizImpl extends BaseBizImpl<VNetports> implements VNetPortsBiz {

	@Resource
	private VNetportsDao vNetportsDao;
	
	@Override
	public PageModel<VNetports> findForPage(PageEntity entity) {
		return vNetportsDao.findForPage(entity);
	}
}
