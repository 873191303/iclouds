package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.DifferencesBiz;
import com.h3c.iclouds.biz.FirewallBiz;
import com.h3c.iclouds.biz.FloatingIpBiz;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.PolicieBiz;
import com.h3c.iclouds.biz.PolicieRuleBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.RouteBiz;
import com.h3c.iclouds.biz.SyncVdcDataBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.biz.VdcBiz;
import com.h3c.iclouds.biz.VlbBiz;
import com.h3c.iclouds.biz.VlbMemberBiz;
import com.h3c.iclouds.biz.VlbPoolBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Firewall;
import com.h3c.iclouds.po.FloatingIp;
import com.h3c.iclouds.po.HealthMonitor;
import com.h3c.iclouds.po.IpAllocation;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Policie;
import com.h3c.iclouds.po.PolicieRule;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Route;
import com.h3c.iclouds.po.Subnet;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.Vdc;
import com.h3c.iclouds.po.Vlb;
import com.h3c.iclouds.po.VlbMember;
import com.h3c.iclouds.po.VlbPool;
import com.h3c.iclouds.po.VlbVip;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 
 * @author zkf5485
 *
 */
@SuppressWarnings("rawtypes")
@Service("syncVdcDataBiz")
public class SyncVdcDataBizImpl extends BaseBizImpl implements SyncVdcDataBiz {

	@Resource
	private FirewallBiz firewallBiz;

	@Resource
	private PolicieBiz policieBiz;

	@Resource
	private PolicieRuleBiz ruleBiz;

	@Resource
	private RouteBiz routeBiz;

	@Resource
	private NetworkBiz networkBiz;

	@Resource(name = "baseDAO")
	private BaseDAO<Subnet> subnetDao;

	@Resource(name = "baseDAO")
	private BaseDAO<IpAllocation> ipusedDao;

	@Resource
	private PortBiz portBiz;

	@Resource
	private VlbPoolBiz vlbPoolBiz;
	
	@Resource
	private VlbBiz vlbBiz;

	@Resource
	private VlbMemberBiz vlbMemberBiz;

	@Resource
	private VdcBiz vdcBiz;

	@Resource
	private UserBiz userBiz;
	
	@Resource
	private NovaVmBiz novaVmBiz;
	
	@Resource
	private FloatingIpBiz floatingIpBiz;
	
	@Resource
	private DifferencesBiz differencesBiz;
	
	@Override
	public void startSyncVdc() {
		//防火墙
		JSONArray fwArray = firewallBiz.getFirewallArray(getClient());
		if (StrUtils.checkParam(fwArray)){
			for (int i = 0; i < fwArray.size(); i++) {
				JSONObject fwJson = fwArray.getJSONObject(i);
				syncFirewall(fwJson);
			}
		}

		//路由器
		JSONArray rtArray = routeBiz.getRouteArray(getClient());
		if (StrUtils.checkParam(rtArray)){
			for (int i = 0; i < rtArray.size(); i++) {
				JSONObject rtJson = rtArray.getJSONObject(i);
				syncRoute(rtJson, null);
			}
		}

		//网络
		JSONArray ntArray = networkBiz.getNetworkArray(getClient());
		if (StrUtils.checkParam(ntArray)){
			for (int i = 0; i < ntArray.size(); i++) {
				JSONObject ntJson = ntArray.getJSONObject(i);
				syncNetwork(ntJson, null, null);
			}
		}
	}
	
	

