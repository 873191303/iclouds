package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.DifferencesDao;
import com.h3c.iclouds.po.Differences;
import com.h3c.iclouds.utils.StrUtils;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Repository("differencesDao")
public class DifferencesDaoImpl extends BaseDAOImpl<Differences> implements DifferencesDao {

    @Override
    public PageModel<Differences> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(Differences.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {	// 模糊查询
            criteria.add(Restrictions.or(
                Restrictions.like("description", "%" + entity.getSearchValue() + "%"),
                Restrictions.like("projectName", "%" + entity.getSearchValue() + "%")
            ));
        }
        Map<String, Object> queryMap = entity.getQueryMap();
        criteria.add(Restrictions.eq("syncDate", queryMap.get("syncDate")));
        Map<String, String> order = new HashMap<String, String>();
        if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
            if (entity.getAsSorting().equals("asc")){
                order.put(entity.getColumnName().toString(), "asc");
            } else {
                order.put(entity.getColumnName().toString(), "desc");
            }
        } else {
            order.put("todoTime", "desc");
        }
        return this.findForPage(Differences.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }

}
