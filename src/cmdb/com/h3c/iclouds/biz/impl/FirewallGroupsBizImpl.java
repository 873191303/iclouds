package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.FirewallGroupsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.AsmMasterDao;
import com.h3c.iclouds.dao.FirewallGroupsDao;
import com.h3c.iclouds.dao.NetPortsLinkDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.FirewallGroups;
import com.h3c.iclouds.po.FirewallGroups2Items;
import com.h3c.iclouds.po.NetPortsLink;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("firewallGroupsBiz")
public class FirewallGroupsBizImpl extends BaseBizImpl<FirewallGroups> implements FirewallGroupsBiz {
	
	@Resource
	private FirewallGroupsDao firewallGroupsDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<FirewallGroups2Items> firewallGroups2ItemsDao;
	
	@Resource
	private NetPortsLinkDao netPortsLinkDao;
	
	@Resource
	private AsmMasterDao asmMasterDao;
	
	@Override
	public void delete(FirewallGroups entity) {
		// 删除连接
		List<AsmMaster> list = this.asmMasterDao.findByPropertyName(AsmMaster.class, "stackId", entity.getId());
		if(list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				this.deleteLinksByMasterId(list.get(i).getId());
			}
		}
		firewallGroupsDao.delete(entity);
	}
	
	@Override
	public PageModel<FirewallGroups> findForPage(PageEntity entity) {
		return firewallGroupsDao.findForPage(entity);
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
	public void addLimit(FirewallGroups group, AsmMaster entity, String remark) {
		this.deleteLinksByMasterId(entity.getId());
		FirewallGroups2Items item = new FirewallGroups2Items();
		item.createdUser(getLoginUser());
		item.setGroupId(group.getGroupId());
		item.setStackId(group.getId());
		item.setMasterId(entity.getId());
		item.setRemark(remark);
		this.firewallGroups2ItemsDao.add(item);
	}

	@Override
	public void removeLimit(String stackId, List<FirewallGroups2Items> list) {
		for (FirewallGroups2Items item : list) {
			if(!item.getStackId().equals(stackId)) {
				throw new MessageException(ResultType.asset_not_in_stack);
			}
			this.deleteLinksByMasterId(item.getMasterId());
			this.firewallGroups2ItemsDao.delete(item);
		}
	}


}
