package com.h3c.iclouds.dao.impl;

import org.springframework.stereotype.Repository;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.User2RoleDao;
import com.h3c.iclouds.po.User2Role;

@Repository("user2RoleDao")
public class User2RoleDaoImpl extends BaseDAOImpl<User2Role> implements User2RoleDao{

	@Override
	public void save(String userId) {
		User2Role user2Role = new User2Role();// 创建用户与角色映射关系数据
		user2Role.setUserId(userId);
		user2Role.setRoleId(CacheSingleton.getInstance().getTenantRoleId());// 初始化用户角色为租户管理员
		user2Role.createdUser(getSessionUserId());
		add(user2Role);// 保存用户与角色关系

		user2Role = new User2Role();// 创建用户与角色映射关系数据
		user2Role.setUserId(userId);
		user2Role.setRoleId(CacheSingleton.getInstance().getChargeRoleId());// 初始化用户角色为主管审批
		user2Role.createdUser(getSessionUserId());
		add(user2Role);// 保存用户与角色关系

		user2Role = new User2Role();// 创建用户与角色映射关系数据
		user2Role.setUserId(userId);
		user2Role.setRoleId(CacheSingleton.getInstance().getSignRoleId());// 初始化用户角色为权限审批
		user2Role.createdUser(getSessionUserId());
		add(user2Role);// 保存用户与角色关系

	}

}
