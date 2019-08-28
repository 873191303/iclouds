package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.Master2LogDao;
import com.h3c.iclouds.po.Master2Log;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/5.
 */
@Repository("master2LogDao")
public class Master2LogDaoImpl extends BaseDAOImpl<Master2Log> implements Master2LogDao {

    /**
     * 分页查询
     * @param entity
     * @return
     */
    @Override
    public PageModel<Master2Log> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(Master2Log.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {//根据搜索条件模糊查询
            criteria.add(Restrictions.or(
                    Restrictions.like("chOwner", "%" + entity.getSearchValue() + "%")
            ));
        }

        if(entity.getSpecialParam() != null) {
            criteria.add(Restrictions.eq("assetId", entity.getSpecialParam()));
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
        return this.findForPage(Master2Log.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }

}
