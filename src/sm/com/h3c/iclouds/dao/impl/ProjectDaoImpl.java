package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.ProjectDao;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/17.
 */
@Repository("projectDao")
public class ProjectDaoImpl extends BaseDAOImpl<Project> implements ProjectDao {

	@Resource
	SqlQueryBiz sqlQueryBiz;

	@Override
	public PageModel<Project> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Project.class);
		criteria.add(Restrictions.eq("flag", 0));
		// 查询方式
		if (!"".equals(entity.getSearchValue())) { // 模糊查询
			criteria.add(Restrictions.or(Restrictions.like("name", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("extra", "%" + entity.getSearchValue() + "%")));
		}
		if (null != entity.getSpecialParam() && !"".equals(entity.getSpecialParam())) {
			// 用户为电信管理员时不能看到根租户的信息
			if (entity.getSpecialParam().equals(CacheSingleton.getInstance().getCtRoleId())) {
				criteria.add(Restrictions.isNotNull("parent_id"));
			} else {
				criteria.add(Restrictions.eq("parent_id", entity.getSpecialParam()));
			}
		}
		if (StrUtils.checkParam(entity.getQueryMap())) {
			String projectId = (String) entity.getQueryMap().get("projectId");
			if (projectId != null) {
				criteria.add(Restrictions.eq("id", projectId));
			}

		}
		if (null != entity.getSpecialParams() && entity.getSpecialParams().length > 0) {
			criteria.add(Restrictions.in("id", entity.getSpecialParams()));
		}
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())
				&& !"userName".equals(entity.getColumnName())) {

			if (entity.getAsSorting().equals("asc")) {
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("updatedDate", "desc");
		}
		return this.findForPage(Project.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

	@Override
	public List<Project> getExistInProject() {
		Criteria criteria = getSession().createCriteria(Project.class);
		criteria.add(Restrictions.eq("flag", 0));
		criteria.add(Restrictions.isNotNull("cusId"));
		@SuppressWarnings("unchecked")
		List<Project> projects = criteria.list();
		return projects;
	}

	@Override
	public Project getExistProject(String projectId) {
		Criteria criteria = getSession().createCriteria(Project.class);
		criteria.add(Restrictions.eq("flag", 0));
		criteria.add(Restrictions.idEq(projectId));
		return (Project) criteria.uniqueResult();
	}

	public List<Map<String, Object>> findMacAddressByProjectId(String projectId) {
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("projectId", projectId);
		return sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_MACADDRESS_BY_PROJECTID, queryMap);
	}
	
	@Override
	public List<Project> getChildParent(String tenantId) {
		Criteria criteria = getSession().createCriteria(Project.class);
		criteria.add(Restrictions.eq("flag", 0));
		criteria.add(Restrictions.eq("parentId", tenantId));
		@SuppressWarnings("unchecked")
		List<Project> projects = criteria.list();
		return projects;
	}
	
	@Override
	public int monthCount () {
		Criteria criteria = getSession().createCriteria(Project.class);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		criteria.add(Restrictions.ge("createdDate", calendar.getTime()));
		Long result = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		return null == result ? 0 : result.intValue();
	}
}
