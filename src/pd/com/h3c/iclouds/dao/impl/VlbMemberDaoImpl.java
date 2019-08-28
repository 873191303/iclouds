package com.h3c.iclouds.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.VlbMemberDao;
import com.h3c.iclouds.po.VlbMember;
import com.h3c.iclouds.utils.StrUtils;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Repository("vlbMemberDao")
public class VlbMemberDaoImpl extends BaseDAOImpl<VlbMember> implements VlbMemberDao {

    @Override
    public PageModel<VlbMember> findForPage(PageEntity entity) {
        Criteria criteria = getSession().createCriteria(VlbMember.class);
        // 查询方式
        if(!"".equals(entity.getSearchValue())) {	// 模糊查询
            criteria.add(Restrictions.or(
                    Restrictions.like("name", "%" + entity.getSearchValue() + "%")
            ));
        }
        if (entity.getSpecialParams() != null && entity.getSpecialParams().length > 0){
            criteria.add(Restrictions.eq("poolId", entity.getSpecialParams()[0]));
        }
        Map<String, String> order = new HashMap<String, String>();
        if (StrUtils.checkParam(entity.getAsSorting(), entity.getColumnName())){
            if (entity.getAsSorting().equals("asc")){
                order.put(entity.getColumnName().toString(), "asc");
            } else {
                order.put(entity.getColumnName().toString(), "desc");
            }
        } else {
            order.put("updatedDate", "desc");
        }
        return this.findForPage(VlbMember.class, criteria, order, entity.getPageNo(), entity.getPageSize());
    }

}
