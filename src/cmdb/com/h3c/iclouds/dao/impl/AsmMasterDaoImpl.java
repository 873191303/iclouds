package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.AsmMasterDao;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository("asmMasterDao")
public class AsmMasterDaoImpl extends BaseDAOImpl<AsmMaster> implements AsmMasterDao {

	@Override
	public PageModel<AsmMaster> findForPage(PageEntity entity, boolean flag) {
		Criteria criteria = getSession().createCriteria(AsmMaster.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("assetId", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("serial", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("provide", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("iloIP", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("mmac", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("os", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("assetName", "%" + entity.getSearchValue() + "%")
			));
		}
		
		if(StrUtils.checkParam(entity.getSpecialParam())) {
			criteria.add(Restrictions.eq("assetType", entity.getSpecialParam()));
		}
		
		if (flag) {
			criteria.add(Restrictions.isNull("stackId"));
		}
		
		Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
		return this.findForPage(AsmMaster.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

	@Override
	public PageModel<AsmMaster> without(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(AsmMaster.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("assetId", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("serial", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("iloIP", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("assetName", "%" + entity.getSearchValue() + "%")
			));
		}
		
		criteria.add(Restrictions.isNull("stackId"));	// 不存在堆叠
		criteria.add(Restrictions.eq("status", ConfigProperty.MASTER_STATUS2_HANDLE));	// 使用中
		criteria.add(Restrictions.eq("assetType", entity.getSpecialParam()));
		
		Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
		return this.findForPage(AsmMaster.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
	
	@Override
	public int otherUseFlag(String assetType) {
		Criteria criteria = getSession().createCriteria(AsmMaster.class);
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.ne("useFlag","4"),Restrictions.ne("useFlag", "2")),Restrictions.isNull("useFlag")));
		criteria.add(Restrictions.eq("assetType", assetType));
		Long result = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		return null == result ? 0 : result.intValue();
	}
}
