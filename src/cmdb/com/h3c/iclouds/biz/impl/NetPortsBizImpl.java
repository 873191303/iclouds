package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.NetPortsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.IpRelationDao;
import com.h3c.iclouds.dao.NetPortsDao;
import com.h3c.iclouds.po.IpRelation;
import com.h3c.iclouds.po.NetPorts;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("netPortsBiz")
public class NetPortsBizImpl extends BaseBizImpl<NetPorts> implements NetPortsBiz {
	
	@Resource
	private NetPortsDao netPortsDao;

	@Resource
	private IpRelationDao ipRelationDao;

	@Override
	public PageModel<NetPorts> findForPage(PageEntity entity) {
		return netPortsDao.findForPage(entity);
	}

	@Override
	public List<NetPorts> findPortByStack(String stackId, String type) {
		return this.netPortsDao.findPortByStack(stackId, type);
	}

	@Override
	public void deleteNetports(NetPorts netPorts) {
		this.netPortsDao.delete(netPorts);
		List<IpRelation> ipRelations = ipRelationDao.findByPropertyName(IpRelation.class, "ncid", netPorts.getId());
		ipRelations.forEach(ipRelation -> {
			ipRelation.setNcid(null);
			ipRelation.setAssetId(null);
			this.ipRelationDao.update(ipRelation);
		});
	}
}
