package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.NetPortsDao;
import com.h3c.iclouds.po.FirewallGroups2Items;
import com.h3c.iclouds.po.NetPorts;
import com.h3c.iclouds.po.RouterGroups2Items;
import com.h3c.iclouds.po.SwitchGroups2Items;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("netPortsDao")
public class NetPortsDaoImpl extends BaseDAOImpl<NetPorts> implements NetPortsDao {

	@Override
	public PageModel<NetPorts> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(NetPorts.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("remark", "%" + entity.getSearchValue() + "%")
			));
		}
		
		if(entity.getSpecialParam() != null) {
			criteria.add(Restrictions.eq("masterId", entity.getSpecialParam()));
		}

		if (entity.getSpecialParams() != null && entity.getSpecialParams().length > 0){
			criteria.add(Restrictions.in("masterId", entity.getSpecialParams()));
		}

		Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
		return this.findForPage(NetPorts.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NetPorts> findPortByStack(String stackId, String type) {
		Criteria criteria = getSession().createCriteria(NetPorts.class, "netPorts");
		// 物理网口
		criteria.add(Restrictions.eq("ethType", ConfigProperty.CMDB_NETPORT_ETHTYPE1_PHYSICS));
		
		DetachedCriteria queryCar = null;
		if(type.equals(ConfigProperty.CMDB_ASSET_TYPE_SWITCH)) {
			queryCar = DetachedCriteria.forClass(SwitchGroups2Items.class, "items");
		} else if (type.equals(ConfigProperty.CMDB_ASSET_TYPE_ROUTER)){
			queryCar = DetachedCriteria.forClass(RouterGroups2Items.class, "items");
		} else {
			queryCar = DetachedCriteria.forClass(FirewallGroups2Items.class, "items");
		}
		
		queryCar.add(Restrictions.eq("items.stackId", stackId));
		queryCar.add(Property.forName("netPorts.masterId").eqProperty("items.masterId"));
		criteria.add(Subqueries.exists(queryCar.setProjection(Projections.property("items.masterId"))));
		
		criteria.addOrder(Order.asc("seq"));
		return criteria.list();
	}
	
}
