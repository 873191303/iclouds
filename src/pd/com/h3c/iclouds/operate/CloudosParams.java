package com.h3c.iclouds.operate;

import com.h3c.iclouds.utils.StrUtils;

public class CloudosParams {
	
	/** 请求方式：post */
	public static final String POST = "post";
	
	/** 请求方式：get */
	public static final String GET = "get";
	
	/** 请求方式：delete */
	public static final String DELETE = "delete";
	
	/** 请求方式：put */
	public static final String PUT = "put";
	
	/** 请求方式：put */
	public static final String PATCH = "patch";
	
	/** 请求方式：put */
	public static final String INPUTSTREAM = "inputStream";
	
	/** 请求头 */
	public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
	
	/** 请求头 */
	public static final String CONTENT_TYPE = "Content-Type";
	
	/** 重设 */
	public static final String SERVER_ACTION_RESIZE = "resize";
	
	/** 开机 */
	public static final String SERVER_ACTION_OS_START = "os-start";
	/** 修改密码 */
	public static final String SERVER_ACTION_OS_UPDATE = "os-upate";
	
	/** 关机 */
	public static final String SERVER_ACTION_OS_STOP = "os-stop";
	
	/** 关机 */
	public static final String SERVER_ACTION_OS_REBOOT = "reboot";
	
	/** 登录方式1 */
	public static final String CLOUDOS_API_AUTH = "cloudos.api.auth";
	
	/** 登录方式2 */
	public static final String CLOUDOS_API_XAUTH = "cloudos.api.xauth";
	
	/** 云主机 */
	public static final String CLOUDOS_API_SERVER_DETAIL = "cloudos.api.server.detail";
	/** 云主机密码重置 */
	public static final String CLOUDOS_API_SERVER_UPDATEPWD = "cloudos.api.server.updatepwd";
	
	/** 云主机操作 */
	public static final String CLOUDOS_API_SERVER_ACTION = "cloudos.api.server.action";
	
	/** 云主机续租 */
	public static final String CLOUDOS_API_SERVER_RENEWAL = "cloudos.api.server.renewal";
	
	/** 云硬盘扩容 */
	public static final String CLOUDOS_API_SERVER_RENEWALDISK = "cloudos.api.server.renewaldisk";
	/** 云主机删除 */
	public static final String CLOUDOS_API_SERVER_DELETE = "cloudos.api.server.delete";
	
	/** 云主机查询 */
	public static final String CLOUDOS_API_SERVER_GET = "cloudos.api.server.get";
	
	/** 云主机创建 */
	public static final String CLOUDOS_API_SERVER = "cloudos.api.server";
	
	/** 私有网络查询 */
	public static final String CLOUDOS_API_NETWORKS = "cloudos.api.networks";
	
	/** 虚拟网卡 */
	public static final String CLOUDOS_API_PORTS = "cloudos.api.ports";
	
	/** 创建硬盘 */
	public static final String CLOUDOS_API_VOLUMES = "cloudos.api.volumes";
	
	/** 硬盘详细信息 */
	public static final String CLOUDOS_API_VOLUMES_DETAIL = "cloudos.api.volumes.detail";
	
	/** 创建或查询镜像 */
	public static final String CLOUDOS_API_IMAGES = "cloudos.api.images";
	
	/** 加载硬盘 */
	public static final String CLOUDOS_API_OSVOLUMEATTACHMENTS = "cloudos.api.osvolumeattachments";
	
	/** 从指定主机卸载硬盘*/
	public static final String CLOUDOS_API_VOLUMES_OFFATTACHMENTS = "cloudos.api.volumes.offattachments";

	/** 创建虚拟网卡并加载至主机 */
	public static final String CLOUDOS_API_OSINTERFACE = "cloudos.api.osinterface";

	/** 防火墙 */
	public static final String CLOUDOS_API_FIREWALL = "cloudos.api.firewall";

	/** 操作防火墙 */
	public static final String CLOUDOS_API_FIREWALL_ACTION = "cloudos.api.firewall.action";

	/** 路由器 */
	public static final String CLOUDOS_API_ROUTE = "cloudos.api.route";

	/** 操作路由器 */
	public static final String CLOUDOS_API_ROUTE_ACTION = "cloudos.api.route.action";

	/** 网络 */
	public static final String CLOUDOS_API_NETWORK = "cloudos.api.network";

	/** 操作网络 */
	public static final String CLOUDOS_API_NETWORK_ACTION = "cloudos.api.network.action";

	/** 子网 */
	public static final String CLOUDOS_API_SUBNET = "cloudos.api.subnet";

	/** 操作子网 */
	public static final String CLOUDOS_API_SUBNET_ACTION = "cloudos.api.subnet.action";

	/** 负载均衡 */
	public static final String CLOUDOS_API_VLBPOOL = "cloudos.api.vlbPool";

	/** 操作负载均衡 */
	public static final String CLOUDOS_API_VLBPOOL_ACTION = "cloudos.api.vlbPool.action";

	/** 监视器 */
	public static final String CLOUDOS_API_HEALTHMONITOR = "cloudos.api.healthMonitor";

	/** 安全组 */
	public static final String CLOUDOS_API_GROUP_SECURITY = "cloudos.api.group.security";

	/** 安全组 */
	public static final String CLOUDOS_API_SECURITY = "cloudos.api.security";

	/** 安全组 */
	public static final String CLOUDOS_API_RULES = "cloudos.api.rules";

	/** 安全组 */
	public static final String CLOUDOS_API_GROUP_RULES = "cloudos.api.group.rules";
	
	/** 虚拟网卡操作 */
	public static final String CLOUDOS_API_PORTS_ACTION = "cloudos.api.ports.action";

