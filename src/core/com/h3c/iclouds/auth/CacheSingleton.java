package com.h3c.iclouds.auth;

import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.QuotaClass;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CacheSingleton {
	
	private static CacheSingleton cacheSingleton = new CacheSingleton();

	/** 系统基本配置 */
	private Map<String, String> configMap;

	/** 当前服务器系统类型 */
	private String systemType = "linux";
	
	/** 系统配置文件路径 */
	private static final String CONFIG_PATH = "/config.properties";
	
	/** cloudosApi路径 */
	private static final String CLOUDOS_API_PATH = "/cloudosApi.properties";
	
	/** eisooApi路径 */
	private static final String EISOO_API_PATH = "/eisooApi.properties";
	
	/** issoApi路径 */
	private static final String ISSO_API_PATH = "/issoApi.properties";

	/** zabbixApi路径 */
	private static final String ZABBIX_API_PATH = "/zabbixApi.properties";

	/** zabbixApi路径 */
	private static final String TONGHUASHUN_CONFIG_PATH = "/ths.properties";

	/** SNMP OID路径 */
	private static final String SNMPOID_CONFIG_PATH = "/snmpOID.properties";
	
	/** 监控Api路径 */
	private static final String MONITOR_API_PATH = "/monitorApi.properties";
	
	/** sdnApi路径 */
	private static final String SDN_API_PATH = "/sdnApi.properties";
	
	private String cloudosFlag;
	
	/** 云管理API */
	private Map<String, String> cloudosApi = new HashMap<>();
	
	/** 爱数API */
	private Map<String, String> eisooApi = new HashMap<>();
	
	/** issoAPI */
	private Map<String, String> issoApi = new HashMap<>();

	/** zabbixConfigMap */
	private Map<String, String> zabbixConfigMap = new HashMap<>();

	/** zabbixConfigMap */
	private Map<String, String> tonghuashunConfigMap = new HashMap<>();

	/** zabbixConfigMap */
	private Map<String, String> snmpOIDConfigMap = new HashMap<>();
	
	/** monitorAPI */
	private Map<String, String> monitorApi = new HashMap<>();
	
	/** sdnAPI */
	private Map<String, String> sdnApi = new HashMap<>();
	
	/** 云管理配额资源分类表 */
	private List<QuotaClass> quotaClasses;
	
	/** 云管理员 */
	private String cloudRoleId;
	
	/** 运维管理员-运营 */
	private String operationRoleId;
	
	/** 电信管理员 */
	private String ctRoleId;
	
	/** 租户管理员 */
	private String tenantRoleId;
	
	/** 主管审批审批-区域经理 */
	private String chargeRoleId;
	
	/** 权签人角色-省公司 */
	private String signRoleId;
	
	/** 处理人角色-需求处理 */
	private String handleRoleId;
	
	/** 调度人角色-需求调度 */
	private String dispatchRoleId;
	
	/** 一线处理-事件工单 */
	private String ewoFirstRoleId;
	
	/** 二线处理-事件工单 */
	private String ewoSecondRoleId;
	
	/** 电信租户id */
	private String ctTenantId = "";

	/** 默认群组id */
	private String defaultGroupId;
	
	private Set<String> workFlowRoleId = new HashSet<>();
	
	/** 路由器设备类型id */
	private String routerAssetType;
	
	/** 交换机设备类型id */
	private String switchAssetType;
	
	private String projectPath;

	private String vmType;
	
	private String ipAddr;
	
	private boolean isJUnit = false;

	private String vmClassId;

	private String storageClassId;
	
	private boolean isDevMode = false;

	private String rootProject = "81cc455b2e9a4af3b6e74509390069b9";

	private CloudosClient adminClient;
	
	private boolean isFlagOpe = false;
	
	private boolean isRabbitMqMethod = false;
	
	private boolean issoSyn;
	
	private boolean monitorSyn;
	
	private boolean ipRepeat;
	
	//同花顺id
	private String thsTenantId;
	
	// 虚拟机转镜像同时执行任务上限
	private int vmToImageCount = 3;

    /** 不需要过滤的uri */
	private Set<String> ignoreURISet = new HashSet<>();

    /** 不需要过滤的后缀 */
	private Set<String> ignoreSuffixSet = new HashSet<>();
	
	private Map<String, String> ticket2TokenMap = new ConcurrentHashMap<>();

	private String cloudosTokenMethod = null;

	/** 租户id-租户名称 */
	private Map<String, String> projectNameMap = new ConcurrentHashMap<>();

	private String defaultMonitorId = null;

	public static CacheSingleton getInstance() {
		if(cacheSingleton == null) {
			cacheSingleton = new CacheSingleton();
		}
		return cacheSingleton;
	}
	
	private CacheSingleton() {
		this.configMap = getProperties(CONFIG_PATH);
		String name = this.getOsName();
		if(name.toLowerCase().indexOf("window") > -1) {
			this.systemType = "window";
		} else {
			this.systemType = "linux";
		}
		ConfigProperty.initProjectInfo(configMap);
		
		// 系统初始化角色
		this.cloudRoleId = this.getConfigValue("sm.role.cloud.admin");
		this.operationRoleId = this.getConfigValue("sm.role.operation.admin");
		this.ctRoleId = this.getConfigValue("sm.role.ct.admin");
		this.tenantRoleId = this.getConfigValue("sm.role.tenant.admin");
		this.chargeRoleId = this.getConfigValue("sm.role.workflow.charge");
		this.signRoleId = this.getConfigValue("sm.role.workflow.sign");
		this.defaultGroupId = this.getConfigValue("sm.group.default");
		
		this.workFlowRoleId.add(chargeRoleId);
		this.workFlowRoleId.add(signRoleId);
		
		this.handleRoleId = this.getConfigValue("sm.role.workflow.handle");
		this.dispatchRoleId = this.getConfigValue("sm.role.workflow.dispatch");
		
		this.workFlowRoleId.add(handleRoleId);
		this.workFlowRoleId.add(this.getConfigValue("sm.role.workflow.test"));
		this.workFlowRoleId.add(dispatchRoleId);
		
		// 事件工单
		this.ewoFirstRoleId = this.getConfigValue("sm.role.workflow.firstLine");
		this.ewoSecondRoleId = this.getConfigValue("sm.role.workflow.secondLine");
		this.workFlowRoleId.add(ewoFirstRoleId);
		this.workFlowRoleId.add(ewoSecondRoleId);
		
		// 初始化设备类型
		this.routerAssetType = this.getConfigValue("asm.master.asset.type.router");
		this.switchAssetType = this.getConfigValue("asm.master.asset.type.switch");
		
		this.vmType = this.getConfigValue("cmdb.cloud.vm");
		this.vmClassId = this.getConfigValue("vm_classid");
		this.storageClassId = this.getConfigValue("storage_classid");
		
		if(this.getConfigValue("flag.dev").equals("open")) {
			this.isDevMode = true;
		}
		if(this.getConfigValue("flag.ope").equals("open")) {
			this.isFlagOpe = true;
		}
		if(this.getConfigValue("iyun.task.method").equals("rabbitmq")) {
			this.isRabbitMqMethod = true;
		}

		this.issoSyn = Boolean.parseBoolean(this.getConfigValue("iyun.sso.user.sychronization"));
		this.monitorSyn = Boolean.parseBoolean(this.getConfigValue("iyun.monitor.sychronization"));
		this.ipRepeat = Boolean.parseBoolean(this.getConfigValue("iyun.network.ippool.repeat"));
		
		this.cloudosFlag = this.getConfigValue("flag.cloudos");
		this.rootProject = this.getConfigValue("rootid");
		this.thsTenantId = this.getConfigValue("iclouds.tonghuashun.tenant.id");
		
		String count = this.getConfigValue("iyun.vm.to.image.count");
		if(StrUtils.tranInteger(count) != 0) {
			vmToImageCount = StrUtils.tranInteger(count);
		}

        // 允许忽略的uri
		String ignoreURI = this.getConfigValue("filter.ignore.uri");
        if(StrUtils.checkParam(ignoreURI)) {
			String[] array = ignoreURI.split(",");
            if(array != null && array.length > 0) {
                for (String s : array) {
                    this.ignoreURISet.add(s);
                }
            }
        }
        // 允许忽略的后缀
        String ignoreSuffix = this.getConfigValue("filter.ignore.suffix");
        if(StrUtils.checkParam(ignoreURI)) {
			String[] array = ignoreSuffix.split(",");
            if(array != null && array.length > 0) {
                for (String s : array) {
                    this.ignoreSuffixSet.add(s);
                }
            }
        }

		// 初始化内置cloudos使用的内容
		ConfigProperty.initCloudosStopId(getCloudosApi("cloudos.stop.id"));
	}

	public boolean isFlagOpe() {
		return isFlagOpe;
	}

	public Map<String, String> getConfigMap() {
		return configMap;
	}
	
	public String getConfigValue(String key) {
		String value = configMap.get(key);
		return value == null ? "" : value;
	}

	public Map<String, String> getProperties(String path) {
		InputStream ins = null;
		Map<String, String> map = new HashMap<>();
		try {
			ins = this.getClass().getResourceAsStream(path);
			Properties props = new Properties();
			props.load(ins);
			for(Object obj : props.keySet()) {
				map.put(obj.toString(), props.get(obj).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(ins != null) ins.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	public String getSystemType() {
		return systemType;
	}

	public String getOsName() {
		return System.getProperty("os.name");	// 获取操作系统名称
	}

	public String getCloudRoleId() {
		return cloudRoleId;
	}

	public String getOperationRoleId() {
		return operationRoleId;
	}
	
	public String getCtRoleId() {
		return ctRoleId;
	}

	public String getTenantRoleId() {
		return tenantRoleId;
	}
	
	public String getChargeRoleId() {
		return chargeRoleId;
	}

	public String getSignRoleId() {
		return signRoleId;
	}

	public String getDefaultGroupId() {
		return defaultGroupId;
	}

	public Set<String> getWorkFlowRoleId() {
		return workFlowRoleId;
	}

	public String getRouterAssetType() {
		return routerAssetType;
	}

	public String getSwitchAssetType() {
		return switchAssetType;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getVmType() {
		return vmType;
	}

	public List<QuotaClass> getQuotaClasses() {
		return quotaClasses;
	}

	public void setQuotaClasses(List<QuotaClass> quotaClasses) {
		this.quotaClasses = quotaClasses;
	}

	public void setJUnit(boolean isJUnit) {
		this.isJUnit = isJUnit;
	}

	public String getCloudosApi(String key) {
		if(cloudosApi == null || cloudosApi.isEmpty()) {
			cloudosApi = getProperties(CLOUDOS_API_PATH);
		}
		return cloudosApi.get(key);
	}
	
	public String getEisooApi (String key) {
		if(eisooApi == null || eisooApi.isEmpty()) {
			eisooApi = CacheSingleton.getInstance().getProperties(EISOO_API_PATH);
		}
		return eisooApi.get(key);
	}
	
	public String getIssoApi (String key) {
		if(issoApi == null || issoApi.isEmpty()) {
			issoApi = CacheSingleton.getInstance().getProperties(ISSO_API_PATH);
		}
		return issoApi.get(key);
	}
	
	public String getMonitorApi (String key) {
		if(monitorApi == null || monitorApi.isEmpty()) {
			monitorApi = CacheSingleton.getInstance().getProperties(MONITOR_API_PATH);
		}
		return monitorApi.get(key);
	}
	
	public String getSdnApi (String key) {
		if(sdnApi == null || sdnApi.isEmpty()) {
			sdnApi = CacheSingleton.getInstance().getProperties(SDN_API_PATH);
		}
		return sdnApi.get(key);
	}
	
	public String getZabbixConfig (String key) {
		if(zabbixConfigMap == null || zabbixConfigMap.isEmpty()) {
			zabbixConfigMap = CacheSingleton.getInstance().getProperties(ZABBIX_API_PATH);
		}
		return zabbixConfigMap.get(key);
	}

	public String getTonghuashunConfigKey(String key) {
		return getTonghuashunConfigMap().get(key);
	}

	public void startThread(Runnable task) {
		ThreadPoolTaskExecutor poolTaskExecutor = SpringContextHolder.getBean("taskExecutor");
		LogUtils.info(this.getClass(), "线程池当前活动连接数: " + poolTaskExecutor.getActiveCount());
		poolTaskExecutor.execute(task);
	}
	

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getVmClassId() {
		return vmClassId;
	}

	public void setVmClassId(String vmClassId) {
		this.vmClassId = vmClassId;
	}

	public String getStorageClassId() {
		return storageClassId;
	}

	public void setStorageClassId(String storageClassId) {
		this.storageClassId = storageClassId;
	}

	public boolean isDevMode() {
		return isDevMode;
	}

	public void setDevMode(boolean isDevMode) {
		this.isDevMode = isDevMode;
	}

	public String getCloudosFlag() {
		return cloudosFlag;
	}

	public void setCloudosFlag(String cloudosFlag) {
		this.cloudosFlag = cloudosFlag;
	}

	public String getCtTenantId() {
		return ctTenantId;
	}

	public void setCtTenantId(String ctTenantId) {
		this.ctTenantId = ctTenantId;
	}

	public String getRootProject() {
		return rootProject;
	}

	public CloudosClient getAdminClient() {
		return adminClient;
	}

	public void setAdminClient(CloudosClient adminClient) {
		this.adminClient = adminClient;
	}

	public boolean isRabbitMqMethod() {
		return isRabbitMqMethod;
	}

	public int getVmToImageCount() {
		return vmToImageCount;
	}

	public void setVmToImageCount(int vmToImageCount) {
		this.vmToImageCount = vmToImageCount;
	}

	public String getDispatchRoleId() {
		return dispatchRoleId;
	}

	public void setDispatchRoleId(String dispatchRoleId) {
		this.dispatchRoleId = dispatchRoleId;
	}

	public String getHandleRoleId() {
		return handleRoleId;
	}

	public void setHandleRoleId(String handleRoleId) {
		this.handleRoleId = handleRoleId;
	}

	public String getEwoFirstRoleId() {
		return ewoFirstRoleId;
	}

	public void setEwoFirstRoleId(String ewoFirstRoleId) {
		this.ewoFirstRoleId = ewoFirstRoleId;
	}

	public String getEwoSecondRoleId() {
		return ewoSecondRoleId;
	}

	public void setEwoSecondRoleId(String ewoSecondRoleId) {
		this.ewoSecondRoleId = ewoSecondRoleId;
	}

    public Set<String> getIgnoreURISet() {
        return ignoreURISet;
    }

    public Set<String> getIgnoreSuffixSet() {
        return ignoreSuffixSet;
    }

	public Map<String, String> getTicket2TokenMap() {
		return ticket2TokenMap;
	}

	public void setTicket2TokenMap(Map<String, String> ticket2TokenMap) {
		this.ticket2TokenMap = ticket2TokenMap;
	}

	public boolean isIssoSyn () {
		return issoSyn;
	}
	
	public boolean isMonitorSyn () {
		return monitorSyn;
	}
	
	public boolean isIpRepeat () {
		return ipRepeat;
	}
	
	public String getZabbixAppUrl() {
		String zabbixAppUrl = getZabbixConfig("zabbix.url");
		String zabbixAppIp = getZabbixConfig("zabbix.ip");
		String zabbixAppPort = getZabbixConfig("zabbix.port");
		zabbixAppUrl = zabbixAppUrl.replace("app_ip", zabbixAppIp);
		zabbixAppUrl = zabbixAppUrl.replace("app_port", zabbixAppPort);
		return zabbixAppUrl;
	}

	public String getThsTenantId () {
		return thsTenantId;
	}
	
	public void setThsTenantId (String thsTenantId) {
		this.thsTenantId = thsTenantId;
	}

    public Map<String, String> getTonghuashunConfigMap() {
        if(tonghuashunConfigMap.isEmpty()) {
            tonghuashunConfigMap = CacheSingleton.getInstance().getProperties(TONGHUASHUN_CONFIG_PATH);
        }
        return tonghuashunConfigMap;
    }

	public Map<String, String> getSnmpOIDConfigMap() {
		if(snmpOIDConfigMap.isEmpty()) {
			snmpOIDConfigMap = CacheSingleton.getInstance().getProperties(SNMPOID_CONFIG_PATH);
		}
		return snmpOIDConfigMap;
	}

    /**
     * 处理默认domainId,主要区分1137版本的差异
     * @param domainId
     * @return
     */
    public String getDomainId(String domainId) {
        if(domainId != null) {
            return domainId;
        }
        String version = this.getCloudosApi("cloudos.api.version");
        if("1137".equals(version)) {
            domainId = this.getCloudosApi("cloudos.default.1137.domain.id");
        }
        return domainId;
    }

	/**
	 * 是否按照jar包的方式获取token
	 * @return
     */
	public boolean isCloudosTokenByJar() {
		if(this.cloudosTokenMethod == null) {
			cloudosTokenMethod = this.getCloudosApi("cloudos.token.mode");
		}
		return "byJar".equals(cloudosTokenMethod);
	}

	public Map<String, String> getProjectNameMap() {
		// 检查是否含根租户内容
		if(!projectNameMap.containsKey(this.getRootProject())) {
			projectNameMap.put(this.getRootProject(), this.getConfigValue("iclouds.cloudos.api.tenant"));
		}
		return projectNameMap;
	}

	public String getDefaultMonitorId() {
		return defaultMonitorId;
	}

	public void setDefaultMonitorId(String defaultMonitorId) {
		this.defaultMonitorId = defaultMonitorId;
	}
}
