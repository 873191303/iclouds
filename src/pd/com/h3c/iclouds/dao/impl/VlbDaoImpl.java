package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.VlbDao;
import com.h3c.iclouds.po.Vlb;
import com.h3c.iclouds.po.VlbVip;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Repository("vlbDao")
public class VlbDaoImpl extends BaseDAOImpl<Vlb> implements VlbDao {

	@Resource
	private ProjectBiz projectBiz;

	@Resource
	private SqlQueryBiz sqlQueryBiz;

	@Override
	public PageModel<Vlb> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Vlb.class);
		// 查询方式
		if (!"".equals(entity.getSearchValue())) { // 模糊查询
			criteria.add(Restrictions.or(Restrictions.like("name", "%" + entity.getSearchValue() + "%")));
		}
		Map<String, Object> queryMap = entity.getQueryMap();
		String projectId = StrUtils.tranString(queryMap.get("projectId"));//租户过滤
		projectId = projectBiz.getFilterProjectId(projectId, "normal");//负载均衡的负载均衡组列表
		if (null != projectId) {
			criteria.add(Restrictions.eq("projectId", projectId));
		}
		Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
		return this.findForPage(Vlb.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

	@Override
	public PageModel<VlbVip> vipPoolList(PageEntity entity) {
		String fId = entity.getSpecialParam();
		String listSql = getSession().getNamedQuery(SqlQueryProperty.QUERY_PUBLIC_VIPS).getQueryString();
		listSql=listSql.replaceAll(":fId", "'"+fId+"'");
		StringBuffer listSqlBuffer = new StringBuffer();
		listSqlBuffer.append(listSql);

		String countSql = getSession().getNamedQuery(SqlQueryProperty.QUERY_PUBLIC_VIPS_COUNT).getQueryString();
		countSql=countSql.replaceAll(":fId", "'"+fId+"'");
		StringBuffer countSqlBuffer = new StringBuffer();
		countSqlBuffer.append(countSql);
		
	
		SessionBean sessionBean = getSessionBean();
		StringBuffer where = new StringBuffer();
		if (sessionBean.getSuperUser()) {
			where.append(" ");
		} else if (sessionBean.getSuperRole()) {
			where.append(" AND tenant_id='" + sessionBean.getProjectId() + "'");
		} else {
			where.append(" AND tenant_id='" + sessionBean.getProjectId() + "'");
		}
		countSqlBuffer.append(where.toString());
		listSqlBuffer.append(where.toString());

		String searchValue = entity.getSearchValue();
		StringBuffer like = new StringBuffer();
		if (searchValue != null && !"".equals(entity.getSearchValue())) {
			like.append("AND v.name like '%" + searchValue + "%' ");
			listSqlBuffer.append(like.toString());
			countSqlBuffer.append(like.toString());
		}
		List<Map<String, Object>> countList = sqlQueryBiz.queryBySql(countSqlBuffer.toString());
		PageModel<VlbVip> pages = new PageModel<VlbVip>();
		pages.setPageNo(entity.getPageNo() * entity.getPageSize());
		pages.setPageSize(entity.getPageSize());
		pages.setTotalRecords(StrUtils.tranInteger(countList.get(0).get("count")));
		if (pages.getTotalRecords() > 0) {
			if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())) {
				listSqlBuffer.append(" ORDER BY " + entity.getColumnName() + " " + entity.getAsSorting());
			} else { // 默认排序
				listSqlBuffer.append(" ORDER BY v.updateddate desc");
			}
			if (entity.getPageSize() != -1) {
				listSqlBuffer.append(
						" LIMIT " + entity.getPageSize() + " OFFSET " + entity.getPageNo() * entity.getPageSize());
			}
			listSql = listSqlBuffer.toString();
			List<Map<String, Object>> resultList = sqlQueryBiz.queryBySql(listSql);
			List<VlbVip> data = new ArrayList<>();
			for (Map<String, Object> map2 : resultList) {
				VlbVip vlbVip = new VlbVip();
				InvokeSetForm.settingForm(map2, vlbVip);
				vlbVip.setVipAddress((String) map2.get("vip_address"));
				vlbVip.setPortId((String) map2.get("port_id"));
				data.add(vlbVip);
			}
			pages.setDatas(data);
		} else {
			pages.setDatas(new ArrayList<VlbVip>());
		}
		return pages;
	}

}
