package com.h3c.iclouds.biz;

import java.util.List;
import java.util.Map;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.WorkFlow;
import com.h3c.iclouds.po.WorkRole;

public interface WorkRoleBiz extends BaseBiz<WorkRole> {
	
	/**
	 * 保存流程角色
	 * @param list
	 * @param workFlowId
	 */
	public void saveRoleByList(List<Map<String, String>> list, String workFlowId, WorkFlow entity);
}
