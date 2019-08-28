package com.h3c.iclouds.biz.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.Storage2OveBiz;
import com.h3c.iclouds.dao.Storage2OveDao;
import com.h3c.iclouds.po.Storage2Ove;

@Service("storage2OveBiz")
public class Storage2OveBizImp extends BaseBizImpl<Storage2Ove> implements Storage2OveBiz{

	@Resource
	private Storage2OveDao storage2OveDao; 
	
	@Override
	public List<Storage2Ove> findTop5() {
		return storage2OveDao.findTop5();
	}

}
