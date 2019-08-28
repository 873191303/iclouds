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
import com.h3c.iclouds.dao.Inc2ApproveLogDao;
import com.h3c.iclouds.po.inc.Inc2ApproveLog;
import com.h3c.iclouds.utils.StrUtils;

@Repository("inc2ApproveLogDao")
@Transactional
public class Inc2ApproveLogDaoImpl extends BaseDAOImpl<Inc2ApproveLog> implements Inc2ApproveLogDao {

	@Override
	public PageModel<Inc2ApproveLog> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Inc2ApproveLog.class);
		
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
		return this.findForPage(Inc2ApproveLog.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Inc2ApproveLog> findByMasterId(String masterId) {
		Criteria criteria = getSession().createCriteria(Inc2ApproveLog.class);
		// 只看到自己的申请单
		criteria.add(Restrictions.eq("reqId", masterId));
		criteria.addOrder(Order.desc("createdDate"));	// 按时间倒叙
		return criteria.list();
	}

}
