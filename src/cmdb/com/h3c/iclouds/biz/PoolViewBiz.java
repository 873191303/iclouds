package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.PoolRelation;
import com.h3c.iclouds.po.PoolView;

import java.util.List;

/**
 * Created by yKF7317 on 2017/2/20.
 */
public interface PoolViewBiz extends BaseBiz<PoolView> {

    List<PoolRelation> getView();

    void saveItemAndRelation(String objId, String objName, String type, String uuid, String
            previousId);

    void updateItemAndRelation(String objId, String objName, String
            previousId);

    void deleteItemAndRelation(String objId);
}
