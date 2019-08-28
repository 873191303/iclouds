package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.StorageManageDao;
import com.h3c.iclouds.po.StorageManage;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@Repository("storageManageDao")
public class StorageManageDaoImpl extends BaseDAOImpl<StorageManage> implements StorageManageDao {

    /**
     * 分页查询
     * @param entity
     * @return
     */
    @Override
    public PageModel<StorageManage> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(StorageManage.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {//根据搜索条件模糊查询
            criteria.add(Restrictions.or(
                    Restrictions.like("name", "%" + entity.getSearchValue() + "%")
            ));
        }
        if (StrUtils.checkParam(entity.getSpecialParam())){
            criteria.add(Restrictions.eq("gid", entity.getSpecialParam()));
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
        return this.findForPage(StorageManage.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }
}
