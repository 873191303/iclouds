package com.h3c.iclouds.biz.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.SoftwareBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.SoftwareDao;
import com.h3c.iclouds.po.Software;

@Service("softwareBiz")
public class SoftwareBizImpl extends BaseBizImpl<Software> implements SoftwareBiz {
	
	@Resource
	private SoftwareDao softwareDao;
	
	@Override
	public PageModel<Software> findForPage(PageEntity entity) {
		return softwareDao.findForPage(entity);
	}


}
