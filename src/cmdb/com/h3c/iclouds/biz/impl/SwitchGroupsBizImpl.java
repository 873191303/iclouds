package com.h3c.iclouds.biz.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.SwitchGroupsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.AsmMasterDao;
import com.h3c.iclouds.dao.NetPortsLinkDao;
import com.h3c.iclouds.dao.SwitchGroupsDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.NetPortsLink;
import com.h3c.iclouds.po.SwitchGroups;
import com.h3c.iclouds.po.SwitchGroups2Items;

@Service("switchGroupsBiz")
public class SwitchGroupsBizImpl extends BaseBizImpl<SwitchGroups> implements SwitchGroupsBiz {
	
	@Resource
	private SwitchGroupsDao switchGroupsDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<SwitchGroups2Items> switchGroups2ItemsDao;
	
	@Resource
	private NetPortsLinkDao netPortsLinkDao;
	
	@Resource
	private AsmMasterDao asmMasterDao;
	
	@Override
	public void delete(SwitchGroups entity) {
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
	public PageModel<SwitchGroups> findForPage(PageEntity entity) {
		return switchGroupsDao.findForPage(entity);
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
	public void addLimit(SwitchGroups group, AsmMaster entity, String remark) {
		this.deleteLinksByMasterId(entity.getId());
		SwitchGroups2Items item = new SwitchGroups2Items();
		item.createdUser(getLoginUser());
		item.setGroupId(group.getGroupId());
		item.setStackId(group.getId());
		item.setMasterId(entity.getId());
		item.setRemark(remark);
		this.switchGroups2ItemsDao.add(item);
	}

	@Override
	public void removeLimit(String stackId, List<SwitchGroups2Items> list) {
		for (SwitchGroups2Items item : list) {
			if(!item.getStackId().equals(stackId)) {
				throw new MessageException(ResultType.asset_not_in_stack);
			}
			this.deleteLinksByMasterId(item.getMasterId());
			this.switchGroups2ItemsDao.delete(item);
		}
	}


}
