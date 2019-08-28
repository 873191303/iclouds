package com.h3c.iclouds.biz;

import java.util.Map;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Vdc;

/**
 * Created by yKF7317 on 2016/11/23.
 */
public interface VdcBiz extends BaseBiz<Vdc> {

    Map<String, Object> save(Vdc entity, String name);

    void updateItem(Vdc entity, Vdc vdc);

    Vdc getVdc(String projectId);

    void clearLock(Vdc vdc);

    boolean checkLock(String projectId);
}
