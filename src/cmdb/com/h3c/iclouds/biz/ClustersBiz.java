package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Clusters;

/**
 * Created by yKF7317 on 2016/11/9.
 */
public interface ClustersBiz extends BaseBiz<Clusters> {

    Object save(Clusters entity);

    Object update(String id, Clusters entity);

    Object delete(String id);

    void synAdd(Clusters entity);

    void synDelete(Clusters entity);
}
