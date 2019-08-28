package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.FirewallDao;
import com.h3c.iclouds.dao.RouteDao;
import com.h3c.iclouds.po.Firewall;
import com.h3c.iclouds.po.Route;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Repository("firewallDao")
public class FirewallDaoImpl extends BaseDAOImpl<Firewall> implements FirewallDao {

    @Resource
    private ProjectBiz projectBiz;

    @Resource
    private RouteDao routeDao;

    @Override
    public PageModel<Firewall> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(Firewall.class);
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
            if (StrUtils.checkParam(flag) && flag.equals("normal")){
                criteria.add(Restrictions.not(Restrictions.in("status", BaseRestControl.getExceptionStatus())));
                List<Route> routes = routeDao.findByPropertyName(Route.class, "tenantId", projectId);
                if (StrUtils.checkParam(routes)){
                    List<String> fwIds = new ArrayList<>();
                    for (Route route : routes) {
                        String fwId = route.getFwId();
                        if (StrUtils.checkParam(fwId)){
                            fwIds.add(fwId);
                        }
                    }
                    if (StrUtils.checkParam(fwIds)){
                        criteria.add(Restrictions.not(Restrictions.in("id", fwIds.toArray())));
                    }
                }
            }
        }
        Map<String, String> order = new HashMap<String, String>();
        addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
        return this.findForPage(Firewall.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }

}
