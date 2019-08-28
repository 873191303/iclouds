package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.bean.nova.MonitorInitBean;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.SpePort;

/**
 * Created by zkf5485 on 2017/6/6.
 */
public interface SpePortBiz extends BaseBiz<SpePort> {

    void initMonitor(MonitorInitBean entity, NovaVm novaVm);

    void removeMonitor(SpePort entity);
}
