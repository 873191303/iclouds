package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.StorageGroups;

/**
 * Created by yKF7317 on 2016/11/14.
 */
public interface StorageGroupsBiz extends BaseBiz<StorageGroups> {
	
	void syncGroup(StorageGroups storageGroups);
	
}
