package com.h3c.iclouds.biz.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.InterfacesBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.InterfacesDao;
import com.h3c.iclouds.po.Interfaces;

@Service("interfacesBiz")
public class InterfacesBizImpl extends BaseBizImpl<Interfaces> implements InterfacesBiz{

	
	@Resource
	private InterfacesDao interfacesDao;

	@Override
	public PageModel<Interfaces> findForPage(PageEntity entity) {
		return interfacesDao.findForPage(entity);
	}

}
