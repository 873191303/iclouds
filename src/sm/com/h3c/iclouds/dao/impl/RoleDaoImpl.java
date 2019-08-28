package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.RoleDao;
import com.h3c.iclouds.po.Role;
import com.h3c.iclouds.utils.StrUtils;

@Repository("roleDao")
public class RoleDaoImpl extends BaseDAOImpl<Role> implements RoleDao {

	@Override
	public PageModel<Role> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Role.class);
		if(!this.getSessionBean().getSuperUser()) {	// 不是超级角色用户
			// 电信管理员租户允许看到2个审批角色及租户创建的角色
			if(this.getSessionBean().getSuperRole()) {
				criteria.add(Restrictions.or(
					Restrictions.in("id", new String[]{
							CacheSingleton.getInstance().getChargeRoleId(), CacheSingleton.getInstance().getSignRoleId()
					}),
					Restrictions.eq("projectId", this.getProjectId())
				));
			} else {
				criteria.add(Restrictions.eq("id", "-1"));	// 普通用户看不到角色
			}
			
		}
		
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("roleName", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("roleDesc", "%" + entity.getSearchValue() + "%")));
		}
		
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
			if (entity.getAsSorting().equals("asc")){
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
//			order.put("roleName", "asc");
			order.put("updatedDate", "desc");
		}
		return this.findForPage(Role.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
	
}
