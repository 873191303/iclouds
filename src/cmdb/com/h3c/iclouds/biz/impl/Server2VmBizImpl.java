package com.h3c.iclouds.biz.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.Server2VmBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.Server2VmDao;
import com.h3c.iclouds.po.Server2Vm;

import java.util.UUID;

@Service("server2VmBiz")
public class Server2VmBizImpl extends BaseBizImpl<Server2Vm> implements Server2VmBiz {

	@Resource
	private Server2VmDao server2VmDao;

	@Override
	public PageModel<Server2Vm> findForPage(PageEntity entity) {
		return server2VmDao.findForPage(entity);
	}

	@Override
	public void synAdd(Server2Vm entity) {
		entity.createdUser(this.getLoginUser());
		String id = UUID.randomUUID().toString();
		entity.setId(id);
		server2VmDao.add(entity);
	}
}
