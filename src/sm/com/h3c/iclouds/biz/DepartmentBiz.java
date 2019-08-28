package com.h3c.iclouds.biz;

import java.util.Set;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Department;

public interface DepartmentBiz extends BaseBiz<Department> {
	
	/**
	 * 根据用户id查询用户归属的顶级部门
	 * @param userId
	 * @return
	 */
	public String getHeadDeptIdByUserId(String userId);
	
	/**
	 * 根据用户id查询用户部门下的子部门id
	 * @param userId
	 * @return
	 */
	public Set<String> getChildDeptIdByUserId(String userId);
	
}