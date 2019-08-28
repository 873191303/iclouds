package com.h3c.iclouds.biz.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.ExtColumnsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.ExtColumnsDao;
import com.h3c.iclouds.po.ExtColumns;

@Service("extColumnsBiz")
public class ExtColumnsBizImpl extends BaseBizImpl<ExtColumns> implements ExtColumnsBiz {
	
	@Resource
	private ExtColumnsDao extColumnsDao;

	@Override
	public PageModel<ExtColumns> findForPage(PageEntity entity) {
		return extColumnsDao.findForPage(entity);
	}
	
}
