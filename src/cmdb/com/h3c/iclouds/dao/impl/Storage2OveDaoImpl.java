package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.Storage2OveDao;
import com.h3c.iclouds.po.Storage2Ove;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * cvk容量
 * Created by yKF7317 on 2016/11/9.
 */
@Repository("storage2OveDao")
public class Storage2OveDaoImpl extends BaseDAOImpl<Storage2Ove> implements Storage2OveDao {
   
	@Override
	public List<Storage2Ove> storageTopList () {
		Criteria criteria = getSession().createCriteria(Storage2Ove.class);
		criteria.setMaxResults(5);
		criteria.addOrder(Order.desc("capaOverflow"));
		return criteria.list();
	}
	
	@Override
	public List<Storage2Ove> findTop5() {
		Criteria criteria = getSession().createCriteria(Storage2Ove.class);
		criteria.setMaxResults(5);
		criteria.addOrder(Order.desc("capaOverflow"));
		return criteria.list();
	}
}
