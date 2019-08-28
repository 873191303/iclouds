package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.NetGroupDao;
import com.h3c.iclouds.po.NetGroup;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ykf7317 on 2017/9/5.
 */
@Repository("netGroupDao")
public class NetGroupDaoImpl extends BaseDAOImpl<NetGroup> implements NetGroupDao {
	
	@Override
	public PageModel<NetGroup> findForPage (PageEntity entity) {
		Criteria criteria = getSession().createCriteria(NetGroup.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 模糊查询
			criteria.add(Restrictions.or(
					Restrictions.like("stackName", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("ip", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("remark", "%" + entity.getSearchValue() + "%")
			));
		}
		if (StrUtils.checkParam(entity.getSpecialParam())) {
			criteria.add(Restrictions.eq("assType", entity.getSpecialParam()));
		}
		criteria.add(Restrictions.eq("isAlone", ConfigProperty.NO));
		Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
		return this.findForPage(NetGroup.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
}
