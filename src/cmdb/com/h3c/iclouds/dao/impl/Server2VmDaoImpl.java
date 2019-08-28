package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.Server2VmDao;
import com.h3c.iclouds.po.Server2Vm;
import com.h3c.iclouds.utils.StrUtils;

/**
 * Created by yKF7317 on 2016/11/9.
 */
@Repository("server2VmDao")
public class Server2VmDaoImpl extends BaseDAOImpl<Server2Vm> implements Server2VmDao {
    /**
     * 分页查询
     * @param entity
     * @return
     */
    @Override
    public PageModel<Server2Vm> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(Server2Vm.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {//根据搜索条件模糊查询
            criteria.add(Restrictions.or(
                    Restrictions.like("vmName", "%" + entity.getSearchValue() + "%")
            ));
        }
        if(StrUtils.checkParam(entity.getSpecialParam())) {
            criteria.add(Restrictions.eq("hostId", entity.getSpecialParam()));
        }
        if(entity.getSpecialParams() != null &&entity.getSpecialParams().length > 0) {
            criteria.add(Restrictions.in("hostId", entity.getSpecialParams()));
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
        return this.findForPage(Server2Vm.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }
}
