package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.StorageClustersDao;
import com.h3c.iclouds.po.StorageClusters;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@Repository("storageClustersDao")
public class StorageClustersDaoImpl extends BaseDAOImpl<StorageClusters> implements StorageClustersDao {

    /**
     * 分页查询
     * @param entity
     * @return
     */
    @Override
    public PageModel<StorageClusters> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(StorageClusters.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {//根据搜索条件模糊查询
            criteria.add(Restrictions.or(
                    Restrictions.like("name", "%" + entity.getSearchValue() + "%")
            ));
        }
        if(StrUtils.checkParam(entity.getSpecialParam())) {
            if (entity.getSpecialParam().equals("alone")){//独立存储列表
                criteria.add(Restrictions.eq("type", "0"));
            } else if (entity.getSpecialParam().equals("cluster")){//集群列表
                criteria.add(Restrictions.eq("type", "1"));
            } else {//管理组下的集群列表
                criteria.add(Restrictions.eq("gid", entity.getSpecialParam()));
            }
        }
        Map<String, String> order = new HashMap<String, String>();
        addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
        return this.findForPage(StorageClusters.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }
}
