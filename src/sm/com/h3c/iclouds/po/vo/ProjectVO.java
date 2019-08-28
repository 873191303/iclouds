package com.h3c.iclouds.po.vo;

import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.User;

/**
 * iyun创建租户时同步创建用户组合主机组以及用户参数
 * Created by yKF7317 on 2017/8/3.
 */
public class ProjectVO {
	
	private UserVO user;
	
	private String name;
	
	private String tenantId;
	
	public static ProjectVO create(Project project, User user) {
		ProjectVO projectVO = new ProjectVO();
		projectVO.setTenantId(project.getId());
		projectVO.setName(project.getName());
		projectVO.setUser(UserVO.create(ConfigProperty.ZABBIX_ADMIN_TYPE, user));
		return projectVO;
	}
	
	public UserVO getUser () {
		return user;
	}
	
	public void setUser (UserVO user) {
		this.user = user;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getTenantId () {
		return tenantId;
	}
	
	public void setTenantId (String tenantId) {
		this.tenantId = tenantId;
	}
}
