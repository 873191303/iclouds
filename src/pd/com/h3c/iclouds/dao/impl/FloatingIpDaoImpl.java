package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.FloatingIpDao;
import com.h3c.iclouds.po.FloatingIp;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author  yKF7408
* @date 2017年1月7日 上午9:47:52
*/

@Repository("floatingIpDao")
public class FloatingIpDaoImpl extends BaseDAOImpl<FloatingIp> implements FloatingIpDao{
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Override
	public PageModel<FloatingIp> findForPage(PageEntity entity) {
		Criteria criteria = this.getSession().createCriteria(FloatingIp.class);
		// 查询方式
		if (!"".equals(entity.getSearchValue())) { // 模糊查询
			criteria.add(Restrictions.or(
					Restrictions.like("floatingIp", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("name", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("norm", "%" + entity.getSearchValue() + "%")
				));
		}

		boolean isAdmin = this.getSessionBean().getSuperUser();
		boolean isSuperRole = this.getSessionBean().getSuperRole();
		if (!isAdmin && isSuperRole ) { // 租户管理员
			criteria.add(Restrictions.eq("tenantId", this.getProjectId()));
		}
		if (!isAdmin && !isSuperRole) { // 普通用户
			criteria.add(Restrictions.eq("owner", this.getSessionUserId()));
		}
		
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())) {
			if (entity.getAsSorting().equals("asc")) {
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} 

		return super.findForPage(FloatingIp.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
	
	@Override
	public List<String> checkNetwork(FloatingIp floatingIp) {
		String listSql = getSession().getNamedQuery(SqlQueryProperty.QUERY_PUBLIC_NETWORKIDS).getQueryString();
		listSql=listSql.replaceAll(":fId", "'"+floatingIp.getId()+"'");
		StringBuffer listSqlBuffer = new StringBuffer();
		listSqlBuffer.append(listSql);
		List<Map<String, Object>> countList = sqlQueryBiz.queryBySql(listSqlBuffer.toString());
		List<String> result=new ArrayList<>();
		for (Map<String, Object> map : countList) {
			result.add((String) map.get("id"));
		}
		return result;
	}
	
	@Override
	public int allotionCount () {
		Criteria criteria = getSession().createCriteria(FloatingIp.class);
		criteria.add(Restrictions.isNotNull("fixedPortId"));
		Long result = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		return null == result ? 0 : result.intValue();
	}
}
