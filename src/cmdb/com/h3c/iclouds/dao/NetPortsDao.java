package com.h3c.iclouds.dao;

import java.util.List;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.NetPorts;

public interface NetPortsDao extends BaseDAO<NetPorts> {
	
	List<NetPorts> findPortByStack(String stackId, String type);

}
