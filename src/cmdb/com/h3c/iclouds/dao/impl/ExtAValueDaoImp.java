package com.h3c.iclouds.dao.impl;


import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.ExtAValueDao;
import com.h3c.iclouds.po.ExtAValue;
import com.h3c.iclouds.utils.StrUtils;

@Repository("extAValueDao")
public class ExtAValueDaoImp extends BaseDAOImpl<ExtAValue> implements ExtAValueDao {
	
	
	@Override
	public PageModel<ExtAValue> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(ExtAValue.class);
		// 查询条件
		if(StrUtils.checkParam(entity.getSearchValue())) {	
			criteria.add(Restrictions.or(
				Restrictions.like("extName", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("extValue", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("assetName", "%" + entity.getSearchValue() + "%")
			));
		}
		//资产id
		if(entity.getSpecialParam() != null) {
			criteria.add(Restrictions.eq("assetID", entity.getSpecialParam()));
		}
		return this.findForPage(ExtAValue.class, criteria, null, entity.getPageNo(), entity.getPageSize());
	}

}
