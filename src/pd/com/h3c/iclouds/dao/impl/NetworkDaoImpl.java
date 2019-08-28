package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.NetworkDao;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.utils.StrUtils;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Repository
public class NetworkDaoImpl extends BaseDAOImpl<Network> implements NetworkDao {
    
    @Override
    public PageModel<Network> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(Network.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {	// 模糊查询
            criteria.add(Restrictions.or(
                    Restrictions.like("name", "%" + entity.getSearchValue() + "%")
            ));
        }
        
        if (null != entity.getSpecialParam() && "public".equals(entity.getSpecialParam())) {
            criteria.add(Restrictions.eq("externalNetworks", true));
        } else {
            criteria.add(Restrictions.eq("externalNetworks", false));
            Map<String, Object> queryMap = entity.getQueryMap();
            String flag = StrUtils.tranString(queryMap.get("flag"));//判断是否为其它资源的上级列表
            boolean isAdmin = this.getSessionBean().getSuperUser();
            if (!isAdmin){
                criteria.add(Restrictions.eq("tenantId", this.getProjectId()));
            }
            if (StrUtils.checkParam(flag)){
                criteria.add(Restrictions.not(Restrictions.in("status", BaseRestControl.getExceptionStatus())));
                if (isAdmin){
                    String projectId = StrUtils.tranString(queryMap.get("projectId"));//租户过滤
                    if (StrUtils.checkParam(projectId)) {
                        criteria.add(Restrictions.eq("tenantId", projectId));
                    } else {
                        criteria.add(Restrictions.eq("tenantId", this.getProjectId()));
                    }
                }
                if (flag.equals("normal")) {//用于负载均衡连接的网络列表（管理员只能获取到自己所属租户的列表，只能获取到正常状态且已连接路由器的网络列表）
                    criteria.add(Restrictions.isNotNull("routeId"));
                } else if (flag.equals("router")) {//用于路由器连接私有网络列表（没有连接路由器的网络）
                    String routerId = StrUtils.tranString(queryMap.get("routeId"));
                    if (StrUtils.checkParam(routerId)) {
                        criteria.add(Restrictions.eq("routeId", routerId));
                    } else {
                        criteria.add(Restrictions.isNull("routeId"));
                    }
                }
            }
        }
        Map<String, String> order = new HashMap<String, String>();
        addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
        return this.findForPage(Network.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }
    
}
