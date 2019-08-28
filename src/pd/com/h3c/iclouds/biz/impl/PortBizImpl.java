package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.FloatingIpBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.biz.Server2VmBiz;
import com.h3c.iclouds.biz.SpePortBiz;
import com.h3c.iclouds.client.CasClient;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.NetworkDao;
import com.h3c.iclouds.dao.NovaVmDao;
import com.h3c.iclouds.dao.PortDao;
import com.h3c.iclouds.dao.SecurityGroupDao;
import com.h3c.iclouds.dao.UserDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.FloatingIp;
import com.h3c.iclouds.po.Group2Port;
import com.h3c.iclouds.po.IpAllocation;
import com.h3c.iclouds.po.IpAllocationPool;
import com.h3c.iclouds.po.IpAvailabilityRange;
import com.h3c.iclouds.po.NetTemplater;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.SecurityGroup;
import com.h3c.iclouds.po.Server2Vm;
import com.h3c.iclouds.po.SpePort;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.IpValidator;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 网卡biz实现类
 * Created by yKF7317 on 2016/11/26.
 */
@Service("portBiz")
public class PortBizImpl extends BaseBizImpl<Port> implements PortBiz {

	@Resource
	private PortDao portDao;

	@Resource(name = "baseDAO")
	private BaseDAO<IpAllocation> ipUsedDao;

	@Resource
	private SecurityGroupDao securityGroupDao;

	@Resource(name = "baseDAO")
	private BaseDAO<Group2Port> g2PDao;

	@Resource
	private NetworkDao networkDao;

	@Resource
	private NovaVmDao novaVmDao;

	@Resource
	private UserDao userDao;
	
	@Resource
	private FloatingIpBiz floatingIpBiz;
	
	@Resource
	private ProjectBiz projectBiz;
	
	@Resource
	private Server2VmBiz server2VmBiz;
	
	@Resource
	private SpePortBiz spePortBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<NetTemplater> templateDao;
	
	@Resource
	private QuotaUsedBiz quotaUsedBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<IpAllocationPool> ipAlloPoolDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<IpAvailabilityRange> ipRangeDao;
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Override
	public PageModel<Port> findForPage(PageEntity entity) {
		return portDao.findForPage(entity);
	}
	
