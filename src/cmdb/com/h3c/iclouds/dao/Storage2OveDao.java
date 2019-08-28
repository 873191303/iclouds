package com.h3c.iclouds.dao;

import java.util.List;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.Storage2Ove;

public interface Storage2OveDao extends BaseDAO<Storage2Ove>{
	
	List<Storage2Ove> findTop5();
	
	List<Storage2Ove> storageTopList ();
}
