package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.Asset2Drawer;

public interface Asset2DrawerDao extends BaseDAO<Asset2Drawer> {
	
	public Asset2Drawer findMaxUByDrawId(String drawId);

}
