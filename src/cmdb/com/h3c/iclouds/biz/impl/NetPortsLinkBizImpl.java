package com.h3c.iclouds.biz.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.NetPortsLinkBiz;
import com.h3c.iclouds.dao.NetPortsLinkDao;
import com.h3c.iclouds.po.NetPortsLink;

@Service("netPortsLinkBiz")
public class NetPortsLinkBizImpl extends BaseBizImpl<NetPortsLink> implements NetPortsLinkBiz {
	
	@Resource
	private NetPortsLinkDao netPortsLinkDao;

	@Override
	public List<NetPortsLink> findByNetPortId(String netPortId) {
		return netPortsLinkDao.findByNetPortId(netPortId);
	}

	@Override
	public void saveLink(String operationId, NetPortsLink entity) {
		List<NetPortsLink> list = findByNetPortId(operationId);
		this.netPortsLinkDao.delete(list);
		this.add(entity);
	}

}
