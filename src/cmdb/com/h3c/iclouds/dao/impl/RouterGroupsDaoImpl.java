package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.RouterGroupsDao;
import com.h3c.iclouds.po.RouterGroups;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository("routerGroupsDao")
public class RouterGroupsDaoImpl extends BaseDAOImpl<RouterGroups> implements RouterGroupsDao {

	@Override
	public PageModel<RouterGroups> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(RouterGroups.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("stackName", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("remark", "%" + entity.getSearchValue() + "%")
			));
		}
		
		Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
		return this.findForPage(RouterGroups.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
	
}
