package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.RouteDao;
import com.h3c.iclouds.po.Route;
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
@Repository("routeDao")
public class RouteDaoImpl extends BaseDAOImpl<Route> implements RouteDao {

    @Resource
    private ProjectBiz projectBiz;

    @Override
    public PageModel<Route> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(Route.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {	// 模糊查询
            criteria.add(Restrictions.or(
                    Restrictions.like("name", "%" + entity.getSearchValue() + "%")
            ));
        }
        Map<String, Object> queryMap = entity.getQueryMap();
        String flag = StrUtils.tranString(queryMap.get("flag"));//判断是否为其它资源的上级列表
        String projectId = StrUtils.tranString(queryMap.get("projectId"));//租户过滤
        projectId = projectBiz.getFilterProjectId(projectId, flag);
        if (null != projectId) {
            criteria.add(Restrictions.eq("tenantId", projectId));
        }
        if (StrUtils.checkParam(flag) && flag.equals("normal")){
            criteria.add(Restrictions.not(Restrictions.in("status", BaseRestControl.getExceptionStatus())));
        }
        Map<String, String> order = new HashMap<String, String>();
        addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
        return this.findForPage(Route.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }
}
