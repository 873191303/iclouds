package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.BackupNode2TasksDao;
import com.h3c.iclouds.po.BackupNode2Tasks;
import com.h3c.iclouds.utils.StrUtils;


@Repository("backupNode2TasksDao")
public class BackupNode2TasksDaoImpl extends BaseDAOImpl<BackupNode2Tasks> implements BackupNode2TasksDao {
	@Override
	public PageModel<BackupNode2Tasks> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(BackupNode2Tasks.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {	// 模糊查询
            criteria.add(Restrictions.or(
                    Restrictions.like("taskName", "%" + entity.getSearchValue() + "%")
            ));
        }
        if (null != entity.getSpecialParams() && entity.getSpecialParams().length > 0) {
        	criteria.add(Restrictions.in("backupNodeId", entity.getSpecialParams())); // in  
        } else {
        	criteria.add(Restrictions.eq("backupNodeId", "-1"));
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
        return this.findForPage(BackupNode2Tasks.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
}
