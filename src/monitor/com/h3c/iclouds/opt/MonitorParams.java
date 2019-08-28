package com.h3c.iclouds.opt;

public class MonitorParams {
	
	/** 请求ip */
	public static final String MONITOR_IP = "monitor.ip";
	
	/** 请求端口 */
	public static final String MONITOR_PORT = "monitor.port";
	
	/** 请求方式：post */
	public static final String POST = "post";
	
	/** 请求方式：get */
	public static final String GET = "get";
	
	/** 请求方式：delete */
	public static final String DELETE = "delete";
	
	/** 请求方式：put */
	public static final String PUT = "put";
	
	/** url： 新增用户 */
	public static final String MONITOR_API_USER_SAVE = "/v1/user/save/{tenantId}";
	
	/** url： 删除用户 */
	public static final String MONITOR_API_USER_DELETE = "/v1/user/delete/{loginName}";
	
	/** url： 修改用户 */
	public static final String MONITOR_API_USER_UPDATE = "/v1/user/update/{loginName}";
	
	/** url： 新增租户 */
	public static final String MONITOR_API_PROJECT_SAVE = "/v1/project";
	
	/** url： 删除租户 */
	public static final String MONITOR_API_PROJECT_DELETE = "/v1/project/{tenantId}";
	
	/** url： 加入监控 */
	public static final String MONITOR_API_HOST_ADD = "/v1/host";
}
