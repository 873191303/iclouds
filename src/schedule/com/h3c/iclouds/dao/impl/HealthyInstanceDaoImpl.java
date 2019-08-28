package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.HealthyInstanceDao;
import com.h3c.iclouds.po.HealthyInstance;
import com.h3c.iclouds.po.HealthyValue;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Repository("healthyInstanceDao")
public class HealthyInstanceDaoImpl extends BaseDAOImpl<HealthyInstance> implements HealthyInstanceDao {

    @Override
    public PageModel<HealthyInstance> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(HealthyInstance.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {	// 模糊查询
            criteria.add(Restrictions.or(
                Restrictions.like("instanceId", "%" + entity.getSearchValue() + "%"),
                Restrictions.like("instanceName", "%" + entity.getSearchValue() + "%")
            ));
        }
        if(entity.getQueryMap().containsKey("type")) {
            criteria.add(Restrictions.eq("type", entity.getQueryMap().get("type").toString()));
        }
        if(entity.getQueryMap().containsKey("status")) {
            criteria.add(Restrictions.eq("status", entity.getQueryMap().get("status").toString()));
        }
        Map<String, String> order = new HashMap<>();
        this.addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
        return this.findForPage(HealthyInstance.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }

    @Override
    public List<HealthyValue> data(String id, Date start, Date end) {
        Criteria criteria = getSession().createCriteria(HealthyValue.class);
        criteria.add(Restrictions.eq("instanceId", id));
        criteria.add(Restrictions.le("collectTime", end));
        criteria.add(Restrictions.ge("collectTime", start));
        criteria.addOrder(Order.asc("collectTime"));
        return criteria.list();
    }
}
