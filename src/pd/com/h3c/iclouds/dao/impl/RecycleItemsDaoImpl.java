package com.h3c.iclouds.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.RecycleItemsDao;
import com.h3c.iclouds.po.RecycleItems;
import com.h3c.iclouds.utils.StrUtils;

@Repository("recycleItemsDao")
public class RecycleItemsDaoImpl extends BaseDAOImpl<RecycleItems> implements RecycleItemsDao {

	@Resource
	private SqlQueryBiz sqlQueryBiz;

	@Override
	public PageModel<Map<String, Object>> findCompleteForPage(PageEntity entity) {
		String listSql = getSession().getNamedQuery(SqlQueryProperty.QUERY_RECYCLEITEMS).getQueryString();
		StringBuffer listSqlBuffer = new StringBuffer();
		listSqlBuffer.append(listSql);

		String countSql = getSession().getNamedQuery(SqlQueryProperty.QUERY_RECYCLEITEMS_COUNT).getQueryString();
		StringBuffer countSqlBuffer = new StringBuffer();
		countSqlBuffer.append(countSql);
		//云主机云主机云主机
		// 根据情况增加过滤条件
		SessionBean sessionBean = getSessionBean();
		StringBuffer where = new StringBuffer();
		if (sessionBean.getSuperUser()) {
			where.append(" where  i.classid='1'");
			where.append(" And i.recycleaction <> '0' ");
		} else if (sessionBean.getSuperRole()) {
			where.append(" where  n.projectId='" + sessionBean.getProjectId() + "'");
			where.append(" AND i.classid='1'");
			where.append(" And i.recycleaction <> '0' ");
			//where.append(" And n.vmstate not in ('state_deleting' ) ");
		} else {
			where.append(" where  n.projectId='" + sessionBean.getProjectId() + "'");
			where.append(" AND n.owner='" + sessionBean.getUserId() + "'");
			where.append(" AND i.classid='1'");
			where.append(" And i.recycleaction <> '0' ");
			//where.append(" And n.vmstate not in ('state_deleting' ) ");
		}
		countSqlBuffer.append(where.toString());
		listSqlBuffer.append(where.toString());
		String searchValue = entity.getSearchValue();
		StringBuffer like = new StringBuffer();
		if (searchValue != null && !"".equals(entity.getSearchValue())) {
			like.append(" AND n.hostname like '%" + searchValue + "%' ");
			listSqlBuffer.append(like.toString());
			countSqlBuffer.append(like.toString());
		}

		List<Map<String, Object>> countList = sqlQueryBiz.queryBySql(countSqlBuffer.toString());
		PageModel<Map<String, Object>> pages = new PageModel<Map<String, Object>>();
		pages.setPageNo(entity.getPageNo() * entity.getPageSize());
		pages.setPageSize(entity.getPageSize());
		pages.setTotalRecords(StrUtils.tranInteger(countList.get(0).get("count")));
		if (pages.getTotalRecords() > 0) {
			if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())) {
				listSqlBuffer.append(" ORDER BY " + entity.getColumnName() + " " + entity.getAsSorting());
			} else { // 默认排序
				listSqlBuffer.append(" ORDER BY updateddate desc");
			}
			if (entity.getPageSize() != -1) {
				listSqlBuffer.append(
						" LIMIT " + entity.getPageSize() + " OFFSET " + entity.getPageNo() * entity.getPageSize());
			}