	/**
	 * 同步防火墙
	 */
	public void syncFirewall(JSONObject fwJson){
		
		String fwCdId = fwJson.getString("id");
		String tenantId = fwJson.getString("tenant_id");
		String status = fwJson.getString("status");
		Vdc vdc = vdcBiz.getVdc(tenantId);
		if(vdc == null || ConfigProperty.STOP_SET.contains(tenantId)) {
			return;
		}
		String vdcId = vdc.getId();
		
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("cloudosId", fwCdId);
		Firewall firewall = firewallBiz.singleByClass(Firewall.class, queryMap);
		if (!StrUtils.checkParam(firewall)){//本地新增防火墙和规则集
			firewall = firewallBiz.getFirewallByJson(fwJson);
			firewall.setUuid(UUID.randomUUID().toString());
			firewall.createdUser(ConfigProperty.SYSTEM_FLAG);
			String pyCdId = fwJson.getString("firewall_policy_id");
			JSONObject policyJson = policieBiz.getPolicyJson(pyCdId, getClient());
			Policie policy = policieBiz.getPolicyByJson(policyJson);
			policy.setStatus("3");
			policy.createdUser(ConfigProperty.SYSTEM_FLAG);
			JSONObject normRecord = firewallBiz.getNormJson(fwCdId, getClient());
			Integer throughPut = normRecord.getInteger("throughPut");
			firewall.setThroughPut(throughPut);
			firewallBiz.localSave(firewall, policy, vdcId, status);
			
			JSONArray ruleIds = policyJson.getJSONArray("firewall_rules");
			if (StrUtils.checkParam(ruleIds)){//同步规则
				for (int i = 0; i < ruleIds.size(); i++) {
					String ruleCdId = ruleIds.getString(i);
					JSONObject ruleJson = ruleBiz.getRuleJson(ruleCdId, getClient());
					if (StrUtils.checkParam(ruleJson)){
						syncRule(ruleJson, policy.getId());
					}
				}
			}
			
		} else {//更新状态
			if (!status.equals(firewall.getStatus())){
				firewallBiz.updateStatus(firewall.getId(), status);
			}
		}
		
		JSONArray rtIds = fwJson.getJSONArray("router_ids");
		if (StrUtils.checkParam(rtIds)){//同步防火墙下挂载的路由器
			for (int i = 0; i < rtIds.size(); i++) {
				String rtCdId = rtIds.getString(i);
				JSONObject rtJson = routeBiz.getRouteJson(rtCdId, getClient());
				if (StrUtils.checkParam(rtJson)){
					syncRoute(rtJson, firewall.getId());
				}
			}
		}
	}
	
	/**
	 * 同步防火墙规则
	 */
	public void syncRule(JSONObject ruleJson, String pyId){
		String ruleCdId = ruleJson.getString("id");
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("cloudosId", ruleCdId);
		PolicieRule rule = ruleBiz.singleByClass(PolicieRule.class, queryMap);
		if (!StrUtils.checkParam(rule)){
			rule = ruleBiz.getRulebyJson(ruleJson);
			rule.setPolicyId(pyId);
			ruleBiz.save(rule);
		}
	}

	/**
	 * 同步路由器
	 */
	public  void syncRoute(JSONObject rtJson, String fwId){
		
		String rtCdId = rtJson.getString("id");
		String tenantId = rtJson.getString("tenant_id");
		String status = rtJson.getString("status");
		Vdc vdc = vdcBiz.getVdc(tenantId);
		if(vdc == null || ConfigProperty.STOP_SET.contains(tenantId)) {
			return;
		}
		String vdcId = vdc.getId();
		
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("cloudosId", rtCdId);
		Route route = routeBiz.singleByClass(Route.class, queryMap);
		if (!StrUtils.checkParam(route)){//本地新增路由器
			route = routeBiz.getRouteByJson(rtJson);
			route.setFwId(fwId);
			route.setUuid(UUID.randomUUID().toString());
			route.createdUser(ConfigProperty.SYSTEM_FLAG);
			routeBiz.localSave(route, vdcId, status);
		} else {//更新状态
			if (!status.equals(route.getStatus())){
				routeBiz.updateStatus(route.getId(), status);
			}
		}
		
		JSONArray portArray = portBiz.getPortArray(rtCdId, null, getClient());
		if (StrUtils.checkParam(portArray)){
			for (int i = 0; i < portArray.size(); i++) {
				JSONObject portJson = portArray.getJSONObject(i);
				String deviceOwner = portJson.getString("device_owner");
				
				if ("network:router_gateway".equals(deviceOwner)) {//同步外部网关信息
					JSONObject gatewayJson = rtJson.getJSONObject("external_gateway_info");
					if (StrUtils.checkParam(gatewayJson)) {
						String networkCdId = gatewayJson.getString("network_id");
						if (StrUtils.checkParam(networkCdId)) {
							
							JSONObject ntJson = networkBiz.getNetworkJson(networkCdId, getClient());
							if (StrUtils.checkParam(ntJson)) {
								syncNetwork(ntJson, null, null);//同步公网信息
								
								Map<String, Object> map = new HashMap<>();
								map.put("cloudosId", networkCdId);
								String gwPortId = syncPort(portJson, route.getId(), null);//同步外部网关网卡信息
								route.setGwPortId(gwPortId);//回写外部网关网卡信息
								routeBiz.update(route);
							}
						}
					}
				} else if ("network:router_interface".equals(deviceOwner)) {//同步挂载私网信息
					String ptCdId = portJson.getString("id");
					String ntCdId = portJson.getString("network_id");
					JSONObject ntJson = networkBiz.getNetworkJson(ntCdId, getClient());
					if (StrUtils.checkParam(ntJson)){
						syncNetwork(ntJson, route.getId(), ptCdId);
					}
				}
			}
		}
	}

