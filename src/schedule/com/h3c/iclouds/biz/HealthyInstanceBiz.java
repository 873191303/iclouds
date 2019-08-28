package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.HealthyInstance;
import com.h3c.iclouds.po.HealthyValue;

import java.util.Date;
import java.util.List;

public interface HealthyInstanceBiz extends BaseBiz<HealthyInstance> {

    List<HealthyValue> data(String id, Date start, Date end);

}
