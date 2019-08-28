package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.ExtColumnsDao;
import com.h3c.iclouds.po.ExtColumns;
import com.h3c.iclouds.utils.StrUtils;

@Repository("extColumnsDao")
public class ExtColumnsDaoImpl extends BaseDAOImpl<ExtColumns> implements ExtColumnsDao {

	@Override
	public PageModel<ExtColumns> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(ExtColumns.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("xcName", "%" + entity.getSearchValue() + "%")
			));
		}
		
		if(entity.getSpecialParam() != null) {
			criteria.add(Restrictions.eq("assType", entity.getSpecialParam()));
		}
		
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
			if (entity.getAsSorting().equals("asc")){
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("seq", "asc");
		}
		return this.findForPage(ExtColumns.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
	
}
