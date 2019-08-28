package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.TaskLogDao;
import com.h3c.iclouds.po.TaskLogView;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/4/20.
 */
@Repository("taskLogDao")
public class TaskLogDaoImpl extends BaseDAOImpl<TaskLogView> implements TaskLogDao {
	
	@Override
	public PageModel<TaskLogView> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(TaskLogView.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 模糊查询
			criteria.add(Restrictions.or(
					Restrictions.like("projectName", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("busType", "%" + entity.getSearchValue() + "%")
			));
		}
		
		if(!this.getSessionBean().getSuperUser()) {
			// 租户管理员只允许查看自己租户下的内容
			if(this.getSessionBean().getSuperRole()) {
				criteria.add(Restrictions.eq("projectId", this.getProjectId()));
			} else {
				criteria.add(Restrictions.eq("createdBy", this.getSessionUserId()));	
			}
		}
		
		Map<String, String> order = new HashMap<>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
			if (entity.getAsSorting().equals("asc")){
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("pushTime", "desc");
		}
		return this.findForPage(TaskLogView.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
	
}
