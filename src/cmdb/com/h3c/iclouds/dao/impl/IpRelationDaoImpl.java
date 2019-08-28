package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.IpRelationDao;
import com.h3c.iclouds.po.IpRelation;

@Repository("ipRelationDao")
public class IpRelationDaoImpl extends BaseDAOImpl<IpRelation> implements IpRelationDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<IpRelation> findAssetIp(String assetId, String classId) {
		Criteria criteria = getSession().createCriteria(IpRelation.class);
		criteria.add(Restrictions.like("assetId", assetId));
		criteria.add(Restrictions.like("classId", classId));
		criteria.add(Restrictions.isNotNull("ncid"));
		return criteria.list();
	}

	/**
	 * 分页查询
	 * @param entity
	 * @return
	 */
	@Override
	public PageModel<IpRelation> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(IpRelation.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {//根据搜索条件模糊查询
			criteria.add(Restrictions.or(
					Restrictions.like("ip", "%" + entity.getSearchValue() + "%")
			));
		}
		if(entity.getSpecialParam() != null) {
			criteria.add(Restrictions.or(
					Restrictions.eq("assetId", entity.getSpecialParam()),
					Restrictions.eq("ncid", entity.getSpecialParam())
			));
		}
		criteria.add(Restrictions.eq("isIlop", 1));
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
			if (entity.getAsSorting().equals("asc")){
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("updatedDate", "desc");//默认根据修改日期排序
		}
		return this.findForPage(IpRelation.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

}
