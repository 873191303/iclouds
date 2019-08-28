package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.PortDao;
import com.h3c.iclouds.po.Port;
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
 * Created by yKF7408 on 2016/11/26.
 */
@Repository("portDao")
public class PortDaoImpl extends BaseDAOImpl<Port> implements PortDao {
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
    @Override
    public PageModel<Port> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(Port.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {	// 模糊查询(根据网卡名称和网络名称)
			List<String> portIds = getPortIdsByNetworkName(entity.getSearchValue());
            criteria.add(StrUtils.checkCollection(portIds) ?
					Restrictions.or(Restrictions.like("name", "%" + entity.getSearchValue() + "%"),
							Restrictions.in("id", portIds)
					) : Restrictions.like("name", "%" + entity.getSearchValue() + "%")
			);
        }
        if (null != entity.getSpecialParam() && !"".equals(entity.getSpecialParam())) {
        	criteria.add(Restrictions.eq("tenantId", entity.getSpecialParam()));
        }
        if (entity.getSpecialParams() != null && entity.getSpecialParams().length > 0){
            criteria.add(Restrictions.in("id", entity.getSpecialParams()));
        }
		Map<String, Object> queryMap = entity.getQueryMap();
        String userId = StrUtils.tranString(queryMap.get("userId"));
        String deviceOwner = StrUtils.tranString(queryMap.get("deviceOwner"));
        String deviceId = StrUtils.tranString(queryMap.get("deviceId"));
        String flag = StrUtils.tranString(queryMap.get("flag"));
        if (StrUtils.checkParam(userId)) {
			criteria.add(Restrictions.eq("userId", userId));
		}
		if (StrUtils.checkParam(deviceOwner)) {
			criteria.add(Restrictions.like("deviceOwner", "%" + deviceOwner + "%"));
		}
		if (StrUtils.checkParam(deviceId)) {
			criteria.add(Restrictions.eq("deviceId", deviceId));
		}
		if (StrUtils.checkParam(flag)) {
        	if ("attachable".equals(flag)) {
				criteria.add(Restrictions.isNull("deviceId"));
			}
			if ("port".equals(flag)) {
				criteria.add(Restrictions.or(Restrictions.like("deviceOwner", "%compute%"),
						Restrictions.isNull("deviceOwner")));
			}
		}
        Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
        return this.findForPage(Port.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }


	@SuppressWarnings("unchecked")
	@Override
	public List<Port> getPorts() {
		List<Port> list = new ArrayList<Port>();
		Criteria criteria = getSession().createCriteria(Port.class);
		this.authenFilter(criteria);
		criteria.add(Restrictions.or(Restrictions.not(Restrictions.in("deviceOwner", portOwnerList())),Restrictions.isNull("deviceOwner")));
		try {
			list = criteria.list();	
		} catch (Exception e) {
			this.exception(e);
			e.printStackTrace();
		} 
		return list;
	}
	
	private List<String> portOwnerList() {
		List<String> owners = new ArrayList<String>();
		owners.add(ConfigProperty.PORT_OWNER_FLOATINGIP);
		owners.add(ConfigProperty.PORT_OWNER_ROUTE_GATEWAY);
		owners.add(ConfigProperty.PORT_OWNER_ROUTE_INTERFACE);
		owners.add(ConfigProperty.PORT_OWNER_LOADBANLANCER);
		return owners;
	}
	
	private void authenFilter(Criteria criteria) {
		boolean isAdmin = this.getSessionBean().getSuperUser();
		boolean isSuperRole = this.getSessionBean().getSuperRole();
		if (!isAdmin) {
			if (isSuperRole) {
				criteria.add(Restrictions.eq("tenantId", this.getProjectId()));
			} else {
				criteria.add(Restrictions.eq("userId", this.getSessionUserId()));
			}
		}
	}
	
	private List<String> getPortIdsByNetworkName(String networkName) {
    	Map<String, Object> queryMap = StrUtils.createMap("networkName", "%" + networkName + "%");
		List<String> portIds = new ArrayList<>();
		List<Map<String, Object>> list = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_PORT_BY_NETWORK_NAME, queryMap);
		if (StrUtils.checkCollection(list)) {
			for (Map<String, Object> map : list) {
				String portId = StrUtils.tranString(map.get("portid"));
				portIds.add(portId);
			}
		}
		return portIds;
	}
}
