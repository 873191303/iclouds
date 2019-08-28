package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.GroupDao;
import com.h3c.iclouds.po.Groups;
import com.h3c.iclouds.utils.StrUtils;

@Repository("groupDao")
public class GroupDaoImpl extends BaseDAOImpl<Groups> implements GroupDao {

	@Override
	public PageModel<Groups> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Groups.class);
		
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("groupName", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("description", "%" + entity.getSearchValue() + "%")));
		}
		
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
			if (entity.getAsSorting().equals("asc")){
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("groupName", "asc");
			order.put("updatedDate", "desc");
		}
		return this.findForPage(Groups.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
	
}
