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
import com.h3c.iclouds.dao.WorkRoleDao;
import com.h3c.iclouds.po.WorkRole;
import com.h3c.iclouds.utils.StrUtils;

@Repository("workRoleDao")
@Transactional
public class WorkRoleDaoImpl extends BaseDAOImpl<WorkRole> implements WorkRoleDao {

	@Override
	public PageModel<WorkRole> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(WorkRole.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
					Restrictions.like("workFlowName", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("remark", "%" + entity.getSearchValue() + "%"), 
					Restrictions.like("roleName", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("roleKey", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("processName", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("processSegment", "%" + entity.getSearchValue() + "%")
			));
		}

		if(entity.getSpecialParams() != null && entity.getSpecialParams().length > 0) {
			String workFlowId = entity.getSpecialParams()[0];
			if(workFlowId != null && workFlowId.length() > 0) {
				criteria.add(Restrictions.eq("workFlowId", workFlowId));
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
			order.put("workFlowName", "asc");
			order.put("updatedDate", "desc");
		}
		return this.findForPage(WorkRole.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

}
