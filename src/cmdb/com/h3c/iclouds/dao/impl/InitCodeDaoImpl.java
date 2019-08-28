package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.InitCodeDao;
import com.h3c.iclouds.po.InitCode;
import com.h3c.iclouds.utils.StrUtils;

@Repository("initCodeDao")
public class InitCodeDaoImpl extends BaseDAOImpl<InitCode> implements InitCodeDao {

	@Override
	public PageModel<InitCode> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(InitCode.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("codeTypeId", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("codeId", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("typeDesc", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("codeName", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("sysCode", "%" + entity.getSearchValue() + "%")
			));
		}
		
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
			if (entity.getAsSorting().equals("asc")){
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("typeDesc", "asc");
			order.put("codeSeq", "asc");
		}
		return this.findForPage(InitCode.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
	
}
