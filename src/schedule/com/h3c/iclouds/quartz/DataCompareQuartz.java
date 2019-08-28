package com.h3c.iclouds.quartz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.DifferencesBiz;
import com.h3c.iclouds.biz.FirewallBiz;
import com.h3c.iclouds.biz.FloatingIpBiz;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.PolicieBiz;
import com.h3c.iclouds.biz.PolicieRuleBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.RenewalBiz;
import com.h3c.iclouds.biz.RouteBiz;
import com.h3c.iclouds.biz.TaskBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.biz.VolumeBiz;
import com.h3c.iclouds.common.CompareEnum;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.Firewall;
import com.h3c.iclouds.po.FloatingIp;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Policie;
import com.h3c.iclouds.po.PolicieRule;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Renewal;
import com.h3c.iclouds.po.Route;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.po.TaskDispatch;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.ThreadContext;

/**
 * iyun与cloudos差异对比
 * 
 * @author zkf5485 对比差异部分保存到 DifferencesBiz.addDiff
 *
 */
public class DataCompareQuartz extends BaseQuartz {

	@Resource(name = "baseDAO")
	private BaseDAO<TaskDispatch> baseDAO;

	// 用于保存差异部分
	@Resource
	private DifferencesBiz differencesBiz;

	@Resource
	private ProjectBiz projectBiz;
	
	@Resource
	private FirewallBiz firewallBiz;
	
	@Resource
	private RouteBiz routeBiz;
	
	@Resource
	private UserBiz userBiz;

	@Resource
    private PolicieRuleBiz policieRuleBiz;
	
	@Resource
	private PolicieBiz policieBiz;
	
	@Resource
	private NetworkBiz networkBiz;

	@Resource
	private NovaVmBiz novaVmBiz;
	
	@Resource
	private VolumeBiz volumeBiz;
	
	@Resource
	private FloatingIpBiz floatingIpBiz;

	@Resource
	private PortBiz portBiz;

	@Resource
	private TaskBiz taskBiz;
	
	// 租期过期
	@Resource
	private RenewalBiz renewalBiz;
	private static CacheSingleton singleton = CacheSingleton.getInstance();
	
	private void initTaskAutoWired() {
		if(baseDAO == null) {
			baseDAO = SpringContextHolder.getBean("baseDAO");
			differencesBiz = SpringContextHolder.getBean("differencesBiz");
			projectBiz = SpringContextHolder.getBean("projectBiz");
			routeBiz = SpringContextHolder.getBean("routeBiz");
			firewallBiz = SpringContextHolder.getBean("firewallBiz");
			policieRuleBiz = SpringContextHolder.getBean("policieRuleBiz");
			userBiz = SpringContextHolder.getBean("userBiz");
			policieBiz = SpringContextHolder.getBean("policieBiz");
			networkBiz = SpringContextHolder.getBean("networkBiz");
			novaVmBiz = SpringContextHolder.getBean("novaVmBiz");
			volumeBiz = SpringContextHolder.getBean("volumeBiz");
			floatingIpBiz = SpringContextHolder.getBean("floatingIpBiz");
			portBiz = SpringContextHolder.getBean("portBiz");
			taskBiz = SpringContextHolder.getBean("taskBiz");
			renewalBiz = SpringContextHolder.getBean("renewalBiz");
		}
	}

	@Override
	public void startQuartz() {
		this.initTaskAutoWired();
		Date time = new Date();
		Map<String, Object> queryMap = StrUtils.createMap("syncType", CompareEnum.SYNCTYPE_COMPARE.toString());
		TaskDispatch entity = baseDAO.singleByClass(TaskDispatch.class, queryMap);
		if (entity == null) {
			this.warn("Not set compare data for cloudos!");
			//return;
			entity = new TaskDispatch(); 
			entity.setId("1");
		}
		ThreadContext.set("syncDate", time);
		ThreadContext.set("taskDispatchId", entity.getId());
		CloudosClient client = getClient();
		if (client == null) {
			this.warn("Get cloudos client failure, task over.");
		}
		// 先更新节点时间
		entity.setLastSyncTime(time);
		baseDAO.update(entity);

		Map<String, String> projectNameMap = null;
		try {
			projectNameMap = diffProject();	// 对比cloudos中的租户信息
		} catch (Exception e) {
			this.exception(e, "Compare project error.");
		}
		
		if(projectNameMap == null) {
			this.warn("Not get project info, compare over.");
			return;
		}
		
		try {
			diffUser(projectNameMap);	// 对比cloudos中的用户信息
		} catch (Exception e) {
			this.exception(e, "Compare project error.");
		}
		
		Map<String, JSONObject> route2fwMap = null;
		try {
			// 对比cloudos中的防火墙信息
			route2fwMap = this.diffFirewall(projectNameMap);
		} catch (Exception e) {
			this.exception(e, "Compare firewall error.");
		}
		
		Map<String, JSONObject> network2routerMap = null;
		try {
			network2routerMap = this.diffRouter(projectNameMap, route2fwMap);
		} catch (Exception e) {
			this.exception(e, "Compare router error.");
		}
		
		try {
			this.diffNetwork(projectNameMap, network2routerMap);
		} catch (Exception e) {
			this.exception(e, "Compare network error.");
		}
		
		Map<String, String> hostIdNameMap = null;
		try {
			hostIdNameMap = this.diffCloudHost(projectNameMap);
		} catch (Exception e) {
			this.exception(e, "Compare cloud host error.");
		}
		
		try {
			this.diffFloatingIp(projectNameMap, hostIdNameMap);
		} catch (Exception e) {
			this.exception(e, "Compare cloud host error.");
		}
		
		try {
			this.diffVolume(projectNameMap, hostIdNameMap);
		} catch (Exception e) {
			this.exception(e, "Compare volume error.");
		}
		
//		try {
//			this.diffRenewal();
//		} catch (Exception e) {
//			this.exception(e, "Compare Renewal error.");
//		}
		
	}
	
	 
    
	
	
	
	private void diffFloatingIp(Map<String, String> projectNameMap, Map<String, String> hostIdNameMap) {
		CompareEnum dataType = CompareEnum.FLOATINGIP;
		CloudosClient client = this.getClient();
		String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_FLOATINGIP);
		JSONArray floatingips = HttpUtils.getJSONArray(client.get(uri), "floatingips");
		if(!StrUtils.checkCollection(floatingips)) {
			this.addMsg("获取CloudOS公网IP失败.");
			return;
		}
		List<FloatingIp> list = this.floatingIpBiz.getAll(FloatingIp.class);
		Map<String, FloatingIp> map = new HashMap<String, FloatingIp>();
		if(StrUtils.checkCollection(list)) {
			list.forEach(entity -> map.put(entity.getCloudosId(), entity));
		}
		
