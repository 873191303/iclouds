package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.Specs2KeyValueDao;
import com.h3c.iclouds.po.business.Specs2KeyValue;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/1/12.
 */
@Repository("specs2KeyValueDao")
public class Specs2KeyValueDaoImpl extends BaseDAOImpl<Specs2KeyValue> implements Specs2KeyValueDao {

    @Override
    public PageModel<Specs2KeyValue> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(Specs2KeyValue.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {//查询条件
            criteria.add(Restrictions.or(
                    Restrictions.like("value", "%" + entity.getSearchValue() + "%")
            ));
        }
        if(StrUtils.checkParam(entity.getSpecialParam())) {
            criteria.add(Restrictions.eq("key", entity.getSpecialParam()));
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
        return this.findForPage(Specs2KeyValue.class, criteria, order, entity.getPageNo(), entity.getPageSize());

    }
}
