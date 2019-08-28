package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.WorkFlowDao;
import com.h3c.iclouds.po.WorkFlow;
import com.h3c.iclouds.utils.StrUtils;

@Repository("workFlowDao")
@Transactional
public class WorkFlowDaoImpl extends BaseDAOImpl<WorkFlow> implements WorkFlowDao {

	@Override
	public PageModel<WorkFlow> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(WorkFlow.class);
		
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("name", "%" + entity.getSearchValue() + "%")
			));
		}
		
		if(entity.getSpecialParams() != null && entity.getSpecialParams().length > 0) {
			String status = entity.getSpecialParams()[0];
			if(status != null && status.length() > 0) {
				criteria.add(Restrictions.eq("status", status));
			}
		}
		
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
			if (entity.getAsSorting().equals("asc")){
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("name", "asc");
			order.put("updatedDate", "desc");
		}
		return this.findForPage(WorkFlow.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

}
