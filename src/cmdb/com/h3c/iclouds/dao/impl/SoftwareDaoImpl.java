package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.SoftwareDao;
import com.h3c.iclouds.po.Software;
import com.h3c.iclouds.utils.StrUtils;

@Repository("softwareDao")
public class SoftwareDaoImpl extends BaseDAOImpl<Software> implements SoftwareDao {

	@Override
	public PageModel<Software> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Software.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("scode", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("shortName", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("sname", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("remark", "%" + entity.getSearchValue() + "%")
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
			order.put("updatedDate", "desc");
		}
		return this.findForPage(Software.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
	
}
