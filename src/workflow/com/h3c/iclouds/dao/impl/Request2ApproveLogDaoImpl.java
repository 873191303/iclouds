package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.Request2ApproveLogDao;
import com.h3c.iclouds.po.business.Request2ApproveLog;
import com.h3c.iclouds.utils.StrUtils;

@Repository("request2ApproveLogDao")
@Transactional
public class Request2ApproveLogDaoImpl extends BaseDAOImpl<Request2ApproveLog> implements Request2ApproveLogDao {

	@Override
	public PageModel<Request2ApproveLog> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Request2ApproveLog.class);
		
		// 只看到自己的申请单
		criteria.add(Restrictions.eq("reqId", entity.getSpecialParam()));
		
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
			if (entity.getAsSorting().equals("asc")){
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("createdDate", "desc");
		}
		return this.findForPage(Request2ApproveLog.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Request2ApproveLog> findByMasterId(String masterId) {
		Criteria criteria = getSession().createCriteria(Request2ApproveLog.class);
		// 只看到自己的申请单
		criteria.add(Restrictions.eq("reqId", masterId));
		criteria.addOrder(Order.desc("createdDate"));	// 按时间倒叙
		return criteria.list();
	}

}
