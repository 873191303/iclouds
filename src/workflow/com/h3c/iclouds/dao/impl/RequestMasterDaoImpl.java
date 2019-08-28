package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.RequestMasterDao;
import com.h3c.iclouds.po.business.Request2ApproveLog;
import com.h3c.iclouds.po.business.RequestMaster;
import com.h3c.iclouds.utils.DateUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("requestMasterDao")
@Transactional
public class RequestMasterDaoImpl extends BaseDAOImpl<RequestMaster> implements RequestMasterDao {
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Override
	public PageModel<RequestMaster> findForPageByApprover(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(RequestMaster.class, "master");
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("projectName", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("reqCode", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("cusName", "%" + entity.getSearchValue() + "%")
			));
		}
		
		/*定义子查询对象并命名别名*/
		DetachedCriteria queryCar = DetachedCriteria.forClass(Request2ApproveLog.class, "items");
		queryCar.add(Restrictions.eq("items.approver", this.getSessionBean().getUserId()));
		queryCar.add(Property.forName("master.id").eqProperty("items.reqId"));
		criteria.add(Subqueries.exists(queryCar.setProjection(Projections.property("items.reqId"))));
		
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
		return this.findForPage(RequestMaster.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

	@Override
	public PageModel<RequestMaster> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(RequestMaster.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("projectName", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("reqCode", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("cusName", "%" + entity.getSearchValue() + "%")
			));
		}
		
		Map<String, Object> queryMap = entity.getQueryMap();
		if(StrUtils.checkParam(queryMap)) {
			if(queryMap.containsKey("step")) {
				String step = StrUtils.tranString(queryMap.get("step"));
				if(StrUtils.checkParam(step)) {
					if("4".equals(step)) {	// 处于流程中的状态
						String[] stepArray = new String[]{
							ConfigProperty.MASTER_STEP2_DEPARTMENT_APPROVE,
							ConfigProperty.MASTER_STEP3_SIGN_APPROVE,
							ConfigProperty.MASTER_STEP4_CONTROL,
							ConfigProperty.MASTER_STEP5_HANDLE,
							ConfigProperty.MASTER_STEP6_TEST
						};
						criteria.add(Restrictions.in("step", stepArray));
					} else {
						criteria.add(Restrictions.eq("step", step));
					}
				}
			} else if(queryMap.containsKey("step_")) {
				criteria.add(Restrictions.ne("step", queryMap.get("step_")));
			}
			
			// 是否变更：1、已变更；2、未变更
			String chgFlag = StrUtils.tranString(queryMap.get("chgFlag"));
			if(StrUtils.checkParam(chgFlag)) {
				criteria.add(Restrictions.eq("chgFlag", chgFlag));
			}
			
			// 开始时间
			String startDate = StrUtils.tranString(queryMap.get("startDate"));
			if(StrUtils.checkParam(startDate)) {
				startDate += " 00:00:00";	// 时段设置
				Date date = DateUtils.getDateByString(startDate);
				if(null != date) {
					criteria.add(Restrictions.ge("createdDate", date));
				}
			}
			
			// 结束时间
			String endDate = StrUtils.tranString(queryMap.get("endDate"));
			if(StrUtils.checkParam(endDate)) {
				endDate += " 23:59:59";	// 时段设置
				Date date = DateUtils.getDateByString(endDate);
				if(null != date) {
					criteria.add(Restrictions.le("createdDate", date));
				}
			}
		}

		String showAll = entity.getSpecialParam();
		if(!StrUtils.equals(showAll, "list-all")) {	// 是否有查询所有的标识
			// 只看到自己的申请单
			criteria.add(Restrictions.eq("createdBy", this.getSessionBean().getUserId()));
		}

		Map<String, String> order = new HashMap<>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
			if (entity.getAsSorting().equals("asc")){
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("updatedDate", "desc");
		}
		return this.findForPage(RequestMaster.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

	@Override
	public PageModel<Map<String, Object>> findCompleteForPage(PageEntity entity, boolean needHistory) {
		String sqlName = needHistory ? SqlQueryProperty.QUERY_REQUEST_MASTER_APPROVE_AND_HISTORY : SqlQueryProperty.QUERY_REQUEST_MASTER_APPROVE;
		String sqlCountName = needHistory ? SqlQueryProperty.QUERY_REQUEST_MASTER_APPROVE_AND_HISTORY_COUNT : SqlQueryProperty.QUERY_REQUEST_MASTER_APPROVE_COUNT;
		String listSql = getSession().getNamedQuery(sqlName).getQueryString();
		StringBuffer listSqlBuffer = new StringBuffer();
		listSqlBuffer.append(listSql);
		
		String countSql = getSession().getNamedQuery(sqlCountName).getQueryString();
		
		String searchValue = entity.getSearchValue();
		StringBuffer like = new StringBuffer();
		if(needHistory) {
			like.append(" WHERE 1=1 ");
		}
		Map<String, Object> queryMap = entity.getQueryMap();
		if(StrUtils.checkParam(queryMap)) {
			if(queryMap.containsKey("allowed") && StrUtils.checkParam(queryMap.get("allowed"))) {
				Integer allowed = StrUtils.tranInteger(queryMap.get("allowed"));
				like.append("AND allowed = " + allowed);
			}
			
			// 开始时间
			String startDate = StrUtils.tranString(queryMap.get("startDate"));
			if(StrUtils.checkParam(startDate)) {
				startDate += " 00:00:00";	// 时段设置
				Date date = DateUtils.getDateByString(startDate);
				if(null != date) {
					like.append("AND createdDate >= '" + DateUtils.getDate(date) + "'");
				}
			}
			
			// 结束时间
			String endDate = StrUtils.tranString(queryMap.get("endDate"));
			if(StrUtils.checkParam(endDate)) {
				endDate += " 23:59:59";	// 时段设置
				Date date = DateUtils.getDateByString(endDate);
				if(null != date) {
					like.append("AND createdDate <= '" + DateUtils.getDate(date) + "'");
				}
			}
		}
		if(searchValue != null && !"".equals(entity.getSearchValue())) {
			like.append("AND (reqcode like '%" + searchValue + "%' ");
			like.append("OR projectname like '%" + searchValue + "%' ");
			like.append("OR projectdesc like '%" + searchValue + "%' )");
		}
		
		listSqlBuffer.append(like.toString());
		countSql += like.toString();
		
		// 替换为当前用户
		countSql = countSql.replaceAll(":USERID", "'" + this.getSessionBean().getUserId() + "'");
		
		List<Map<String, Object>> countList = sqlQueryBiz.queryBySql(countSql);
		PageModel<Map<String, Object>> pages = new PageModel<Map<String, Object>>();
		pages.setPageNo(entity.getPageNo() * entity.getPageSize());
		pages.setPageSize(entity.getPageSize());
		pages.setTotalRecords(StrUtils.tranInteger(countList.get(0).get("count")));
		if(pages.getTotalRecords() > 0) {
			if(StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())) {
				listSqlBuffer.append(" ORDER BY " + entity.getColumnName() + " " + entity.getAsSorting());
			} else {	// 默认排序
				listSqlBuffer.append(" ORDER BY createddate desc");
			}
			if(entity.getPageSize() != -1) {
				listSqlBuffer.append(" LIMIT " + entity.getPageSize() + " OFFSET " + entity.getPageNo() * entity.getPageSize());	
			}
			
			listSql = listSqlBuffer.toString();
			listSql = listSql.replaceAll(":USERID", "'" + this.getSessionBean().getUserId() + "'");
			List<Map<String, Object>> resultList = sqlQueryBiz.queryBySql(listSql);
			if(resultList == null) {
				pages.setDatas(new ArrayList<Map<String, Object>>());
			} else {
				pages.setDatas(resultList);
			}
		} else {
			pages.setDatas(new ArrayList<Map<String, Object>>());
		}
		return pages;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String findLastReqCode() {
		Criteria criteria = getSession().createCriteria(RequestMaster.class);
		String reqCode = DateUtils.getDate("yyyyMMdd");
		criteria.add(Restrictions.like("reqCode", reqCode + "%"));
		criteria.addOrder(Order.desc("reqCode"));
		List<RequestMaster> list = criteria.list();
		if(StrUtils.checkCollection(list)) {
			RequestMaster entity = list.get(0);
			String entityReqCode = entity.getReqCode();
			entityReqCode = entityReqCode.replace(reqCode, "");
			int num = Integer.valueOf(entityReqCode);
			reqCode += StrUtils.additionalNum(++num, 3);
		} else {
			reqCode += "001";
		}
		reqCode = "B" + reqCode; 
		return reqCode;
	}
	
	@Override
	public int count (String type) {
		Criteria criteria = getSession().createCriteria(RequestMaster.class);
		criteria.add(Restrictions.ne("step", "1")).add(Restrictions.ne("step", "3"));
		if (StrUtils.checkParam(type) && type.equals("month")) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -1);
			criteria.add(Restrictions.ge("createdDate", calendar.getTime()));
		}
		Long result = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		return null == result ? 0 : result.intValue();
	}
}
