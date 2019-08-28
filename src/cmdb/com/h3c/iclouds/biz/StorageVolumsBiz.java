package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.po.StorageVolums;

/**
 * Created by yKF7317 on 2016/11/14.
 */
public interface StorageVolumsBiz extends BaseBiz<StorageVolums> {

    String findHostName(StorageVolums volums);

    PageList<StorageVolums> transPage(PageList<StorageVolums> page);
    
    void deleteVolumn(StorageVolums volums);
}
