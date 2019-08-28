package com.h3c.iclouds.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.StrategyDao;
import com.h3c.iclouds.po.Strategy;

@Repository("strategyDao")
public class StrategyDaoImpl extends BaseDAOImpl<Strategy> implements StrategyDao{

	@Override
	public List<Strategy> getStrategy() {
		String hql = "select * FROM iyun_vm_strategy";
//		this.find(hql);
//        Criteria criteria = getSession().createCriteria(Strategy.class);
//        List<Strategy> network2Subnets =findByHql("from "+"Strategy");
        
//		List<Strategy> Strategy = criteria.list();
		return this.getAll(Strategy.class);
	}

	@Override
	public void updateStrategy(Strategy strategy) {
		//this.update(strategy);
		String day = strategy.getDay();
		String type = strategy.getType();
		String hql = "update Strategy set day = '"+day+"' where type = '"+type+"'";
		int count = this.updateByHql(hql);
	}

}
