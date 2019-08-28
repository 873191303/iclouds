package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.Cvm2OveDao;
import com.h3c.iclouds.po.Cvm2Ove;
import com.h3c.iclouds.po.Server2Ove;
import com.h3c.iclouds.utils.StrUtils;

@Repository("cvm2OveDao")
public class Cvm2OveDaoImp extends BaseDAOImpl<Cvm2Ove> implements Cvm2OveDao {
	/**
	 * 分页查询
	 * 
	 * @param entity
	 * @return
	 */
	@Override
	public PageModel<Cvm2Ove> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Server2Ove.class);
		// 查询方式
		if (!"".equals(entity.getSearchValue())) {// 根据搜索条件模糊查询
			criteria.add(Restrictions.like("cvmName", "%" + entity.getSearchValue() + "%"));
		}
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())) {
			if (entity.getAsSorting().equals("asc")) {
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		}
		return this.findForPage(Cvm2Ove.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Cvm2Ove> findTop5() {
		Criteria criteria = getSession().createCriteria(Cvm2Ove.class);
		return criteria.setFirstResult(0).setMaxResults(5).addOrder(Order.desc("cpuUsage")).list();
	}
	

}
