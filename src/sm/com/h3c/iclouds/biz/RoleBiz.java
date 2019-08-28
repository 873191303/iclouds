package com.h3c.iclouds.biz;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Role;
import com.h3c.iclouds.po.User;

import java.util.List;
import java.util.Map;

public interface RoleBiz extends BaseBiz<Role> {

	/**
	 * 保存角色
	 * @param entity
	 * @param map
	 * @return
	 */
	ResultType update(Role entity, Map<String, Object> map, String type);
	
	/**
	 * 获取角色可授权的用户
	 * @param roleId
	 * @return
	 */
	List<User> getUser(String roleId);
	
	JSONObject getUserJson(String userCloudId, CloudosClient client);
}
