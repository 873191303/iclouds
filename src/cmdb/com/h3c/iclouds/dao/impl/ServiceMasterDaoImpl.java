package com.h3c.iclouds.dao.impl;

import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.ServiceMasterDao;
import com.h3c.iclouds.po.ServiceMaster;

@Repository("serviceMasterDao")
public class ServiceMasterDaoImpl extends BaseDAOImpl<ServiceMaster> implements ServiceMasterDao{

}
