package com.h3c.iclouds.dao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.PfmValue2HistoryDao;
import com.h3c.iclouds.po.CasItem;
import com.h3c.iclouds.po.PfmValue1D;
import com.h3c.iclouds.po.PfmValue1H;
import com.h3c.iclouds.po.PfmValue2History;
import com.h3c.iclouds.po.PfmValue6H;
import com.h3c.iclouds.utils.StrUtils;

@Repository("pfmValue2HistoryDao")
public class PfmValue2HistoryDaoImpl extends BaseDAOImpl<PfmValue2History> implements PfmValue2HistoryDao {

    @Resource(name = "baseDAO")
    private BaseDAO<CasItem> casItemDao;
    
    @Resource(name = "baseDAO")
    private BaseDAO<PfmValue1H> value1HDao;
    
    @Resource(name = "baseDAO")
    private BaseDAO<PfmValue6H> value6HDao;
    
    @Resource(name = "baseDAO")
    private BaseDAO<PfmValue1D> value1DDao;
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PageModel findHistoryCondense(PageEntity entity, int condenseType) {
    	BaseDAO dao = null;
    	Class clazz = null;
    	switch (condenseType) {
			case 1:
				clazz = PfmValue1H.class;
				dao = value1HDao;
				break;
			case 6:
				clazz = PfmValue6H.class;
				dao = value6HDao;
				break;
			case 24:
				clazz = PfmValue1D.class;
				dao = value1DDao;
				break;
			default:
				break;
		}
        Criteria criteria = this.createCriteria(entity, condenseType);
        Map<String, String> order = new HashMap<String, String>();
        order.put("collectTime", "asc");
        return dao.findForPage(clazz, criteria, order, entity.getPageNo(), entity.getPageSize());
	}
    
	@SuppressWarnings("rawtypes")
	private Criteria createCriteria(PageEntity entity, int condenseType) {
		entity.setPageSize(-1);
		Class clazz = null;
		// 根据查询对象，设置最大 最小时间
		int maxHour = 24;
		int minHour = 7 * 24;
		switch (condenseType) {
			case 1:
				clazz = PfmValue1H.class;
				maxHour = 30 * 24;
				break;
			case 6:
				clazz = PfmValue6H.class;
				maxHour = 60 * 24;
				break;
			case 24:
				clazz = PfmValue1D.class;
				maxHour = 365 * 24;
				break;
			default:
				condenseType = 0;
				minHour = 1;
				clazz = PfmValue2History.class;
				break;
		}
		Criteria criteria = getSession().createCriteria(clazz);
		Map<String, Object> queryMap = entity.getQueryMap();
		boolean date = false;
		if (StrUtils.checkParam(queryMap.get("hour"))) {
			date = true;
			Integer hour = StrUtils.tranInteger(queryMap.get("hour"));
			hour = hour > maxHour ? maxHour : (hour < minHour ? minHour : hour);
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR_OF_DAY, -hour);
			Date startDate = calendar.getTime();
			criteria.add(Restrictions.ge("collectTime", startDate));
		} else {
			if (StrUtils.checkParam(queryMap.get("startDate"))) {
				date = true;
				Long startTime = StrUtils.tranLong(queryMap.get("startDate"));
				Date startDate = new Date(startTime);
				criteria.add(Restrictions.ge("collectTime", startDate));
			}
			if (StrUtils.checkParam(queryMap.get("endDate"))) {
				date = true;
				Long endTime = StrUtils.tranLong(queryMap.get("endDate"));
				Date endDate = new Date(endTime);
				criteria.add(Restrictions.le("collectTime", endDate));
			}
		}
		if (StrUtils.checkParam(queryMap.get("uuid"))){
            String uuid = StrUtils.tranString(queryMap.get("uuid"));
            criteria.add(Restrictions.eq("uuid", uuid));
        } else {
        	criteria.add(Restrictions.eq("uuid", "-1"));
        }
		if (StrUtils.checkParam(queryMap.get("itemType"))){
            String itemType = StrUtils.tranString(queryMap.get("itemType"));
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("item", itemType);
            CasItem casItem = casItemDao.singleByClass(CasItem.class, paramMap);
            if (StrUtils.checkParam(casItem)){
                criteria.add(Restrictions.eq("itemId", casItem.getId()));
            } else {
                criteria.add(Restrictions.eq("itemId", "-1"));
            }
        }
		if(!date){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, - maxHour);
            Date startDate = calendar.getTime();
            criteria.add(Restrictions.ge("collectTime", startDate));
        }
		return criteria;
	}

	public PageModel<PfmValue2History> findForPage(PageEntity entity) {
		Criteria criteria = this.createCriteria(entity, 0);
        Map<String, String> order = new HashMap<String, String>();
        order.put("collectTime", "asc");
        return this.findForPage(PfmValue2History.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }

}
