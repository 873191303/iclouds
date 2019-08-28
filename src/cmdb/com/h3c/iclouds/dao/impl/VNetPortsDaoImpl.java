package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.VNetportsDao;
import com.h3c.iclouds.po.VNetports;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/9.
 */
@Repository("vNetPortsDao")
public class VNetPortsDaoImpl extends BaseDAOImpl<VNetports> implements VNetportsDao {
    /**
     * 分页查询
     * @param entity
     * @return
     */
    @Override
    public PageModel<VNetports> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(VNetports.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {//根据搜索条件模糊查询
            criteria.add(Restrictions.or(
                    Restrictions.like("portType", "%" + entity.getSearchValue() + "%")
            ));
        }
        if(entity.getSpecialParam() != null) {
            criteria.add(Restrictions.eq("vmId", entity.getSpecialParam()));
        }
        Map<String, String> order = new HashMap<String, String>();
        if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
            if (entity.getAsSorting().equals("asc")){
                order.put(entity.getColumnName().toString(), "asc");
            } else {
                order.put(entity.getColumnName().toString(), "desc");
            }
        } else {
            order.put("updatedDate", "desc");//默认根据修改日期排序
        }
        return this.findForPage(VNetports.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }
}
