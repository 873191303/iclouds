package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.Server2OveDao;
import com.h3c.iclouds.po.Server2Ove;
import com.h3c.iclouds.utils.StrUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/9.
 */
@Repository("server2OveDao")
public class Server2OveDaoImpl extends BaseDAOImpl<Server2Ove> implements Server2OveDao {
	/**
	 * 分页查询
	 * 
	 * @param entity
	 * @return
	 */
	@Override
	public PageModel<Server2Ove> findForPage(PageEntity entity) {
		Criteria criteria = getSession().createCriteria(Server2Ove.class);
		// 查询方式
		if (!"".equals(entity.getSearchValue())) {// 根据搜索条件模糊查询
			criteria.add(Restrictions.or(Restrictions.like("hostName", "%" + entity.getSearchValue() + "%"),
					Restrictions.like("ip", "%" + entity.getSearchValue() + "%")));
		}
		if (StrUtils.checkParam(entity.getSpecialParam())) {
			criteria.add(Restrictions.or(Restrictions.eq("custerId", entity.getSpecialParam()),
					Restrictions.eq("poolId", entity.getSpecialParam()),
					Restrictions.eq("belongId", entity.getSpecialParam())));
		}
		Map<String, Object> queryMap = entity.getQueryMap();
		if (StrUtils.checkParam(queryMap.get("flag"))) {
			if ("pool".equals(queryMap.get("flag").toString())) {
				criteria.add(Restrictions.isNull("custerId"));
			}
		}
		Map<String, String> order = new HashMap<String, String>();
		if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())) {
			if (entity.getAsSorting().equals("asc")) {
				order.put(entity.getColumnName().toString(), "asc");
			} else {
				order.put(entity.getColumnName().toString(), "desc");
			}
		}
		return this.findForPage(Server2Ove.class, criteria, order, entity.getPageNo(), entity.getPageSize());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Server2Ove> findTop5(String previousId) {
		Criteria criteria = getSession().createCriteria(Server2Ove.class);
		if (StrUtils.checkParam(previousId)) {
			criteria.add(Restrictions.or(Restrictions.eq("custerId", previousId), Restrictions.eq("poolId", previousId),
					Restrictions.eq("belongId", previousId)));
		}
		return criteria.setFirstResult(0).setMaxResults(5).addOrder(Order.desc("cpuOverSize"))
				.addOrder(Order.desc("ramOverSize")).list();

	}
	  @Override
    public List<Server2Ove> cpuTopList () {
        Criteria criteria = getSession().createCriteria(Server2Ove.class);
        criteria.setMaxResults(5);
        criteria.addOrder(Order.desc("cpuOverSize"));
        return criteria.list();
    }
    
    @Override
    public List<Server2Ove> memoryTopList () {
        Criteria criteria = getSession().createCriteria(Server2Ove.class);
        criteria.setMaxResults(5);
        criteria.addOrder(Order.desc("ramOverSize"));
        return criteria.list();
    }
}
