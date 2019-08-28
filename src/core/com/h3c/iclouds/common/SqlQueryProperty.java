package com.h3c.iclouds.common;

/**
 * 与sqlquery.xml对应，用于做映射
 * @author zkf5485
 *
 */
public class SqlQueryProperty {
	
	/**
	 * 查询用户资源
	 */
	public static final String QUERY_RESOURCE_BY_USERID = "query.resource.by.userId";
	
	/**
	 * 根据业务id查询审批权限
	 */
	public static final String APPROVE_AUTH = "approve.auth";
	
	/**
	 * 查询最高级别部门id
	 */
	public static final String QUERY_HEAD_DEPARTMENT = "query.head.department";
	
	/**
	 * 查询低级部门列表
	 */
	public static final String QUERY_LOWER_DEPARTMENTS = "query.lower.departments";
	
	/**
	 * 查询可审批的需求申请单
	 */
	public static final String QUERY_REQUEST_MASTER_APPROVE = "query.request.master.approve";
	
	/**
	 * 查询可审批的需求申请单数量
	 */
	public static final String QUERY_REQUEST_MASTER_APPROVE_COUNT = "query.request.master.approve.count";
	
	/**
	 * 查询可审批的需求申请单及历史审批数量
	 */
	public static final String QUERY_REQUEST_MASTER_APPROVE_AND_HISTORY_COUNT = "query.request.master.approve.and.history.count";
	
	/**
	 * 查询可审批的需求申请单及历史审批申请单
	 */
	public static final String QUERY_REQUEST_MASTER_APPROVE_AND_HISTORY = "query.request.master.approve.and.history";
	
	/**
	 * 查询可审批的事件工单
	 */
	public static final String QUERY_INC_MASTER_APPROVE = "query.inc.master.approve";
	
	/**
	 * 查询可审批的事件工单
	 */
	public static final String QUERY_INC_MASTER_APPROVE_COUNT = "query.inc.master.approve.count";
	
	/**
	 * 查询可审批的事件工单
	 */
	public static final String QUERY_INC_MASTER_APPROVE_AND_HISTORY = "query.inc.master.approve.and.history";
	
	/**
	 * 查询可审批的事件工单
	 */
	public static final String QUERY_INC_MASTER_APPROVE_AND_HISTORY_COUNT = "query.inc.master.approve.and.history.count";
	
	/**
	 * 查询当前环节可审批人（不包含部门过滤）
	 */
	public static final String QUERY_MASTER_APPROVER = "query.master.approver";
	
	/**
	 * 查询当前环节可审批人,需要做部门过滤
	 */
	public static final String QUERY_MASTER_APPROVER_DEPARTMENT = "query.master.approver.department";
	
	/**
	 * 查询机房中设备个数
	 */
	public static final String QUERY_ASM_IN_ROOM_COUNT = "query.asm.in.room.count";
	
	/**
	 * 查询设备在机柜的情况
	 */
	public static final String QUERY_ASSET_IN_DRAW = "query.asset.in.draw";
	
	/**
	 * 查询云主机列表
	 */
	public static final String QUERY_NOVAVM_LIST = "query.novavm.list";
	
	/**
	 * 查询云主机列表个数
	 */
	public static final String QUERY_NOVAVM_LIST_COUNT = "query.novavm.list.count";
	
	/**
	 * 查询云主机详细信息
	 */
	public static final String QUERY_NOVAVM_DETAIL = "query.novavm.detail";
	
	/**
	 * 查询当前还未是租户的客户
	 */
	public static final String QUERY_NOT_TENANT_CUSTOM = "query.not.tenant.custom";
	
	/**
	 * 查询租户的管理员名称
	 */
	public static final String QUERY_ADMINNAME_BY_PROJECTID = "query.adminName.by.projectId";
	
	/**
	 * 查询租户列表
	 */
	public static final String QUERY_PROJECT = "query.project";
	
