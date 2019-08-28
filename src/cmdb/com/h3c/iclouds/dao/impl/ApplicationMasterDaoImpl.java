package com.h3c.iclouds.dao.impl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.po.bean.AppCenter;
import com.h3c.iclouds.biz.Project2QuotaBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.ApplicationMasterDao;
import com.h3c.iclouds.po.ApplicationMaster;
import com.h3c.iclouds.po.Project2Quota;
import com.h3c.iclouds.po.QuotaUsed;
import com.h3c.iclouds.utils.StrUtils;

@Repository("applicationMasterDao")
public class ApplicationMasterDaoImpl extends BaseDAOImpl<ApplicationMaster> implements ApplicationMasterDao {

	@Resource
	private SqlQueryBiz sqlQueryBiz;

	@Resource
	private Project2QuotaBiz project2QuotaBiz;

	@Resource
	private QuotaUsedBiz quotaUsedBiz;

	@Override
	public PageModel<ApplicationMaster> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(ApplicationMaster.class);
		//
		SessionBean sessionBean = getSessionBean();
		if (sessionBean.getSuperUser()) {

		} else if (sessionBean.getSuperRole()) {
			criteria.add(Restrictions.eq("projectId", sessionBean.getProjectId()));
		} else {
			criteria.add(Restrictions.eq("projectId", sessionBean.getProjectId()));
			criteria.add(Restrictions.eq("owner", sessionBean.getUserId()));
		}
		// 查询方式
		if (!"".equals(entity.getSearchValue())) { // 模糊查询
			criteria.add(Restrictions.or(Restrictions.like("name", "%" + entity.getSearchValue() + "%")));
		}
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())) {
			if (entity.getAsSorting().equals("asc")) {
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("updatedDate", "desc");
		}
		return this.findForPage(ApplicationMaster.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

	@Override
	public Object get() {
		String app = getSession().getNamedQuery(SqlQueryProperty.QUERY_APP_CENTER_APP).getQueryString();
		String db = getSession().getNamedQuery(SqlQueryProperty.QUERY_APP_CENTER_DB).getQueryString();
		String nova = getSession().getNamedQuery(SqlQueryProperty.QUERY_APP_CENTER_NOVA).getQueryString();
		String root_project_nova = getSession().getNamedQuery(SqlQueryProperty.QUERY_APP_CENTER_ROOT_NOVA)
				.getQueryString();
		String ips = getSession().getNamedQuery(SqlQueryProperty.QUERY_APP_CENTER_FLOATINGIPS).getQueryString();
//		String sum = getSession().getNamedQuery(SqlQueryProperty.QUERY_APP_CENTER_SUM).getQueryString();

		SessionBean sessionBean = getSessionBean();
		StringBuffer appwhere = new StringBuffer();
		if (sessionBean.getSuperUser()) {
			appwhere.append(" ");
		} else if (sessionBean.getSuperRole()) {
			appwhere.append(" where projectId='" + sessionBean.getProjectId() + "'");
		} else {
			appwhere.append(" where projectId='" + sessionBean.getProjectId() + "'");
			appwhere.append(" AND owner='" + sessionBean.getUserId() + "'");
		}
		app += appwhere.toString();

		List<Map<String, Object>> num_nova = null;
		StringBuffer novawhere = new StringBuffer();
		if (sessionBean.getSuperUser()) {
			num_nova = sqlQueryBiz.queryBySql(root_project_nova);
			// where3.append(" where vmstate='state_normal'");
		} else if (sessionBean.getSuperRole()) {
			novawhere.append(" where n.projectId='" + sessionBean.getProjectId() + "'");
			nova += novawhere.toString();
			num_nova = sqlQueryBiz.queryBySql(nova);
			// where3.append(" and vmstate='state_normal'");
		} else {
			novawhere.append(" where n.projectId='" + sessionBean.getProjectId() + "'");
			novawhere.append(" AND n.owner='" + sessionBean.getUserId() + "'");
			nova += novawhere.toString();
			num_nova = sqlQueryBiz.queryBySql(nova);
			// where3.append(" and vmstate='state_normal'");
		}

		StringBuffer dbwhere = new StringBuffer();
		if (sessionBean.getSuperUser()) {
			dbwhere.append(" ");
		} else if (sessionBean.getSuperRole()) {
			dbwhere.append(" where projectId='" + sessionBean.getProjectId() + "'");
		} else {
			dbwhere.append(" where projectId='" + sessionBean.getProjectId() + "'");
			dbwhere.append(" AND dbowner='" + sessionBean.getUserId() + "'");
		}
		db += dbwhere.toString();
		StringBuffer ipswhere = new StringBuffer();
		if (sessionBean.getSuperUser()) {
			ipswhere.append(" ");
		} else if (sessionBean.getSuperRole()) {
			ipswhere.append(" where f.tenant_id='" + sessionBean.getProjectId() + "'");
		} else {
			ipswhere.append(" where f.tenant_id='" + sessionBean.getProjectId() + "'");
			ipswhere.append(" And p.device_owner='" + sessionBean.getUserId() + "'");
		}
		ips += ipswhere.toString();

		List<Map<String, Object>> num_app = sqlQueryBiz.queryBySql(app);
		List<Map<String, Object>> num_db = sqlQueryBiz.queryBySql(db);
		List<Map<String, Object>> num_ips = sqlQueryBiz.queryBySql(ips);

		AppCenter result = new AppCenter();
		for (Map<String, Object> map : num_ips) {
			BigInteger temp = (BigInteger) map.get("ips");
			if (StrUtils.checkParam(temp)) {
				result.setNum_ips(temp.intValue());
			} else {
				result.setNum_ips(0);
			}

		}
		for (Map<String, Object> map : num_app) {
			BigInteger temp = (BigInteger) map.get("num_app");
			if (StrUtils.checkParam(temp)) {
				result.setNum_app(temp.intValue());
			} else {
				result.setNum_app(0);
			}
		}
		for (Map<String, Object> map : num_db) {
			BigInteger temp = (BigInteger) map.get("num_db");
			if (StrUtils.checkParam(temp)) {
				result.setNum_db(temp.intValue());
			} else {
				result.setNum_db(0);
			}

		}
		for (Map<String, Object> map : num_nova) {
			BigInteger temp = (BigInteger) map.get("num_nova");
			if (StrUtils.checkParam(temp)) {
				result.setNum_nova(temp.intValue());
			} else {
				result.setNum_nova(0);
			}
		}
		String projectId = sessionBean.getProjectId();
		Map<String, Object> map = new HashMap<>();
		map.put("tenantId", projectId);
		map.put("classCode", "cores");
		Project2Quota project2Quota = project2QuotaBiz.singleByClass(Project2Quota.class, map);
		QuotaUsed quotaUsed = quotaUsedBiz.singleByClass(QuotaUsed.class, map);
		// 测试
//		 quotaUsed.setQuotaUsed(7);
//		 quotaUsedBiz.update(quotaUsed);
		if (StrUtils.checkParam(project2Quota)) {
			result.setCores(project2Quota.getHardLimit());
		} else {
			result.setCores(0);
		}
		if (StrUtils.checkParam(quotaUsed)) {
			result.setUse_cores(quotaUsed.getQuotaUsed());
		} else {
			result.setUse_cores(0);
		}

		map.put("classCode", "ram");
		project2Quota = project2QuotaBiz.singleByClass(Project2Quota.class, map);
		quotaUsed = quotaUsedBiz.singleByClass(QuotaUsed.class, map);
		// 测试
//		 quotaUsed.setQuotaUsed(14);
//		 quotaUsedBiz.update(quotaUsed);
		if (StrUtils.checkParam(project2Quota)) {
			result.setRam(project2Quota.getHardLimit());
		} else {
			result.setRam(0);
		}

		if (StrUtils.checkParam(quotaUsed)) {
			result.setUse_ram(quotaUsed.getQuotaUsed());
		} else {
			result.setUse_ram(0);
		}

		map.put("classCode", "gigabytes");
		project2Quota = project2QuotaBiz.singleByClass(Project2Quota.class, map);
		quotaUsed = quotaUsedBiz.singleByClass(QuotaUsed.class, map);
		// 测试
		// quotaUsed.setQuotaUsed(0);
		// quotaUsedBiz.update(quotaUsed);
		if (StrUtils.checkParam(project2Quota)) {
			result.setGigabytes(project2Quota.getHardLimit());
		} else {
			result.setGigabytes(0);
		}

		if (StrUtils.checkParam(quotaUsed)) {
			result.setUse_gigabytes(quotaUsed.getQuotaUsed());
		} else {
			result.setUse_gigabytes(0);
		}

		
		/*map.put("classCode", "instances");
		project2Quota = project2QuotaBiz.singleByClass(Project2Quota.class, map);
		quotaUsed = quotaUsedBiz.singleByClass(QuotaUsed.class, map);
		 quotaUsed.setQuotaUsed(7);
		 quotaUsedBiz.update(quotaUsed);
		map.put("classCode", "ips");
		project2Quota = project2QuotaBiz.singleByClass(Project2Quota.class, map);
		quotaUsed = quotaUsedBiz.singleByClass(QuotaUsed.class, map);
		 quotaUsed.setQuotaUsed(7);
		 quotaUsedBiz.update(quotaUsed);*/
		return result;
	}

}
