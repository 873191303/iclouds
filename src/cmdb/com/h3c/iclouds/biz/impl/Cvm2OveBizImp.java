package com.h3c.iclouds.biz.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.Cvm2OveBiz;
import com.h3c.iclouds.dao.Cvm2OveDao;
import com.h3c.iclouds.po.Cvm2Ove;

@Service("cvm2OveBiz")
public class Cvm2OveBizImp extends BaseBizImpl<Cvm2Ove> implements Cvm2OveBiz{

	@Resource
	private Cvm2OveDao cvm2OveDao;
	
	@Override
	public List<Cvm2Ove> findTop5() {
		return cvm2OveDao.findTop5();
	}

}
