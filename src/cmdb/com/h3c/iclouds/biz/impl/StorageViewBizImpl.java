package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.StorageViewBiz;
import com.h3c.iclouds.biz.StorageVolumsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.StorageItem;
import com.h3c.iclouds.po.StorageRelation;
import com.h3c.iclouds.po.StorageView;
import com.h3c.iclouds.po.StorageVolums;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by yKF7317 on 2017/2/20.
 */
@Service("storageViewBiz")
public class StorageViewBizImpl extends BaseBizImpl<StorageView> implements StorageViewBiz {

    @Resource(name = "baseDAO")
    private BaseDAO<StorageRelation> storageRelationDao;

    @Resource(name = "baseDAO")
    private BaseDAO<StorageItem> storageItemDao;

    @Resource
    private StorageVolumsBiz storageVolumsBiz;

    @Override
    public List<StorageRelation> getView() {
        List<StorageRelation> storageRelationList = storageRelationDao.getAll(StorageRelation.class);
        List<StorageRelation> relations = new ArrayList<>();
        if (StrUtils.checkParam(storageRelationList)){
            for (StorageRelation storageRelation : storageRelationList) {
                if (!StrUtils.checkParam(storageRelation.getPrevious())){
                    sort(storageRelation, storageRelationList);
                    relations.add(storageRelation);
                }
            }
        }
        return relations;
    }

    public void saveItemAndRelation(String objId, String objName, String type, String uuid, String previousId){
        if (!StrUtils.checkParam(uuid)){
            uuid = UUID.randomUUID().toString();
        }
        StorageRelation storageRelation = new StorageRelation();
        StorageItem storageItem = new StorageItem();
        storageItem.setId(objId);
        storageItem.setName(objName);
        storageItem.setItemType(type);
        storageItem.setUuid(uuid);
        storageItemDao.add(storageItem);
        storageRelation.setNetId(objId);
        storageRelation.setPrevious(previousId);
        storageRelationDao.add(storageRelation);
    }

    public void sort(StorageRelation storageRelation, List<StorageRelation> storageRelationList){
        List<StorageRelation> relations = new ArrayList<>();
        for (StorageRelation relation : storageRelationList) {
            if (storageRelation.getNetId().equals(relation.getPrevious())){
                sort(relation, storageRelationList);
                relations.add(relation);
            }
        }
        storageRelation.setChildren(relations);
        storageRelation.setChildCount(relations.size());
        if (ConfigProperty.STORAGE_ALONE.equals(storageRelation.getPoolType()) || ConfigProperty.STORAGE_CLUSTER.equals
                (storageRelation.getPoolType())){
            int count = storageVolumsBiz.findCountByPropertyName(StorageVolums.class, "sid",
                    storageRelation.getNetId());
            storageRelation.setVolumnCount(count);
        }
    }
    
    @Override
    public void deleteItemAndRelation (String objId) {
        StorageRelation storageRelation = storageRelationDao.singleByClass(StorageRelation.class, StrUtils.createMap
                ("netId", objId));
        if (StrUtils.checkParam(storageRelation)) {
            storageRelationDao.delete(storageRelation);
        }
        StorageItem storageItem = storageItemDao.findById(StorageItem.class, objId);
        if (StrUtils.checkParam(storageItem)) {
            storageItemDao.delete(storageItem);
        }
    }
}
