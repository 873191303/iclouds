package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.FloatingIpBiz;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.biz.SyncVdcDataBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.FloatingIpDao;
import com.h3c.iclouds.dao.NovaVmDao;
import com.h3c.iclouds.dao.RouteDao;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.FloatingIp;
import com.h3c.iclouds.po.IpAllocation;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Route;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.VlbVip;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zKF7420
 *         date 2017年1月7日 上午9:45:06
 */

@Service("floatingIpBiz")
public class FloatingIpBizImpl extends BaseBizImpl<FloatingIp> implements FloatingIpBiz {
	
	@Resource
	private NetworkBiz networkBiz;
	
	@Resource
	private FloatingIpDao floatingIpDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<IpAllocation> ipAllocationDao;
	
	@Resource
	private PortBiz portBiz;
	
	@Resource
	private UserBiz userBiz;
	
	@Resource
	private RouteDao routeDao;
	
	@Resource
	private NovaVmDao novaVmDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<VlbVip> vipDao;
	
	@Resource
	private SqlQueryBiz queryBiz;
	
	@Resource
	private SyncVdcDataBiz syncVdcDataBiz;
	
	@Resource
	private QuotaUsedBiz quotaUsedBiz;
	
	@Override
	public PageModel<FloatingIp> findForPage (PageEntity entity) {
		return floatingIpDao.findForPage(entity);
	}
	
	@Override
	public Object delete (FloatingIp entity, CloudosClient client) {
		if (StrUtils.checkParam(entity.getFixedPortId())) {
			return BaseRestControl.tranReturnValue(ResultType.already_allocation_to_resource);
		}
		JSONObject floatingIpJson = getFloatingIpJson(entity.getCloudosId(), client);
		if (StrUtils.checkParam(floatingIpJson)) {
			String result = cloudDelete(entity.getCloudosId(), client);
			if (!"success".equals(result)) {
				return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, result);
			}
		}
		localDelete(entity.getId());
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@Override
	public Object update (FloatingIp floatingIp, CloudosClient client) {
		String result = cloudUpdate(floatingIp, client);
		if (!"success".equals(result)) {
			return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, result);
		}
		localUpdate(floatingIp);
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@Override
	public void localDelete (String id) {
		FloatingIp floatingIp = floatingIpDao.findById(FloatingIp.class, id);
		Port port = portBiz.findById(Port.class, floatingIp.getFloatingPortId());
		floatingIpDao.delete(floatingIp);
		if (StrUtils.checkParam(port)) {
			List<IpAllocation> allocations = ipAllocationDao.findByPropertyName(IpAllocation.class, "portId",
					port.getId());
			if (StrUtils.checkCollection(allocations)) {
				// 删除本地虚拟网卡和公网ip
				IpAllocation allocation = allocations.get(0);
				ipAllocationDao.delete(allocation);
			}
			portBiz.delete(port);
		}
		String projectId = floatingIp.getTenantId();
		quotaUsedBiz.change(ConfigProperty.FLOATINGIP_QUOTA_CLASSCODE, projectId, false, 1);
	}
	
	@Override
	public void localSave (FloatingIp floatingIp) {
		String ipId = floatingIpDao.add(floatingIp);
		CloudosClient client = this.getSessionBean().getCloudClient();
		JSONObject portJson = portBiz.getPortJson(floatingIp.getPortCdId(), client);
		String portId = syncVdcDataBiz.syncPort(portJson, ipId, floatingIp.getOwner());
		floatingIp.setFloatingPortId(portId);
		floatingIpDao.update(floatingIp);
		quotaUsedBiz.change(ConfigProperty.FLOATINGIP_QUOTA_CLASSCODE, floatingIp.getTenantId(), true, 1);
	}
	
	@Override
	public void localUpdate (FloatingIp floatingIp) {
		String fixPortId = floatingIp.getFixedPortId();
		if (StrUtils.checkParam(fixPortId)) {
			Port port = portBiz.findById(Port.class, fixPortId);
			Network network = networkBiz.findById(Network.class, port.getNetWorkId());
			floatingIp.setRouterId(network.getRouteId());
			IpAllocation ipAllocation = ipAllocationDao.findByPropertyName(IpAllocation.class, "portId", fixPortId).get(0);
			floatingIp.setFixedIp(ipAllocation.getIpAddress());
		} else {
			floatingIp.setFixedIp(null);
			floatingIp.setRouterId(null);
		}
		floatingIpDao.update(floatingIp);
	}
	
