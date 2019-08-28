package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.MaintenancsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.MaintenancsDao;
import com.h3c.iclouds.po.Maintenancs;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by yKF7317 on 2016/11/5.
 */
@Service("maintenancsBiz")
public class MaintenancsBizImpl extends BaseBizImpl<Maintenancs> implements MaintenancsBiz {

    @Resource
    private MaintenancsDao maintenancsDao;

    @Override
    public PageModel<Maintenancs> findForPage(PageEntity entity) {
        return maintenancsDao.findForPage(entity);
    }
}
