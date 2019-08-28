package com.h3c.iclouds.biz;

import java.util.List;
import java.util.Map;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Strategy;

public interface StrategyBiz extends BaseBiz<Strategy>{
	
	List<Strategy> selStrategyAll();
	
	ResultType updateStrategy(Map<String, String> map);

}
