package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.StorageGroups2ItemsDao;
import com.h3c.iclouds.po.StorageGroups2Items;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@Repository("storageGroups2ItemsDao")
public class StorageGroups2ItemsDaoImpl extends BaseDAOImpl<StorageGroups2Items> implements StorageGroups2ItemsDao {

    /**
     * 分页查询
     * @param entity
     * @return
     */
    @Override
    public PageModel<StorageGroups2Items> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(StorageGroups2Items.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {//根据搜索条件模糊查询
            criteria.add(Restrictions.or(
                    Restrictions.like("cname", "%" + entity.getSearchValue() + "%")
            ));
        }
        if (StrUtils.checkParam(entity.getSpecialParam())){
            criteria.add(Restrictions.eq("cid", entity.getSpecialParam()));
        }
        Map<String, String> order = new HashMap<String, String>();
        addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
        return this.findForPage(StorageGroups2Items.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }

}
