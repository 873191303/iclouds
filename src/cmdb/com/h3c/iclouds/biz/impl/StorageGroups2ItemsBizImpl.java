package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.StorageGroups2ItemsBiz;
import com.h3c.iclouds.biz.StorageViewBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.StorageGroups2ItemsDao;
import com.h3c.iclouds.po.StorageGroups2Items;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@Service(value = "storageGroups2ItemsBiz")
public class StorageGroups2ItemsBizImpl extends BaseBizImpl<StorageGroups2Items> implements StorageGroups2ItemsBiz {

    @Resource
    private StorageGroups2ItemsDao storageGroups2ItemsDao;

    @Resource
    private StorageViewBiz storageViewBiz;
    
    @Override
    public PageModel<StorageGroups2Items> findForPage(PageEntity entity) {
        return storageGroups2ItemsDao.findForPage(entity);
    }

    @Override
    public void syncItem(StorageGroups2Items groups2Items) {
        storageGroups2ItemsDao.add(groups2Items);
        storageViewBiz.saveItemAndRelation(groups2Items.getId(), groups2Items.getName(), ConfigProperty.STORAGE_ITEM, null, groups2Items.getCid());
    }
    
    @Override
    public void deleteItem (StorageGroups2Items groups2Items) {
        storageGroups2ItemsDao.delete(groups2Items);
        storageViewBiz.deleteItemAndRelation(groups2Items.getId());
//        AsmMaster asmMaster = asmMasterBiz.findById(AsmMaster.class, groups2Items.getMasterId());
//        if (StrUtils.checkParam(asmMaster)) {
//            asmMasterBiz.delete(asmMaster);
//        }
    }
}
