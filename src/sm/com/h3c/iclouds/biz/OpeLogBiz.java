package com.h3c.iclouds.biz;

import java.util.Date;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.OpeLog;

public interface OpeLogBiz extends BaseBiz<OpeLog> {
	
	public void save(String uuid, String logTypeId, String params, String result, Date startTime);
	
}
