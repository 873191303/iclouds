package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.PfmValue;

import java.util.Date;

public interface PfmValueBiz extends BaseBiz<PfmValue>{

    void save(String uuid, String resType, Float keyValue, String item, Date collectTime, boolean needCheck);

    void save(String uuid, String resType, Float keyValue, String item, Date collectTime);

}