	/**
	 * 查詢租戶的mac地址
	 */
	public static final String QUERY_MACADDRESS_BY_PROJECTID = "query.macAddress.by.projectId";
	
	/**
	 * 查询回收站列表
	 */
	public static final String QUERY_RECYCLEITEMS = "query.recycleitems";
	
	/**
	 * 查询回收站列表
	 */
	public static final String QUERY_RECYCLEITEMS_COUNT = "query.recycleitems.count";
	
	/**
	 * 查询云硬盘回收站列表
	 */
	public static final String QUERY_RECYCLEITEMS_VOLUME_COUNT = "query.recycleitems.volume.count";
	/**
	 * 查询云硬盘回收站列表
	 */
	public static final String QUERY_RECYCLEITEMS_VOLUME = "query.recycleitems.volume";
	
	/**
	 * 除自身和父级租户以外的配额
	 */
	public static final String QUERY_QUOTA_SUM = "query.quota.sum";
	
	/**
	 * 查询应用管理配置拓扑图
	 */
	public static final String QUERY_APP_VIEWS = "query.app.views";
	

	public static final String QUERY_NOVA_CIDR ="query.nova.cidr";
	/**
	 * App个数
	 */
	public static final String QUERY_APP_CENTER_APP ="query.app.center.app";
	/**
	 * 数据库个数
	 */
	public static final String QUERY_APP_CENTER_DB ="query.app.center.db";
	/**
	 * 云主机个数
	 */
	public static final String QUERY_APP_CENTER_NOVA ="query.app.center.nova";
	
	/**
	 * 根租户云主机个数
	 */
	public static final String QUERY_APP_CENTER_ROOT_NOVA ="query.app.center.root.nova";
	/**
	 * 公网IP个数
	 */
	public static final String QUERY_APP_CENTER_FLOATINGIPS ="query.app.center.floatingips";
	/**
	 * 用户的资源已使用量
	 */
	public static final String QUERY_APP_CENTER_SUM ="query.app.center.sum";
	/**
	 * 查询VDC使用了的网段
	 */
	public static final String QUERY_VDC_IPS ="query.vdc.ips";
	
	
	/**
	 * 用户最近几次登录日志
	 */
	public static final String QUERY_USER_LATELY_LOGS = "query.user.lately.logs";
	
	/**
	 * 最新的未读公告
	 */
	public static final String QUERY_TENANT_LATELY_UNREAND_NOTICES = "query.tenant.lately.unread.notices";
	
	/**
	 * floatingip绑定云主机使用的云主机列表 
	 */
	public static final String QUERY_TENANT_FLOATING_NOVAM = "query.tenant.floatingip.novam";
	
	/**
	 * 路由连接到私网的网络列表 
	 */
	public static final String QUERY_ROUTE_INTERFACE_NETWORK = "query.route.interface.network";
	
	/**
	 * 公网上的路由
	 */
	public static final String QUERY_PUBLIC_NETWORK_ROUTE = "query.public.network.route";
	
	/**
	 * floatingip绑定云主机使用的云主机列表 （用户下的主机）
	 */
	public static final String QUERY_USER_FLOATING_NOVAM = "query.user.floatingip.novam";
	
	/**
	 * 公网查询具有网卡的主机 
	 */
	public static final String QUERY_PUBLIC_PORT_NOVAVM = "query.public.port.novavm";
	
	/**
	 * 公网查询具有网卡的主机个数  
	 */
	public static final String QUERY_PUBLIC_PORT_NOVAVM_COUNT = "query.public.port.novavm.count";
	
	/**
	 * 最小规格要求 
	 */
	public static final String QUERY_PUBLIC_NOVAFLAVOR = "query.public.novaflavor";
	
	/**
	 * 查询公网IP可连的监听器
	 */
	public static final String QUERY_PUBLIC_VIPS = "query.public.vips";
	
