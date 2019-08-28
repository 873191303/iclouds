package com.h3c.iclouds.dao.impl;

import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.AppItemsDao;
import com.h3c.iclouds.po.AppItems;

@Repository("appItemsDao")
public class AppItemsDaoImpl extends BaseDAOImpl<AppItems> implements AppItemsDao{

}
