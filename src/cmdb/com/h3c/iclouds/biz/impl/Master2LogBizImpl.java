package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.Master2LogBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.Master2LogDao;
import com.h3c.iclouds.po.Master2Log;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * Created by yKF7317 on 2016/11/5.
 */
@Repository("master2LogBiz")
public class Master2LogBizImpl extends BaseBizImpl<Master2Log> implements Master2LogBiz {

    @Resource
    private Master2LogDao master2LogDao;

    @Override
    public PageModel<Master2Log> findForPage(PageEntity entity) {
        return master2LogDao.findForPage(entity);
    }
}
