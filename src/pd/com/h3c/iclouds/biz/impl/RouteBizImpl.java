package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.FirewallBiz;
import com.h3c.iclouds.biz.FloatingIpBiz;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.biz.RouteBiz;
import com.h3c.iclouds.biz.SyncVdcDataBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.RouteDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.Firewall;
import com.h3c.iclouds.po.FloatingIp;
import com.h3c.iclouds.po.IpAllocation;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Route;
import com.h3c.iclouds.po.Subnet;
import com.h3c.iclouds.po.VdcItem;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.VdcHandle;
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
 * 路由器biz实现类
 * Created by yKF7317 on 2016/11/23.
 */
@Service("routeBiz")
public class RouteBizImpl extends BaseBizImpl<Route> implements RouteBiz {
	
	@Resource
	private RouteDao routeDao;
	
	@Resource
	private FirewallBiz firewallBiz;
	
	@Resource
	private NetworkBiz networkBiz;
	
	@Resource
	private PortBiz portBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<IpAllocation> ipAllocationDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<VdcItem> vdcItemDao;
	
	@Resource
	private FloatingIpBiz floatingIpBiz;
	
	@Resource
	private SyncVdcDataBiz syncVdcDataBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Subnet> subnetDao;
	
	@Resource
	private ProjectBiz projectBiz;
	
	@Resource
	private QuotaUsedBiz quotaUsedBiz;
	
	@Override
	public PageModel<Route> findForPage (PageEntity entity) {
		return routeDao.findForPage(entity);
	}
	