	@Override
	public Object save(Port entity, CloudosClient client) {
		if (!StrUtils.checkParam(entity.getNetWorkId())) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, "networkId not null");
		}
		User user = userDao.findById(User.class, entity.getUserId());
		entity.setTenantId(user.getProjectId());
		if (!projectBiz.checkSaveRole(user.getId(), user.getProjectId())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		Map<String, String> nameQueryMap = new HashMap<>();
		nameQueryMap.put("name", entity.getName());
		nameQueryMap.put("tenantId", user.getProjectId());
		List<Port> ps = portDao.findByMap(Port.class, nameQueryMap);
		if (null != ps && ps.size() > 0) {
			return BaseRestControl.tranReturnValue(ResultType.name_repeat);
		}
		ResultType rs = quotaUsedBiz.checkQuota(ConfigProperty.PORT_QUOTA_CLASSCODE, entity.getTenantId(), 1);
		if (!ResultType.success.equals(rs)) {
			return BaseRestControl.tranReturnValue(rs);
		}
		Network network = networkDao.findById(Network.class, entity.getNetWorkId());
		if (!StrUtils.checkParam(network)) {
			return BaseRestControl.tranReturnValue(ResultType.network_not_exist);
		}
		if (network.getExternalNetworks()) {
			return BaseRestControl.tranReturnValue(ResultType.picked_public_network);
		}

		if (!entity.getTenantId().equals(network.getTenantId())) {
			return BaseRestControl.tranReturnValue(ResultType.network_not_in_current_tenant);
		}
		String ip = entity.getAddress();
		if (StrUtils.checkParam(ip)) {
			Map<String, String> querymap = new HashMap<>();
			querymap.put("ipAddress", ip);
			querymap.put("subnetId", network.getSubnetId());
			List<IpAllocation> ipUseds = ipUsedDao.findByMap(IpAllocation.class, querymap);
			if (StrUtils.checkParam(ipUseds) || network.getGatewayIp().equals(ip)){//判断ip是否被占用
				return BaseRestControl.tranReturnValue(ResultType.ip_was_used);
			}
			if (!this.checkVip(ip, network.getSubnetId())){//检查ip是否在关联子网可用ip段内
				return BaseRestControl.tranReturnValue(ResultType.ip_notIn_range);
			}
		}
		if (!validateSG(entity)) {
			return BaseRestControl.tranReturnValue(ResultType.securitygroup_not_exist);
		}
		entity.setNetworkCloudosId(network.getCloudosId());
		entity.setAdminStateUp(Boolean.TRUE);
		entity.createdUser(this.getLoginUser());
		JSONObject response = this.cloudosSave(entity, user.getCloudosId(), client);
		if (!ResourceHandle.judgeResponse(response)) {
			return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, HttpUtils.getError(response));
		}
		this.localSave(entity, response, network.getSubnetId());
		return BaseRestControl.tranReturnValue(ResultType.success);
	}

	private void assignPort(JSONObject response, Port port) {
		JSONObject record = response.getJSONObject("record");
		JSONObject pJson = record.getJSONObject("port");
		port.setMacAddress(pJson.getString("mac_address"));
		port.setPortType(response.getString("binding:vif_type"));
		port.setStatus(pJson.getString("status"));
		port.setDeviceId(pJson.getString("device_id"));
		port.setDeviceOwner(pJson.getString("device_owner"));
		port.setCloudosId(pJson.getString("id"));
		if (null == port.getAddress()) {
			JSONArray fixIps = pJson.getJSONArray("fixed_ips");
			if (null != fixIps && fixIps.size() > 0) {
				JSONObject subJson = fixIps.getJSONObject(0);
				String ip = subJson.getString("ip_address");
				port.setAddress(ip);
			}
		}
	}
	
	private boolean validateSG(Port entity) {
		List<String> securityGroupIds = entity.getSecurityGroupIds();
		if (null != securityGroupIds && securityGroupIds.size() > 0) {
			for (String tempId : securityGroupIds) {
				SecurityGroup securityGroup = securityGroupDao.findById(SecurityGroup.class, tempId);
				if (null == securityGroup) {// 验证选择的安全组是否存在
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public Object dettach(String id, String serverId, CloudosClient client) {
		request.setAttribute("id", id);
		Port port = portDao.findById(Port.class, id);
		if (!StrUtils.checkParam(port)) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		request.setAttribute("name", port.getName());
		if (!projectBiz.checkOptionRole(port.getTenantId(), port.getUserId())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		NovaVm novavm = novaVmDao.findById(NovaVm.class, serverId);
		if (null == novavm) {
			return BaseRestControl.tranReturnValue(ResultType.novam_not_exist);
		}
		String vmstate = novavm.getVmState();
		if (!"state_normal".equals(vmstate) && !"state_stop".equals(vmstate)) {
			return BaseRestControl.tranReturnValue(ResultType.novavm_state_illegal);
		}
		if (!port.getDeviceId().equals(novavm.getUuid())) {
			return BaseRestControl.tranReturnValue(ResultType.port_not_attach_server);
		}
		List<FloatingIp> floatingIps = floatingIpBiz.findByPropertyName(FloatingIp.class, "fixedPortId", port.getId());
		if (StrUtils.checkCollection(floatingIps)) {
			return BaseRestControl.tranReturnValue(ResultType.floatingip_use_in_novavm);
		}
		//查看是否已经加入监控
		SpePort spePort = spePortBiz.singleByClass(SpePort.class, StrUtils.createMap("portId", port.getId()));
		if (StrUtils.checkParam(spePort)) {
			if(StrUtils.checkParam(spePort.getMonitorId())) {
				return BaseRestControl.tranReturnValue(ResultType.port_use_in_monitor);
			}
		}
		if (port.getIsinit()) {//初始化网卡直接删除
			this.deleteLocalPort(port);
		} else {//解除挂载
			port.setDeviceId(null);
			port.setDeviceOwner(null);
			portDao.update(port);
			novaVmDao.update(novavm);
		}
		JSONObject portJson = this.getPortJson(port.getCloudosId(), client);
		if (null != portJson && StrUtils.checkParam(portJson.getString("device_id"))) {
			String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_DETTACH_PORT);
			uri = HttpUtils.tranUrl(uri, client.getTenantId(), novavm.getUuid(), port.getCloudosId());
			JSONObject response = client.delete(uri);
			if (!ResourceHandle.judgeResponse(response)) {
				this.warn(response);
				throw new MessageException(ResultType.cloudos_exception);
			}
		} else {
			String casIp = singleton.getConfigValue("cas.product.ip");
			String casPort = singleton.getConfigValue("cas.product.port");
			String casUserName = singleton.getConfigValue("cas.product.username");
			String casPassword = singleton.getConfigValue("cas.product.password");
			CasClient casClient = new CasClient(casIp, Integer.parseInt(casPort), casUserName, casPassword);
			String vmCasId = getVmCasId(novavm.getUuid());
			if (null == vmCasId) {
				throw new MessageException(ResultType.select_vm_in_cas_failure);
			}
			if (!casDettachVm(vmCasId, port.getMacAddress(), casClient)) {
				throw new MessageException(ResultType.cas_dettach_failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}

	@Override
	public Object delete(String id, CloudosClient client) {
		request.setAttribute("id", id);
		Port port = portDao.findById(Port.class, id);
		if (null == port) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		request.setAttribute("name", port.getName());
		if (!projectBiz.checkOptionRole(port.getTenantId(), port.getUserId())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		String deviceId = port.getDeviceId();
		if (null != deviceId && !"".equals(deviceId)) {
			//若网卡挂载的云主机不存在则删除网卡（兼容上个版本错误）
			String deviceOwner = port.getDeviceOwner();
			NovaVm novaVm = novaVmDao.singleByClass(NovaVm.class, StrUtils.createMap("uuid", deviceId));
			if (deviceOwner.contains("compute") && StrUtils.checkParam(novaVm)) {
				return BaseRestControl.tranReturnValue(ResultType.port_still_attach_host);
			}
		}
		// 删除cloudos资源
		String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_PORTS_ACTION), port.getCloudosId());
		JSONObject response = client.get(uri);
		if (ResourceHandle.judgeResponse(response)) {
			response = client.delete(uri);
			if (!ResourceHandle.judgeResponse(response)) {
				return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, HttpUtils.getError(response));
			}
		}
		// 删除本地资源
		deleteLocalPort(port);
		return BaseRestControl.tranReturnValue(ResultType.success);
	}

	public void deleteLocalPort(Port port) {
 		FloatingIp floatingIp = floatingIpBiz.singleByClass(FloatingIp.class, StrUtils.createMap("fixedPortId", port
				.getId()));
		if (StrUtils.checkParam(floatingIp)) {
			floatingIp.setFixedPortId(null);
			floatingIp.setFixedIp(null);
			floatingIp.setLastRouterId(floatingIp.getRouterId());
			floatingIp.setRouterId(null);
			floatingIpBiz.update(floatingIp);
		}
		List<IpAllocation> usedIps = ipUsedDao.findByPropertyName(IpAllocation.class, "portId", port.getId());
		if (null != usedIps && usedIps.size() > 0) {
			ipUsedDao.delete(usedIps);
		}

		List<Group2Port> g2ps = g2PDao.findByPropertyName(Group2Port.class, "portId", port.getId());
		if (null != g2ps && g2ps.size() > 0) {
			g2PDao.delete(g2ps);
		}
		SpePort spePort = spePortBiz.singleByClass(SpePort.class, StrUtils.createMap("portId", port.getId()));
		if (StrUtils.checkParam(spePort)) {
			spePortBiz.delete(spePort);
		}
		portDao.delete(port);
		quotaUsedBiz.change(ConfigProperty.PORT_QUOTA_CLASSCODE, port.getTenantId(), false, 1);
	}

	@Override
	public Object attachHost(String id, String serverId, CloudosClient client) {
		request.setAttribute("id", id);
		Port port = portDao.findById(Port.class, id);
		if (!StrUtils.checkParam(port)) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		request.setAttribute("name", port.getName());
		if (!projectBiz.checkOptionRole(port.getTenantId(), port.getUserId())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		NovaVm novavm = novaVmDao.findById(NovaVm.class, serverId);
		if (null == novavm) {
			return BaseRestControl.tranReturnValue(ResultType.novam_not_exist);
		}
		//校验虚拟机状态
		String vmstate = novavm.getVmState();
		if (!"state_normal".equals(vmstate) && !"state_stop".equals(vmstate)) {
			return BaseRestControl.tranReturnValue(ResultType.only_can_attach_normal_or_stop_state_novavm);
		}
		//校验归属用户
		if (!port.getUserId().equals(novavm.getOwner())) {
			return BaseRestControl.tranReturnValue(ResultType.cross_user_operation);
		}
		//校验是否已经挂载到云主机
		if (StrUtils.checkParam(port.getDeviceId())) {
			NovaVm vm = novaVmDao.singleByClass(NovaVm.class, StrUtils.createMap("uuid", port.getDeviceId()));
			if (StrUtils.checkParam(vm)) {//检查是否以及挂载到云主机
				return BaseRestControl.tranReturnValue(ResultType.port_already_attach_server);
			}
		}
		//校验该租户是否有cas下发网卡的配置项参数
		NetTemplater templater = templateDao.singleByClass(NetTemplater.class, StrUtils.createMap("tenantId", port.getTenantId()));
		if (!StrUtils.checkParam(templater)) {
			this.warn("Cas Attatch Param Error");
			return BaseRestControl.tranReturnValue(ResultType.lack_of_template_param);
		}
		//校验与其它网卡网络是否冲突
		List<Port> ports = portDao.findByPropertyName(Port.class, "deviceId", novavm.getUuid());
		if (StrUtils.checkCollection(ports)) {
			for (Port port1 : ports) {
				if (port1.getSubnetId().equals(port.getSubnetId())) {
					return BaseRestControl.tranReturnValue(ResultType.subnet_conflict_with_other_port);
				}
			}
		}
		//cas下发
		String casIp = singleton.getConfigValue("cas.product.ip");
		String casPort = singleton.getConfigValue("cas.product.port");
		String casUserName = singleton.getConfigValue("cas.product.username");
		String casPassword = singleton.getConfigValue("cas.product.password");
		CasClient casClient = new CasClient(casIp, Integer.parseInt(casPort), casUserName, casPassword);
		String vmCasId = getVmCasId(novavm.getUuid());
		if (null == vmCasId) {
			return BaseRestControl.tranReturnValue(ResultType.select_vm_in_cas_failure);
		}
		if (!casAttachVm(vmCasId, port.getAddress(), port.getMacAddress(), casClient, templater)) {//通过cas挂载
			return BaseRestControl.tranReturnValue(ResultType.cas_attach_failure);
		}
		port.updatedUser(this.getLoginUser());
		port.setDeviceOwner("compute:cas01");
		port.setDeviceId(novavm.getUuid());
		portDao.update(port);
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@Override
	public Port get(NovaVm novaVm) {
		Map<String, Object> params = new HashMap<>();
		String uuid = novaVm.getUuid();
		if (StrUtils.checkParam(uuid)) {
			params.put("deviceId", uuid);
		} else {
			return null;
		}
		
		params.put("isinit", true);
		params.put("tenantId", novaVm.getProjectId());
		return singleByClass(Port.class, params);
	}
	
	@Override
	public List<Port> getPorts() {
		return portDao.getPorts();
	}
	
	@Override
	public JSONArray getPortArray (String deviceId, String deviceOwner, CloudosClient client) {
		String portUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_PORTS);
		//获取该路由器的外部网关网卡
		Map<String, Object> portMap = new HashMap<>();
		if (StrUtils.checkParam(deviceId)) {
			portMap.put("device_id", deviceId);
		}
		if (StrUtils.checkParam(deviceOwner)) {
			portMap.put("device_owner", deviceOwner);
		}
		return HttpUtils.getArray(portUri, "ports", portMap, client);
	}
	
	@Override
	public JSONObject getPortJson (String cloudosId, CloudosClient client) {
		if (!StrUtils.checkParam(cloudosId)) {
			return null;
		}
		String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_PORTS_ACTION), cloudosId);
		JSONObject json = HttpUtils.getJson(uri, "port", client);
		return json;
	}
	
	@Override
	public Port getPortByJson (JSONObject ptJson) {
		Port port = new Port();
		String ptCdId = ptJson.getString("id");
		String ptName = ptJson.getString("name");
		if (!StrUtils.checkParam(ptName)) {
			ptName = UUID.randomUUID().toString();
		}
		Boolean stateUp = ptJson.getBoolean("admin_state_up");
		String tenantId = ptJson.getString("tenant_id");
		String mac = ptJson.getString("mac_address");
		String status = ptJson.getString("status");
		String deviceOwner = ptJson.getString("device_owner");
		port.setCloudosId(ptCdId);
		port.setName(ptName);
		port.setTenantId(tenantId);
		port.setStatus(status);
		port.setAdminStateUp(stateUp);
		port.setDeviceOwner(deviceOwner);
		port.setMacAddress(mac);
		return port;
	}
	
	/**
	 * 查询虚拟机在cas里面的id
	 * @param uuid
	 * @return
	 */
	private String getVmCasId(String uuid) {
		Server2Vm server2Vm = server2VmBiz.singleByClass(Server2Vm.class, StrUtils.createMap("uuid", uuid));
		if (StrUtils.checkParam(server2Vm)) {//云主机同步表里面没有数据则去cas查询云主机在cas上的id
			return server2Vm.getCasId();
		} else {
			return null;
		}
	}
	
	/**
	 * 在cas上挂载网卡
	 * @return
	 */
	private boolean casAttachVm(String vmCasId, String ip, String mac, CasClient casClient, NetTemplater templater) {
		Map<String, Object> param = new HashMap<>();
		Map<String, Object> domainMap = new HashMap<>();
		Map<String, Object> networkMap = new HashMap<>();
		networkMap.put("ipAddr", ip);
		networkMap.put("mac", mac);
		networkMap.put("vsId", templater.getVsId());
		networkMap.put("vsName", templater.getVsName());
		networkMap.put("deviceModel", templater.getDeviceModel());
		networkMap.put("isKernelAccelerated", templater.getIsKernelAccelerated());
		networkMap.put("mode", templater.getMode());
		networkMap.put("profileId", templater.getProfileId());
		networkMap.put("profileName", templater.getProfileName());
		domainMap.put("network", networkMap);
		domainMap.put("id", vmCasId);
		param.put("domain", domainMap);
		String uri = "/cas/casrs/vm/addDevice";
		JSONObject jsonObject = casClient.put(uri, param);
		if (ResourceHandle.judgeResponse(jsonObject)) {
			jsonObject = jsonObject.getJSONObject("record");
			if (jsonObject.containsKey("data") && jsonObject.containsKey("message")) {
				String result = jsonObject.getString("message");
				if (result.contains("成功")) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 在cas上卸载网卡
	 * @return
	 */
	private boolean casDettachVm(String vmCasId, String mac, CasClient casClient) {
		Map<String, Object> param = new HashMap<>();
		Map<String, Object> domainMap = new HashMap<>();
		Map<String, Object> networkMap = new HashMap<>();
		networkMap.put("mac", mac);
		domainMap.put("network", networkMap);
		domainMap.put("id", vmCasId);
		param.put("domain", domainMap);
		String uri = "/cas/casrs/vm/delDevice";
		JSONObject jsonObject = casClient.put(uri, param);
		if (ResourceHandle.judgeResponse(jsonObject)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 检查vip是否在网络允许使用ip段内
	 * @param ip
	 * @param subnetId
	 * @return
	 */
	public boolean checkVip(String ip, String subnetId){
		if (!StrUtils.checkParam(ip)){
			return true;
		}
		boolean ifInRange = false;//是否在范围内,默认不在范围内
		IpAllocationPool ipAlloPool = ipAlloPoolDao.findByPropertyName(IpAllocationPool.class, "subnetId", subnetId).get(0);
		String ipAlloPoolId = ipAlloPool.getId();
		List<IpAvailabilityRange> ipranges = ipRangeDao.findByPropertyName(IpAvailabilityRange.class, "allocationPoolId", ipAlloPoolId);
		if (StrUtils.checkParam(ipranges)){
			for (IpAvailabilityRange iprange : ipranges) {
				String startIp = iprange.getFirstIp();
				String endIp = iprange.getLastIp();
				if (IpValidator.checkIpScope(ip, startIp, endIp)){
					ifInRange = true;
				}
			}
		}else {
			if (IpValidator.checkIpScope(ip, ipAlloPool.getFirstIp(), ipAlloPool.getLastIp())){
				ifInRange = true;
			}
		}
		return ifInRange;
	}
	
	public void subPortQuota(String uuid, String projectId, CloudosClient client) {
		JSONArray portArray = this.getPortArray(uuid, null, client);
		if (!StrUtils.checkCollection(portArray)) {
			quotaUsedBiz.change(ConfigProperty.PORT_QUOTA_CLASSCODE, projectId, false, 1);
		}
	}
	
	public Map<String, Object> getIpsByTenant(String tenantId, int pageSize, int startIndex) {
		List<String> ips = new ArrayList<>();
		Map<String, Object> queryMap = StrUtils.createMap("tenantId", tenantId);
		Map<String, Object> countResult = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_TENANT_PRIVATE_IP_COUNT,
				queryMap).get(0);
		queryMap.put("pageSize", pageSize < 0 ? 0 : pageSize);
		queryMap.put("count",  startIndex < 0 ? 0 : startIndex * pageSize);
		List<Map<String, Object>> ipList = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_TENANT_PRIVATE_IP, queryMap);
		if (StrUtils.checkCollection(ipList)) {
			for (Map<String, Object> map : ipList) {
				ips.add(StrUtils.tranString(map.get("ip")));
			}
		}
		countResult.put("ips", ips);
		return countResult;
	}
	
	public JSONObject cloudosSave(Port entity, String userCdId, CloudosClient client) {
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_PORTS);
		Map<String, Object> dataMap = ResourceHandle.tranToMap(entity, ConfigProperty.RESOURCE_TYPE_PORT);
		dataMap.put("h3c_user_id", userCdId);
		dataMap.put("tenant_id", entity.getTenantId());
		if (null != entity.getAddress() && !"".equals(entity.getAddress())) {
			List<Map<String, String>> fixedIps = new ArrayList<>();
			Map<String, String> subMap = new HashMap<>();
			subMap.put("ip_address", entity.getAddress());
			fixedIps.add(subMap);
			dataMap.put("fixed_ips", fixedIps);
		}
		if (StrUtils.checkParam(entity.getMacAddress())) {
			dataMap.put("mac_address", entity.getMacAddress());
		}
		Map<String, Object> param = ResourceHandle.getParamMap(dataMap, "port"); // 准备参数
		return client.post(uri, param);
	}
	
	public void localSave(Port entity, JSONObject response, String subnetId) {
		// 本地存储
		this.assignPort(response, entity);
		if (!StrUtils.checkParam(entity.getDeviceId())) {
			entity.setDeviceOwner(null);
			entity.setDeviceId(null);
		}
		String portId = portDao.add(entity);// 保存端口数据
		IpAllocation ipUsed = new IpAllocation();
		ipUsed.setPortId(portId);
		ipUsed.setIpAddress(entity.getAddress());
		ipUsed.setSubnetId(subnetId);
		ipUsedDao.add(ipUsed);// 保存ip使用数据
		
		List<String> securityGroupIds = entity.getSecurityGroupIds();
		if (null != securityGroupIds && securityGroupIds.size() > 0) {
			for (String tempId : securityGroupIds) {
				Group2Port group2Port = new Group2Port();
				group2Port.setPortId(portId);
				group2Port.setSecurityGroupId(tempId);
				group2Port.createdUser(this.getLoginUser());
				g2PDao.add(group2Port);// 添加安全组与端口映射关系数据
			}
		} else {
			// TODO... 保存默认安全组对应port信息
//			JSONObject record = response.getJSONObject("record");
//			JSONObject pJson = record.getJSONObject("port");
//			JSONArray fixIps = pJson.getJSONArray("security_groups");
//			String sgId = (String) fixIps.get(0);
		}
		quotaUsedBiz.change(ConfigProperty.PORT_QUOTA_CLASSCODE, entity.getTenantId(), true, 1);
	}
	
}
