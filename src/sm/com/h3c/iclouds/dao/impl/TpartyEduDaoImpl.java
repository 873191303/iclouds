package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.TpartyEduDao;
import com.h3c.iclouds.po.TpartyEdu;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/19.
 */
@Repository("tpartyEduDao")
public class TpartyEduDaoImpl extends BaseDAOImpl<TpartyEdu> implements TpartyEduDao {

	@Override
	public PageModel<TpartyEdu> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(TpartyEdu.class);
		// 查询方式
		if (!"".equals(entity.getSearchValue())) { // 模糊查询
			criteria.add(Restrictions.or(
					Restrictions.like("eduCode", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("eduName", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("status", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("remark", "%" + entity.getSearchValue() + "%")
					));
		}
		Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
		return this.findForPage(TpartyEdu.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
}
