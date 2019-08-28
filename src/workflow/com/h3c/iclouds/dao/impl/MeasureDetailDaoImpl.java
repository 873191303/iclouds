package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.MeasureDetailDao;
import com.h3c.iclouds.po.business.MeasureDetail;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/1/12.
 */
@Repository("measureDetailDao")
public class MeasureDetailDaoImpl extends BaseDAOImpl<MeasureDetail> implements MeasureDetailDao {

    @Override
    public PageModel<MeasureDetail> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(MeasureDetail.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {//查询条件
            criteria.add(Restrictions.or(
                    Restrictions.like("description", "%" + entity.getSearchValue() + "%"),
                    Restrictions.like("type", "%" + entity.getSearchValue() + "%"),
                    Restrictions.like("userName", "%" + entity.getSearchValue() + "%"),
                    Restrictions.like("projectName", "%" + entity.getSearchValue() + "%"),
                    Restrictions.like("instanceType", "%" + entity.getSearchValue() + "%"),
                    Restrictions.like("instanceName", "%" + entity.getSearchValue() + "%")
            ));
        }
        Map<String, Object> queryMap = entity.getQueryMap();
        if (this.getSessionBean().getSuperUser()) {
            if (StrUtils.checkParam(queryMap.get("projectId"))) {
                criteria.add(Restrictions.eq("tenantId", StrUtils.tranString(queryMap.get("projectId"))));
            }
        } else {
            if (this.getSessionBean().getSuperRole()) {
                criteria.add(Restrictions.eq("tenantId", this.getProjectId()));
            } else {
                criteria.add(Restrictions.eq("userId", this.getSessionBean().getUserId()));
            }
        }
        if (StrUtils.checkParam(entity.getSpecialParam())){
            criteria.add(Restrictions.eq("instanceId", entity.getSpecialParam()));
        }
        Map<String, String> order = new HashMap<String, String>();
        addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
        return this.findForPage(MeasureDetail.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }
}
