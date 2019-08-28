package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.StorageClusters;

import java.util.List;

/**
 * Created by yKF7317 on 2016/11/14.
 */
public interface StorageClustersBiz extends BaseBiz<StorageClusters> {

    void syncCluster(StorageClusters clusters, String type);
    
    void deleteCluster(StorageClusters storageClusters);
    
    Object optionGroups2Item(String clusterId, List<String> ids, boolean flag);
    
    Object optionMaster(String clusterId, List<String> ids);
}
