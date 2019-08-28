package com.h3c.iclouds.common;

import com.h3c.iclouds.utils.StrUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigProperty {

	public static String PROJECT_SESSION_KEY = null;

	public static String PROJECT_TOKEN_KEY = null;

	public static String PROJECT_OPE_KEY = null;
	
	public static String PROJECT_INIT_CTNAME = null;
	
	/**
	 * iyun token前缀
	 */
	public static String PROJECT_TOKEN_IYUN_PROFIX = null;
	
	/**
	 * abc token前缀
	 */
	public static String PROJECT_TOKEN_ABC_PROFIX = null;
	
	public static int IYUN_TOKEN_TIMEOUT = 1800000;
	
	public static final String REQUEST_UUID = "REQUEST_UUID";
	
	public static final String LOG_WRITE_TYPE = "log_write_type";
	
	public static final String LOG_WRITE_TYPE_STOP = "log_write_type_stop";
	
	public static final String LOGS_TOP_FLAG = "logs_top_flag";
	
	public static final String SYSTEM_FLAG = "SYSTEM_FLAG";

	public static final String RESULT = "result";

	public static final String RECORD = "record";

	public static final String TRANSLATE = "translate";

	public static final String SYS_FLAG_YUNWEI = "2";
	
	public static final String SYS_FLAG_YUNYING = "1";

	public static final String CLOUDOS_COLLBACK_MAP_KEY = "cloudosCollback";

	/** email 服务器 */
	public static final String EMAIL_HOST = "iclouds.email.host";

	/** email 发件地址 */
	public static final String EMAIL_SOURCE_ADDRESS = "iclouds.email.source.address";

	/** email 端口 */
	public static final String EMAIL_PORT = "iclouds.email.port";

	/** email 用户名 */
	public static final String EMAIL_USERNAME = "iclouds.email.username";

	/** email pwd */
	public static final String EMAIL_PWD = "iclouds.email.pwd";

	/** 不允许操作的id */
	public static Set<String> STOP_SET = new HashSet<>();

	public static void initProjectInfo(Map<String, String> configMap) {
		PROJECT_SESSION_KEY = configMap.get("project.session.key");
		PROJECT_INIT_CTNAME = configMap.get("iyun.init.ct.name");
		PROJECT_TOKEN_KEY = configMap.get("project.token.key");
		PROJECT_OPE_KEY = configMap.get("project.ope.key");
		int timeout = StrUtils.tranInteger(configMap.get("project.token.timeout"));
		if(timeout > 0) {
			IYUN_TOKEN_TIMEOUT = timeout * 1000;
		}
		
		PROJECT_TOKEN_IYUN_PROFIX = configMap.get("project.token.iyun.profix");
		if(!StrUtils.checkParam(PROJECT_TOKEN_IYUN_PROFIX)) {
			PROJECT_TOKEN_IYUN_PROFIX = "iyun_token:";
		}
		
		PROJECT_TOKEN_ABC_PROFIX = configMap.get("project.token.abc.profix");
		if(!StrUtils.checkParam(PROJECT_TOKEN_ABC_PROFIX)) {
			PROJECT_TOKEN_ABC_PROFIX = "abc_token:";
		}

	}

	public static void initCloudosStopId(String stopIds) {
		if(StrUtils.checkParam(stopIds)) {
			String[] ids = stopIds.split(",");
			if(ids != null && ids.length > 0) {
				for (String id : ids) {
					STOP_SET.add(id);
				}
			}
		}
	}

	/** 需求申请单邮件key */
	public static final String REQUEST_MASTER_MAIL_KEY = "requestMaster:";
	
	/** 事件申请单邮件key */
	public static final String INC_MASTER_MAIL_KEY = "incMaster:";

	/** 1:未部署 */
	public static final String WORKFLOW_STATUS1_UNDEPLOY = "1";

	/** 2:已部署 */
	public static final String WORKFLOW_STATUS2_DEPLOY = "2";

	/** 3:挂起 */
	public static final String WORKFLOW_STATUS3_HANGUP = "3";

	/** 1:待提交 */
	public static final String MASTER_STEP_SUBMIT = "1";

	/** 2:开始审批 */
	public static final String MASTER_STEP_APPROVE = "2";

	/** 1:待提交 */
	public static final String MASTER_STEP1_SUBMIT_APPROVE = "usertask1";

	/** 2:待区域经理审批 */
	public static final String MASTER_STEP2_DEPARTMENT_APPROVE = "usertask2";

	/** 3:待权签审批 */
	public static final String MASTER_STEP3_SIGN_APPROVE = "usertask3";

	/** 4:待调度 */
	public static final String MASTER_STEP4_CONTROL = "usertask4";

	/** 5:待处理 */
	public static final String MASTER_STEP5_HANDLE = "usertask5";

	/** 6:待测试 */
	public static final String MASTER_STEP6_TEST = "usertask6";

	/** 7:待客户验证 */
	public static final String MASTER_STEP7_VALIDATE = "usertask7";
	
	/** 6:结束环节 */
	public static final String WORKFLOW_END = "endevent";

	/** 8:关闭 */
	public static final String MASTER_STEP8_CLOSE = "3";

	/** 1:待确认 */
	public static final String MASTER_STATUS1_CONFIRM = "1";

	/** 2:处理中 */
	public static final String MASTER_STATUS2_HANDLE = "2";

	/** 3:已关闭 */
	public static final String MASTER_STATUS3_CLOSED = "3";

	/** 4:驳回 */
	public static final String MASTER_STATUS4_REJECT = "4";

	/** 1:待提交 */
	public static final String INC_STEP_SUBMIT = "1";

	/** 2:开始审批 */
	public static final String INC_STEP_APPROVE = "2";

	/** 1:待提交 */
	public static final String INC_STEP1_SUBMIT_APPROVE = "ewo_usertask1";

	/** 2:一线处理 */
	public static final String INC_STEP2_FIRST_LINE = "ewo_usertask2";

	/** 3:二线处理 */
	public static final String INC_STEP3_SECOND_LINE = "ewo_usertask3";

	/** 4:用户确认 */
	public static final String INC_STEP4_VALIDATE = "ewo_usertask4";

	/** 4:用户确认 */
	public static final String INC_STEP5_CLOSE = "3";

	/**
	 * 流程流转条件
	 */
	public static final String REQUEST_PARAMETER = "requestParameter";
	
	/**
	 * 文件上传key
	 */
	public static final String FILE_KEY = "fileKey";

	/**
	 * 流程申请人低于第二级部门
	 */
	public static final String REQUEST_STARTFLOW_LOWER2 = "approve";

	/**
	 * 流程申请人高于第二级部门（包括第二级部门）
	 */
	public static final String REQUEST_STARTFLOW_UPPER2 = "reject";

	/**
	 * 流程流转条件:拒绝
	 */
	public static final String REQUEST_PARAMETER_REJECT = "reject";

	/**
	 * 流程流转条件:通过
	 */
	public static final String REQUEST_PARAMETER_APPROVE = "approve";

	/**
	 * 流程流转审批人
	 */
	public static final String REQUEST_APPROVER = "approver";

	/**
	 * 需要做部门控制的环节需要加上department在activiti:candidateGroups中
	 */
	public static final String REQUEST_DEPARTMENT_CONTROL = "department";

	/**
	 * 流程流转批注
	 */
	public static final String REQUEST_COMMENT = "comment";

	/** 1:新增 */
	public static final String ITEM_REQTYPE1_INSERT = "1";

	/** 2:修改 */
	public static final String ITEM_REQTYPE2_UPDATE = "2";

	/** 3:删除 */
	public static final String ITEM_REQTYPE3_DELETE = "3";

	/** 4:不变 */
	public static final String ITEM_REQTYPE4_CONSTANT = "4";

	/** 1:已变更 */
	public static final String MASTER_CHGFLAG1_YES = "1";

	/** 2:未变更 */
	public static final String MASTER_CHGFLAG2_NO = "2";

	/** 0:是,正常 */
	public static final String YES = "0";

	/** 1:否，停用 */
	public static final String NO = "1";

	/** 1:整型 */
	public static final String ITEM_DATATYPE1_INT = "1";

	/** 2:浮点型 */
	public static final String ITEM_DATATYPE2_FLOAT = "2";

	/** 3:字符串型 */
	public static final String ITEM_DATATYPE3_STRING = "3";

	/** 4:整型数组 */
	public static final String ITEM_DATATYPE4_INT_ARRAY = "4";

	/** 5:浮点型数组 */
	public static final String ITEM_DATATYPE5_FLOAT_ARRAY = "5";

	/** 6:字符型数组 */
	public static final String ITEM_DATATYPE6_CHAR_ARRAY = "6";

	/** 1:资源类型主机 */
	public static final String TEMPLATES_CLASS1_HOST = "1";

	/** 2:资源类型云硬盘 */
	public static final String TEMPLATES_CLASS2_DISK = "2";

	/** 由系统写入的默认内容 */
	public static final String SYSTEM_WRITE = "默认内容";

	/** 资产类型 */
	public static final String CMDB_ASSET_TYPE = "cmdb.assert.type";
	
	/** 路由器 */
	public static final String CMDB_ASSET_TYPE_ROUTER = "router";

	/** 交换机 */
	public static final String CMDB_ASSET_TYPE_SWITCH = "switch";

	/** 服务器 */
	public static final String CMDB_ASSET_TYPE_SERVER = "server";

	/** 存储 */
	public static final String CMDB_ASSET_TYPE_STOCK = "stock";

	/** 板卡 */
	public static final String CMDB_ASSET_TYPE_BOARDS = "boards";

	/** 防火墙 */
	public static final String CMDB_ASSET_TYPE_FIREWALL = "firewall";

	/** 其他 */
	public static final String CMDB_ASSET_TYPE_OTHER = "other";

	/** 角色类型：系统角色 */
	public static final String SM_ROLE_FLAG1_SYSTEM = "1";

	/** 角色类型：租户角色 */
	public static final String SM_ROLE_FLAG2_PROJECT = "2";

	/** IP创建并使用 */
	public static final String IP_HISTORY_CREATE = "create";

	/** IP使用 */
	public static final String IP_HISTORY_USE = "use";

	/** IP移除 */
	public static final String IP_HISTORY_DELETE = "delete";

	/** 资产状态：草稿 */
	public static final String CMDB_ASSET_FLAG1_DRAFT = "1";

	/** 资产状态：使用中 */
	public static final String CMDB_ASSET_FLAG2_USE = "2";

	/** 资产状态：已退库 */
	public static final String CMDB_ASSET_FLAG3_UNUSE = "3";

	/** 资产状态：草稿 */
	public static final String CMDB_SOFTWARE_FLAG1_DRAFT = "1";

	/** 资产状态：使用中 */
	public static final String CMDB_SOFTWARE_FLAG2_USE = "2";

	/** 资产状态：停用 */
	public static final String CMDB_SOFTWARE_FLAG3_UNUSE = "3";

	/** 端口连接：上联口 */
	public static final String CMDB_NETPORT_LINK_TRUNK = "trunk";

	/** 端口连接：下联口 */
	public static final String CMDB_NETPORT_LINK_ACCESS = "access";

	/** 网口连接：物理网口 */
	public static final String CMDB_NETPORT_ETHTYPE1_PHYSICS = "1";

	/** 网口连接：虚拟网口 */
	public static final String CMDB_NETPORT_ETHTYPE2_VIRTUAL = "2";

	/** 执行任务：等待执行 */
	public static final String TASK_STATUS1_WAIT = "1";

	/** 执行任务：执行中 */
	public static final String TASK_STATUS2_HANDLE = "2";

	/** 执行任务：执行完毕：成功 */
	public static final String TASK_STATUS3_END_SUCCESS = "3";

	/** 执行任务：执行完毕：失败 */
	public static final String TASK_STATUS4_END_FAILURE = "4";

	/** 执行任务：执行完毕：失败:cloudos认证失败 */
	public static final String TASK_STATUS5_END_CLOUDOS_ERROR = "5";
	
	/** 执行任务：取消任务 */
	public static final String TASK_STATUS6_CANCEL = "6";

	/** 资源操作类型：删除 */
	public static final String RESOURCE_OPTION_DELETE = "0";

	/** 资源操作类型：新增 */
	public static final String RESOURCE_OPTION_ADD = "1";

	/** 资源操作类型：修改 */
	public static final String RESOURCE_OPTION_UPDATE = "2";

	/** 资源类型: 防火墙 */
	public static final String RESOURCE_TYPE_FIREWALL = "1";

	/** 资源类型: 路由器 */
	public static final String RESOURCE_TYPE_ROUTE = "2";

	/** 资源类型: 网络 */
	public static final String RESOURCE_TYPE_NETWORK = "3";

	/** 资源类型: 负载均衡 */
	public static final String RESOURCE_TYPE_VLBPOOL = "4";

	/** 资源类型: 防火墙策略集 */
	public static final String RESOURCE_TYPE_FIREWALL_POLICY = "5";

	/** 资源类型: 策略规则 */
	public static final String RESOURCE_TYPE_FIREWALL_POLICY_RULE = "6";

	/** 资源类型: 子网 */
	public static final String RESOURCE_TYPE_SUBNET = "7";

	/** 资源类型: 负载均衡组 */
	public static final String RESOURCE_TYPE_VLB = "8";

	/** 资源类型: 负载均衡成员 */
	public static final String RESOURCE_TYPE_VLBMEMBER = "9";

	/** 资源类型: 负载均衡监听器 */
	public static final String RESOURCE_TYPE_HEALTHMONITOR = "10";

	/** 资源类型: 负载均衡vip池 */
	public static final String RESOURCE_TYPE_VLBVIP = "11";

	/** 资源类型: 虚拟网卡 */
	public static final String RESOURCE_TYPE_PORT = "12";

	/** 资源操作状态：创建中 */
	public static final String RESOURCE_OPTION_STATUS_CREATING = "0";

	/** 资源操作状态：修改中 */
	public static final String RESOURCE_OPTION_STATUS_UPDATING = "1";

	/** 资源操作状态：删除中 */
	public static final String RESOURCE_OPTION_STATUS_DELETING = "2";

	/** 资源操作状态：成功 */
	public static final String RESOURCE_OPTION_STATUS_SUCCESS = "3";

	/** 资源操作状态：创建异常 */
	public static final String RESOURCE_OPTION_STATUS_CREATE_EXCEPTION = "4";

	/** 资源操作状态：修改失败 */
	public static final String RESOURCE_OPTION_STATUS_UPDATE_FAILURE = "5";

	/** 资源操作状态：删除失败 */
	public static final String RESOURCE_OPTION_STATUS_DELETE_FAILURE = "6";

	/** 资源操作状态：连接失败 */
	public static final String RESOURCE_OPTION_STATUS_LINK_FAILURE = "7";

	/** 资源操作状态：等待执行 */
	public static final String RESOURCE_OPTION_STATUS_WAITING = "8";

	/** 资源操作状态：连接防火墙中 */
	public static final String RESOURCE_OPTION_STATUS_LINKING_FIREWALL = "9";

	/** 资源操作状态：断开连接防火墙中 */
	public static final String RESOURCE_OPTION_STATUS_UNLINKING_FIREWALL = "10";

	/** 资源操作状态：删除异常 */
	public static final String RESOURCE_OPTION_STATUS_DELETE_EXCEPTION = "12";

	/** 资源操作状态：修改异常 */
	public static final String RESOURCE_OPTION_STATUS_UPDATE_EXCEPTION = "13";

	/** 资源操作状态：连接路由器中 */
	public static final String RESOURCE_OPTION_STATUS_LINKING_ROUTE = "14";

	/** 资源操作状态：断开连接路由器中 */
	public static final String RESOURCE_OPTION_STATUS_UNLINKING_ROUTE = "15";

	/** 资源类型: 云硬盘(云硬盘资源基础大类为2) */
	public static final String RESOURCE_TYPE_VOLUME = "volumes";

	/** 资源类型：存储总容量*/
	public static final String RESOURCE_TYPE_GIGABYTES = "gigabytes";

	/** 资源类型：内存容量*/
	public static final String RESOURCE_TYPE_RAM = "ram";

	/** 资源类型：主机数量*/
	public static final String RESOURCE_TYPE_INSTANCES = "instances";

	/** 资源类型：快照数量*/
	public static final String RESOURCE_TYPE_SNAPSHOTS = "snapshots";

	/** 资源类型：cores*/
	public static final String RESOURCE_TYPE_CORES = "cores";

	/** 资源类型：IPsec策略数量*/
	public static final String RESOURCE_TYPE_IPSECPOLICY = "ipsercpolicy";

	/** 资源类型：安全组数量*/
	public static final String RESOURCE_TYPE_SECURITYGROUP = "securitygroup";

	/** 资源类型：安全组规则数量*/
	public static final String RESOURCE_TYPE_SECURITYGROUPRULE = "srcuritygrouprule";

	/** 资源类型：VPN数量*/
	public static final String RESOURCE_TYPE_VPNSERVICE = "vpnservice";

	/** 资源类型: 虚拟网卡 */
	public static final String RESOURCE_TYPE_IPS = "ips";

	/** 资源类型: 公网ip */
	public static final String RESOURCE_TYPE_FLOATINGIP = "floatingip";

	public final static ConcurrentHashMap<Integer, String> novaVmState = new ConcurrentHashMap<>();
	
	static {
		novaVmState.put(1, ResultType.state_normal.toString());
		novaVmState.put(2, ResultType.state_stop.toString());
		novaVmState.put(3, ResultType.state_creating.toString());
		novaVmState.put(4, ResultType.state_exception.toString());
		novaVmState.put(5, ResultType.state_hang_up.toString());
		novaVmState.put(6, ResultType.state_create_failure.toString());
		novaVmState.put(7, ResultType.state_rebooting.toString());
		novaVmState.put(8, ResultType.state_starting.toString());
		novaVmState.put(9, ResultType.state_stoping.toString());
		novaVmState.put(10, ResultType.state_deleting.toString());
		novaVmState.put(11, ResultType.state_delete_failure.toString());
		novaVmState.put(12, ResultType.state_image_translating.toString());
		
		novaVmState.put(13, ResultType.state_image_translate_failure.toString());
		
		novaVmState.put(14, ResultType.state_image_translating_create_image.toString());
		
		novaVmState.put(15, ResultType.state_image_translating_create_vm.toString());
		
		novaVmState.put(16, ResultType.state_image_translating_create_template.toString());
		
		novaVmState.put(17, ResultType.state_image_translating_replace_vm.toString());

		novaVmState.put(18, ResultType.state_cloning.toString());
		
		novaVmState.put(19, ResultType.state_clone_operating.toString());
		
	}
	public final static ConcurrentHashMap<Integer, String> novaVmPowerState = new ConcurrentHashMap<>();
	static {
		novaVmPowerState.put(1, "开机");
		novaVmPowerState.put(2, "关机");
		novaVmPowerState.put(3, "休眠");
		novaVmPowerState.put(4, "睡眠");
	}
	public final static List<String> class_computer_resource = Arrays
			.asList(new String[] { "cores", "instances", "ram" });

	public final static List<String> class_storage_resource = Arrays
			.asList(new String[] { "gigabytes", "snapshots", "volumes" });
	public final static List<String> class_network_resource = Arrays
			.asList(new String[] { "router", "vpnservice", "network", "firewall", "ips", "loadbalancer", "floatingip",
					"listener", "security_group", "ipsecpolicy", "security_group_rule" });
	public final static List<String> class_third_resource = Arrays
			.asList(new String[] { "humansize", "storagesize", "monthsize" });

	// 虚拟网卡的device_owner

	/**虚拟网卡的device_owner：路由私网接口 */
	public static final String PORT_OWNER_ROUTE_INTERFACE = "network:router_interface";

	/**虚拟网卡的device_owner：路由网关(公网接口) */
	public static final String PORT_OWNER_ROUTE_GATEWAY = "network:router_gateway";

	/**虚拟网卡的device_owner：floating IP */
	public static final String PORT_OWNER_FLOATINGIP = "network:floatingip";

	/**虚拟网卡的device_owner：虚拟机 */
	public static final String PORT_OWNER_NOVAVM = "compute:cas";

	/**虚拟网卡的device_owner：负载均衡 */
	public static final String PORT_OWNER_LOADBANLANCER = "neutron:LOADBALANCER";

	/** 通知公告状态：草稿 */
	public static final String NOTICE_STATUS1_DRAFT = "1";

	/** 通知公告状态：发布 */
	public static final String NOTICE_STATUS2_USE = "2";

	/** 通知公告等级：紧急 */
	public static final String NOTICE_GRADE1_CRITICAL = "1";

	/** 通知公告状态：一般 */
	public static final String NOTICE_GRADE2_USE = "2";

	/** 云硬盘删除标志位:正常 */
	public static final String VOLUME_DELETED0_NORMAL = "0";

	/** 云硬盘删除标志位:回收站 */
	public static final String VOLUME_DELETED1_RECYCLE = "1";

	public static final String APP_DATABASE = "3";

	public static final String APP_CLUSTER_DATABASE = "13";

	public static final String APP_SERVICE = "2";

	public static final String APP_CLUSTER_SERVICE = "12";

	public static final String APP_APPLICATION = "0";

	/** 用户密码最小长度*/
	public static final int PASSWORD_MIN_LENGTH = 6;

	/** 用户密码最大长度*/
	public static final int PASSWORD_MAX_LENGTH = 18;

	/** map中错误信息对应的key*/
	public static final String ERROR_MSG_KEY = "errorMsg";

	/** 登录记错时间段,单位分钟 */
	public static final int LOGIN_SEPERATE_TIME = 1;

	/** 限制登录时间段,单位分钟 */
	public static final int LOGIN_RESTRICT_TIME = 1;

	/** 一定时间内允许最多输错几次密码 */
	public static final int PASSWORD_MAX_ERROR_TIMES = 5;
	
	/** floatingIp普通修改*/
	public static final String FLOATING_IP_NORMAL_CHANGE = "0";

	/** floatingIp的虚拟网卡修改*/
	public static final String FLOATING_IP_PORT_CHANGE = "1";

	/** 应用配置视图不改变 */
	public static final String RESOURCE_OPTION_NOCHANGE = "3";

	/** 硬盘运行状态：0 正常*/
	public static final String VOLUME_STATE_AVAILABLE = "0";
	
	/** 硬盘运行状态：1 不可用*/
	public static final String VOLUME_STATE_ERROR = "1";
	
	/** 硬盘运行状态：2 创建中*/
	public static final String VOLUME_STATE_CREATING = "2";
	
	/** 硬盘运行状态：5 已加载*/
	public static final String VOLUME_STATE_ATTACHED = "5";
	
	/** 硬盘运行状态：6 挂载中*/
	public static final String VOLUME_STATE_ATTACHING = "6";
	
	/** 硬盘运行状态：7 卸载中*/
	public static final String VOLUME_STATE_DETACHING = "7";
	
	/** 硬盘运行状态：8 删除中*/
	public static final String VOLUME_STATE_DELETING = "8";
	
	/** 硬盘运行状态：9 创建异常*/
	public static final String VOLUME_STATE_CREATE_EXCEPTION = "9";
	
	/** 硬盘运行状态：10 删除失败*/
	public static final String VOLUME_STATE_DELETE_FAILURE = "10";
	
	/** 硬盘运行状态：11 挂载失败*/
	public static final String VOLUME_STATE_ATTACH_FAILURE = "11";
	
	/** 硬盘运行状态：12 卸载失败*/
	public static final String VOLUME_STATE_DETTACH_FAILURE = "12";
	
	/** 硬盘运行状态：10 删除异常*/
	public static final String VOLUME_STATE_DELETE_EXCEPTION = "13";
	
	/** 硬盘运行状态：11 挂载异常*/
	public static final String VOLUME_STATE_ATTACH_EXCEPTION = "14";
	
	/** 硬盘运行状态：12 卸载异常*/
	public static final String VOLUME_STATE_DETTACH_EXCEPTION = "15";
	
	/** cloudos硬盘运行状态：可用*/
	public static final String VOLUME_STATE_CLOUDOS_AVAILABLE = "available";
	
	/** cloudos硬盘运行状态：创建中*/
	public static final String VOLUME_STATE_CLOUDOS_CREATING = "creating";
	
	/** cloudos硬盘运行状态：挂载中*/
	public static final String VOLUME_STATE_CLOUDOS_ATTACHING = "attaching";
	
	/** cloudos硬盘运行状态：卸载中*/
	public static final String VOLUME_STATE_CLOUDOS_DETACHING = "dettaching";
	
	/** cloudos硬盘运行状态：已挂载*/
	public static final String VOLUME_STATE_CLOUDOS_ATTACHED = "in-use";
	
	/** cloudos硬盘运行状态：不可用*/
	public static final String VOLUME_STATE_CLOUDOS_ERROR = "error";
	
	//消息通道前缀
	public static final String NOVAVM_PREFIX = "user_channel";
	
	/** 普通硬盘*/
	public static final String VOLUME_TYPE_NORMAL = "1";

	/** 固态硬盘*/
	public static final String VOLUME_TYPE_SSD = "2";

	/** 主机池视图对象类型：主机池*/
	public static final String POOLVIEW_POOLHOST_TYPE = "1";

	/** 主机池视图对象类型：集群*/
	public static final String POOLVIEW_CLUSTERS_TYPE = "2";

	/** 主机池视图对象类型：宿主机*/
	public static final String POOLVIEW_SERVER2OVE_TYPE = "3";

	/** 验证码前缀 */
	public static final String REDIS_CODE_VERIFY = "redis.code.verify";
	
	/** 操作tkoen前缀 */
	public static final String REDIS_CODE_OPE_TOKEN = "redis.code.ope.token";

	/** 存储组*/
	public static final String STORAGE_GROUP = "1";

	/** 存储集群 */
	public static final String STORAGE_CLUSTER = "2";

	/** 存储设备 */
	public static final String STORAGE_ITEM = "3";

	/** 独立存储 */
	public static final String STORAGE_ALONE = "4";
	
	/** 授权租户管理员 */
	public static final String AUTH_TENANT_ADMIN = "superRole";
	
	/** 云网盘类型虚拟机 */
	public static final String IYYANYSHARES = "iyyanyshares";
	
	/** 云备份类型虚拟机 */
	public static final String IYYANYBACKUPS = "iyyanybackups";
	
	/** sso票据凭证前缀 */
	public static final String IYUN_SSO_UID_PROFIX = "iyun.sso.uid.profix";
	
	/** 资产环境类型: 空闲 */
	public static final String CMDB_ASSET_USEFLAG_FREE = "1";
	/** 资产环境类型: 测试 */
	public static final String CMDB_ASSET_USEFLAG_TEST = "2";
	/** 资产环境类型: UAT */
	public static final String CMDB_ASSET_USEFLAG_UAT = "3";
	/** 资产环境类型: 生产环境 */
	public static final String CMDB_ASSET_USEFLAG_PRODUCT = "4";
	
	/** 配额classCode: 防火墙 */
	public static final String FIREWALL_QUOTA_CLASSCODE = "firewall";
	
	/** 配额classCode: 路由器 */
	public static final String ROUTER_QUOTA_CLASSCODE = "router";
	
	/** 配额classCode: 网络 */
	public static final String NETWORK_QUOTA_CLASSCODE = "network";
	
	/** 配额classCode: 负载均衡组 */
	public static final String VLB_QUOTA_CLASSCODE = "loadbalancer";
	
	/** 配额classCode: 监听器 */
	public static final String VLBPOOL_QUOTA_CLASSCODE = "listener";
	
	/** 配额classCode: 网卡 */
	public static final String PORT_QUOTA_CLASSCODE = "ips";
	
	/** 配额classCode: 公网ip */
	public static final String FLOATINGIP_QUOTA_CLASSCODE = "floatingip";
	
	/** 配额classCode: 云硬盘 */
	public static final String VOLUME_QUOTA_CLASSCODE = "volumes";
	
	/** 配额classCode: 硬盘容量 */
	public static final String GIGABYTES_QUOTA_CLASSCODE = "gigabytes";
	
	/** 配额classCode: cpu */
	public static final String CORES_QUOTA_CLASSCODE = "cores";
	
	/** 配额classCode: 内存 */
	public static final String RAM_QUOTA_CLASSCODE = "ram";
	
	/** 配额classCode: 云主机 */
	public static final String INSTANCES_QUOTA_CLASSCODE = "instances";
	
	/** 应用图标默认文件名称 */
	public static final String APP_ICON_NAME = "icon.jpg";
	
	// 超级管理员
	public static final String ZABBIX_SUPER_ADMIN_TYPE = "3";
	
	// 管理员
	public static final String ZABBIX_ADMIN_TYPE = "2";
	
	// 普通用户
	public static final String ZABBIX_USER_TYPE = "1";
	
	/** 扣费类型：0 自动扣费 */
	public static final String BILL_TYPE_AUTO = "0";
	
	/** 扣费类型：1 手动扣费 */
	public static final String BILL_TYPE_MANUAL = "1";
	
	/** 扣费类型：0 按天计费 */
	public static final String BILL_TYPE_DAY = "0";
	
	/** 扣费类型：1 按月计费 */
	public static final String BILL_TYPE_MONTH = "1";
	
	/** 扣费类型：1 按年计费 */
	public static final String BILL_TYPE_YEAR = "2";
	
	/** 服务器ip、mac清单文件名称 */
	public static final String MASTER_IP_MAC_FILE_NAME = "output.txt";

	/** 网络流量：进口类型 */
	public static final String NETFLOW_IN = "inFlow";

	/** 网络流量：出口类型 */
	public static final String NETFLOW_OUT = "outFlow";

	/** 公网流量产品 */
	public static final String NETWORK_FLOW_CLASSID = "ff8080815e54d84e015e5505f62a008a";
	
	/** 获取虚拟防火墙流量脚本路径 */
	public static final String VF_FLOW_SHELL_PATH = "/iyunHome/fwFlow";
	
	/** 执行获取虚拟防火墙流量脚本服务器ip */
	public static final String VF_FLOW_SHELL_HOST_IP = "vf.flow.shell.host.ip";
	
	/** 执行获取虚拟防火墙流量脚本服务器用户名 */
	public static final String VF_FLOW_SHELL_HOST_USERNAME = "vf.flow.shell.host.username";
	
	/** 执行获取虚拟防火墙流量脚本服务器密码 */
	public static final String VF_FLOW_SHELL_HOST_PASSWORD = "vf.flow.shell.host.password";
}

