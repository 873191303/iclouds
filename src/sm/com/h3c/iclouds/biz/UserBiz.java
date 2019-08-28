package com.h3c.iclouds.biz;

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.opt.IssoClient;
import com.h3c.iclouds.opt.MonitorClient;
import com.h3c.iclouds.po.User;

import java.util.Date;
import java.util.Map;

public interface UserBiz extends BaseBiz<User> {

	/**
	 * 保存角色
	 * @param entity
	 * @param map
	 * @return
	 */
	ResultType update(User entity, Map<String, Object> map, String type);
	
	void update(User entity, String newPassword, boolean update);
	
	void save(User entity, String roleId, Map<String, Object> map, CloudosClient client, IssoClient issoClient,
			  MonitorClient monitorClient);
	
	void deleteUser(User entity);
	
	/**
	 * 验证用户状态与生效失效时间是否一致
	 * @param entity
	 * @return
	 */
	boolean checkUserExpireTime(User entity);

	SessionBean createSessionBean(User user, String sysFlag, String remoteIp);

	Date saveLogin2Log(ResultType resut, String userId, String remoteIp, String loginName);

    /**
     * 注销token
     * @param token
     * @param isExpire
     */
    void logout(String token, boolean isExpire);
}
