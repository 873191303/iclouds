package com.h3c.iclouds.dao;

import java.util.List;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.po.Strategy;


public interface StrategyDao extends BaseDAO<Strategy>{
	List<Strategy> getStrategy();
	void updateStrategy(Strategy strategy);

}
