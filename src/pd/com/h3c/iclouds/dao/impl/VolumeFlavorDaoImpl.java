package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.VolumeFlavorDao;
import com.h3c.iclouds.po.VolumeFlavor;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
* @author  zKF7420
* @date 2017年2月20日 下午5:23:20
*/

@Repository("volumeFlavorDao")
public class VolumeFlavorDaoImpl extends BaseDAOImpl<VolumeFlavor> implements VolumeFlavorDao {
	
	@Override
	public PageModel<VolumeFlavor> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(VolumeFlavor.class);
		// 查询方式
		if(!"".equals(entity.getSearchValue())) {	// 模糊查询
			criteria.add(Restrictions.or(
					Restrictions.like("name", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("size", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("volumeType", "%" + entity.getSearchValue() + "%")
			));
		}
		Map<String, String> order = new HashMap<String, String>();
		addOrder(entity.getAsSorting(), entity.getColumnName(), "updatedDate", order);
		return this.findForPage(VolumeFlavor.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

}

