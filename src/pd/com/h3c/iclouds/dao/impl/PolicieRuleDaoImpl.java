package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.PolicieRuleDao;
import com.h3c.iclouds.po.PolicieRule;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Repository("policieRuleDao")
public class PolicieRuleDaoImpl extends BaseDAOImpl<PolicieRule> implements PolicieRuleDao {

    @Override
    public PageModel<PolicieRule> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(PolicieRule.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {	// 模糊查询
            criteria.add(Restrictions.or(
                    Restrictions.like("name", "%" + entity.getSearchValue() + "%")
            ));
        }
        if (null !=entity.getSpecialParams() && entity.getSpecialParams().length > 0){
            criteria.add(Restrictions.in("policyId", entity.getSpecialParams()));
        }
        Map<String, String> order = new HashMap<String, String>();
        order.put("position", "asc");
        return this.findForPage(PolicieRule.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }
}
