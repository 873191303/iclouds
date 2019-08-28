package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.ContactDao;
import com.h3c.iclouds.po.business.Contact;
import com.h3c.iclouds.utils.StrUtils;

@Repository("contactDao")
@Transactional
public class ContactDaoImpl extends BaseDAOImpl<Contact> implements ContactDao {

	@Override
	public PageModel<Contact> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Contact.class);

		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("cname", "%" + entity.getSearchValue() + "%")
			));
		}
		if(entity.getSpecialParam() != null) {
			criteria.add(Restrictions.eq("createdBy", this.getSessionBean().getUserId()));
		}
		
		criteria.add(Restrictions.eq("cusId", entity.getSpecialParam()));
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
			if (entity.getAsSorting().equals("asc")){
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("updatedDate", "desc");
		}
		return this.findForPage(Contact.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

}
