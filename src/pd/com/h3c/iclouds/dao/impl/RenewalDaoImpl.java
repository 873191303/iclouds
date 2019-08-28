package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.RenewalDao;
import com.h3c.iclouds.po.NovaVmView;
import com.h3c.iclouds.po.Project2Azone;
import com.h3c.iclouds.po.Renewal;
import com.h3c.iclouds.utils.StrUtils;

@Repository("renewalDao")
public class RenewalDaoImpl extends BaseDAOImpl<Renewal> implements RenewalDao {

	@Override
	public List<Renewal> selRenewalByAdmin(String userName) {
		Criteria criteria = getSession().createCriteria(Project2Azone.class);
		criteria.add(Restrictions.eq("userName", userName));
		return criteria.list();
	}

	@Override
	public void insertRenewal(Renewal dto) {
		add(dto);
	}

	@Override
	public void updateRenewal(Renewal dto) {
		update(dto);

	}

	@Override
	public PageModel<Renewal> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Renewal.class);
		// 查询方式
		if (!"".equals(entity.getSearchValue())) { // 模糊查询
			criteria.add(Restrictions.or(Restrictions.like("userUuid", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("userName", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("resourceUuid", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("resourceName", "%" + entity.getSearchValue() + "%")));
		}
		if (!this.getSessionBean().getSuperUser()) {
			criteria.add(Restrictions.eq("userUuid", this.getSessionBean().getUserId()));
		}
		Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "endTime", order);
		return this.findForPage(Renewal.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

}
