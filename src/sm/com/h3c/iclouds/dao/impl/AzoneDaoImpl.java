package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.AzoneDao;
import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/19.
 */
@Repository("azoneDao")
public class AzoneDaoImpl extends BaseDAOImpl<Azone> implements AzoneDao {

	@Override
	public PageModel<Azone> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Azone.class);

		// 查询方式
		if (!"".equals(entity.getSearchValue())) { // 模糊查询
			criteria.add(Restrictions.or(Restrictions.like("lableName", "%" + entity.getSearchValue() + "%")));
		}
		if (null != entity.getSpecialParams() && entity.getSpecialParams().length > 0) {
			criteria.add(Restrictions.in("uuid", entity.getSpecialParams()));
		}
		criteria.add(Restrictions.eq("deleted", "0"));
		Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
		return this.findForPage(Azone.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
}
