package com.h3c.iclouds.dao.impl;

import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.AppRelationsDao;
import com.h3c.iclouds.po.AppRelations;

@Repository("appRelationsDao")
public class AppRelationsDaoImpl extends BaseDAOImpl<AppRelations> implements AppRelationsDao {

}
