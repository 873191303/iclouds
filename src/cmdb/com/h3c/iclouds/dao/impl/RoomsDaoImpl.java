package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.RoomsDao;
import com.h3c.iclouds.po.Rooms;
import com.h3c.iclouds.utils.StrUtils;

@Repository("roomsDao")
public class RoomsDaoImpl extends BaseDAOImpl<Rooms> implements RoomsDao {

	@Override
	public PageModel<Rooms> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Rooms.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 角色名称是否为空
			criteria.add(Restrictions.or(
				Restrictions.like("roomName", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("shortName", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("region", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("admin", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("roomOnwer", "%" + entity.getSearchValue() + "%"),
				Restrictions.like("localAdmin", "%" + entity.getSearchValue() + "%")
			));
		}
		
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
			if (entity.getAsSorting().equals("asc")){
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		} else {
			order.put("region", "asc");
		}
		return this.findForPage(Rooms.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
	
}
