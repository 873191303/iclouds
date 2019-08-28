package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.StorageVolumsDao;
import com.h3c.iclouds.po.StorageVolums;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@Repository("storageVolumsDao")
public class StorageVolumsDaoImpl extends BaseDAOImpl<StorageVolums> implements StorageVolumsDao {

    /**
     * 分页查询
     * @param entity
     * @return
     */
    @Override
    public PageModel<StorageVolums> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(StorageVolums.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {//根据搜索条件模糊查询
            criteria.add(Restrictions.or(
                    Restrictions.like("volumeName", "%" + entity.getSearchValue() + "%")
            ));
        }
        if (StrUtils.checkParam(entity.getSpecialParam())){
            criteria.add(Restrictions.eq("sid", entity.getSpecialParam()));
        }
        if (entity.getSpecialParams() != null && entity.getSpecialParams().length > 0){
            criteria.add(Restrictions.in("sid", entity.getSpecialParams()));//查询某个集群集合下的所有挂载信息
        }
        Map<String, String> order = new HashMap<String, String>();
        addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
        return this.findForPage(StorageVolums.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }


}
