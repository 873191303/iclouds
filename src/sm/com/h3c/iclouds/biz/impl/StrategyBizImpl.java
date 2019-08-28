package com.h3c.iclouds.biz.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.StrategyBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.StrategyDao;
import com.h3c.iclouds.po.Strategy;

@Service("strategyBiz")
public class StrategyBizImpl extends BaseBizImpl<Strategy> implements StrategyBiz{
	
	@Resource
	private StrategyDao strategyDao;

	@Override
	public List<Strategy> selStrategyAll() {
		return strategyDao.getStrategy();
	}

	@Override
	public ResultType updateStrategy(Map<String, String> map) {
		Strategy dto = new Strategy();
		dto.setDay(map.get("day"));
		dto.setType(map.get("type"));
		try {
			strategyDao.updateStrategy(dto);
			return ResultType.success;
		} catch (Exception e) {
			return ResultType.failure;
		}
		
	}

}
