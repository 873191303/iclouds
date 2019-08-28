package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.Class2ItemsDao;
import com.h3c.iclouds.po.Class2Items;
import com.h3c.iclouds.utils.StrUtils;

@Repository("class2ItemsDao")
public class Class2ItemsDaoImpl extends BaseDAOImpl<Class2Items> implements Class2ItemsDao {

	@Override
	public PageModel<Class2Items> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Class2Items.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {//根据搜索条件模糊查询
			criteria.add(Restrictions.or(
				Restrictions.like("itemId", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("itemName", "%" + entity.getSearchValue() + "%")
			));
		}
		
		if(StrUtils.checkParam(entity.getSpecialParam())) {
			criteria.add(Restrictions.eq("resType", entity.getSpecialParam()));
		}
		
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
			if (entity.getAsSorting().equals("asc")){
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("itemId", "asc");
		}
		return this.findForPage(Class2Items.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
	
}
