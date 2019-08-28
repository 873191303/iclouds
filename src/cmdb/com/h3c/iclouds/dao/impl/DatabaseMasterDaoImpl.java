package com.h3c.iclouds.dao.impl;

import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.DatabaseMasterDao;
import com.h3c.iclouds.po.DatabaseMaster;

@Repository("databaseMasterDao")
public class DatabaseMasterDaoImpl extends BaseDAOImpl<DatabaseMaster> implements DatabaseMasterDao{

}
