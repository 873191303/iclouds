package com.h3c.iclouds.biz;

import java.util.Map;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Groups;

public interface GroupBiz extends BaseBiz<Groups> {

	/**
	 * 保存角色
	 * @param entity
	 * @param map
	 * @return
	 */
	public ResultType update(Groups entity, Map<String, Object> map);
	
}