		for (int i = 0; i < floatingips.size(); i++) {
			JSONObject floatingip = floatingips.getJSONObject(i);
			String id = floatingip.getString("id");
			String name = floatingip.getString("name");
			String projectId = floatingip.getString("tenant_id");
			String projectName = projectNameMap.get(projectId);
			String cloudosPortId = floatingip.getString("port_id");	// 连接端口
			String floating_ip_address = floatingip.getString("floating_ip_address");	// 连接端口
			
			
			if(!StrUtils.checkParam(id, name)) {
				this.addMsg("在CloudOS中公网IP信息异常, 异常内容: " + floatingip.toJSONString());
				continue;
			}
			
			FloatingIp entity = map.remove(id);
			if(entity == null) {
				this.differencesBiz.addDiff(id, projectName, "公网IP[" + name + "][" + floating_ip_address +
						"][" + id + "]在CloudOS中存在，在IYun中不存在", CompareEnum.IN_CLOUDOS, dataType);
			} else {
				String iyunPortId = entity.getFixedPortId();	// 公网IP关联着网卡
				if(iyunPortId == null) {
					if(cloudosPortId != null) {
						uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_PORTS_ACTION, cloudosPortId);
						JSONObject portObj = HttpUtils.getJSONObject(client.get(uri), "port");
						if(portObj != null) {
							String binding_host_id = portObj.getString("binding:host_id");
							String device_id = portObj.getString("device_id");
							String cloudosPortName = portObj.getString("name");
							String hostName = hostIdNameMap.get(device_id);
							if(StrUtils.checkParam(binding_host_id)) {	// 连接的是云主机
								if(StrUtils.checkParam(device_id)) {
									this.differencesBiz.addDiff(entity.getId(), projectName,
											"在CloudOS中公网IP[" + name + "][" + id + "]挂载在云主机[" + hostName +
													"][" + device_id + "]上，在IYun上未做挂载。",
											CompareEnum.DATA_DIFF, CompareEnum.FLOATINGIP);
								} else {
									this.differencesBiz.addDiff(entity.getId(), projectName,
											"在CloudOS中公网IP[" + name + "][" + id + "]连接的port[" + cloudosPortName +
													"][" + cloudosPortId + "]对应的device_id为空。",
											CompareEnum.DATA_DIFF, CompareEnum.FLOATINGIP);
								}
							} else {	// 负载均衡
								// TODO 暂时不处理负载均衡的内容
							}
						} else {
							this.differencesBiz.addDiff(entity.getId(), projectName,
									"在CloudOS中公网IP[" + name + "][" + id + "]连接的port[" + cloudosPortId + "], 但是调用API查询不到对应的记录。", 
									CompareEnum.DATA_DIFF, CompareEnum.FLOATINGIP);
						}
					}
				} else {
					Port port = this.portBiz.findById(Port.class, iyunPortId);
					if(cloudosPortId == null) {
						String device_id = port.getDeviceId();
						if(StrUtils.checkParam(device_id)) {
							String owner = port.getDeviceOwner();
							if(owner.contains("compute")) {	// 云主机+
								String hostName = hostIdNameMap.get(device_id);
								if(StrUtils.checkParam(hostName)) {
									this.differencesBiz.addDiff(entity.getId(), projectName,
											"在IYun中公网IP[" + name + "][" + id + "]挂载在云主机[" + hostName + "][" + device_id + "]上，在CloudOS上未做挂载。", 
											CompareEnum.DATA_DIFF, CompareEnum.FLOATINGIP);
								} else {
									this.differencesBiz.addDiff(entity.getId(), projectName,
											"在IYun中公网IP[" + name + "][" + id + "]连接的port[" + port.getName() + "][" + iyunPortId + "]对应的device_id[" + device_id + "]查询不到云主机信息。", 
											CompareEnum.DATA_DIFF, CompareEnum.FLOATINGIP);
								}
							}
						} else {
							this.differencesBiz.addDiff(entity.getId(), projectName,
									"在IYun中公网IP[" + name + "][" + id + "]连接的port[" + port.getName() + "][" + iyunPortId + "]对应的device_id为空。", 
									CompareEnum.DATA_DIFF, CompareEnum.FLOATINGIP);
						}
					} else {
						if(!port.getCloudosId().equals(cloudosPortId)) {
							uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_PORTS_ACTION, cloudosPortId);
							JSONObject portObj = HttpUtils.getJSONObject(client.get(uri), "port");
							if(portObj != null) {
								String device_id = portObj.getString("device_id");
								String hostName = hostIdNameMap.get(device_id);
								this.differencesBiz.addDiff(entity.getId(), projectName, 
										"公网IP[" + name + "][" + id + "]在IYun中连接着云主机[" + hostIdNameMap.get(port.getDeviceId()) + "][" + port.getDeviceId() + "], 但是在CloudOS中连接云主机[" + hostName + "][" + device_id + "]", 
										CompareEnum.DATA_DIFF, CompareEnum.FLOATINGIP);
							} else {
								this.differencesBiz.addDiff(entity.getId(), projectName, 
										"公网IP[" + name + "][" + id + "]在IYun中连接着云主机[" + hostIdNameMap.get(port.getDeviceId()) + "][" + port.getDeviceId() + "], 但是在CloudOS中连接云主机查询不到对应连接网卡", 
										CompareEnum.DATA_DIFF, CompareEnum.FLOATINGIP);
							}
						}
					}
				}
			}
		}
		
		if(!map.isEmpty()) {
			for (FloatingIp entity : map.values()) {
				String id = entity.getId();
				String name = entity.getName();
				String projectName = projectNameMap.get(entity.getTenantId());
				this.differencesBiz.addDiff(id, projectName, "公网IP[" + name + "][" + id +
						"]在IYun中存在，在CloudOS中不存在", CompareEnum.IN_IYUN, dataType);
			}
		}		
	}
	
	private void diffVolume(Map<String, String> projectNameMap, Map<String, String> hostIdNameMap) {
		CompareEnum dataType = CompareEnum.VOLUME;
		CloudosClient client = this.getClient();
		String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_VOLUMES_DETAIL, client.getTenantId());
		JSONArray volumes = HttpUtils.getJSONArray(client.get(uri), "volumes");
		if(!StrUtils.checkCollection(volumes)) {
			this.addMsg("获取CloudOS云硬盘失败.");
			return;
		}
		List<Volume> list = this.volumeBiz.getAll(Volume.class);
		Map<String, Volume> map = new HashMap<String, Volume>();
		if(StrUtils.checkCollection(list)) {
			list.forEach(entity -> {
				if(StrUtils.checkParam(entity.getUuid())) {
					map.put(entity.getUuid(), entity);
				}
			});
		}
		
		for (int i = 0; i < volumes.size(); i++) {
			JSONObject volume = volumes.getJSONObject(i);
			String id = volume.getString("id");
			String name = volume.getString("name");
			String projectId = volume.getString("os-vol-tenant-attr:tenant_id");
			String projectName = projectNameMap.get(projectId);
			
			String cloudosServerId = null;
			String cloudosServerName = null;
			JSONArray attachments = volume.getJSONArray("attachments");
			if(StrUtils.checkCollection(attachments)) {
				cloudosServerId = attachments.getJSONObject(0).getString("server_id");
				cloudosServerName = hostIdNameMap.get(cloudosServerId);
			}
			
			if(!StrUtils.checkParam(id, name)) {
				this.addMsg("在CloudOS中云硬盘信息异常, 异常内容: " + volume.toJSONString());
				continue;
			}
			
			Volume entity = map.remove(id);
			if(entity == null) {
				this.differencesBiz.addDiff(id, projectName, "云硬盘[" + name + "][" + id + "]在CloudOS中存在，在IYun中不存在", CompareEnum.IN_CLOUDOS, dataType);
			} else {
				String volumeName = entity.getName();
				if(!name.equals(volumeName)) {
					this.differencesBiz.addDiff(id, projectName, 
						"云硬盘[" + name + "][" + id + "]的名称与CloudOS不一致, CloudOS: " + name + ", IYun: " + volumeName, 
						CompareEnum.DATA_DIFF, dataType);
				}
				
				// 未获取到云主机信息，不做连接对比
				if(hostIdNameMap != null) {
					String iyunServerId = entity.getHost();
					if(iyunServerId == null) {	// iyun没有挂载主机
						if(StrUtils.checkParam(cloudosServerId)) {	// cloudos有挂载主机
							this.differencesBiz.addDiff(entity.getId(), projectName, "云硬盘[" + name + "][" + id + "]在CloudOS中连接着云主机[" + cloudosServerName + "][" + cloudosServerId + "]", 
									CompareEnum.DATA_DIFF, CompareEnum.VOLUME);
						}
					} else {
						NovaVm novaVm = this.novaVmBiz.findById(NovaVm.class, iyunServerId);
						// iyun连接着路由器，在cloudos中没有连接
						if(!StrUtils.checkParam(cloudosServerId)) {
							this.differencesBiz.addDiff(entity.getId(), projectName, "云硬盘[" + name + "][" + id + "]在IYun中连接着云主机[" + novaVm.getHostName() + "][" + iyunServerId + "]", 
									CompareEnum.DATA_DIFF, CompareEnum.VOLUME);
						} else if(!cloudosServerId.equals(novaVm.getUuid())){
							this.differencesBiz.addDiff(entity.getId(), projectName, 
									"云硬盘[" + name + "][" + id + "]在IYun中连接着云主机[" + novaVm.getHostName() + "][" + iyunServerId + "], 但是在CloudOS中连接云主机[" + cloudosServerName + "][" + cloudosServerId + "]", 
									CompareEnum.DATA_DIFF, CompareEnum.VOLUME);
						}
					}
				}
			}
		}
		
		if(!map.isEmpty()) {
			for (Volume entity : map.values()) {
				String id = entity.getId();
				String name = entity.getHostName();
				String projectName = projectNameMap.get(entity.getProjectId());
				this.differencesBiz.addDiff(id, projectName, "云硬盘[" + name + "][" + id + "]在IYun中存在，在CloudOS中不存在", CompareEnum.IN_IYUN, dataType);
			}
		}
	}
	
	private Map<String, String> diffCloudHost(Map<String, String> projectNameMap) {
		CompareEnum dataType = CompareEnum.CLOUDHOST;
		CloudosClient client = this.getClient();
		String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_DETAIL, client.getTenantId());
		JSONArray servers = HttpUtils.getJSONArray(client.get(uri), "servers");
		if(!StrUtils.checkCollection(servers)) {
			this.addMsg("获取CloudOS云主机失败.");
			return null;
		}
		Map<String, String> hostIdNameMap = new HashMap<String, String>();
		List<NovaVm> list = novaVmBiz.getAll(NovaVm.class);
		Map<String, NovaVm> map = new HashMap<String, NovaVm>();

		Set<String> taskingHostIds = new HashSet<>();
		if(StrUtils.checkCollection(list)) {
			list.forEach(entity -> {
				hostIdNameMap.put(entity.getId(), entity.getHostName());
				if(StrUtils.checkParam(entity.getUuid())) {
					// 存在任务则不做同步
					int count = taskBiz.count(Task.class, StrUtils.createMap("busId", entity.getId()));
					if(count == 0) {
						map.put(entity.getUuid(), entity);
					} else {
						taskingHostIds.add(entity.getUuid());
					}
				}
			});
		}
		
		for (int i = 0; i < servers.size(); i++) {
			JSONObject server = servers.getJSONObject(i);
			String id = server.getString("id");
			String name = server.getString("name");
			String projectId = server.getString("tenant_id");
			String projectName = projectNameMap.get(projectId);
			String status = server.getString("status");
			if(!StrUtils.checkParam(id, name)) {
				this.addMsg("在CloudOS中云主机信息异常, 异常内容: " + server.toJSONString());
				continue;
			}
			if("BUILD".equals(status)) {	// 是否处于创建状态
				this.addMsg("云主机正处理创建中的状态，创建信息: " + server.toJSONString());
				continue;
			}
			if(taskingHostIds.contains(id)) {	// 有任务状态的云主机不做同步
				this.addMsg("云主机存在任务，不做同步: " + server.toJSONString());
				continue;
			}

			hostIdNameMap.put(id, name);
			
			NovaVm entity = map.remove(id);
			if(entity == null) {
				this.differencesBiz.addDiff(id, projectName, "云主机[" + name + "][" + id + "]在CloudOS中存在，在IYun中不存在", CompareEnum.IN_CLOUDOS, dataType);
			} else {
				String hostName = entity.getHostName();
				if(!name.equals(hostName)) {
					this.differencesBiz.addDiff(entity.getId(), projectName, 
						"云主机[" + entity.getHostName() + "][" + entity.getId() + "]的名称与CloudOS不一致, CloudOS: " + name + ", IYun: " + hostName, 
						CompareEnum.DATA_DIFF, dataType);
				}
			}
		}
		
		if(!map.isEmpty()) {
			for (NovaVm entity : map.values()) {
				String id = entity.getId();
				String name = entity.getHostName();
				String projectName = projectNameMap.get(entity.getProjectId());
				this.differencesBiz.addDiff(id, projectName, "云主机[" + name + "][" + id + "]在IYun中存在，在CloudOS中不存在", CompareEnum.IN_IYUN, dataType);
			}
		}
		return hostIdNameMap;
	}
	
	private void diffNetwork(Map<String, String> projectNameMap, Map<String, JSONObject> network2routerMap) {
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_NETWORK);
		// 获取CloudOS的用户
		CloudosClient client = this.getClient();
		JSONArray networks = HttpUtils.getJSONArray(client.get(uri), "networks");
		if(networks == null) {
			this.addMsg("获取CloudOS网络/公网池失败.");
			return;
		}
		List<Network> list = networkBiz.getAll(Network.class);
		Map<String, Network> map = new HashMap<String, Network>();
		if(StrUtils.checkCollection(list)) {
			list.forEach(entity -> map.put(entity.getCloudosId(), entity));
		}
		
		for (int i = 0; i < networks.size(); i++) {
			JSONObject network = networks.getJSONObject(i);
			String id = network.getString("id");
			String name = network.getString("name");
			String projectId = network.getString("tenant_id");
			String projectName = projectNameMap.get(projectId);

			boolean router_external = network.getBoolean("router:external");	// 是否为公网
			CompareEnum dataType = router_external ? CompareEnum.PUBLIC_NETWORK : CompareEnum.NETWORK;	// 类型为网络
			String typeName = router_external ? "公网池" : "网络";
			
			JSONObject router = network2routerMap.get(id);
			String routerId = null;
			String routerName = null;
			if(router != null) {
				routerId = router.getString("id");
				routerName = router.getString("name");
			}
			if(!StrUtils.checkParam(id, name)) {
				this.addMsg("在CloudOS中网络信息异常, 异常内容: " + network.toJSONString());
				continue;
			}
			
			Network entity = map.remove(id);
			if(entity == null) {
				this.differencesBiz.addDiff(id, projectName, typeName + "[" + name + "][" + id +
						"]在CloudOS中存在，在IYun中不存在", CompareEnum.IN_CLOUDOS, dataType);
			} else {
				// 路由器获取异常，不同步连接关系
				if(!entity.getExternalNetworks() && network2routerMap != null) {//私网检查路由器连接关系
					String iyunRouterId = entity.getRouteCloudosId();
					String iyunRouterName = entity.getRouteName();
					if(iyunRouterId == null) {
						if(StrUtils.checkParam(routerId)) {
							this.differencesBiz.addDiff(entity.getId(), projectName, typeName + "[" + name + "][" + id +
									"]在CloudOS中连接着路由器[" + routerName + "][" + routerId + "]",
									CompareEnum.DATA_DIFF, CompareEnum.NETWORK);
						}
					} else {
						// iyun连接着路由器，在cloudos中没有连接
						if(!StrUtils.checkParam(routerId)) {
							this.differencesBiz.addDiff(entity.getId(), projectName, typeName + "[" + name + "][" +
									id + "]在IYun中连接着路由器[" + iyunRouterName + "][" + iyunRouterId + "]",
									CompareEnum.DATA_DIFF, CompareEnum.NETWORK);
						} else if(!routerId.equals(iyunRouterId)){
							this.differencesBiz.addDiff(entity.getId(), projectName, 
									typeName + "[" + name + "][" + id + "]在IYun中连接着路由器[" + iyunRouterName +
											"][" + iyunRouterId + "], 但是在CloudOS中连接路由器[" + routerName + "][" + routerId + "]",
									CompareEnum.DATA_DIFF, dataType);
						}
					}
				}
			}
		}
		
		if(!map.isEmpty()) {
			for (Network entity : map.values()) {
				String id = entity.getId();
				String name = entity.getName();
				String projectName = projectNameMap.get(entity.getTenantId());
				String typeName = entity.getExternalNetworks() ? "公网池" : "网络";
				CompareEnum dataType = entity.getExternalNetworks() ? CompareEnum.PUBLIC_NETWORK : CompareEnum.NETWORK;
				this.differencesBiz.addDiff(id, projectName, typeName + "[" + name + "][" + id + "]在IYun中存在，在CloudOS中不存在", CompareEnum.IN_IYUN, dataType);
			}
		}
	}
	
	private Map<String, JSONObject> diffRouter(Map<String, String> projectNameMap, Map<String, JSONObject> route2fwMap) {
		CompareEnum dataType = CompareEnum.ROUTER;	// 类型为防火墙
		// 网络id	-->	路由器
		Map<String, JSONObject> network2routerMap = new HashMap<String, JSONObject>();
		
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_ROUTE);
		// 获取CloudOS的用户
		CloudosClient client = this.getClient();
		JSONArray routers = HttpUtils.getJSONArray(client.get(uri), "routers");
		if(null == routers) {
			this.addMsg("获取CloudOS路由器失败.");
			return null;
		}
		
		List<Route> list = this.routeBiz.getAll(Route.class);
		Map<String, Route> map = new HashMap<String, Route>();
		if(StrUtils.checkCollection(list)) {
			list.forEach(entity -> map.put(entity.getCloudosId(), entity));
		}
		
		for (int i = 0; i < routers.size(); i++) {
			JSONObject router = routers.getJSONObject(i);
			String id = router.getString("id");
			String name = router.getString("name");
			String projectId = router.getString("tenant_id");
			JSONObject fwObj = route2fwMap.get(id);	// 连接防火墙的id
			String fwId = null;
			String fwName = null;
			if(fwObj != null) {
				fwId = fwObj.getString("id");
				fwName = fwObj.getString("name");
			}
			
			String projectName = projectNameMap.get(projectId);
			if(!StrUtils.checkParam(id, name)) {
				this.addMsg("在CloudOS中路由器信息异常, 异常内容: " + router.toJSONString());
				continue;
			}
			
			Route entity = map.remove(id);
			if(entity == null) {
				this.differencesBiz.addDiff(id, projectName, "路由器[" + name + "][" + id + "]在CloudOS中存在，在IYun中不存在", CompareEnum.IN_CLOUDOS, dataType);
			} else {
				String portUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_PORTS);
				Map<String, Object> paramMap = new HashMap<>();
				paramMap.put("device_id", id);
				JSONArray ports = HttpUtils.getJSONArray(client.get(portUri, paramMap), "ports");
				if(StrUtils.checkCollection(ports)) {
					for (int j = 0; j < ports.size(); j++) {
						JSONObject port = ports.getJSONObject(j);
						String network_id = port.getString("network_id");
						if(StrUtils.checkParam(network_id)) {
							network2routerMap.put(network_id, router);
						}
					}
				}
				
				// 防火墙信息异常则不做关系同步
				if(route2fwMap != null) {
					// 检查存在的连接防火墙是否一致
					String route2fwId = entity.getFirewallCloudosId();
					String route2fwName = entity.getFirewallName();
					// iyun中连接防火墙为空，但是cloudos中不为空
					if(route2fwId == null) {
						if(StrUtils.checkParam(fwId)) {
							this.differencesBiz.addDiff(id, projectName, "路由器[" + name + "][" + id +
									"]在CloudOS中连接着防火墙[" + fwName + "][" + fwId + "]",
									CompareEnum.DATA_DIFF, CompareEnum.ROUTER_LINK_FIREWALL);
						}
					} else {
						// iyun连接着防火墙，在cloudos中没有连接
						if(!StrUtils.checkParam(fwId)) {
							this.differencesBiz.addDiff(entity.getId(), projectName, "路由器[" + name +
									"][" + id + "]在IYun中连接着防火墙[" + route2fwName + "][" + route2fwId + "]",
									CompareEnum.DATA_DIFF, CompareEnum.ROUTER_LINK_FIREWALL);
						} else if(!entity.getFirewallCloudosId().equals(fwId)){
							this.differencesBiz.addDiff(entity.getId(), projectName, 
									"路由器[" + name + "][" + id + "]在IYun中连接着防火墙[" + route2fwName + "][" + route2fwId + "], 但是在CloudOS中连接防火墙[" + fwName + "][" + fwId + "]", 
									CompareEnum.DATA_DIFF, CompareEnum.ROUTER);
						}
					}
				}
			}
		}
		
		if(!map.isEmpty()) {
			for (Route entity : map.values()) {
				String id = entity.getId();
				String name = entity.getName();
				String projectName = projectNameMap.get(entity.getTenantId());
				this.differencesBiz.addDiff(id, projectName, "路由器[" + name + "][" + id + "]在IYun中存在，在CloudOS中不存在", CompareEnum.IN_IYUN, dataType);
			}
		}
		return network2routerMap;
	}
	
	/**
	 * 同步防火墙
	 * 同步防火墙规则集
	 * 同步防火墙规则集规则
	 * @param projectNameMap	租户id/uuid对应租户名称
	 * @return
	 */
	private Map<String, JSONObject> diffFirewall(Map<String, String> projectNameMap) {
		CompareEnum dataType = CompareEnum.FIREWALL;	// 类型为防火墙
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FIREWALL);
		
		// 路由器 --> 防火墙
		Map<String, JSONObject> route2fwMap = new HashMap<String, JSONObject>();
		// 获取CloudOS的用户
		CloudosClient client = this.getClient();
		JSONArray firewalls = HttpUtils.getJSONArray(client.get(uri), "firewalls");
		if(null == firewalls) {
			this.addMsg("获取CloudOS防火墙失败.");
			return null;
		}
		List<Firewall> list = this.firewallBiz.getAll(Firewall.class);
		Map<String, Firewall> map = new HashMap<String, Firewall>();
		if(StrUtils.checkCollection(list)) {
			list.forEach(entity -> map.put(entity.getCloudosId(), entity));
		}
		
		for (int i = 0; i < firewalls.size(); i++) {
			JSONObject firewall = firewalls.getJSONObject(i);
			String id = firewall.getString("id");
			String name = firewall.getString("name");
			String projectId = firewall.getString("tenant_id");
			String firewall_policy_id = firewall.getString("firewall_policy_id");	// 防火墙规则集id
			String projectName = projectNameMap.get(projectId);
			if(!StrUtils.checkParam(id, name)) {
				this.addMsg("在CloudOS中防火墙信息异常, 异常内容: " + firewall.toJSONString());
				continue;
			}
			
			// 设置路由器-防火墙的关系
			JSONArray router_ids = firewall.getJSONArray("router_ids");
			if(StrUtils.checkCollection(router_ids)) {
				for (int j = 0; j < router_ids.size(); j++) {
					route2fwMap.put(router_ids.getString(j), firewall);
				}
			}
			
			Firewall entity = map.remove(id);
			if(entity == null) {
				this.differencesBiz.addDiff(id, projectName, "防火墙[" + name + "][" + id +
						"]在CloudOS中存在，在IYun中不存在", CompareEnum.IN_CLOUDOS, dataType);
			} else {
				if(entity.getPolicyId() == null) {
					if(StrUtils.checkParam(firewall_policy_id)) {
						this.differencesBiz.addDiff(firewall_policy_id, projectName, 
								"防火墙[" + name + "][" + id + "]规则集在CloudOS中存在，在IYun中不存在",
								CompareEnum.IN_CLOUDOS, CompareEnum.FIREWALL_POLICY);
					}
				} else {
					// 子查询字段，不为空则对象必定存在
					Policie policie = policieBiz.findById(Policie.class, entity.getPolicyId());
					// 同步防火墙规则集
					String policyUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_POLICIE_ACTION);
					policyUri = HttpUtils.tranUrl(policyUri, policie.getCloudosId());
					JSONObject policyObject = this.getClient().get(policyUri);
					JSONObject obj = HttpUtils.getJSONObject(policyObject, "firewall_policy");
					if(obj == null) {
						this.differencesBiz.addDiff(policie.getId(), projectName, 
								"防火墙[" + name + "][" + id + "]规则集在IYun中存在，在CloudOS中不存在",
								CompareEnum.IN_IYUN, CompareEnum.FIREWALL_POLICY);
						continue;
					}
					
					// 获取iyun中规则集规则
					List<PolicieRule> prList = policieRuleBiz.findByPropertyName(PolicieRule.class, "policyId", policie.getId());
					Map<String, PolicieRule> policieRuleMap = new HashMap<String, PolicieRule>();
					if(StrUtils.checkCollection(prList)) {
						prList.forEach(prEntity -> policieRuleMap.put(prEntity.getCloudosId(), prEntity));
					}
					// 获取cloudos规则集规则明细
					JSONArray firewall_rules = obj.getJSONArray("firewall_rules");
					boolean diffFlag = false;
					if(StrUtils.checkCollection(firewall_rules)) {
						for (int j = 0; j < firewall_rules.size(); j++) {
							uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_POLICIE_RULE_ACTION);
							String fwRuleId = firewall_rules.getString(j);
							uri = HttpUtils.tranUrl(uri, fwRuleId);
							JSONObject prJsonObj = client.get(uri);
							JSONObject firewall_rule = HttpUtils.getJSONObject(prJsonObj, "firewall_rule");
							if(firewall_rule == null) {
								// 在cloudos中数据不正确
								this.warn("Get policy rule failure. url: " + uri);
							} else {
								int position = firewall_rule.getInteger("position");
								PolicieRule prEntity = policieRuleMap.remove(fwRuleId);
								if(prEntity == null || (position != prEntity.getPosition().intValue())) {
									this.differencesBiz.addDiff(entity.getId(), projectName,
										"防火墙[" + name + "][" + entity.getId() + "]规则集规则不一致,差异同步将以cloudos为准",
											CompareEnum.DATA_DIFF, CompareEnum.FIREWALL_RULE);
									diffFlag = true;
									break;
								}
							}
						}
					}
					if(!diffFlag && !policieRuleMap.isEmpty()) {
						this.differencesBiz.addDiff(entity.getId(), projectName,
								"防火墙[" + name + "][" + entity.getId() + "]规则集规则不一致,差异同步将以cloudos为准",
								CompareEnum.DATA_DIFF, CompareEnum.FIREWALL_RULE);
					}
				}
			}
		}
		
		if(!map.isEmpty()) {
			for (Firewall entity : map.values()) {
				String id = entity.getId();
				String name = entity.getName();
				String projectName = projectNameMap.get(entity.getTenantId());
				this.differencesBiz.addDiff(id, projectName, "防火墙[" + name + "][" + id +
						"]在IYun中存在，在CloudOS中不存在", CompareEnum.IN_IYUN, dataType);
			}
		}
		return route2fwMap;
	}
	
	/**
	 * 对比cloudos中的用户信息 需要过滤cloudos中的八个预设值租户: ConfigProperty.STOP_SET
	 * 过滤iyun中的电信的用户, 如果用户的租户id为电信租户则不做差异对比 租户id获取方式:
	 * CacheSingleton.getInstance().getCtTenantId()
	 */
	private void diffUser(Map<String, String> projectNameMap) {
		CompareEnum dataType = CompareEnum.USER;	// 类型为用户
		
		// 获取CloudOS的用户
		CloudosClient client = this.getClient();
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_USERS);
		JSONArray users = HttpUtils.getJSONArray(client.get(uri), "users");
		if(null == users) {
			this.addMsg("获取CloudOS用户失败.");
			return;
		}
		
		List<User> list = this.userBiz.getAll(User.class);
		Map<String, User> map = new HashMap<String, User>();
		if(StrUtils.checkCollection(list)) {
			list.forEach(entity -> {
				// 电信租户下的用户不算
				if(!entity.getProjectId().equals(singleton.getCtTenantId()))
					map.put(entity.getCloudosId(), entity);
			});
		}
		
		for (int i = 0; i < users.size(); i++) {
			JSONObject user = users.getJSONObject(i);
			String id = user.getString("id");
			String name = user.getString("name");
			String projectId = user.getString("default_project_id");
			String projectName = projectNameMap.get(projectId);
			if(!StrUtils.checkParam(id, name)) {
				this.addMsg("在CloudOS中用户信息异常, 异常内容: " + user.toJSONString());
				continue;
			}
			
			// CloudOS 初始化的两个租户不在同步范围内
			if(ConfigProperty.STOP_SET.contains(id)) {
				continue;
			}
			User entity = map.remove(id);
			if(entity == null) {
				this.differencesBiz.addDiff(id, projectName, "用户[" + name + "][" + id + "]在CloudOS中存在，在IYun中不存在", CompareEnum.IN_CLOUDOS, dataType);
			}
		}
		
		if(!map.isEmpty()) {
			for (User entity : map.values()) {
				String id = entity.getId();
				String name = entity.getUserName();
				String projectName = projectNameMap.get(entity.getProjectId());
				this.differencesBiz.addDiff(id, projectName, "用户[" + name + "][" + id + "]在IYun中存在，在CloudOS中不存在", CompareEnum.IN_IYUN, dataType);
			}
		}
	}
	
	/**
	 * 对比cloudos中的租户信息 需要过滤cloudos中的两个预设值租户: ConfigProperty.PROJECT_STOP_SET
	 * 过滤iyun中的电信租户 租户id获取方式: CacheSingleton.getInstance().getCtTenantId()
	 */
	private Map<String, String> diffProject() {
		CompareEnum dataType = CompareEnum.PROJECT;	// 类型为租户
		
		Map<String, String> projectNameMap = new HashMap<String, String>();
		// 获取CloudOS的租户
		CloudosClient client = this.getClient();
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_PROJECTS);
		JSONArray projects = HttpUtils.getJSONArray(client.get(uri), "projects");
		if(null == projects) {
			this.addMsg("获取CloudOS租户失败.");
			return projectNameMap;
		}
		
		// 获取IYun中的租户
		List<Project> list = this.projectBiz.getAll(Project.class);
		Map<String, Project> map = new HashMap<String, Project>();
		if(StrUtils.checkCollection(list)) {
			list.forEach(entity -> {
				// 电信租户不算
				if(!entity.getId().equals(singleton.getCtTenantId())) {
					map.put(entity.getId(), entity);
					projectNameMap.put(entity.getId(), entity.getName());
				}
			});
		}
		
		for (int i = 0; i < projects.size(); i++) {
			JSONObject project = projects.getJSONObject(i);
			String id = project.getString("id");
			String name = project.getString("name");
			if(!StrUtils.checkParam(id, name)) {
				this.addMsg("在CloudOS中租户信息异常, 异常内容: " + project.toJSONString());
				continue;
			}
			
			projectNameMap.put(id, name);
			
			// CloudOS 初始化的两个租户不在同步范围内
			if(ConfigProperty.STOP_SET.contains(id)) {
				continue;
			}
			Project entity = map.remove(id);
			if(entity == null) {
				this.differencesBiz.addDiff(id, name, "租户[" + name + "][" + id + "]在CloudOS中存在，在IYun中不存在", CompareEnum.IN_CLOUDOS, dataType);
			} else {
				if(!entity.getName().equals(name)) {
					this.differencesBiz.addDiff(id, name,  
							"租户[" + entity.getName() + "][" + id + "]的名称与CloudOS不一致, CloudOS: " + name + ", IYun: " + entity.getName(), 
							CompareEnum.DATA_DIFF, dataType);
				}
			}
		}
		
		if(!map.isEmpty()) {
			for (Project entity : map.values()) {
				String id = entity.getId();
				String name = entity.getName();
				this.differencesBiz.addDiff(id, name, "租户[" + name + "]在IYun中存在，在CloudOS中不存在", CompareEnum.IN_IYUN, dataType);
			}
		}
		return projectNameMap;
	}
	
	/**
	 * 获取admin操作权限
	 * 
	 * @return
	 */
	private CloudosClient getClient() {
		CloudosClient client = CloudosClient.createAdmin();
		return client;
	}
	
	@SuppressWarnings("unchecked")
	private void addMsg(String msg) {
		List<String> list = (List<String>) ThreadContext.get("msgList");
		if(list == null) {
			list = new ArrayList<String>();
			ThreadContext.set("msgList", list);
		}
		this.warn(msg);
		list.add(msg);
	}

}
