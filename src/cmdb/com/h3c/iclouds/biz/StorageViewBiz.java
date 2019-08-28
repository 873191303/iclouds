package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.StorageRelation;
import com.h3c.iclouds.po.StorageView;

import java.util.List;

/**
 * Created by yKF7317 on 2017/2/20.
 */
public interface StorageViewBiz extends BaseBiz<StorageView> {

    List<StorageRelation> getView();

    void saveItemAndRelation(String objId, String objName, String type, String uuid, String previousId);
    
    void deleteItemAndRelation(String objId);
}
