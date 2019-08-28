package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Server2Vm;

/**
 * Created by yKF7317 on 2016/11/9.
 */
public interface Server2VmBiz extends BaseBiz<Server2Vm> {

    void synAdd(Server2Vm entity);

}
