package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Vdc;
import com.h3c.iclouds.po.VdcInfo;

import java.util.List;

/**
 * Created by yKF7317 on 2017/1/10.
 */
public interface VdcInfoBiz extends BaseBiz<VdcInfo> {

    void save(List<VdcInfo> list, Vdc vdc);

}
