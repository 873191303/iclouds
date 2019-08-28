package com.h3c.iclouds.biz.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.Class2ItemsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.Class2ItemsDao;
import com.h3c.iclouds.po.Class2Items;

@Service("class2ItemsBiz")
public class Class2ItemsBizImpl extends BaseBizImpl<Class2Items> implements Class2ItemsBiz {
	
	@Resource
	private Class2ItemsDao class2ItemsDao;

	@Override
	public PageModel<Class2Items> findForPage(PageEntity entity) {
		return class2ItemsDao.findForPage(entity);
	}
	
}
