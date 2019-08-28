package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.IncMasterDao;
import com.h3c.iclouds.po.inc.Inc2ApproveLog;
import com.h3c.iclouds.po.inc.IncMaster;
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

@Repository("incMasterDao")
@Transactional
public class IncMasterDaoImpl extends BaseDAOImpl<IncMaster> implements IncMasterDao {
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Override
	public PageModel<IncMaster> findForPageByApprover(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(IncMaster.class, "master");
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("incCode", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("topic", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("content", "%" + entity.getSearchValue() + "%")
			));
		}
		
		/*定义子查询对象并命名别名*/
		DetachedCriteria queryCar = DetachedCriteria.forClass(Inc2ApproveLog.class, "log");
		queryCar.add(Restrictions.eq("log.approver", this.getSessionBean().getUserId()));
		queryCar.add(Property.forName("master.id").eqProperty("log.reqId"));
		criteria.add(Subqueries.exists(queryCar.setProjection(Projections.property("log.reqId"))));
		
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
		return this.findForPage(IncMaster.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

	@Override
	public PageModel<IncMaster> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(IncMaster.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("incNo", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("topic", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("content", "%" + entity.getSearchValue() + "%")
			));
		}
		
		Map<String, Object> queryMap = entity.getQueryMap();
		if(StrUtils.checkParam(queryMap)) {
			if(queryMap.containsKey("step")) {
				// 只看到自己的申请单
				criteria.add(Restrictions.eq("step", queryMap.get("step")));
			} else if(queryMap.containsKey("step_")) {
				criteria.add(Restrictions.ne("step", queryMap.get("step_")));
			}
		}

		String showAll = entity.getSpecialParam();
		if(!StrUtils.equals(showAll, "list-all")) {    // 是否有查询所有的标识
			// 只看到自己的申请单
			criteria.add(Restrictions.eq("createdBy", this.getSessionBean().getUserId()));
		}

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
		return this.findForPage(IncMaster.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

	@Override
	public PageModel<Map<String, Object>> findCompleteForPage(PageEntity entity) {
		String listSql = getSession().getNamedQuery(SqlQueryProperty.QUERY_INC_MASTER_APPROVE_AND_HISTORY).getQueryString();
		StringBuffer listSqlBuffer = new StringBuffer();
		listSqlBuffer.append(listSql);
		
		String countSql = getSession().getNamedQuery(SqlQueryProperty.QUERY_INC_MASTER_APPROVE_AND_HISTORY_COUNT).getQueryString();
		String searchValue = entity.getSearchValue();
		StringBuffer like = new StringBuffer();
		like.append(" WHERE 1=1 ");
		
		Map<String, Object> queryMap = entity.getQueryMap();
		if(StrUtils.checkParam(queryMap)) {
			if(queryMap.containsKey("allowed") && StrUtils.checkParam(queryMap.get("allowed"))) {
				Integer allowed = StrUtils.tranInteger(queryMap.get("allowed"));
				like.append("AND allowed = '" + allowed + "'");
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
		
		if(StrUtils.checkParam(searchValue)) {
			like.append("AND (incno like '%" + searchValue + "%' ");
			like.append("OR topic like '%" + searchValue + "%' ");
			like.append("OR content like '%" + searchValue + "%' )");
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
				listSqlBuffer.append(" ORDER BY updateddate desc");
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
	public String findLastIncNo() {
		Criteria criteria = getSession().createCriteria(IncMaster.class);
		String incNo = DateUtils.getDate("yyyyMMdd");
		criteria.add(Restrictions.like("incNo", incNo + "%"));
		criteria.addOrder(Order.desc("incNo"));
		List<IncMaster> list = criteria.list();
		if(StrUtils.checkCollection(list)) {
			IncMaster entity = list.get(0);
			String entityIncNo = entity.getIncNo();
			entityIncNo = entityIncNo.replace(incNo, "");
			int num = Integer.valueOf(entityIncNo);
			incNo += StrUtils.additionalNum(++num, 4);
		} else {
			incNo += "0001";
		}
		incNo = "S" + incNo;
		return incNo;
	}
	
	@Override
	public int count (String type) {
		Criteria criteria = getSession().createCriteria(IncMaster.class);
		criteria.add(Restrictions.ne("step", "1"));
		if (StrUtils.checkParam(type) && type.equals("month")) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -1);
			criteria.add(Restrictions.ge("createdDate", calendar.getTime()));
		}
		Long result = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		return null == result ? 0 : result.intValue();
	}
}
