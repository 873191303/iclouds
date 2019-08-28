package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.StorageGroups2Items;

/**
 * Created by yKF7317 on 2016/11/14.
 */
public interface StorageGroups2ItemsBiz extends BaseBiz<StorageGroups2Items> {

    void syncItem(StorageGroups2Items groups2Items);
    
    void deleteItem(StorageGroups2Items groups2Items);

}
