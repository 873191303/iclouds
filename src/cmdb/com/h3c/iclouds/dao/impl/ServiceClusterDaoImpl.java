package com.h3c.iclouds.dao.impl;

import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.ServiceClusterDao;
import com.h3c.iclouds.po.ServiceCluster;

@Repository("serviceClusterDao")
public class ServiceClusterDaoImpl extends BaseDAOImpl<ServiceCluster> implements ServiceClusterDao{

}
