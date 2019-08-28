package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.VolumeDao;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("volumeDao")
public class VolumeDaoImpl extends BaseDAOImpl<Volume> implements VolumeDao {

	@Override
	public PageModel<Volume> findForPage(PageEntity entity) {
		  Criteria criteria = getSession().createCriteria(Volume.class);
	        // 查询方式
	        if(!"".equals(entity.getSearchValue())) {	// 模糊查询
	            criteria.add(Restrictions.or(
	                    Restrictions.like("name", "%" + entity.getSearchValue() + "%"),
	                    Restrictions.like("status", "%" + entity.getSearchValue() + "%"),
	                    Restrictions.like("flavorName", "%" + entity.getSearchValue() + "%"),
	                    Restrictions.like("host", "%" + entity.getSearchValue() + "%")
	            ));
	        }
	        
	        boolean isAdmin = this.getSessionBean().getSuperUser();	
	        boolean isSuperRole = this.getSessionBean().getSuperRole();
	        if (!isAdmin){
	        	if (isSuperRole) {
	        		criteria.add(Restrictions.eq("projectId", this.getProjectId()));
	        	} else {
	        		criteria.add(Restrictions.eq("owner2", this.getSessionUserId()));
	        	}
	        }
	        
	        criteria.add(Restrictions.eq("deleted", "0"));
	        Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
	        return this.findForPage(Volume.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Volume> findVolume(NovaVm novaVm) {
		// TODO Auto-generated method stub
		Criteria criteria = getSession().createCriteria(Volume.class);
		criteria.add(Restrictions.or(Restrictions.eq("volumeType", "1"),Restrictions.eq("volumeType", "2")));
		criteria.add(Restrictions.eq("host", novaVm.getId()));
		List<Volume> volumes=criteria.list();
		return volumes;
	}
	
	@Override
	public int monthCount () {
		Criteria criteria = getSession().createCriteria(Volume.class);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		criteria.add(Restrictions.ge("createdDate", calendar.getTime()));
		Long result = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		return null == result ? 0 : result.intValue();
	}
}
