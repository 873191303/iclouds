package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.NovaVmViewDao;
import com.h3c.iclouds.po.NovaVmView;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository("novaVmViewDao")
public class NovaVmViewDaoImpl extends BaseDAOImpl<NovaVmView> implements NovaVmViewDao {

	@Override
	public PageModel<NovaVmView> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(NovaVmView.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 模糊查询
			criteria.add(Restrictions.or(
					Restrictions.like("hostname", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("manageIp", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("ipAddress", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("name", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("projectname", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("owner", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("publicIp", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("cidr", "%" + entity.getSearchValue() + "%")
			));
		}
		if (entity.getSpecialParams() != null && entity.getSpecialParams().length > 0){
			String [] uuids = entity.getSpecialParams();
			criteria.add(Restrictions.in("uuid", uuids));
			criteria.add(Restrictions.in("vmstate", new String [] {"state_stop", "state_normal"}));
		}
		if (StrUtils.checkParam(entity.getSpecialParam())) {
			criteria.add(Restrictions.eq("userId", entity.getSpecialParam()));
			criteria.add(Restrictions.in("vmstate", new String [] {"state_stop", "state_normal"}));
		} else {
			if (!this.getSessionBean().getSuperUser()) {
				if (!this.getSessionBean().getSuperRole()) {
					criteria.add(Restrictions.eq("userId", this.getSessionBean().getUserId()));
				} else {
					criteria.add(Restrictions.eq("projectId", this.getProjectId()));
				}
			}
		}
		Map<String, String> order = new HashMap<String, String>();
		if (null != entity.getColumnName() && entity.getColumnName().equals("privateIp")) {
			entity.setColumnName("ipAddress");
		}
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
		return this.findForPage(NovaVmView.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
}
