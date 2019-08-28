package com.h3c.iclouds.dao;

import java.util.List;
import java.util.Map;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.Network2Subnet;
import com.h3c.iclouds.po.Project2Network;

public interface Network2SubnetDao extends BaseDAO<Network2Subnet>{

	List<Map<String, String>> get(Project2Network project2Network);

}
