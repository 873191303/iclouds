package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.StorageGroupsBiz;
import com.h3c.iclouds.biz.StorageViewBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.StorageGroupsDao;
import com.h3c.iclouds.po.StorageGroups;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@Service(value = "storageGroupsBiz")
public class StorageGroupsBizImpl extends BaseBizImpl<StorageGroups> implements StorageGroupsBiz {

    @Resource
    private StorageGroupsDao storageGroupsDao;
    
    @Resource
    private StorageViewBiz storageViewBiz;
    
    @Override
    public PageModel<StorageGroups> findForPage(PageEntity entity) {
        return storageGroupsDao.findForPage(entity);
    }
    
    @Override
    public void syncGroup (StorageGroups storageGroups) {
        storageGroupsDao.add(storageGroups);
        storageViewBiz.saveItemAndRelation(storageGroups.getId(), storageGroups.getName(), ConfigProperty.STORAGE_GROUP, null,
                null);
    }
}
