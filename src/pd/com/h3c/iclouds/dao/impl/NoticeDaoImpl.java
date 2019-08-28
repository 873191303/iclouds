package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.NoticeDao;
import com.h3c.iclouds.po.Notice;
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
* @author  zKF7420
* @date 2017年1月11日 上午11:09:54
*/

@Repository(value = "noticeDao")
public class NoticeDaoImpl extends BaseDAOImpl<Notice> implements NoticeDao {
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Override
	public PageModel<Notice> findForPage(PageEntity entity) {
		  Criteria criteria = getSession().createCriteria(Notice.class);
	        // 查询方式
	        if(!"".equals(entity.getSearchValue())) {	// 模糊查询
	            criteria.add(Restrictions.or(
	                    Restrictions.like("title", "%" + entity.getSearchValue() + "%"),
	    				Restrictions.like("brief", "%" + entity.getSearchValue() + "%"),
	    				Restrictions.like("grade", "%" + entity.getSearchValue() + "%"),
	    				Restrictions.like("status", "%" + entity.getSearchValue() + "%")
	            ));
	        }
	        
	        if (null !=entity.getSpecialParam() && !"".equals(entity.getSpecialParam())){
	            criteria.add(Restrictions.eq("tenantId", entity.getSpecialParam()));
	        }
	        if (!this.getSessionBean().getSuperUser()) {
	        	criteria.add(Restrictions.eq("status", ConfigProperty.NOTICE_STATUS2_USE));  // 普通租户只能看到发布状态的通知公告
	        }
	        if (null != entity.getSpecialParams() && entity.getSpecialParams().length > 0){
	            criteria.add(Restrictions.in("id", entity.getSpecialParams()));
	        }
	        criteria.add(Restrictions.eq("deleted", "0")); 
	        
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
	        return this.findForPage(Notice.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
	
	@Override
	public PageModel<Notice> findForPageByTenant(PageEntity entity) {
		String listSql = getSession().getNamedQuery(SqlQueryProperty.QUERY_NOVAVM_LIST).getQueryString();
		StringBuffer listSqlBuffer = new StringBuffer();
		listSqlBuffer.append(listSql);
		
		String countSql = getSession().getNamedQuery(SqlQueryProperty.QUERY_NOVAVM_LIST_COUNT).getQueryString();
		StringBuffer countSqlBuffer = new StringBuffer();
		countSqlBuffer.append(countSql);
		//根据情况增加过滤条件
		StringBuffer where = new StringBuffer();
		//过滤条件为不在回收站中有记录
		boolean isSuperRole = this.getSessionBean().getSuperRole();
		if (isSuperRole) {
			where.append(" AND n.projectId='" + getProjectId()+"'");
		} else {
			where.append(" AND n.owner='" + this.getSessionUserId() +"'");
		}
		where.append(" AND n.vmstate in ('state_stop','state_normal')");
		countSqlBuffer.append(where.toString());
		listSqlBuffer.append(where.toString());
		
		String searchValue = entity.getSearchValue();
		StringBuffer like = new StringBuffer();
		if(searchValue != null && !"".equals(entity.getSearchValue())) {
			like.append("AND n.hostname like '%" + searchValue + "%' ");
			listSqlBuffer.append(like.toString());
			countSqlBuffer.append(like.toString());
		}
		List<Map<String, Object>> countList = sqlQueryBiz.queryBySql(countSqlBuffer.toString());
		PageModel<Notice> pages = new PageModel<Notice>();
		pages.setPageNo(entity.getPageNo() * entity.getPageSize());
		pages.setPageSize(entity.getPageSize());
		pages.setTotalRecords(StrUtils.tranInteger(countList.get(0).get("count")));
		if(pages.getTotalRecords() > 0) {
			if(StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())) {
				listSqlBuffer.append(" ORDER BY " + entity.getColumnName() + " " + entity.getAsSorting());
			} else {	// 默认排序
				listSqlBuffer.append(" ORDER BY n.updateddate desc");
			}
			if (entity.getPageSize()!=-1) {
				listSqlBuffer.append(" LIMIT " + entity.getPageSize() + " OFFSET " + entity.getPageNo() * entity.getPageSize());
			}
			listSql = listSqlBuffer.toString();
			List<Map<String, Object>> resultList = sqlQueryBiz.queryBySql(listSql);
			List<Notice> data = new ArrayList<>();
			for (Map<String, Object> map : resultList) {
				Notice notice = new Notice();
				InvokeSetForm.settingForm(map, notice);
				data.add(notice);
			}
			pages.setDatas(data);
		} else {
			pages.setDatas(new ArrayList<Notice>());
		}
		return pages;
	}
}
