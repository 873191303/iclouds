package com.h3c.iclouds.dao;

import java.util.List;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.Server2Ove;

/**
 * Created by yKF7317 on 2016/11/9.
 */
public interface Server2OveDao extends BaseDAO<Server2Ove> {	
	List<Server2Ove> findTop5(String previousId);
	List<Server2Ove> cpuTopList();
	
	List<Server2Ove> memoryTopList();
}