	/** 操作监视器 */
	public static final String CLOUDOS_API_HEALTHMONITOR_ACTION = "cloudos.api.healthMonitor.action";

	/** 负载均衡vip池 */
	public static final String CLOUDOS_API_VIPPOOL = "cloudos.api.vipPool";

	/** 操作负载均衡vip池 */
	public static final String CLOUDOS_API_VIPPOOL_ACTION = "cloudos.api.vipPool.action";

	/** 策略集 */
	public static final String CLOUDOS_API_POLICIE = "cloudos.api.policie";

	/** 操作策略集 */
	public static final String CLOUDOS_API_POLICIE_ACTION = "cloudos.api.policie.action";

	/** 策略集规则 */
	public static final String CLOUDOS_API_POLICIE_RULE = "cloudos.api.policie.rule";

	/** 操作策略集规则 */
	public static final String CLOUDOS_API_POLICIE_RULE_ACTION = "cloudos.api.policie.rule.action";

	/** 防火墙吞吐量 */
	public static final String CLOUDOS_API_FIREWALL_NORMS = "cloudos.api.firewall.norms";

	/** 操作防火墙吞吐量 */
	public static final String CLOUDOS_API_FIREWALL_NORMS_ACTION = "cloudos.api.firewall.norms.action";

	/** 负载均衡组 */
	public static final String CLOUDOS_API_VLB = "cloudos.api.vlb";

	/** 操作负载均衡组 */
	public static final String CLOUDOS_API_VLB_ACTION = "cloudos.api.vlb.action";

	/** 负载均衡成员 */
	public static final String CLOUDOS_API_VLBMEMBER = "cloudos.api.vlbMember";

	/** 操作负载均衡成员 */
	public static final String CLOUDOS_API_VLBMEMBER_ACTION = "cloudos.api.vlbMember.action";

	/** 策略集插入规则 */
	public static final String CLOUDOS_API_POLICIE_INSERT_RULE = "cloudos.api.policie.insert.rule";

	/** 策略集移除规则 */
	public static final String CLOUDOS_API_POLICIE_REMOVE_RULE = "cloudos.api.policie.remove.rule";

	/** 网络连接路由器 */
	public static final String CLOUDOS_API_SUBNET_LINK_ROUTE = "cloudos.api.subnet.link.route";

	/** 网络断开连接路由器 */
	public static final String CLOUDOS_API_SUBNET_UNLINK_ROUTE = "cloudos.api.subnet.unlink.route";

	/** 监听器绑定资源池 */
	public static final String CLOUDOS_API_HEALTH_LINK_POOL = "cloudos.api.health.link.pool";
	
	/** 组织api */
	public static final String CLOUDOS_API_PROJECTS = "cloudos.api.projects";
	
	public static final String CLOUDOS_API_PROJECTS_ACTION = "cloudos.api.projects.action";
	
	/** 配额api */
	public static final String CLOUDOS_API_QUOTA = "cloudos.api.quota";
	
	public static final String CLOUDOS_API_USERS = "cloudos.api.users";

	/** 硬盘操作*/
	public static final String CLOUDOS_API_VOLUME_ACTION = "cloudos.api.volume.action";

	public static final String CLOUDOS_API_USERS_ACTION = "cloudos.api.users.action";
	
	public static final String CLOUDOS_API_GRANT = "cloudos.api.grant";
	
	public static final String CLOUDOS_API_V2_QUOTA ="cloudos.api.v2.quota";
	
	/** 公网IP查询和创建 */
	public static final String CLOUDOS_API_FLOATINGIP = "cloudos.api.floatingip";
	
	/** 公网IP操作 */
	public static final String CLOUDOS_API_FLOATINGIP_ACTION = "cloudos.api.floatingip.action";
	/** 计算配额 */
	public static final String CLOUDOS_API_V2_OS_QUOTA_SETS ="cloudos.api.v2.os.quota.sets";
	/**
	 * 租户可用域查询
	 */
	public static final String CLOUDOS_API_KEYSTONE ="cloudos.api.keystone";
	public static final String CLOUDOS_API_AZONES ="cloudos.api.azones";
	public static final String CLOUDOS_API_IMAGE ="cloudos.api.image";
	
	/**
	 * 租户与可用域关系查询
	 */
	public static final String CLOUDOS_API_PROJECT_AZONE = "cloudos.api.project.azone";
	
	/**
	 * 可用域操作
	 */
	public static final String CLOUDOS_API_AZONES_ACTION = "cloudos.api.azones.action";
	
	/**
	 * 规格详细查询
	 */
	public static final String CLOUDOS_API_FLAVOR ="cloudos.api.flavor";
	
	public static final String CLOUDOS_API_FLAVOR_DETAIL ="cloudos.api.flavor.detail";
	
	/**
	 * 上传镜像
	 */
	public static final String CLOUDOS_API_IMAGES_FILE ="cloudos.api.images.file";
	
	
	public static final String CLOUDOS_API_RESTENANCY = "cloudos.api.restenancy.action";
	
	/**
	 * 云主机卸载虚拟网卡
	 */
	public static final String CLOUDOS_API_DETTACH_PORT = "cloudos.api.server.dettach.port";
	
	public static String getVmState(String state) {
		String vmState = "Unknown";
		if (StrUtils.checkParam(state)) {
			switch (state) {
			case "SHUTOFF":
				vmState = "state_stop";
				break;
			case "ACTIVE":
				vmState = "state_normal";
				break;
			case "BUILD":
				vmState = "state_creating";
				break;
			case "REBOOT":
				vmState = "state_rebooting";
				break;
			case "Error":
				vmState = "state_exception";
				break;
			case "Suspended":
				vmState = "state_exception";
				break;
			case "Unknown":
				vmState = "Unknown";
				break;

			}
		}
		return vmState;
	}
}
