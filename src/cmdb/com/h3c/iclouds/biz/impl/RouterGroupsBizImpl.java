package com.h3c.iclouds.biz.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.RouterGroupsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.AsmMasterDao;
import com.h3c.iclouds.dao.NetPortsLinkDao;
import com.h3c.iclouds.dao.RouterGroupsDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.NetPortsLink;
import com.h3c.iclouds.po.RouterGroups;
import com.h3c.iclouds.po.RouterGroups2Items;

@Service("routerGroupsBiz")
public class RouterGroupsBizImpl extends BaseBizImpl<RouterGroups> implements RouterGroupsBiz {
	
	@Resource
	private RouterGroupsDao routerGroupsDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<RouterGroups2Items> routerGroups2ItemsDao;
	
	@Resource
	private NetPortsLinkDao netPortsLinkDao;
	
	@Resource
	private AsmMasterDao asmMasterDao;
	
	@Override
	public void delete(RouterGroups entity) {
		// 删除连接
		List<AsmMaster> list = this.asmMasterDao.findByPropertyName(AsmMaster.class, "stackId", entity.getId());
		if(list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				this.deleteLinksByMasterId(list.get(i).getId());
			}
		}
		super.delete(entity);
	}
	
	@Override
	public PageModel<RouterGroups> findForPage(PageEntity entity) {
		return routerGroupsDao.findForPage(entity);
	}
	
	private void deleteLinksByMasterId(String masterId) {
		List<NetPortsLink> links = netPortsLinkDao.findByPropertyName(NetPortsLink.class, "trunkMasterId", masterId);
		if(links != null && !links.isEmpty()) {
			netPortsLinkDao.delete(links);
		}
		
		links = netPortsLinkDao.findByPropertyName(NetPortsLink.class, "accessMasterId", masterId);
		if(links != null && !links.isEmpty()) {
			netPortsLinkDao.delete(links);
		}
	}

	@Override
	public void addLimit(RouterGroups group, AsmMaster entity, String remark) {
		this.deleteLinksByMasterId(entity.getId());
		RouterGroups2Items item = new RouterGroups2Items();
		item.createdUser(getLoginUser());
		item.setGroupId(group.getGroupId());
		item.setStackId(group.getId());
		item.setMasterId(entity.getId());
		item.setRemark(remark);
		this.routerGroups2ItemsDao.add(item);
	}

	@Override
	public void removeLimit(String stackId, List<RouterGroups2Items> list) {
		for (RouterGroups2Items item : list) {
			if(!item.getStackId().equals(stackId)) {
				throw new MessageException(ResultType.asset_not_in_stack);
			}
			this.deleteLinksByMasterId(item.getMasterId());
			this.routerGroups2ItemsDao.delete(item);
		}
	}


}
