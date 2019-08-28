package com.h3c.iclouds.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.CustomDao;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.business.Custom;
import com.h3c.iclouds.utils.StrUtils;

@Repository("customDao")
@Transactional
public class CustomDaoImpl extends BaseDAOImpl<Custom> implements CustomDao {

	@Override
	public PageModel<Custom> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Custom.class);

		// 查询方式
		if (!"".equals(entity.getSearchValue())) { // 角色名称是否为空
			criteria.add(Restrictions.or(Restrictions.like("cusName", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("custIntroduction", "%" + entity.getSearchValue() + "%")));
		}
		criteria.add(Restrictions.eq("owner", this.getSessionBean().getUserId()));
		
		String status = ConfigProperty.YES;
		Map<String, Object> queryMap = entity.getQueryMap();
		if(StrUtils.checkParam(queryMap)) {
			if(queryMap.containsKey("status")) {
				// 只看到自己的申请单
				status = StrUtils.tranString(queryMap.containsKey("status"));
			}
			// 联系人数量不为0的参数
			if(queryMap.containsKey("ctcount_") && "0".equals(StrUtils.tranString(queryMap.get("ctcount_")))) {
				criteria.add(Restrictions.ne("contactCount", 0));
			}
		}
		
		if(!status.equals("-1")) {
			criteria.add(Restrictions.eq("status", status));
		}

		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())) {
			if (entity.getAsSorting().equals("asc")) {
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("status", "asc");
			order.put("updatedDate", "desc");
			order.put("cusName", "asc");
		}
		return this.findForPage(Custom.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Custom> filterCustom(List<Project> list) {
		Criteria criteria = getSession().createCriteria(Custom.class);
		
		List<String> ids = new ArrayList<String>();
		for (Project project : list) {
			ids.add(project.getCusId());
		}
		criteria.add(Restrictions.eq("status","0"));
		criteria.add(Restrictions.not(Restrictions.in("id", ids)));
		return criteria.list();
		

	}

}
