package com.h3c.iclouds.dao;


import java.util.List;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.Cvm2Ove;

public interface Cvm2OveDao extends BaseDAO<Cvm2Ove>{
	List<Cvm2Ove> findTop5();
}
