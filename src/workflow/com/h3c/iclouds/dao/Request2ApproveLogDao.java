package com.h3c.iclouds.dao;

import java.util.List;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.business.Request2ApproveLog;

public interface Request2ApproveLogDao extends BaseDAO<Request2ApproveLog> {

	/**
	 * 根据申请单id查询记录，按照时间倒叙排列
	 * @param masterId
	 * @return
	 */
	public List<Request2ApproveLog> findByMasterId(String masterId);
	
}
