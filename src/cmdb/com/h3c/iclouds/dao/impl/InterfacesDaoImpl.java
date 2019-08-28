package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.InterfacesDao;
import com.h3c.iclouds.po.Interfaces;
import com.h3c.iclouds.utils.StrUtils;

@Repository
public class InterfacesDaoImpl extends BaseDAOImpl<Interfaces> implements InterfacesDao{

	
	@Override
	public PageModel<Interfaces> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Interfaces.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("type", "%" + entity.getSearchValue() + "%")
			));
		}
		
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
			if (entity.getAsSorting().equals("asc")){
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("updateDate", "asc");
		}
		return this.findForPage(Interfaces.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
}
