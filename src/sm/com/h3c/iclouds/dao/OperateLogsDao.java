package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.OperateLogs;

public interface OperateLogsDao extends BaseDAO<OperateLogs> {
	
	OperateLogs findLastDateByUserId(String userId);
	
	OperateLogs findLastRecordByUserId(String userId);

}
