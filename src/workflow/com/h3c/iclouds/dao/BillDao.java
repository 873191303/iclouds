package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.po.business.Bill;

/**
 * Created by yKF7317 on 2017/8/10.
 */
public interface BillDao extends BaseDAO<Bill> {
	
	String getSql(PageEntity entity);
	
}
