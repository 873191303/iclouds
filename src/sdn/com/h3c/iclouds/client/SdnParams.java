package com.h3c.iclouds.client;

public class SdnParams {
	
	/** 请求ip */
	public static final String SDN_IP = "sdn.ip";
	
	/** 请求端口 */
	public static final String SDN_PORT = "sdn.port";
	
	/** 用户名 */
	public static final String SDN_USERNAME = "sdn.username";
	
	/** 密码 */
	public static final String SDN_PASSWORD = "sdn.password";
	
	/** 域 */
	public static final String SDN_DOMAIN = "sdn.domain";
	
	/** 请求方式：post */
	public static final String POST = "post";
	
	/** 请求方式：get */
	public static final String GET = "get";
	
	/** 请求方式：delete */
	public static final String DELETE = "delete";
	
	/** 请求方式：put */
	public static final String PUT = "put";
	
	/** url：获取token */
	public static final String SDN_API_GET_TOKEN = "/sdn/v2.0/auth";
			
	/** url：获取虚拟防火墙数据 */
	public static final String SDN_API_GET_FIREWALL = "/nem/v1.0/net_resources";
}
