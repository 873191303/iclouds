package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.InitCodeBiz;
import com.h3c.iclouds.biz.StorageClustersBiz;
import com.h3c.iclouds.biz.StorageGroups2ItemsBiz;
import com.h3c.iclouds.biz.StorageGroupsBiz;
import com.h3c.iclouds.biz.StorageViewBiz;
import com.h3c.iclouds.biz.StorageVolumsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.StorageClustersDao;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.InitCode;
import com.h3c.iclouds.po.StorageClusters;
import com.h3c.iclouds.po.StorageGroups;
import com.h3c.iclouds.po.StorageGroups2Items;
import com.h3c.iclouds.po.StorageVolums;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@Service(value = "storageClustersBiz")
public class StorageClustersBizImpl extends BaseBizImpl<StorageClusters> implements StorageClustersBiz {

    @Resource
    private StorageClustersDao storageClustersDao;

    @Resource
    private StorageViewBiz storageViewBiz;
    
    @Resource
    private StorageGroupsBiz storageGroupsBiz;
    
    @Resource
    private StorageGroups2ItemsBiz storageGroups2ItemsBiz;
    
    @Resource
    private StorageVolumsBiz storageVolumsBiz;
    
    @Resource
    private AsmMasterBiz asmMasterBiz;
    
    @Resource
    private InitCodeBiz initCodeBiz;
    
    @Override
    public PageModel<StorageClusters> findForPage(PageEntity entity) {
        return storageClustersDao.findForPage(entity);
    }

    @Override
    public void syncCluster(StorageClusters clusters, String type) {
        storageClustersDao.add(clusters);
        storageViewBiz.saveItemAndRelation(clusters.getId(), clusters.getName(), type, null, clusters.getGid());
    }
    
    @Override
    public void deleteCluster(StorageClusters storageClusters) {
        List<StorageGroups2Items> groups2ItemsList = storageGroups2ItemsBiz.findByPropertyName(StorageGroups2Items
                .class, "cid", storageClusters.getId());
        if (StrUtils.checkCollection(groups2ItemsList)) {
            for (StorageGroups2Items groups2Items : groups2ItemsList) {
                storageGroups2ItemsBiz.deleteItem(groups2Items);
            }
        }
        List<StorageVolums> volumsList = storageVolumsBiz.findByPropertyName(StorageVolums.class, "sid", storageClusters
                .getId());
        if (StrUtils.checkCollection(volumsList)) {
            for (StorageVolums storageVolums : volumsList) {
                storageVolumsBiz.deleteVolumn(storageVolums);
            }
        }
        storageClustersDao.delete(storageClusters);
        storageViewBiz.deleteItemAndRelation(storageClusters.getId());
        if (StrUtils.checkParam(storageClusters.getGid())) {
            StorageGroups storageGroups = storageGroupsBiz.findById(StorageGroups.class, storageClusters.getGid());
            if (StrUtils.checkParam(storageGroups)) {
                storageGroupsBiz.delete(storageGroups);
            }
        }
    }
    
    @Override
    public Object optionGroups2Item(String clusterId, List<String> ids, boolean flag) {
        StorageClusters storageClusters = storageClustersDao.findById(StorageClusters.class, clusterId);
        if (null == storageClusters) {
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        try {
            if (null != ids && ids.size() > 0){
                for (int i = 0; i < ids.size(); i++) {
                    String sid = ids.get(i);
                    StorageGroups2Items item = storageGroups2ItemsBiz.findById(StorageGroups2Items.class, sid);
                    if (null == item) {
                        return BaseRestControl.tranReturnValue(ResultType.storage_item_not_exist);
                    }
                    if (StrUtils.checkParam(item.getCid()) && !item.getCid().equals(clusterId)) {
                        return BaseRestControl.tranReturnValue(ResultType.storage_item_not_belong_cluster);
                    }
                    if (flag) {
                        item.setCid(clusterId);
                    } else {
                        item.setCid(null);
                    }
                    storageGroups2ItemsBiz.update(item);
                }
            }
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            this.exception(e, StorageClusters.class);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }
    
    @Override
    public Object optionMaster(String clusterId, List<String> ids) {
        StorageClusters storageClusters = storageClustersDao.findById(StorageClusters.class, clusterId);
        if (null == storageClusters) {
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        if (!ConfigProperty.NO.equals(storageClusters.getType())) {
            return BaseRestControl.tranReturnValue(ResultType.unallow_alone_storage);
        }
        InitCode initCode = initCodeBiz.getByTypeCode(ConfigProperty.CMDB_ASSET_TYPE_STOCK, ConfigProperty.CMDB_ASSET_TYPE);
        if (null == initCode) {
            return BaseRestControl.tranReturnValue(ResultType.assetType_error);
        }
        try {
            if (StrUtils.checkCollection(ids)){
                for (String s : ids) {
                    AsmMaster asmMaster = this.asmMasterBiz.findById(AsmMaster.class, s);
                    if (null == asmMaster) {
                        return BaseRestControl.tranReturnValue(ResultType.asm_not_exist);
                    }
                    if (!asmMaster.getAssetType().equals(initCode.getId())) {
                        return BaseRestControl.tranReturnValue(ResultType.class_item_in_asm_error);
                    }
                    StorageGroups2Items item = storageGroups2ItemsBiz.singleByClass(StorageGroups2Items.class, StrUtils.createMap("masterId", s));
                    if (StrUtils.checkParam(item) && StrUtils.checkParam(item.getCid()) && !item.getCid().equals
                            (clusterId)) {
                        return BaseRestControl.tranReturnValue(ResultType.storage_item_not_belong_cluster);
                    }
                    if (!StrUtils.checkParam(item)) {
                        item = new StorageGroups2Items();
                        item.setMasterId(s);
                        item.setCid(clusterId);
                        item.createdUser(this.getLoginUser());
                        item.setId(UUID.randomUUID().toString());
                        storageGroups2ItemsBiz.add(item);
                    }
                }
            }
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            this.exception(e, StorageClusters.class);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }
}
