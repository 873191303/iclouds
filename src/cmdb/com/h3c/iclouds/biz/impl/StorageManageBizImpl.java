package com.h3c.iclouds.biz.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.StorageManageBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.StorageManageDao;
import com.h3c.iclouds.po.StorageManage;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@Service(value = "storageManageBiz")
public class StorageManageBizImpl extends BaseBizImpl<StorageManage> implements StorageManageBiz {

    @Resource
    private StorageManageDao storageManageDao;

    @Override
    public PageModel<StorageManage> findForPage(PageEntity entity) {
        return storageManageDao.findForPage(entity);
    }
}
