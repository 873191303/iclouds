package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.OriginalsDao;
import com.h3c.iclouds.po.Originals;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Repository("originalsDao")
public class OriginalsDaoImpl extends BaseDAOImpl<Originals> implements OriginalsDao {

    @Override
    public List<Originals> findLastData(String vassetId, String type) {
        Criteria criteria = getSession().createCriteria(Originals.class);
        criteria.add(Restrictions.eq("vassetId", vassetId));
        criteria.add(Restrictions.eq("type", type));
        criteria.addOrder(Order.desc("collectTime"));
        criteria.setMaxResults(1);
        return criteria.list();
    }

    @Override
    public List<Originals> findOneDateData(String vassetId, Date start, Date end) {
        String hql = "SELECT new Originals(value, type, collectTime) FROM Originals ";
        hql += "WHERE collectTime >= :start AND collectTime <= :end AND vassetId = :vassetId ORDER BY collectTime asc";
        Map<String, Object> queryMap = StrUtils.createMap("vassetId", vassetId);
        queryMap.put("start", start);
        queryMap.put("end", end);
        queryMap.put("vassetId", vassetId);
        return this.findByHql(hql, queryMap);
    }
}
