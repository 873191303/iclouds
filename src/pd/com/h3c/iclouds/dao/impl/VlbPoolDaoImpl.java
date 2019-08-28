package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.VlbPoolDao;
import com.h3c.iclouds.po.VlbPool;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Repository("vlbPoolDao")
public class VlbPoolDaoImpl extends BaseDAOImpl<VlbPool> implements VlbPoolDao {

    @Resource
    private ProjectBiz projectBiz;

    @Override
    public PageModel<VlbPool> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(VlbPool.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {	// 模糊查询
            criteria.add(Restrictions.or(
                    Restrictions.like("name", "%" + entity.getSearchValue() + "%")
            ));
        }
        if (null != entity.getSpecialParams() && entity.getSpecialParams().length > 0){
            String [] deviceIds = entity.getSpecialParams();
            criteria.add(Restrictions.in("id", deviceIds));
            if (StrUtils.checkParam(entity.getSpecialParam())) {
                criteria.add(Restrictions.eq("tenantId", entity.getSpecialParam()));
            }
        } else {
            Map<String, Object> queryMap = entity.getQueryMap();
            String projectId = StrUtils.tranString(queryMap.get("projectId"));//租户过滤
            projectId = projectBiz.getFilterProjectId(projectId, null);
            if (null != projectId) {
                criteria.add(Restrictions.eq("tenantId", projectId));
            }
        }
        Map<String, String> order = new HashMap<String, String>();
        addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
        return this.findForPage(VlbPool.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }

}
