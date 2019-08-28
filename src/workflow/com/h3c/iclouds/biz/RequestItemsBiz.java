package com.h3c.iclouds.biz;

import java.util.Set;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.business.RequestItems;
import com.h3c.iclouds.po.business.RequestMaster;

public interface RequestItemsBiz extends BaseBiz<RequestItems> {

	/**
	 * 验证条目是否正确
	 * 1.验证格式是否正确，包括提交json中的格式是否正确
	 * 2.验证原条目ID是否存在，是否提交了所有原条目并且正确标记了状态
	 * @param entity
	 * @return
	 */
	public ResultType check(Set<RequestItems> list, RequestMaster master, RequestMaster oldEntity);
	
}
