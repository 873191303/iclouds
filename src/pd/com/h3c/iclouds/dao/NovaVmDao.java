package com.h3c.iclouds.dao;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.NovaVm;

import java.util.List;

public interface NovaVmDao extends BaseDAO<NovaVm> {

	void deleteNovavm(NovaVm novaVm);
	
	int monthCount();
	
	List<NovaVm> findListByProfix(String projectId, String profix);
}
