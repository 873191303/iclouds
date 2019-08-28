package com.h3c.iclouds.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.NetPortsLinkDao;
import com.h3c.iclouds.po.NetPortsLink;

@Repository("netPortsLinkDao")
public class NetPortsLinkDaoImpl extends BaseDAOImpl<NetPortsLink> implements NetPortsLinkDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<NetPortsLink> findByNetPortId(String netPortId) {
		Criteria criteria = getSession().createCriteria(NetPortsLink.class);
		criteria.add(Restrictions.or(Restrictions.eq("trunkTo", netPortId), Restrictions.eq("accessTo", netPortId)));
		return criteria.list();
	}

}
