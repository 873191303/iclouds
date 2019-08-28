package com.h3c.iclouds.client;

public class EisooAbcParams {
	
	/** 请求方式：post */
	public static final String POST = "post";
	
	/** 请求方式：get */
	public static final String GET = "get";
	
	/** 请求方式：delete */
	public static final String DELETE = "delete";
	
	/** 请求方式：put */
	public static final String PUT = "put";
	
//	/** 重设 */
//	public static final String SERVER_ACTION_RESIZE = "resize";
//	
//	/** 登录方式1 */
//	public static final String ABC_API_AUTH = "abc.api.auth";
	
	/** 登录方式2 */
	public static final String ABC_API_AUTH = "/v2/auth/token";
	
	/** 爱数配额标志位*/
	public static final String ABC_QUOTA_FLAG = "18";
	
	/** 爱数配额code*/
	public static final String ABC_QUOTA_CODE = "eisoo_backup";
	
	/** 爱数同步成功message*/
	public static final String ABC_SUCCESS_MESSAGE = "OK";
	
	/** 爱数同步成功code*/
	public static final String ABC_SUCCESS_CODE = "15728640";
	
	// TODO 测试用tenant
	public static final String ABC_TEST_TENANT = "d64fcd0922724de996db56b13c0d996e";
}