	@Override
	public Map<String, Object> delete (String id, CloudosClient client) {
		Route route = routeDao.findById(Route.class, id);
		if (StrUtils.checkParam(route.getGwPortId())) {//检查外部网关
			return BaseRestControl.tranReturnValue(ResultType.still_relate_gateway);
		}
		List<Network> networks = networkBiz.findByPropertyName(Network.class, "routeId", id);
		if (null != networks && networks.size() > 0) {//检查是否关联网络
			return BaseRestControl.tranReturnValue(ResultType.still_relate_network);
		}
		String rtCdId = route.getCloudosId();
		JSONObject rtJson = getRouteJson(rtCdId, client);
		if (StrUtils.checkParam(route.getFwId())) {//检查防火墙
			Firewall firewall = firewallBiz.findById(Firewall.class, route.getFwId());
			String fwCdId = firewall.getCloudosId();
			JSONObject fwJson = firewallBiz.getFirewallJson(fwCdId, client);
			if (StrUtils.checkParam(rtJson) && StrUtils.checkParam(fwJson)) {
				if (BaseRestControl.checkStatus(route.getStatus())) {//路由器已经连接防火墙且路由器为正常状态不能删除
					return BaseRestControl.tranReturnValue(ResultType.still_relate_firewall);
				}
				if (StrUtils.checkParam(rtJson)) {//在cloudos断开连接
					cloudLink(firewall, route.getCloudosId(), "unlink", client);
				}
			} else {
				localLink(route, null);
			}
		}
		if (StrUtils.checkParam(rtJson)) {
			String deRs = cloudDelete(route.getCloudosId(), client);//cloudos删除
			if (!"success".equals(deRs)) {
				return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, deRs);
			}
		}
		localDelete(route);//本地删除
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@Override
	public Map<String, Object> linkFirewall (String id, String firewallId, CloudosClient client) {
		Route route = routeDao.findById(Route.class, id);//连接路由器时同步更新关联端口的路由器id
		if (!BaseRestControl.checkStatus(route.getStatus())) {//检查状态
			return BaseRestControl.tranReturnValue(ResultType.status_exception);
		}
		if (StrUtils.checkParam(route.getFwId())) {//检查是否已经连接防火墙
			return BaseRestControl.tranReturnValue(ResultType.still_relate_firewall);
		}
		Firewall firewall = firewallBiz.findById(Firewall.class, firewallId);
		if (null == firewall) {//检查防火墙是否存在
			return BaseRestControl.tranReturnValue(ResultType.firewall_not_exist);
		}
		if (!firewall.getTenantId().equals(route.getTenantId())) {
			return BaseRestControl.tranReturnValue(ResultType.tenant_inconformity);
		}
		List<Route> routes = routeDao.findByPropertyName(Route.class, "fwId", firewall.getId());
		if (StrUtils.checkParam(routes)) {//检查防火墙是否已经连接路由器
			return BaseRestControl.tranReturnValue(ResultType.firewall_already_linked_by_route);
		}
		if (!BaseRestControl.checkStatus(firewall.getStatus())) {//检查防火墙状态
			return BaseRestControl.tranReturnValue(ResultType.parent_status_exception);
		}
		JSONObject rtJson = getRouteJson(route.getCloudosId(), client);
		if (!StrUtils.checkParam(rtJson)) {//检查路由器在cloudos是否存在
			return BaseRestControl.tranReturnValue(ResultType.router_not_exist_in_cloudos);
		}
		String status = rtJson.getString("status");
		route.setStatus(status);
		JSONObject fwJson = firewallBiz.getFirewallJson(firewall.getCloudosId(), client);
		if (!StrUtils.checkParam(fwJson)) {//检查防火墙在cloudos是否存在
			return BaseRestControl.tranReturnValue(ResultType.firewall_not_exist_in_cloudos);
		}
		String rs = cloudLink(firewall, route.getCloudosId(), "link", client);//cloudos连接
		if ("success".equals(rs)) {
			localLink(route, firewallId);//本地连接
			firewallBiz.updateStatus(firewall.getId(), "ACTIVE");
			return BaseRestControl.tranReturnValue(ResultType.success);
		} else {
			return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, rs);
		}
	}
	
	@Override
	public Map<String, Object> unlinkFirewall (String id, CloudosClient client) {
		Route route = routeDao.findById(Route.class, id);//连接路由器时同步更新关联端口的路由器id
		if (!BaseRestControl.checkStatus(route.getStatus())) {//检查状态
			return BaseRestControl.tranReturnValue(ResultType.status_exception);
		}
		if (!StrUtils.checkParam(route.getFwId())) {//检查是否没有关联防火墙
			return BaseRestControl.tranReturnValue(ResultType.unlink_firewall);
		}
		Firewall firewall = firewallBiz.findById(Firewall.class, route.getFwId());
		if (!StrUtils.checkParam(firewall)) {
			return BaseRestControl.tranReturnValue(ResultType.firewall_not_exist);
		}
		JSONObject rtJson = getRouteJson(route.getCloudosId(), client);
		if (StrUtils.checkParam(rtJson)) {//检查路由器在cloudos是否存在
			String status = rtJson.getString("status");
			route.setStatus(status);
			JSONObject fwJson = firewallBiz.getFirewallJson(firewall.getCloudosId(), client);
			if (StrUtils.checkParam(fwJson)) {//检查防火墙在cloudos是否存在
				String rs = cloudLink(firewall, route.getCloudosId(), "unlink", client);
				if (!"success".equals(rs)) {
					return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, rs);
				}
			}
		}
		localLink(route, null);
		firewallBiz.updateStatus(firewall.getId(), "INACTIVE");
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@Override
	public String cloudSave (Route entity, CloudosClient client, String type) {
		String rtDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_ROUTE_ACTION);
		//获取路由器删除uri
		//调用cloudos接口创建路由器
		Map<String, Object> rtDataMap = ResourceHandle.tranToMap(entity, ConfigProperty.RESOURCE_TYPE_ROUTE);
		if (StrUtils.checkParam(entity.getGwNetworkId())) {//同步关联外部网关
			Network publicNetwork = networkBiz.findById(Network.class, entity.getGwNetworkId());
			Map<String, Object> gatewayMap = new HashMap<>();
			gatewayMap.put("network_id", publicNetwork.getCloudosId());
			rtDataMap.put("external_gateway_info", gatewayMap);
		}
		Map<String, Object> rtParams = ResourceHandle.getParamMap(rtDataMap, "router");//转换成跟cloudos对接的参数
		String rtUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_ROUTE);//获取uri
		JSONObject rtResponse = client.post(rtUri, rtParams);//创建路由器
		if (!ResourceHandle.judgeResponse(rtResponse)) {//请求失败时,抛出异常
			return HttpUtils.getError(rtResponse);
		}
		String ip = this.getGenerateIp(rtResponse);
		entity.setExternalGateway(ip);
		String cloudRoutId = ResourceHandle.getId(rtResponse, "router");//获取路由器真实id
		String status = ResourceHandle.getParam(rtResponse, "router", "status");
		entity.setStatus(status);
		if (StrUtils.checkParam(entity.getGwNetworkId())) {//同步获取外部网关网卡真实id
			JSONArray portArray = portBiz.getPortArray(cloudRoutId, "", client);
			JSONObject portJson = portArray.getJSONObject(0);
			String gwPortCdId = portJson.getString("id");
			entity.setGwPortCdId(gwPortCdId);
		}
		entity.setCloudosId(cloudRoutId);//放入对象属性
		rtDeUri = HttpUtils.tranUrl(rtDeUri, cloudRoutId);
		String fwId = entity.getFwId();
		if (StrUtils.checkParam(fwId)) {//同步关联防火墙(用修改防火墙的方式关联路由器)
			Firewall firewall = firewallBiz.findById(Firewall.class, fwId);
			if (!BaseRestControl.checkStatus(firewall.getStatus())) {
				client.delete(rtDeUri);//删除路由器
				return "failure";
			} else {
				String rs = cloudLink(firewall, cloudRoutId, "link", client);
				if (!"success".equals(rs)) {
					client.delete(rtDeUri);//删除路由器
					return rs;
				}
			}
		}
		if (StrUtils.checkParam(type) && "vdc".equals(type)) {
			entity = routeDao.findById(Route.class, entity.getId());
			entity.setCloudosId(cloudRoutId);
			routeDao.update(entity);
			VdcItem vdcItem = vdcItemDao.findById(VdcItem.class, entity.getId());
			vdcItem.setStatus(status);
			vdcItemDao.update(vdcItem);
			if (StrUtils.checkParam(entity.getGwPortId())) {
				Port gwPort = portBiz.findById(Port.class, entity.getGwPortId());
				gwPort.setCloudosId(entity.getGwPortCdId());
				portBiz.update(gwPort);
				IpAllocation ipAllocation = ipAllocationDao.findByPropertyName(IpAllocation.class, "portId", gwPort
						.getId()).get(0);
				ipAllocation.setIpAddress(entity.getExternalGateway());
				ipAllocationDao.update(ipAllocation);
			}
		}
		return "success";
	}
	
	@Override
	public void localSave (Route entity, String vdcId, String status) {
		entity.setStatus(status);
		String routeId = routeDao.add(entity);
		if (StrUtils.checkParam(entity.getGwNetworkId())) {
			CloudosClient client = this.getSessionBean().getCloudClient();
			JSONObject portJson = portBiz.getPortJson(entity.getGwPortCdId(), client);
			String portId = syncVdcDataBiz.syncPort(portJson, routeId, null);
			entity.setGwPortId(portId);
			routeDao.update(entity);
		}
		if (!ConfigProperty.RESOURCE_OPTION_STATUS_CREATING.equals(status) && StrUtils.checkParam(entity.getFwId())) {
			firewallBiz.updateStatus(entity.getFwId(), "ACTIVE");
		}
		quotaUsedBiz.change(ConfigProperty.ROUTER_QUOTA_CLASSCODE, entity.getTenantId(), true, 1);// 将资源使用配额量加1
		new VdcHandle().saveViewAndItem(entity.getId(), entity.getUuid(), entity.getName(), vdcId, entity.getFwId(),
				ConfigProperty.RESOURCE_TYPE_ROUTE, null, status);// 添加vdc视图和视图对象数据
	}
	
	@Override
	public String cloudDelete (String rtCloudId, CloudosClient client) {
		//调用cloudos接口删除路由器
		String rtDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_ROUTE_ACTION);
		//获取路由器删除uri
		rtDeUri = HttpUtils.tranUrl(rtDeUri, rtCloudId);
		JSONObject response = client.delete(rtDeUri);//删除路由器
		if (!ResourceHandle.judgeResponse(response)) {
			return HttpUtils.getError(response);
		}
		return "success";
	}
	
	@Override
	public void localDelete (Route route) {
		routeDao.delete(route);
		quotaUsedBiz.change(ConfigProperty.ROUTER_QUOTA_CLASSCODE, route.getTenantId(), false, 1);//将资源使用配额量减1
		new VdcHandle().deleteViewAndItem(route.getId());//更新vdc视图和视图对象数据
	}
	
	@Override
	public String cloudLink (Firewall firewall, String rtCloudId, String type, CloudosClient client) {
		List<String> routeCloudIds = getRouteCloudIds(firewall.getId(), rtCloudId, type);
		Map<String, Object> fwDataMap = ResourceHandle.tranToMap(firewall, ConfigProperty.RESOURCE_TYPE_FIREWALL);
		fwDataMap.put("router_ids", routeCloudIds);
		fwDataMap.remove("tenant_id");
		Map<String, Object> fwParams = ResourceHandle.getParamMap(fwDataMap, "firewall");//转换成跟cloudos对接的参数
		String fwUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FIREWALL_ACTION);//获取uri
		fwUri = HttpUtils.tranUrl(fwUri, firewall.getCloudosId());
		JSONObject fwResponse = client.put(fwUri, fwParams);//防火墙关联路由器
		if (!ResourceHandle.judgeResponse(fwResponse)) {//请求失败时,抛出异常
			return HttpUtils.getError(fwResponse);
		}
		return "success";
	}
	
	@Override
	public void localLink (Route route, String firewallId) {
		route.setFwId(firewallId);
		routeDao.update(route);
		new VdcHandle().updateView(route.getId(), firewallId);//更新vdc视图和视图对象数据
		updateStatus(route.getId(), route.getStatus());
	}
	
	@Override
	public void vdcSave (Route entity, String vdcId, String projectId) {
		entity.setTenantId(projectId);
		entity.createdUser(this.getLoginUser());
		entity.setVdcId(vdcId);
		ResultType veRs = verify(entity, null);
		if (!ResultType.success.equals(veRs)) {
			throw new MessageException(veRs);
		}
		localSave(entity, vdcId, ConfigProperty.RESOURCE_OPTION_STATUS_CREATING);
	}
	
	@Override
	public ResultType verify (Route entity, CloudosClient client) {
		String projectId = entity.getTenantId();
		String fwId = entity.getFwId();
		Map<String, Object> checkMap = new HashMap<>();
		checkMap.put("name", entity.getName());
		checkMap.put("tenantId", projectId);
		if (!routeDao.checkRepeat(Route.class, checkMap)) {
			return ResultType.name_repeat;
		}
		if (null != fwId) {//检查是否关联防火墙
			Firewall firewall = firewallBiz.findById(Firewall.class, fwId);
			if (null == firewall) {//关联防火墙则判断关联的防火墙是否存在
				return ResultType.firewall_not_exist;
			}
			if (!BaseRestControl.checkStatus(firewall.getStatus())) {
				return ResultType.parent_status_exception;
			}
			if (!firewall.getTenantId().equals(entity.getTenantId())) {
				return ResultType.tenant_inconformity;
			}
			if (null != client) {
				JSONObject fwJson = firewallBiz.getFirewallJson(firewall.getCloudosId(), client);
				if (!StrUtils.checkParam(fwJson)) {//判断关联防火墙在cloudos是否存在
					return ResultType.firewall_not_exist_in_cloudos;
				}
			}
		}
		ResultType resultType = quotaUsedBiz.checkQuota(ConfigProperty.ROUTER_QUOTA_CLASSCODE, projectId, 1);
		if (!ResultType.success.equals(resultType)) {//检查租户是否拥有配额以及是否达到最大值
			return resultType;
		}
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if (!validatorMap.isEmpty()) {
			return ResultType.parameter_error;
		}
		return ResultType.success;
	}
	
	@Override
	public Map<String, Object> setGateway (String id, String networkId, CloudosClient client) {
		Route route = routeDao.findById(Route.class, id);
		if (null == route) {//检查是否存在
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		request.setAttribute("name", route.getName());
		if (!projectBiz.checkOptionRole(route.getTenantId())){
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		String gwPortId = route.getGwPortId();
		if (null != gwPortId && !"".equals(gwPortId)) {//检查是否已经设置外部网关
			return BaseRestControl.tranReturnValue(ResultType.already_has_gateway);
		}
		Network network = networkBiz.findById(Network.class, networkId);
		if (null == network) {//检查网络是否存在
			return BaseRestControl.tranReturnValue(ResultType.network_not_exist);
		}
		if (!Boolean.TRUE.equals(network.getExternalNetworks())) {//检查是否是公网
			return BaseRestControl.tranReturnValue(ResultType.isnot_external_network);
		}
		
		//判断路由器的外部网关地址与路由器挂载的私网地址是否有重叠
		List<Subnet> subnets = new ArrayList<>();
		List<Network> networks = networkBiz.findByPropertyName(Network.class, "routeId", id);
		if (StrUtils.checkCollection(networks)) {
			for (Network privateNetwork : networks) {
				Subnet privateSubnet = subnetDao.findById(Subnet.class, privateNetwork.getSubnetId());
				subnets.add(privateSubnet);
			}
		}
		ResultType resultType = networkBiz.checkNetworkConflict(network.getSubnetId(), subnets);
		if (!ResultType.success.equals(resultType)) {
			return BaseRestControl.tranReturnValue(resultType);
		}
		
		JSONObject rtJson = getRouteJson(route.getCloudosId(), client);
		if (!StrUtils.checkParam(rtJson)) {//检查路由器在cloudos是否存在
			return BaseRestControl.tranReturnValue(ResultType.router_not_exist_in_cloudos);
		}
		JSONObject ntJson = networkBiz.getNetworkJson(network.getCloudosId(), client);
		if (!StrUtils.checkParam(ntJson)) {//检查公网在cloudos是否存在
			return BaseRestControl.tranReturnValue(ResultType.network_not_exist_in_cloudos);
		}
		
		// 路由设置外部网关：产生一个port和一个ip
		String rs = cloudSetGateway(route.getCloudosId(), network.getCloudosId(), client);
		if (!"success".equals(rs)) {
			return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, rs);
		}
		
		localSetGateway(route, networkId);
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@Override
	public Map<String, Object> unSetGateway (String id, CloudosClient client) {
		Route route = routeDao.findById(Route.class, id);
		if (null == route) {//检查是否存在
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		request.setAttribute("name", route.getName());
		if (!projectBiz.checkOptionRole(route.getTenantId())){
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		String gwPortId = route.getGwPortId();
		if (null == gwPortId || "".equals(gwPortId)) {//检查是否没有设置外部网关
			return BaseRestControl.tranReturnValue(ResultType.already_unset_gateway);
		}
		//检查该公网下是否有公网ip正在使用即已分配给使用了归属该路由器的网络的云主机或负载均衡
		Port port = portBiz.findById(Port.class, gwPortId);
		if (StrUtils.checkParam(port)) {
			List<FloatingIp> floatingIps = floatingIpBiz.findByPropertyName(FloatingIp.class, "networkId", port.getNetWorkId());
			if (StrUtils.checkCollection(floatingIps)) {
				for (FloatingIp floatingIp : floatingIps) {
					if (null != floatingIp.getFixedPortId() && !"".equals(floatingIp.getFixedPortId())) {
						Port fixPort = portBiz.findById(Port.class, floatingIp.getFixedPortId());
						Network fixNetwork = networkBiz.findById(Network.class, fixPort.getNetWorkId());
						if (id.equals(fixNetwork.getRouteId())) {
							return BaseRestControl.tranReturnValue(ResultType.relate_floatingip_already_in_use);
						}
					}
				}
			}
		}
		
		Network network = networkBiz.findById(Network.class, port.getNetWorkId());
		JSONObject rtJson = getRouteJson(route.getCloudosId(), client);
		if (StrUtils.checkParam(rtJson)) {//检查路由器在cloudos是否存在
			JSONObject ntJson = networkBiz.getNetworkJson(network.getCloudosId(), client);
			if (StrUtils.checkParam(ntJson)) {//检查公网在cloudos是否存在
				String rs = cloudSetGateway(route.getCloudosId(), null, client);
				if (!"success".equals(rs)) {//在cloudos清除网关
					return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, rs);
				}
			}
		}
		
		//本地修改
		localSetGateway(route, null);
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@Override
	public void localSetGateway(Route route, String networkId) {
		if (StrUtils.checkParam(networkId)) {
			
			Network network = networkBiz.findById(Network.class, networkId);
			// TODO	公网不需要设置路由器，如果存在则置空，为了兼容上个错误的版本
			if (network.getRouteId() != null) {
				network.setRouteId(null);
				networkBiz.update(network);  // 设置routeId
			}
			
			if (StrUtils.checkParam(route.getGwPortId())) {
				Map<String, Object> queryMap = new HashMap<>();
				queryMap.put("portId", route.getGwPortId());
				ipAllocationDao.delete(queryMap, IpAllocation.class);
				portBiz.deleteById(Port.class, route.getGwPortId());
			}
			
			//本地信息更新(网卡、ip、路由器)
			CloudosClient client = this.getSessionBean().getCloudClient();
			JSONArray portArray = portBiz.getPortArray(route.getCloudosId(), "network:router_gateway", client);
			if (StrUtils.checkCollection(portArray)) {
				JSONObject portJson = portArray.getJSONObject(0);
				String portId = syncVdcDataBiz.syncPort(portJson, route.getId(), null);
				route.setGwPortId(portId);
				routeDao.update(route);
				Port port = portBiz.findById(Port.class, portId);
				port.setRouteId(route.getId());
				portBiz.update(port);
			}
		} else {
			Map<String, Object> queryMap = new HashMap<>();
			queryMap.put("portId", route.getGwPortId());
			ipAllocationDao.delete(queryMap, IpAllocation.class);
			portBiz.deleteById(Port.class, route.getGwPortId());
			route.setGwPortId(null);
			routeDao.update(route);
		}
	}
	
	@Override
	public String cloudSetGateway(String rtCdId, String ntCdId, CloudosClient client) {
		String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_ROUTE_ACTION), rtCdId);
		Map<String, Object> innerMap = new HashMap<>();
		if (null != ntCdId) {
			innerMap.put("network_id", ntCdId);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("external_gateway_info", innerMap);
		Map<String, Object> rtParams = ResourceHandle.getParamMap(map, "router");
		JSONObject response = client.put(uri, rtParams);
		if (!ResourceHandle.judgeResponse(response)) {
			return HttpUtils.getError(response);
		}
		return "success";
	}
	
	@Override
	public void updateStatus (String id, String status) {
		Route route = routeDao.findById(Route.class, id);
		route.setStatus(status);
		routeDao.update(route);
		VdcItem vdcItem = vdcItemDao.findById(VdcItem.class, id);
		vdcItem.setStatus(status);
		vdcItemDao.update(vdcItem);
	}
	
	/**
	 * 获取该防火墙下的路由器真实id集合，用于跟cloudos对接
	 * @param fwId 防火墙id
	 * @param routCloudId 路由器cloudosId
	 * @param type 类型
	 * @return 返回值
	 */
	private List<String> getRouteCloudIds (String fwId, String routCloudId, String type) {
		List<String> routeCloudIds = new ArrayList<>();
		if ("link".equals(type)) {//路由器连接防火墙时将路由器真实id集合加上新连接的路由器真实id
			routeCloudIds.add(routCloudId);
			List<Route> routes = routeDao.findByPropertyName(Route.class, "fwId", fwId);
			for (Route route : routes) {
				String routeCloudId = route.getCloudosId();
				if (StrUtils.checkParam(routeCloudId) && !routeCloudId.equals(routCloudId)) {
					routeCloudIds.add(routeCloudId);
				}
			}
		}
		if ("unlink".equals(type)) {//断开连接时返回除当前路由器真实id之外的集合
			List<Route> routes = routeDao.findByPropertyName(Route.class, "fwId", fwId);
			for (Route route : routes) {
				String routeCloudId = route.getCloudosId();
				if (StrUtils.checkParam(routeCloudId) && !routeCloudId.equals(routCloudId)) {
					routeCloudIds.add(routeCloudId);
				}
			}
		}
		return routeCloudIds;
	}
	
	private String getGenerateIp (JSONObject response) {
		JSONObject record = response.getJSONObject("record");
		JSONObject routerJson = record.getJSONObject("router");
		JSONObject gatewayJson = routerJson.getJSONObject("external_gateway_info");
		if (StrUtils.checkParam(gatewayJson)) {
			JSONArray fixedIps = gatewayJson.getJSONArray("external_fixed_ips");
			if (null == fixedIps || fixedIps.size() == 0) {
				throw new MessageException(ResultType.cloudos_update_failure);
			}
			return fixedIps.getJSONObject(0).getString("ip_address");
		}
		return null;
	}
	
	@Override
	public JSONArray getRouteArray (CloudosClient client) {
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_ROUTE);
		JSONArray jsonArray = HttpUtils.getArray(uri, "routers", null, client);
		return jsonArray;
	}
	
	@Override
	public JSONObject getRouteJson (String cloudosId, CloudosClient client) {
		if (!StrUtils.checkParam(cloudosId)) {
			return null;
		}
		String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_ROUTE_ACTION), cloudosId);
		JSONObject routeObject = HttpUtils.getJson(uri, "router", client);
		return routeObject;
	}
	
	@Override
	public Route getRouteByJson (JSONObject rtJson) {
		Route route = new Route();
		String rtName = rtJson.getString("name");
		String rtCdId = rtJson.getString("id");
		Boolean stateUp = rtJson.getBoolean("admin_state_up");
		String tenantId = rtJson.getString("tenant_id");
		String status = rtJson.getString("status");
		route.setName(rtName);
		route.setCloudosId(rtCdId);
		route.setAdminStateUp(stateUp);
		route.setTenantId(tenantId);
		route.setStatus(status);
		return route;
	}
	
	@Override
	public void backWrite (Route route, String fwStatus) {
		routeDao.update(route);
		VdcItem vdcItem = vdcItemDao.findById(VdcItem.class, route.getId());
		vdcItem.setStatus(route.getStatus());
		vdcItemDao.update(vdcItem);
		if (StrUtils.checkParam(route.getGwPortId())) {
			Port gwPort = portBiz.findById(Port.class, route.getGwPortId());
			gwPort.setCloudosId(route.getGwPortCdId());
			portBiz.update(gwPort);
			IpAllocation ipAllocation = ipAllocationDao.findByPropertyName(IpAllocation.class, "portId", gwPort
					.getId()).get(0);
			ipAllocation.setIpAddress(route.getExternalGateway());
			ipAllocationDao.update(ipAllocation);
		}
		if (StrUtils.checkParam(route.getFwId())) {
			firewallBiz.updateStatus(route.getFwId(), fwStatus);
		}
	}
}
