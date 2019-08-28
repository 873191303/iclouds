package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.BillDao;
import com.h3c.iclouds.po.business.Bill;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/8/10.
 */
@Repository("billDao")
public class BillDaoImpl extends BaseDAOImpl<Bill> implements BillDao {
	
	@Override
	public PageModel<Bill> findForPage (PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Bill.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {//查询条件
			criteria.add(Restrictions.or(
					Restrictions.like("instanceType", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("instanceName", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("userName", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("projectName", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("billTime", "%" + entity.getSearchValue() + "%")
			));
		}
		Map<String, Object> queryMap = entity.getQueryMap();
		//根据产品类别查询
		if (StrUtils.checkParam(queryMap.get("classId"))) {
			criteria.add(Restrictions.eq("classId", StrUtils.tranString(queryMap.get("classId"))));
		}
		//根据资源实例查询
		if (StrUtils.checkParam(queryMap.get("instanceId"))) {
			criteria.add(Restrictions.eq("instanceId", StrUtils.tranString(queryMap.get("instanceId"))));
		}
		//根据计费账单查询
		if (StrUtils.checkParam(queryMap.get("measureId"))) {
			criteria.add(Restrictions.eq("measureId", StrUtils.tranString(queryMap.get("measureId"))));
		}
		//根据起始日期和结束日期查询
		if (StrUtils.checkParam(queryMap.get("timeFrom"))){
			Long startTime = StrUtils.tranLong(queryMap.get("timeFrom"));
			Date startDate = new Date(startTime);
			criteria.add(Restrictions.ge("billDate", startDate));
		}
		if (StrUtils.checkParam(queryMap.get("timeTo"))){
			Long endTime = StrUtils.tranLong(queryMap.get("timeTo"));
			Date endDate = new Date(endTime);
			criteria.add(Restrictions.le("billDate", endDate));
		}
		if (!this.getSessionBean().getSuperUser()) {
			if (this.getSessionBean().getSuperRole()) {
				criteria.add(Restrictions.eq("tenantId", this.getProjectId()));
			} else {
				criteria.add(Restrictions.eq("userId", this.getSessionBean().getUserId()));
			}
		}
		Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
		return this.findForPage(Bill.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
	
	@Override
	public String getSql (PageEntity entity) {
		String sql = "SELECT instan.classname,sum(bill.amount) FROM iyun_instance_bilings bill LEFT JOIN " +
				"(SELECT ins.id,ins.name,cla.classname,ins.classid,ins.userid,ins.tenantid FROM iyun_measure_instaces" +
				" ins LEFT JOIN iyun_base_prdclass cla ON cla.id = ins.classid)AS instan ON bill.instanceid = instan.id " +
				"WHERE 1=1";
		StringBuffer sqlBuffer = new StringBuffer(sql);
		String searchValue = entity.getSearchValue();
		if(!"".equals(searchValue)) {//查询条件
			sqlBuffer.append(" AND (instan.name LIKE '%"+searchValue+"%' OR instan.classname LIKE '%"+searchValue+"%')");
		}
		Map<String, Object> queryMap = entity.getQueryMap();
		if (StrUtils.checkParam(queryMap.get("classId"))) {
			sqlBuffer.append(" AND instan.classid = '"+StrUtils.tranString(queryMap.get("classId"))+"'");
		}
		if (StrUtils.checkParam(queryMap.get("instanceId"))) {
			sqlBuffer.append(" AND bill.instanceid = '"+StrUtils.tranString(queryMap.get("instanceId"))+"'");
		}
		if (StrUtils.checkParam(queryMap.get("measureId"))) {
			sqlBuffer.append(" AND bill.measureid = '"+StrUtils.tranString(queryMap.get("classId"))+"'");
		}
		if (StrUtils.checkParam(queryMap.get("timeFrom"))){
			Long startTime = StrUtils.tranLong(queryMap.get("timeFrom"));
			Date startDate = new Date(startTime);
			sqlBuffer.append(" AND bill.bilingdate >= '"+startDate+"'");
		}
		if (StrUtils.checkParam(queryMap.get("timeTo"))){
			Long endTime = StrUtils.tranLong(queryMap.get("timeTo"));
			Date endDate = new Date(endTime);
			sqlBuffer.append(" AND bill.bilingdate <= '"+endDate+"'");
		}
		if (!this.getSessionBean().getSuperUser()) {
			if (this.getSessionBean().getSuperRole()) {
				sqlBuffer.append(" AND instan.tenantid = '"+this.getSessionBean().getProjectId()+"'");
			} else {
				sqlBuffer.append(" AND instan.userid = '"+this.getSessionBean().getUserId()+"'");
			}
		}
		sqlBuffer.append(" GROUP BY instan.classname");
		return sqlBuffer.toString();
	}
}