	/**
	 * 同步网络
	 */
	public  void syncNetwork(JSONObject ntJson, String rtId, String ptCdId){
		String ntCdId = ntJson.getString("id");
		String tenantId = ntJson.getString("tenant_id");
		String status = ntJson.getString("status");
		Boolean external = ntJson.getBoolean("router:external");
		Vdc vdc = vdcBiz.getVdc(tenantId);
		if(vdc == null || ConfigProperty.STOP_SET.contains(tenantId)) {
			return;
		}
		String vdcId = vdc.getId();
		
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("cloudosId", ntCdId);
		Network network = networkBiz.singleByClass(Network.class, queryMap);
		if (!StrUtils.checkParam(network)){
			network = networkBiz.getNetworkByJson(ntJson);
			network.setUuid(UUID.randomUUID().toString());
			network.setRouteId(rtId);
			network.setVdcId(vdcId);
			network.createdUser(ConfigProperty.SYSTEM_FLAG);
			network.setPortCloudId(ptCdId);
			
			JSONArray subnetIds = ntJson.getJSONArray("subnets");
			if (StrUtils.checkParam(subnetIds)){
				String sbCdId = subnetIds.getString(0);
				JSONObject sbJson = networkBiz.getSubnetJson(sbCdId, getClient());
				String name = sbJson.getString("name");
				if(!StrUtils.checkParam(name)) {
					sbJson.put("name", ntJson.get("name"));
				}
				
				Subnet subnet = networkBiz.getSubnetByJson(sbJson);
				subnet.createdUser(ConfigProperty.SYSTEM_FLAG);
				networkBiz.addNetworkParam(network, sbJson);
				networkBiz.localSave(network, subnet, vdcId, status);
				
			}
			
		} else {//更新状态
			if (!status.equals(network.getStatus())){
				if (external) {
					network.setStatus(status);
					networkBiz.update(network);
				} else {
					networkBiz.updateStatus(network.getId(), status);
				}
			}
		}
	}
	
	/**
	 * 同步网卡
	 */
	public String syncPort(JSONObject ptJson, String deviceId, String userId){
		String ptCdId = ptJson.getString("id");
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("cloudosId", ptCdId);
		Port port = portBiz.singleByClass(Port.class, queryMap);
		if (!StrUtils.checkParam(port)){
			String rtId = null;
			String ipAddress = null;
			String subnetId = null;
			JSONArray fixed_ips = ptJson.getJSONArray("fixed_ips");
			if (StrUtils.checkCollection(fixed_ips)) {
				JSONObject ip = fixed_ips.getJSONObject(0);
				String subnetCdId = ip.getString("subnet_id");
				List<Subnet> subnets = subnetDao.findByPropertyName(Subnet.class, "cloudosId",
						subnetCdId);
				if (StrUtils.checkCollection(subnets)) {
					Subnet subnet = subnets.get(0);
					subnetId = subnet.getId();
					Network network = networkBiz.findById(Network.class, subnet.getNetworkId());
					rtId = network.getRouteId();
				}
				ipAddress = ip.getString("ip_address");
			}
			port = portBiz.getPortByJson(ptJson);
			port.setDeviceId(deviceId);
			port.setRouteId(rtId);
			port.setUserId(userId);
			port.createdUser(ConfigProperty.SYSTEM_FLAG);
			portBiz.add(port);
			
			IpAllocation ipUsed = new IpAllocation();
			ipUsed.setPortId(port.getId());
			ipUsed.setIpAddress(ipAddress);
			ipUsed.setSubnetId(subnetId);
			ipusedDao.add(ipUsed);
		}
		return port.getId();
	}