	/**
	 * 查询公网IP可连的监听器个数
	 */
	public static final String QUERY_PUBLIC_VIPS_COUNT = "query.public.vips.count";
	
	
	/**
	 * 查询云主机可挂载的网卡
	 */
	public static final String QUERY_PUBLIC_PORT = "query.public.port";
	
	/**
	 * 查询云主机可挂载的网卡
	 */
	public static final String QUERY_PUBLIC_PORT_COUNT = "query.public.port.count";
	
	/**
	 * 查询公网IP所在的网和私有连接情况
	 */
	public static final String QUERY_PUBLIC_NETWORKIDS = "query.public.networkids";
	
	/**
	 * 创建新的历史数据表
	 */
	public static final String NEW_TABLE_PFM_VALUE_HISTORY = "new.table.pfm.value.history";
	
	/**
	 * 获取需求工单已处理数量
	 */
	public static final String QUERY_REQUEST_MASTER_APPROVER_HANDLED_COUNT = "query.request.master.approver.handled.count";
	
	/**
	 * 获取事件工单已处理数量
	 */
	public static final String QUERY_INC_MASTER_APPROVER_HANDLED_COUNT = "query.inc.master.approver.handled.count";
	
	/**
	 * 统计性能数据
	 */
	public static final String QUERY_PFM_GROUP_VALUE = "query.pfm.group.value";
	
	/**
	 * 查询公网ip可分配的设备id
	 */
	public static final String QUERY_FLOATINGIP_ALLOCATION_PORTID = "query.floatingip.allocation.portid";
	
	/**
	 * 查询主机池底下关联租户信息
	 */
	public static final String QUERY_HOSTPOOL_PROJECT = "query.hostpool.project";
	
	/**
	 * 查询cvk底下关联租户信息
	 */
	public static final String QUERY_SERVER2OVE_PROJECT = "query.server2ove.project";
	
	/**
	 * 新增实服务成员的云主机网卡列表
	 */
	public static final String QUERY_VLB_MEMBER_PORT = "query.vlb.member.port";
	
	/**
	 * 查询云主机某个可用域底下未定价规格信息
	 */
	public static final String QUERY_UNPRICED_VM_FLAVOR = "query.unpriced.vm.flavor";
	
	/**
	 * 查询云主机某个可用域底下已定价规格信息
	 */
	public static final String QUERY_PRICED_VM_FLAVOR = "query.priced.vm.flavor";
	
	/**
	 * 查询云主机底下公网ip
	 */
	public static final String QUERY_VM_PUBLIC_IP = "query.vm.public.ip";
	
	/**
	 * 查询云主机底下私网ip
	 */
	public static final String QUERY_VM_PRIVATE_IP = "query.vm.private.ip";
	
	/**
	 * 根据网络名称查询网卡
	 */
	public static final String QUERY_PORT_BY_NETWORK_NAME = "query.port.by.network.name";
	
	/**
	 * 查询租户的私网ip
	 */
	public static final String QUERY_TENANT_PRIVATE_IP = "query.tenant.private.ip";
	
	/**
	 * 查询租户的私网ip数量
	 */
	public static final String QUERY_TENANT_PRIVATE_IP_COUNT = "query.tenant.private.ip.count";

	/**
	 * 查询租户的私网ip
	 */
	public static final String QUERY_TRIGGER_OWNER = "query.trigger.owner";
	
	/**
	 * 查询计费明细的扣费流水账单
	 */
	public static final String QUERY_MEASURE_BILL = "query.measure.bill";
	
	/**
	 * 查询服务器加入监控的默认模板
	 */
	public static final String QUERY_HOST_TEMPLATE = "query.host.template";

	/**
	 * 查询规定时间内的网络流量统计数据
	 */
	public static final String QUERY_NETFLOW_GROUP = "query.netflow.group";

	/**
	 * 查询规定时间内的某租户的进出口流量数据
	 */
	public static final String QUERY_NETFLOW_TENANTID_GROUP = "query.netflow.tenantid.group";

	
}
