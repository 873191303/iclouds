package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.DepartmentBiz;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.po.Department;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("departmentBiz")
public class DepartmentBizImpl extends BaseBizImpl<Department> implements DepartmentBiz {
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;

	@Override
	public void delete(Department entity) {
		List<Department> list = this.findByPropertyName(Department.class, "parentId", entity.getId());
		if(list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				this.delete(list.get(i));	
			}
		}
		super.delete(entity);
	}

	@Override
	public String getHeadDeptIdByUserId(String userId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("USERID", userId);
		
		Map<String, Object> resultMap = sqlQueryBiz.querySingleByName(SqlQueryProperty.QUERY_HEAD_DEPARTMENT, map);
		if(resultMap != null && !resultMap.isEmpty()) {
			return StrUtils.tranString(resultMap.get("id"));
		}
		return null;
	}

	@Override
	public Set<String> getChildDeptIdByUserId(String userId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("USERID", userId);
		Set<String> set = new HashSet<String>();
		List<Map<String, Object>> list = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_LOWER_DEPARTMENTS, map);
		if(list != null && !list.isEmpty()) {
			for (Map<String, Object> result : list) {
				set.add(StrUtils.tranString(result.get("id")));
			}
		}
		return set;
	}
}
