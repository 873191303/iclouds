package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.VmTopDao;
import com.h3c.iclouds.po.VmTop;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ykf7317 on 2017/8/25.
 */
@Repository("vmTopDao")
public class VmTopDaoImpl extends BaseDAOImpl<VmTop> implements VmTopDao {
	
	@Override
	public List<VmTop> findTop (String type, int size) {
		Criteria criteria = getSession().createCriteria(VmTop.class);
		if (!this.getSessionBean().getSuperUser()) {
			if (!this.getSessionBean().getSuperRole()) {
				criteria.add(Restrictions.eq("owner", this.getSessionBean().getUserId()));
			} else {
				criteria.add(Restrictions.eq("projectid", this.getSessionBean().getProjectId()));
			}
		}
		if (type.equals("cpu")) {
			criteria.add(Restrictions.isNotNull("cpurate"));
			criteria.addOrder(Order.desc("cpurate"));
		} else {
			criteria.add(Restrictions.isNotNull("memrate"));
			criteria.addOrder(Order.desc("memrate"));
		}
		criteria.setMaxResults(size);
		return criteria.list();
	}
	
}
