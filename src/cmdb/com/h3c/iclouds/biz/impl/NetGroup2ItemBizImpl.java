package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.NetGroup2ItemBiz;
import com.h3c.iclouds.biz.NetGroupBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.NetGroup2ItemDao;
import com.h3c.iclouds.po.NetGroup;
import com.h3c.iclouds.po.NetGroup2Item;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by ykf7317 on 2017/9/5.
 */
@Service("netGroup2ItemBiz")
public class NetGroup2ItemBizImpl extends BaseBizImpl<NetGroup2Item> implements NetGroup2ItemBiz {
	
	@Resource
	private NetGroup2ItemDao netGroup2ItemDao;
	
	@Resource
	private NetGroupBiz netGroupBiz;
	
	@Override
	public PageModel<NetGroup2Item> findForPage (PageEntity entity) {
		return netGroup2ItemDao.findForPage(entity);
	}
	
	@Override
	public void deleteItem (NetGroup2Item netGroup2Item) {
		if (ConfigProperty.YES.equals(netGroup2Item.getIsAlone())) {
			NetGroup netGroup = netGroupBiz.findById(NetGroup.class, netGroup2Item.getStackId());
			if (StrUtils.checkParam(netGroup)) {
				netGroupBiz.delete(netGroup);
			}
		}
		netGroup2ItemDao.delete(netGroup2Item);
	}
}
