package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.OperateLogsDao;
import com.h3c.iclouds.po.OperateLogs;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("operateLogsDao")
public class OperateLogsDaoImpl extends BaseDAOImpl<OperateLogs> implements OperateLogsDao {

	@SuppressWarnings("unchecked")
	@Override
	public OperateLogs findLastDateByUserId(String userId) {
		Criteria criteria = getSession().createCriteria(OperateLogs.class);
		criteria.add(Restrictions.eq("userId", userId));
		criteria.add(Restrictions.eq("result", ResultType.success.toString()));
		criteria.addOrder(Order.desc("createdDate"));
		criteria.setMaxResults(1);
		List<OperateLogs> list = criteria.list();
		return (list != null && !list.isEmpty()) ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public OperateLogs findLastRecordByUserId(String userId) {
		Criteria criteria = getSession().createCriteria(OperateLogs.class);
		criteria.add(Restrictions.eq("userId", userId));
		criteria.addOrder(Order.desc("createdDate"));
		criteria.setMaxResults(1);
		List<OperateLogs> list = criteria.list();
		return (list != null && !list.isEmpty()) ? list.get(0) : null;
	}

	@Override
	public PageModel<OperateLogs> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(OperateLogs.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 模糊查询
			criteria.add(Restrictions.or(
					Restrictions.like("remark", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("result", "%" + entity.getSearchValue() + "%"),
                    Restrictions.like("userName", "%" + entity.getSearchValue() + "%"),
                    Restrictions.like("loginName", "%" + entity.getSearchValue() + "%"),
                    Restrictions.like("logTypeName", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("resourceName", "%" + entity.getSearchValue() + "%")
			));
		}
		Map<String, Object> queryMap = entity.getQueryMap();

		if(this.getSessionBean().getSuperUser()) {
			String projectId = StrUtils.tranString(queryMap.get("projectId"));
			if (StrUtils.checkParam(projectId)) {
				criteria.add(Restrictions.eq("projectId", projectId));
			}
		} else {
			if(this.getSessionBean().getSuperRole()) {
				criteria.add(Restrictions.eq("projectId", this.getSessionBean().getProjectId()));
			} else {
				criteria.add(Restrictions.eq("userId", this.getSessionUserId()));
			}
		}
		if (entity.getSpecialParams() != null && entity.getSpecialParams().length > 0){
			criteria.add(Restrictions.in("logTypeId", entity.getSpecialParams()));
		}
		if (StrUtils.checkParam(entity.getSpecialParam())){
			criteria.add(Restrictions.eq("resourceId", entity.getSpecialParam()));
		}
		if (StrUtils.checkParam(queryMap.get("startDate"))){
			Long startTime = StrUtils.tranLong(queryMap.get("startDate"));
			Date startDate = new Date(startTime);
			criteria.add(Restrictions.ge("createdDate", startDate));
		}
		if (StrUtils.checkParam(queryMap.get("endDate"))){
			Long endTime = StrUtils.tranLong(queryMap.get("endDate"));
			Date endDate = new Date(endTime);
			criteria.add(Restrictions.le("createdDate", endDate));
		}
		Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
		return this.findForPage(OperateLogs.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

}
