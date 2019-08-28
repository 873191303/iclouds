package com.h3c.iclouds.dao;

import java.util.List;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.inc.Inc2ApproveLog;

public interface Inc2ApproveLogDao extends BaseDAO<Inc2ApproveLog> {

	/**
	 * 根据申请单id查询记录，按照时间倒叙排列
	 * @param masterId
	 * @return
	 */
	public List<Inc2ApproveLog> findByMasterId(String masterId);
	
}