	@Override
	public String cloudDelete (String cloudId, CloudosClient client) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_FLOATINGIP_ACTION);
		uri = HttpUtils.tranUrl(uri, cloudId);
		JSONObject response = client.delete(uri);
		if (!ResourceHandle.judgeResponse(response)) {
			return HttpUtils.getError(response);
		}
		return "success";
	}
	
	@Override
	public String cloudSave (FloatingIp floatingIp, CloudosClient client, String type) {
		Network network = networkBiz.findById(Network.class, floatingIp.getNetworkId());
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FLOATINGIP);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("name", floatingIp.getName());
		String ip = floatingIp.getFloatingIp();
		if (null != ip && !"".equals(ip)) {
			paramMap.put("floating_ip_address", floatingIp.getFloatingIp());
		}
		paramMap.put("floating_network_id", network.getCloudosId());
		paramMap.put("tenant_id", floatingIp.getTenantId());
		paramMap.put("h3c_norm", floatingIp.getNorm());
		User user = userBiz.findById(User.class, floatingIp.getOwner());
		paramMap.put("h3c_user_id", user.getCloudosId());
		
		//判断是否已经分配到资源
		if (null != floatingIp.getFixedPortId() && !"".equals(floatingIp.getFixedPortId())) {
			Port port = portBiz.findById(Port.class, floatingIp.getFixedPortId());
			if (StrUtils.checkParam(port)) {
				paramMap.put("port_id", port.getCloudosId());
			}
		}
		
		//新增公网ip
		Map<String, Object> map = new HashMap<>();
		map.put("floatingip", paramMap);
		JSONObject response = client.post(uri, map);
		if (!ResourceHandle.judgeResponse(response)) {
			return HttpUtils.getError(response);
		}
		String ipCloudosId = ResourceHandle.getId(response, "floatingip");//公网ip的cloudosId
		if (null == ip || "".equals(ip)) {//回写自动分配的ip
			ip = ResourceHandle.getParam(response, "floatingip", "floating_ip_address");
			floatingIp.setFloatingIp(ip);
		}
		floatingIp.setCloudosId(ipCloudosId);
		
		//查询公网ip的网卡的cloudosId
		String portCdId = null;
		String portName = null;
		JSONArray portArray = portBiz.getPortArray(ipCloudosId, null, client);
		if (StrUtils.checkCollection(portArray)) {
			JSONObject portJson = portArray.getJSONObject(0);
			portCdId = portJson.getString("id");
			portName = portJson.getString("name");
			floatingIp.setPortCdId(portCdId);
		}
		
		//数据同步时直接回写数据库
		if (StrUtils.checkParam(type) && "sync".equals(type)) {
			floatingIpDao.update(floatingIp);
			Port port = portBiz.findById(Port.class, floatingIp.getFloatingPortId());
			port.setCloudosId(portCdId);
			port.setName(portName);
			portBiz.update(port);
		}
		return "success";
	}
	
	public String cloudUpdate (FloatingIp floatingIp, CloudosClient client) {
		String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FLOATINGIP_ACTION), floatingIp.getCloudosId());
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("h3c_norm", floatingIp.getNorm());
		if (StrUtils.checkParam(floatingIp.getFixedPortId())) {
			Port port = portBiz.findById(Port.class, floatingIp.getFixedPortId());
			paramMap.put("port_id", port.getCloudosId());
		} else {
			paramMap.put("port_id", null);
		}
		User user = userBiz.findById(User.class, floatingIp.getOwner());
		paramMap.put("h3c_user_id", user.getCloudosId());
		paramMap.put("name", floatingIp.getName());
		Map<String, Object> map = new HashMap<>();
		map.put("floatingip", paramMap);
		JSONObject response = client.put(uri, map);
		if (!ResourceHandle.judgeResponse(response)) {
			return HttpUtils.getError(response);
		}
		return "success";
	}
	
	public void addResource (List<FloatingIp> floatingIps) {
		if (null == floatingIps || floatingIps.size() == 0)
			return;
		
		for (int i = 0; i < floatingIps.size(); i++) {
			FloatingIp floatingIp = floatingIps.get(i);
			//
			String portId = floatingIp.getFixedPortId();
			if (null != portId && !"".equals(portId)) {
				Port port = portBiz.findById(Port.class, portId);
				String deviceOwner = port.getDeviceOwner();
				String deviceId = port.getDeviceId();
				if (deviceOwner.contains("compute")) {
					floatingIp.setResourceType("主机");
					List<NovaVm> novaVms = novaVmDao.findByPropertyName(NovaVm.class, "uuid", deviceId);
					if (null != novaVms && novaVms.size() > 0) {
						floatingIp.setResource(novaVms.get(0).getHostName());
					}
					
				} else if (deviceOwner.equals(ConfigProperty.PORT_OWNER_LOADBANLANCER)) {
					floatingIp.setResourceType("负载均衡");
					List<VlbVip> pools = vipDao.findByPropertyName(VlbVip.class, "portId", port.getId());
					if (null != pools && pools.size() > 0) {
						floatingIp.setResource(pools.get(0).getName());
					}
				}
			}
		}
	}
	
	@Override
	public ResultType checkBind (FloatingIp floatingIp, Port port) {
		
		Network network = networkBiz.findById(Network.class, port.getNetWorkId());
		if (!StrUtils.checkParam(network.getRouteId())) {//校验分配资源的网卡所属网络是否连接路由器
			return ResultType.network_not_link_to_route;
		}
		Route route = routeDao.findById(Route.class, network.getRouteId());
		if (!StrUtils.checkParam(route)) {//校验分配资源的网卡所属网络连接的路由器是否存在
			return ResultType.route_not_exist;
		}
		if (!StrUtils.checkParam(route.getGwPortId())) {//校验分配资源的网卡所属网络连接的路由器是否设置外部网关
			return ResultType.route_dont_set_gateway;
		}
		Port gwPort = portBiz.findById(Port.class, route.getGwPortId());
		if (!StrUtils.checkParam(gwPort)) {//校验分配资源的网卡所属网络连接的路由器设置的外部网关是否存在
			return ResultType.gwport_not_exist;
		}
		if (!gwPort.getNetWorkId().equals(floatingIp.getNetworkId())) {//校验分配资源的网卡所属网络连接的路由器设置的外部网关与该公网ip所属的公网是否一致
			return ResultType.public_network_notequal_resource_gateway;
		}
		return ResultType.success;
	}
	
	@Override
	public List<String> getPortIds (String networkId) {
		List<String> portIds = new ArrayList<>();
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("networkId", networkId);
		List<Map<String, Object>> portIdMaps = queryBiz.queryByName(SqlQueryProperty.QUERY_FLOATINGIP_ALLOCATION_PORTID,
				queryMap);
		if (StrUtils.checkCollection(portIdMaps)) {
			for (Map<String, Object> portIdMap : portIdMaps) {
				String portId = StrUtils.tranString(portIdMap.get("port_id"));
				portIds.add(portId);
			}
		}
		return portIds;
	}
	
	@Override
	public JSONArray getFloatingIpArray (CloudosClient client) {
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FLOATINGIP);
		JSONArray jsonArray = HttpUtils.getArray(uri, "floatingips", null, client);
		return jsonArray;
	}
	
	@Override
	public JSONObject getFloatingIpJson (String cloudosId, CloudosClient client) {
		if (!StrUtils.checkParam(cloudosId)) {
			return null;
		}
		String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FLOATINGIP_ACTION), cloudosId);
		JSONObject json = HttpUtils.getJson(uri, "floatingip", client);
		return json;
	}
	
	@Override
	public FloatingIp getFloatingIpByJson (JSONObject floatingIpJson) {
		FloatingIp floatingIp = new FloatingIp();
		String name = floatingIpJson.getString("name");
		String cloudId = floatingIpJson.getString("id");
		String ipAddress = floatingIpJson.getString("floating_ip_address");
		String norm = floatingIpJson.getString("h3c_norm");
		String tenantId = floatingIpJson.getString("tenant_id");
		String status = floatingIpJson.getString("status");
		floatingIp.setNorm(norm);
		floatingIp.setStatus(status);
		floatingIp.setTenantId(tenantId);
		floatingIp.setName(name);
		floatingIp.setCloudosId(cloudId);
		floatingIp.setFloatingIp(ipAddress);
		return floatingIp;
	}
	
	@Override
	public int allotionCount () {
		return floatingIpDao.allotionCount();
	}
	
	@Override
	public ResultType verify(FloatingIp entity, CloudosClient client) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);//验证数据
		if (!validatorMap.isEmpty()) {//校验参数
			return ResultType.parameter_error;
		}
		String name = entity.getName();
		Map<String, String> queryMap = new HashMap<>();
		queryMap.put("name", name);
		queryMap.put("tenantId", entity.getTenantId());
		List<FloatingIp> floatingIps = floatingIpDao.findByMap(FloatingIp.class, queryMap);
		if (StrUtils.checkCollection(floatingIps)) {//检查名称是否重复
			return ResultType.name_repeat;
		}
		// 校验公网Ip配额
		ResultType fIpVerify = quotaUsedBiz.checkQuota(ConfigProperty.FLOATINGIP_QUOTA_CLASSCODE, entity.getTenantId
				(), 1);
		if (!ResultType.success.equals(fIpVerify)) {
			return fIpVerify;
		}
		
		//检验公网是否存在以及是否是公网
		String networkId = entity.getNetworkId();
		Network network = networkBiz.findById(Network.class, networkId);
		if (!StrUtils.checkParam(network)) {
			return ResultType.network_public_not_exist;
		}
		if (!network.getExternalNetworks()) {
			return ResultType.network_is_private;
		}
		//校验公网在cloudos是否存在
		JSONObject ntJson = networkBiz.getNetworkJson(network.getCloudosId(), client);
		if (!StrUtils.checkParam(ntJson)) {
			return ResultType.network_not_exist_in_cloudos;
		}
		entity.setSubnetId(network.getSubnetId());
		
		String ip = entity.getFloatingIp();
		if (null != ip && !"".equals(ip)) {
			String gateWayIp = network.getGatewayIp();
			//校验ip是否已被使用
			floatingIps = floatingIpDao.findByPropertyName(FloatingIp.class, "floatingIp", ip);
			if (StrUtils.checkCollection(floatingIps) || ip.equals(gateWayIp)) {
				return ResultType.ip_was_used;
			}
			if (!portBiz.checkVip(ip, network.getSubnetId())){//检查ip是否在关联子网可用ip段内
				return ResultType.ip_notIn_range;
			}
		}
		return ResultType.success;
	}
}
