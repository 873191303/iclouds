package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.NetGroup2ItemDao;
import com.h3c.iclouds.po.NetGroup2Item;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ykf7317 on 2017/9/5.
 */
@Repository("group2ItemDao")
public class NetGroup2ItemDaoImpl extends BaseDAOImpl<NetGroup2Item> implements NetGroup2ItemDao {
	
	@Override
	public PageModel<NetGroup2Item> findForPage (PageEntity entity) {
		Criteria criteria = getSession().createCriteria(NetGroup2Item.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 模糊查询
			criteria.add(Restrictions.or(
					Restrictions.like("serial", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("remark", "%" + entity.getSearchValue() + "%")
			));
		}
		if (StrUtils.checkParam(entity.getSpecialParam())) {
			criteria.add(Restrictions.eq("isAlone", ConfigProperty.NO));
			criteria.add(Restrictions.eq("stackId", entity.getSpecialParam()));
		} else {
			criteria.add(Restrictions.eq("isAlone", ConfigProperty.YES));
			String typeCode = StrUtils.tranString(request.getParameter("typeCode"));
			criteria.add(Restrictions.eq("assetTypeCode", typeCode));
		}
		Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
		return this.findForPage(NetGroup2Item.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
}
