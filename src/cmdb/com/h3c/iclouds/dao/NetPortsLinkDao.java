package com.h3c.iclouds.dao;

import java.util.List;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.NetPortsLink;

public interface NetPortsLinkDao extends BaseDAO<NetPortsLink> {
	
	public List<NetPortsLink> findByNetPortId(String netPortId);

}
