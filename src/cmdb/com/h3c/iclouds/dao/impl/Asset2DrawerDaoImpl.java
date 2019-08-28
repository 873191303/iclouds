package com.h3c.iclouds.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.Asset2DrawerDao;
import com.h3c.iclouds.po.Asset2Drawer;

@Repository("asset2DrawerDao")
public class Asset2DrawerDaoImpl extends BaseDAOImpl<Asset2Drawer> implements Asset2DrawerDao {

	@SuppressWarnings("unchecked")
	@Override
	public Asset2Drawer findMaxUByDrawId(String drawId) {
		Criteria criteria = getSession().createCriteria(Asset2Drawer.class);
		criteria.add(Restrictions.eq("drawsId", drawId));
		criteria.add(Restrictions.isNotNull("unumb"));
		criteria.addOrder(Order.desc("unumb"));
		criteria.setMaxResults(1);
		List<Asset2Drawer> list = criteria.list();
		return (list != null && !list.isEmpty()) ? list.get(0) : null;
	}

}