			listSql = listSqlBuffer.toString();
			List<Map<String, Object>> resultList = trans(sqlQueryBiz.queryBySql(listSql));
			if (resultList == null) {
				pages.setDatas(new ArrayList<Map<String, Object>>());
			} else {
				pages.setDatas(resultList);
			}
		} else {
			pages.setDatas(new ArrayList<Map<String, Object>>());
		}
		return pages;
	}

	@Override
	public PageModel<Map<String, Object>> findVolumeCompleteForPage(PageEntity entity) {
		String listSql = getSession().getNamedQuery(SqlQueryProperty.QUERY_RECYCLEITEMS_VOLUME).getQueryString();
		StringBuffer listSqlBuffer = new StringBuffer();
		listSqlBuffer.append(listSql);

		String countSql = getSession().getNamedQuery(SqlQueryProperty.QUERY_RECYCLEITEMS_VOLUME_COUNT).getQueryString();
		StringBuffer countSqlBuffer = new StringBuffer();
		countSqlBuffer.append(countSql);
		//云硬盘云硬盘
		// 根据情况增加过滤条件
		SessionBean sessionBean = getSessionBean();
		StringBuffer where = new StringBuffer();
		if (sessionBean.getSuperUser()) {
			where.append(" where i.classid='2'");
			where.append(" AND v.deleted = '1'");
		} else if (sessionBean.getSuperRole()) {
			where.append(" where v.projectId='" + sessionBean.getProjectId() + "'");
			where.append(" AND i.classid='2'");
			where.append(" AND v.deleted = '1'");
		} else {
			where.append(" where v.projectId='" + sessionBean.getProjectId() + "'");
			where.append(" AND v.owner2='" + sessionBean.getUserId() + "'");
			where.append(" AND i.classid='2'");
			where.append(" AND v.deleted = '1'");
		}
		countSqlBuffer.append(where.toString());
		listSqlBuffer.append(where.toString());

		String searchValue = entity.getSearchValue();
		StringBuffer like = new StringBuffer();
		if (searchValue != null && !"".equals(entity.getSearchValue())) {
			like.append(" AND v.name like '%" + searchValue + "%' ");
			listSqlBuffer.append(like.toString());
			countSqlBuffer.append(like.toString());
		}

		List<Map<String, Object>> countList = sqlQueryBiz.queryBySql(countSqlBuffer.toString());
		PageModel<Map<String, Object>> pages = new PageModel<Map<String, Object>>();
		pages.setPageNo(entity.getPageNo() * entity.getPageSize());
		pages.setPageSize(entity.getPageSize());
		pages.setTotalRecords(StrUtils.tranInteger(countList.get(0).get("count")));
		if (pages.getTotalRecords() > 0) {
			if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())) {
				listSqlBuffer.append(" ORDER BY " + entity.getColumnName() + " " + entity.getAsSorting());
			} else { // 默认排序
				listSqlBuffer.append(" ORDER BY updateddate desc");
			}
			if (entity.getPageSize() != -1) {
				listSqlBuffer.append(
						" LIMIT " + entity.getPageSize() + " OFFSET " + entity.getPageNo() * entity.getPageSize());
			}

			listSql = listSqlBuffer.toString();
			List<Map<String, Object>> resultList = trans(sqlQueryBiz.queryBySql(listSql));
			if (resultList == null) {
				pages.setDatas(new ArrayList<Map<String, Object>>());
			} else {
				pages.setDatas(resultList);
			}
		} else {
			pages.setDatas(new ArrayList<Map<String, Object>>());
		}
		return pages;
	}

	@SuppressWarnings("deprecation")
	public List<Map<String, Object>> trans(List<Map<String, Object>> resultList) {
		for (Map<String, Object> map : resultList) {

			String ips = (String) map.get("ips");
			if (StrUtils.checkParam(ips)) {
				ips = ips.replaceAll("T", ",");
				ips = ips.replaceAll("F", ",");
				ips=ips.substring(0, ips.length()-1);
				map.put("ips", ips);
			}
			Timestamp inboundtime = (Timestamp) map.get("inboundtime");
			if (StrUtils.checkParam(map.get("inboundtime"))) {
				map.put("inboundtime1", inboundtime.toLocaleString());
			}
			Timestamp recycletime = (Timestamp) map.get("recycletime");
			if (StrUtils.checkParam(map.get("recycletime"))) {

				map.put("recycletime1", recycletime.toLocaleString());
			}

		}
		return resultList;
	}
}
