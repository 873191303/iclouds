package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.ListPriceDao;
import com.h3c.iclouds.po.business.ListPrice;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/1/12.
 */
@Repository("listPriceDao")
public class ListPriceDaoImpl extends BaseDAOImpl<ListPrice> implements ListPriceDao {

    @Override
    public PageModel<ListPrice> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(ListPrice.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {//查询条件
            criteria.add(Restrictions.or(
                    Restrictions.like("name", "%" + entity.getSearchValue() + "%")
            ));
        }
        if(StrUtils.checkParam(entity.getSpecialParam())) {
            criteria.add(Restrictions.eq("classId", entity.getSpecialParam()));
        }
        Map<String, String> order = new HashMap<String, String>();
        addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
        return this.findForPage(ListPrice.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }

    @Override
    public ListPrice findLastModifyPrice(String classId) {
        Criteria criteria = getSession().createCriteria(ListPrice.class);
        criteria.add(Restrictions.eq("classId", classId));
        criteria.addOrder(Order.desc("updatedDate"));
        List<ListPrice> list = criteria.list();
        return StrUtils.checkCollection(list) ? list.get(0) : null;
    }
}
