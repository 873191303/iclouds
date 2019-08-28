package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.po.bean.model.AzoneBean;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.dao.Project2AzoneDao;
import com.h3c.iclouds.po.Project2Azone;
import com.h3c.iclouds.utils.StrUtils;

@Repository("project2AzoneDao")
public class Project2AzoneDaoImpl extends BaseDAOImpl<Project2Azone> implements Project2AzoneDao{
		@SuppressWarnings("unchecked")
		@Override
	    public List<Project2Azone> getProject2Azone(Project2Azone project2Azone) {
	        Criteria criteria = getSession().createCriteria(Project2Azone.class);
	        criteria.add(Restrictions.eq("deleted", 0));
	        criteria.add(Restrictions.eq("id", project2Azone.getId()));
	        return criteria.list();
	    }
		
		@SuppressWarnings("unchecked")
		@Override
	    public List<Project2Azone> getProject2Azone(String projectId) {
	        Criteria criteria = getSession().createCriteria(Project2Azone.class);
	        criteria.add(Restrictions.eq("deleted", "0"));
	        criteria.add(Restrictions.eq("id",projectId));
	        return criteria.list();
	    }
		
		@Override
	    public void save(List<AzoneBean> ids, String id) {
			if (StrUtils.checkCollection(ids)) {
				for (AzoneBean azoneBean : ids) {
					Project2Azone project2Azone = new Project2Azone();
					project2Azone.setIyuUuid(azoneBean.getUuid());
					project2Azone.setId(id);// 租户id
					project2Azone.setDeleted(ConfigProperty.YES);
					add(project2Azone);
				}
			}
	    }
		@Override
		public void update(List<AzoneBean> ids, String id) {
			Map<String, Object> deleteMap = new HashMap<>();
			deleteMap.put("id", id);
			delete(deleteMap, Project2Azone.class);// 先删除之前的租户与可用域的关联关系
			save(ids, id);// 保存新的关联关系
		}
		
}
