package com.h3c.iclouds.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.UserDao;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.StrUtils;

@Repository("userDao")
public class UserDaoImpl extends BaseDAOImpl<User> implements UserDao {

	@Resource
	private SqlQueryBiz sqlQueryBiz;
	@Override
	public PageModel<User> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(User.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("loginName", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("deptName", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("userName", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("remark", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("telephone", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("projectName", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("email", "%" + entity.getSearchValue() + "%")));
		}
		if(!this.getSessionBean().getSuperUser()) {
			criteria.add(Restrictions.eq("projectId", this.getSessionBean().getUser().getProjectId()));	
		}
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
			if (entity.getAsSorting().equals("asc")){
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("userName", "asc");
		}
        //执行查询
		return this.findForPage(User.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
	
	public  List<Map<String, Object>> findAdminUsersByProjectId(String projectId) {

		String tenantRoleId = CacheSingleton.getInstance().getTenantRoleId();
		String cloudRoleId = CacheSingleton.getInstance().getCloudRoleId();
		String operationRoleId = CacheSingleton.getInstance().getOperationRoleId();
		String ctRoleId = CacheSingleton.getInstance().getCtRoleId();
		
		List<String> list = new ArrayList<String>();
		list.add(tenantRoleId);
		list.add(cloudRoleId);
		list.add(operationRoleId);
		list.add(ctRoleId);
		
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("LIST", list);
		queryMap.put("projectId", projectId);
		return sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_ADMINNAME_BY_PROJECTID, queryMap);
	}
	
}