	/**
	 * 同步负载均衡组
	 */
	public void syncVlb(){
		//负载均衡
		JSONArray vbArray = vlbBiz.getVlbArray(getClient());
		if (StrUtils.checkParam(vbArray)){
			for (int i = 0; i < vbArray.size(); i++) {
				JSONObject vbJson = vbArray.getJSONObject(i);
				String userId = vbJson.getString("userId");
				String vbCdId = vbJson.getString("id");
				Map<String, Object> queryMap = new HashMap<>();
				queryMap.put("cloudosId", vbCdId);
				Vlb vlb = vlbBiz.singleByClass(Vlb.class, queryMap);
				if (!StrUtils.checkParam(vlb)){
					vlb = vlbBiz.getVlbByJson(vbJson);
					queryMap.clear();
					queryMap.put("cloudosId", userId);
					User user = userBiz.singleByClass(User.class, queryMap);
					vlb.createdUser(user.getId());
					vlbBiz.add(vlb);
				}
				
				String extra = vbJson.getString("extra");
				if (StrUtils.checkParam(extra)){//同步负载均衡
					JSONObject extraObject = JSONObject.parseObject(extra);
					JSONArray extraArray = extraObject.getJSONArray("pool_vip_monitors");
					if (StrUtils.checkParam(extraArray)){
						for (int j = 0; j < extraArray.size(); j++) {
							JSONObject extraJson = extraArray.getJSONObject(j);
							String vpCdId = extraJson.getString("vipId");
							String poolCdId = extraJson.getString("poolId");
							String monitorCdId = extraJson.getString("monitorId");
							JSONObject vpJson = vlbPoolBiz.getVipJson(vpCdId, getClient());
							JSONObject poolJson = vlbPoolBiz.getPoolJson(poolCdId, getClient());
							JSONObject healthJson = vlbPoolBiz.getHealthJson(monitorCdId, getClient());
							if (StrUtils.checkParam(poolJson)){
								syncPool(poolJson, vpJson, healthJson, vlb.getId());
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 同步负载均衡
	 */
	public void syncPool(JSONObject plJson, JSONObject vpJson, JSONObject hlJson, String vbId){
		String plCdId = plJson.getString("id");
		String tenantId = plJson.getString("tenant_id");
		String sbCdId = plJson.getString("subnet_id");
		String status = plJson.getString("status");
		Vdc vdc = vdcBiz.getVdc(tenantId);
		if(vdc == null || ConfigProperty.STOP_SET.contains(tenantId)) {
			return;
		}
		String vdcId = vdc.getId();
		
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("cloudosId", plCdId);
		VlbPool vlbPool = vlbPoolBiz.singleByClass(VlbPool.class, queryMap);
		if (!StrUtils.checkParam(vlbPool)){
			vlbPool = vlbPoolBiz.getPoolByJson(plJson);
			vlbPool.setLbId(vbId);
			vlbPool.setUuid(UUID.randomUUID().toString());
			vlbPool.createdUser(ConfigProperty.SYSTEM_FLAG);
			
			queryMap.put("cloudosId", sbCdId);
			Subnet subnet = subnetDao.singleByClass(Subnet.class, queryMap);
			vlbPool.setFactSubnetId(subnet.getId());
			
			VlbVip vlbVip = vlbPoolBiz.getVipByJson(vpJson);
			vlbVip.createdUser(ConfigProperty.SYSTEM_FLAG);
			
			sbCdId = vpJson.getString("subnet_id");
			queryMap.put("cloudosId", sbCdId);
			subnet = subnetDao.singleByClass(Subnet.class, queryMap);
			vlbPool.setVainSubnetId(subnet.getId());
			
			String ptCdId = vpJson.getString("port_id");
			vlbPool.setPortCdId(ptCdId);
			
			HealthMonitor monitor = vlbPoolBiz.gethealthByJson(hlJson);
			
			monitor.setLbId(vbId);
			monitor.createdUser(ConfigProperty.SYSTEM_FLAG);
			
			vlbPoolBiz.localSave(vlbPool, monitor, vlbVip, vdcId, status);
		} else {
			if (StrUtils.checkParam(status) && !status.equals(vlbPool.getStatus())){
				vlbPoolBiz.updateStatus(vlbPool.getId(), status);
			}
		}
		
		JSONArray memCdIds = plJson.getJSONArray("members");
		if (StrUtils.checkParam(memCdIds)){//同步负载均衡成员
			for (int i = 0; i < memCdIds.size(); i++) {
				String memCdId = memCdIds.getString(i);
				JSONObject memJson = vlbMemberBiz.getMemberJson(memCdId, getClient());
				if (StrUtils.checkParam(memJson)){
					syncMember(memJson, vlbPool.getId());
				}
			}
		}
	}
	
	/**
	 * 同步负载均衡成员
	 */
	public void syncMember(JSONObject memJson, String plId){
		String memCdId = memJson.getString("id");
		String address = memJson.getString("address");
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("cloudosId", memCdId);
		VlbMember member = vlbMemberBiz.singleByClass(VlbMember.class, queryMap);
		if (!StrUtils.checkParam(member)){
			member = vlbMemberBiz.getMember(memJson);
			member.setPoolId(plId);
			member.createdUser(ConfigProperty.SYSTEM_FLAG);
			VlbPool pool = vlbPoolBiz.findById(VlbPool.class, plId);
			queryMap.clear();
			queryMap.put("subnetId", pool.getVainSubnetId());
			queryMap.put("ipAddress", address);
			IpAllocation ipAllocation = ipusedDao.singleByClass(IpAllocation.class, queryMap);
			if(ipAllocation != null) {
				Port port = portBiz.findById(Port.class, ipAllocation.getPortId());
				queryMap.clear();
				queryMap.put("uuid", port.getDeviceId());
				NovaVm novaVm = novaVmBiz.singleByClass(NovaVm.class, queryMap);
				member.setVmId(novaVm.getId());
				vlbMemberBiz.add(member);
			}
		}
	}
	
	public void syncFloatingIp(JSONObject floatingIpJson){
		String cloudId = floatingIpJson.getString("id");
		String status = floatingIpJson.getString("status");
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("cloudosId", cloudId);//检查网络信息是否已经同步
		FloatingIp floatingIp = floatingIpBiz.singleByClass(FloatingIp.class, queryMap);
		if (!StrUtils.checkParam(floatingIp)) {
			String networkCdId = floatingIpJson.getString("floating_network_id");
			queryMap.put("cloudosId", networkCdId);//检查网络信息是否已经同步
			Network network = networkBiz.singleByClass(Network.class, queryMap);
			floatingIp = floatingIpBiz.getFloatingIpByJson(floatingIpJson);
			floatingIp.setNetworkId(network.getId());
			String fixPortCdId = floatingIpJson.getString("port_id");
			String fixedIp = floatingIpJson.getString("fixed_ip_address");
			String routeCdId = floatingIpJson.getString("router_id");
			//找出分配资源的ip对应的网卡和关联路由（检查分配资源的ip、路由、公网是否已经同步）
			if (null != fixPortCdId && !"".equals(fixPortCdId)) {
				floatingIp.setFixedPortId(differencesBiz.getFixedPortId(fixPortCdId));
				floatingIp.setRouterId(differencesBiz.getRouterId(routeCdId));
				floatingIp.setFixedIp(fixedIp);
			}
			
			//获取用户本地id
			String userCdId = floatingIpJson.getString("h3c_user_id");
			if (null != userCdId && !"".equals(userCdId)) {
				List<User> users = userBiz.findByPropertyName(User.class, "cloudosId", userCdId);
				floatingIp.setOwner(users.get(0).getId());
			}
			
			//获取公网网卡的cloudosId
			JSONArray portArray = portBiz.getPortArray(cloudId, null, getClient());
			if (StrUtils.checkCollection(portArray)) {
				JSONObject portJson = portArray.getJSONObject(0);
				floatingIp.setPortCdId(portJson.getString("id"));
			}
			floatingIpBiz.localSave(floatingIp);
		} else {
			if (!status.equals(floatingIp.getStatus())) {
				floatingIp.setStatus(status);
				floatingIpBiz.update(floatingIp);
			}
		}
	}

	public CloudosClient getClient() {
		return CloudosClient.createAdmin();
	}
}
