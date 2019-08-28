package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.FloatingIpBiz;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.biz.RouteBiz;
import com.h3c.iclouds.biz.SyncVdcDataBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.Network2SubnetDao;
import com.h3c.iclouds.dao.NetworkDao;
import com.h3c.iclouds.dao.Project2NetworkDao;
import com.h3c.iclouds.dao.VlbPoolDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosNetwork;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.FloatingIp;
import com.h3c.iclouds.po.IpAllocation;
import com.h3c.iclouds.po.IpAllocationPool;
import com.h3c.iclouds.po.IpAvailabilityRange;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.Network2Subnet;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Project2Network;
import com.h3c.iclouds.po.Route;
import com.h3c.iclouds.po.Sub2Dns;
import com.h3c.iclouds.po.Sub2Route;
import com.h3c.iclouds.po.Subnet;
import com.h3c.iclouds.po.VdcItem;
import com.h3c.iclouds.po.VlbPool;
import com.h3c.iclouds.po.VlbVip;
import com.h3c.iclouds.po.bean.outside.TenantNetworkBean;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.IpValidator;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.VdcHandle;
import com.h3c.iclouds.validate.ValidatorUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * Created by yKF7317 on 2016/11/23.
 */
@Service("networkBiz")
public class NetworkBizImpl extends BaseBizImpl<Network> implements NetworkBiz {
	
	@Resource
	private NetworkDao networkDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Subnet> subnetDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<IpAllocationPool> ipPoolsDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<IpAvailabilityRange> ipAvailDao;
	
	@Resource
	private PortBiz portBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<IpAllocation> ipUsedDao;
	
	@Resource
	private RouteBiz routeBiz;
	
	@Resource
	private VlbPoolDao vlbPoolDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<VlbVip> vipDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Sub2Dns> sub2DnsDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Sub2Route> sub2RouteDao;
	
	@Resource
	private Project2NetworkDao p2nDao;
	
	@Resource
	private Network2SubnetDao n2sDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<VdcItem> vdcItemDao;
	
	@Resource
	private FloatingIpBiz floatingIpBiz;
	
	@Resource
	private SyncVdcDataBiz syncVdcDataBiz;
	
	@Resource
	private QuotaUsedBiz quotaUsedBiz;
	
	@Override
	public PageModel<Network> findForPage (PageEntity entity) {
		return networkDao.findForPage(entity);
	}
	
	@Override
	public Map<String, Object> delete (String id, CloudosClient client, boolean external) {
		Network network = networkDao.findById(Network.class, id);
		if (network.getExternalNetworks() != external){
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		String ntCdId = network.getCloudosId();
		JSONObject ntJson = getNetworkJson(ntCdId, client);
		if (external) {
			Set<FloatingIp> ips = network.getFloatingIps();
			if (StrUtils.checkCollection(ips)) {//检查是否仍有公网ip
				return BaseRestControl.tranReturnValue(ResultType.network_still_has_floatingip);
			}
			List<IpAllocation> ipUseds = ipUsedDao.findByPropertyName(IpAllocation.class, "subnetId", network.getSubnetId
					());
			if (StrUtils.checkCollection(ipUseds)) {//检查是否被路由器挂载
				return BaseRestControl.tranReturnValue(ResultType.already_link_by_router);
			}
		} else {
			List<IpAllocation> ipUseds = ipUsedDao.findByPropertyName(IpAllocation.class, "subnetId", network.getSubnetId
					());
			if (StrUtils.checkParam(network.getRouteId())) {//检查路由器
				if (StrUtils.checkParam(ipUseds) && ipUseds.size() > 1) {
					return BaseRestControl.tranReturnValue(ResultType.some_ip_used);
				}
				Route route = routeBiz.findById(Route.class, network.getRouteId());
				JSONObject rtJson = routeBiz.getRouteJson(route.getCloudosId(), client);
				if (StrUtils.checkParam(ntJson) && StrUtils.checkParam(rtJson)) {
					if (BaseRestControl.checkStatus(network.getStatus())) {
						return BaseRestControl.tranReturnValue(ResultType.still_relate_route);
					}
					if (StrUtils.checkParam(ntJson)) {
						cloudLink(network.getSubnetCloudosId(), route.getCloudosId(), client, "unlink");
					}
				} else {
					localLink(network, null, null, "unlink", null);
				}
			} else {
				if (StrUtils.checkParam(ipUseds)) {
					return BaseRestControl.tranReturnValue(ResultType.some_ip_used);
				}
			}
		}
		if (StrUtils.checkParam(ntJson)) {//cloudos删除
			String deRs = cloudDelete(network.getCloudosId(), network.getSubnetCloudosId(), client);
			if (!"success".equals(deRs)) {
				BaseRestControl.tranReturnValue(ResultType.cloudos_exception, deRs);
			}
		}
		localDelete(network);//本地删除
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@Override
	public Map<String, Object> linkRoute (String id, String routeId, CloudosClient client) {
		Network network = networkDao.findById(Network.class, id);//连接路由器时同步更新关联端口的路由器id
		if (!BaseRestControl.checkStatus(network.getStatus())) {
			return BaseRestControl.tranReturnValue(ResultType.status_exception);
		}
		if (StrUtils.checkParam(network.getRouteId())) {
			return BaseRestControl.tranReturnValue(ResultType.still_relate_route);
		}
		Route route = routeBiz.findById(Route.class, routeId);
		if (null == route) {
			return BaseRestControl.tranReturnValue(ResultType.route_not_exist);
		}
		if (!BaseRestControl.checkStatus(route.getStatus())) {
			return BaseRestControl.tranReturnValue(ResultType.parent_status_exception);
		}
		if (!network.getTenantId().equals(route.getTenantId())) {
			return BaseRestControl.tranReturnValue(ResultType.tenant_inconformity);
		}
		JSONObject ntJson = getNetworkJson(network.getCloudosId(), client);
		if (!StrUtils.checkParam(ntJson)) {
			return BaseRestControl.tranReturnValue(ResultType.network_not_exist_in_cloudos);
		}
		String status = ntJson.getString("status");
		network.setStatus(status);
		JSONObject rtJson = routeBiz.getRouteJson(route.getCloudosId(), client);
		if (!StrUtils.checkParam(rtJson)) {
			return BaseRestControl.tranReturnValue(ResultType.router_not_exist_in_cloudos);
		}
		//检查与路由器的外部网关是否冲突
		if (StrUtils.checkParam(route.getGwPortId())) {
			List<Subnet> subnets = new ArrayList<>();
			Subnet subnet = subnetDao.findById(Subnet.class, network.getSubnetId());
			subnets.add(subnet);
			Port gwPort = portBiz.findById(Port.class, route.getGwPortId());
			ResultType resultType = this.checkNetworkConflict(gwPort.getSubnetId(), subnets);
			if (!ResultType.success.equals(resultType)) {
				return BaseRestControl.tranReturnValue(resultType);
			}
		}
		JSONObject linkResponse = this.cloudLink(network.getSubnetCloudosId(), route.getCloudosId(), client,
				"link");
		if (!ResourceHandle.judgeResponse(linkResponse)) {
			return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, HttpUtils.getError(linkResponse));
		}
		String portCloudId = ResourceHandle.getParam(linkResponse, null, "port_id");//获取cloudos的真实id
		localLink(network, routeId, portCloudId, "link", client);
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@Override
	public Map<String, Object> unlinkRoute (String id, CloudosClient client) {
		Network network = networkDao.findById(Network.class, id);//断开连接路由器时同步更新关联端口的路由器id
		if (!BaseRestControl.checkStatus(network.getStatus())) {
			return BaseRestControl.tranReturnValue(ResultType.status_exception);
		}
		if (!StrUtils.checkParam(network.getRouteId())) {
			return BaseRestControl.tranReturnValue(ResultType.unlink_route);
		}
		List<VlbVip> vlbvips = vipDao.findByPropertyName(VlbVip.class, "vainSubnetId", network
				.getSubnetId());
		List<VlbPool> vlbPools = vlbPoolDao.findByPropertyName(VlbPool.class, "factSubnetId", network
				.getSubnetId());
		if (StrUtils.checkParam(vlbPools, vlbvips)) {//检查该路由器底下有没有关联负载均衡
			return BaseRestControl.tranReturnValue(ResultType.still_relate_vlbPool);
		}
		List<IpAllocation> ipAllocations = ipUsedDao.findByPropertyName(IpAllocation.class, "subnetId", network
				.getSubnetId());
		if (StrUtils.checkCollection(ipAllocations)) {
			for (IpAllocation ipAllocation : ipAllocations) {
				if (!ipAllocation.getIpAddress().equals(network.getGatewayIp())) {
					List<FloatingIp> floatingIps = floatingIpBiz.findByPropertyName(FloatingIp.class, "fixedIp",
							ipAllocation.getIpAddress());
					if (StrUtils.checkCollection(floatingIps)) {
						return BaseRestControl.tranReturnValue(ResultType.already_link_by_floatingip);
					}
				}
			}
		}
		Route route = routeBiz.findById(Route.class, network.getRouteId());
		if (!StrUtils.checkParam(route)) {
			return BaseRestControl.tranReturnValue(ResultType.route_not_exist);
		}
		JSONObject ntJson = getNetworkJson(network.getCloudosId(), client);
		if (!StrUtils.checkParam(ntJson)) {
			return BaseRestControl.tranReturnValue(ResultType.network_not_exist_in_cloudos);
		}
		String status = ntJson.getString("status");
		network.setStatus(status);
		JSONObject rtJson = routeBiz.getRouteJson(route.getCloudosId(), client);
		if (StrUtils.checkParam(rtJson)) {
			JSONObject unlinkResponse = this.cloudLink(network.getSubnetCloudosId(), route.getCloudosId(), client,
					"unlink");
			if (!ResourceHandle.judgeResponse(unlinkResponse)) {
				return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, HttpUtils.getError(unlinkResponse));
			}
		}
		localLink(network, null, null, "unlink", null);
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	/**
	 * cloudos保存
	 */
	@Override
	public String cloudSave (Network network, Subnet subnet, CloudosClient client, String type) {
		String ntDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_NETWORK_ACTION);
		String sbDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SUBNET_ACTION);
		//调用cloudos接口创建网络
		Map<String, Object> ntDataMap = ResourceHandle.tranToMap(network, ConfigProperty.RESOURCE_TYPE_NETWORK);
		Map<String, Object> ntParams = ResourceHandle.getParamMap(ntDataMap, "network");//转换成跟cloudos对接的参数
		String ntUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_NETWORKS);//获取uri
		JSONObject ntResponse = client.post(ntUri, ntParams);//创建网络
		if (!ResourceHandle.judgeResponse(ntResponse)) {//请求失败时,抛出异常
			return HttpUtils.getError(ntResponse);
		}
		String cloudNetId = ResourceHandle.getId(ntResponse, "network");//获取网络真实id
		String status = ResourceHandle.getParam(ntResponse, "network", "status");//获取网络真实id
		ntDeUri = HttpUtils.tranUrl(ntDeUri, cloudNetId);
		network.setCloudosId(cloudNetId);//给真实id属性赋值
		network.setStatus(status);
		//调用cloudos接口创建子网
		Map<String, Object> sbDataMap = ResourceHandle.tranToMap(subnet, ConfigProperty.RESOURCE_TYPE_SUBNET);
		sbDataMap.put("network_id", cloudNetId);
		if (!network.getExternalNetworks()) {//私网创建参数
			if (StrUtils.checkParam(network.getDnsList())) {
				sbDataMap.put("dns_nameservers", network.getDnsList());
			}
			if (StrUtils.checkParam(network.getHostRouteList())) {
				sbDataMap.put("host_routes", network.getHostRouteList());
			}
		}
		if (StrUtils.checkParam(network.getIpPoolList())) {
			sbDataMap.put("allocation_pools", network.getIpPoolList());
		}
		Map<String, Object> sbParams = ResourceHandle.getParamMap(sbDataMap, "subnet");//转换成跟cloudos对接的参数
		String sbUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SUBNET);//获取uri
		JSONObject sbResponse = client.post(sbUri, sbParams);//创建子网
		if (!ResourceHandle.judgeResponse(sbResponse)) {//请求失败时,抛出异常并删除网络
			client.delete(ntDeUri);//删除cloudos创建的网络
			return HttpUtils.getError(sbResponse);
		}
		String cloudSubId = ResourceHandle.getId(sbResponse, "subnet");//获取子网真实id
		String cidr = ResourceHandle.getParam(sbResponse, "subnet", "cidr");
		subnet.setCidr(cidr);
		sbDeUri = HttpUtils.tranUrl(sbDeUri, cloudSubId);
		subnet.setCloudosId(cloudSubId);//给真实id属性赋值
		if (!network.getExternalNetworks()) {//私网同步连接路由器
			if (StrUtils.checkParam(network.getRouteId())) {
				Route route = routeBiz.findById(Route.class, network.getRouteId());
				if (!BaseRestControl.checkStatus(route.getStatus())) {
					client.delete(ntDeUri);//删除cloudos创建的网络
					client.delete(sbDeUri);//删除cloudos创建的子网
					return "failure";
				}
				String linkUri = singleton.getCloudosApi(CloudosParams
                        .CLOUDOS_API_SUBNET_LINK_ROUTE);
				Map<String, Object> linkMap = new HashMap<>();
				linkMap.put("subnet_id", cloudSubId);
				linkUri = HttpUtils.tranUrl(linkUri, route.getCloudosId());
				JSONObject linkResponse = client.put(linkUri, linkMap);
				if (!ResourceHandle.judgeResponse(linkResponse)) {//请求失败时,抛出异常并删除网络和子网
					client.delete(ntDeUri);//删除cloudos创建的网络
					client.delete(sbDeUri);//删除cloudos创建的子网
					return HttpUtils.getError(linkResponse);
				}
				Map<String, Object> unlinkMap = new HashMap<>();
				unlinkMap.put("subnet_id", cloudSubId);
				String portCloudId = ResourceHandle.getParam(linkResponse, null, "port_id");//获取cloudos的真实id
				network.setPortCloudId(portCloudId);
			}
		}
		if (StrUtils.checkParam(type) && "vdc".equals(type)) {
			if (StrUtils.checkParam(network.getRouteId())) {
				Map<String, String> queryMap = new HashMap<>();
				queryMap.put("subnetId", subnet.getId());
				queryMap.put("ipAddress", subnet.getGatewayIp());
				if (StrUtils.checkParam(network.getRouteId())) {
					List<IpAllocation> ipUseds = ipUsedDao.findByMap(IpAllocation.class, queryMap);
					if (StrUtils.checkParam(ipUseds)) {
						IpAllocation ipUsed = ipUseds.get(0);
						Port port = portBiz.findById(Port.class, ipUsed.getPortId());
						port.setCloudosId(network.getPortCloudId());
						port.setStatus(status);
						portBiz.update(port);
					}
				}
			}
			network = networkDao.findById(Network.class, network.getId());
			network.setCloudosId(cloudNetId);
			network.setStatus(status);
			networkDao.update(network);
			subnetDao.update(subnet);
			VdcItem vdcItem = vdcItemDao.findById(VdcItem.class, network.getId());
			vdcItem.setStatus(status);
			vdcItemDao.update(vdcItem);
		}
		return "success";
	}
	
	@Override
	public void localSave (Network network, Subnet subnet, String vdcId, String status) {
		network.setStatus(status);
		String startIp = network.getStartIp();
		String endIp = network.getEndIp();
		String networkId = networkDao.add(network);//保存网络
		subnet.setNetworkId(networkId);
		String subnetId = subnetDao.add(subnet);//保存子网
		if (!network.getExternalNetworks() && StrUtils.checkParam(network.getRouteId())) {//私网同步连接路由器
			if (ConfigProperty.RESOURCE_OPTION_STATUS_CREATING.equals(status)) {
				Port port = new Port();
				port.setTenantId(network.getTenantId());
				port.setName(UUID.randomUUID().toString());
				port.setStatus(network.getStatus());
				port.setRouteId(network.getRouteId());
				port.setDeviceId(network.getRouteId());
				port.setDeviceOwner("network:router_interface");
				port.createdUser(network.getCreatedBy());
				String portId = portBiz.add(port);
				IpAllocation ipAllocation = new IpAllocation();
				ipAllocation.setIpAddress(subnet.getGatewayIp());
				ipAllocation.setSubnetId(subnetId);
				ipAllocation.setPortId(portId);
				ipUsedDao.add(ipAllocation);
			} else {
				CloudosClient client = this.getSessionBean().getCloudClient();
				JSONObject portJson = portBiz.getPortJson(network.getPortCloudId(), client);
				syncVdcDataBiz.syncPort(portJson, network.getRouteId(), null);
			}
		}
		IpAllocationPool ipAllPool = new IpAllocationPool();
		ipAllPool.setSubnetId(subnetId);
		ipAllPool.setFirstIp(startIp);
		ipAllPool.setLastIp(endIp);
		String ipPoolId = ipPoolsDao.add(ipAllPool);//保存ip池
		if (StrUtils.checkParam(network.getIpPoolList())) {
			List<Map<String, String>> ipList = network.getIpPoolList();
			for (Map<String, String> ip : ipList) {//将每一个ip段的首末ip存入ip可用表
				IpAvailabilityRange ipAvai = new IpAvailabilityRange();
				ipAvai.setAllocationPoolId(ipPoolId);
				ipAvai.setFirstIp(ip.get("start"));
				ipAvai.setLastIp(ip.get("end"));
				ipAvailDao.add(ipAvai);
			}
		}
		if (!network.getExternalNetworks()) {//私网本地数据的新增
			addDnsAndRoute(network.getDnsList(), network.getHostRouteList(), subnetId);//保存主机路由和dns
			quotaUsedBiz.change(ConfigProperty.NETWORK_QUOTA_CLASSCODE, network.getTenantId(), true, 1);//将资源使用配额量加1
			new VdcHandle().saveViewAndItem(network.getId(), network.getUuid(), network.getName(), vdcId, network
					.getRouteId(), ConfigProperty.RESOURCE_TYPE_NETWORK, null, status);//创建vdc在vdc视图和视图对象里面的数据);
		}
	}
	
	@Override
	public String cloudDelete (String ntCdId, String sbCdId, CloudosClient client) {
		//调用cloudos
		String ntDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_NETWORK_ACTION);
		String sbDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SUBNET_ACTION);
		sbDeUri = HttpUtils.tranUrl(sbDeUri, sbCdId);
		ntDeUri = HttpUtils.tranUrl(ntDeUri, ntCdId);
		JSONObject sbDeResponse = client.delete(sbDeUri);//删除cloudos创建的子网
		if (!ResourceHandle.judgeResponse(sbDeResponse)) {//请求失败时,抛出异常
			return HttpUtils.getError(sbDeResponse);
		}
		client.delete(ntDeUri);//删除cloudos创建的网络
		return "success";
		
	}
	
	@Override
	public void localDelete (Network network) {
		deleteRelateData(network.getId());//删除相关数据
		networkDao.delete(network);
		if (!network.getExternalNetworks()) {
			quotaUsedBiz.change(ConfigProperty.NETWORK_QUOTA_CLASSCODE, network.getTenantId(), false, 1);//将资源使用配额量减1
			new VdcHandle().deleteViewAndItem(network.getId());
		}
	}
	
	@Override
	public JSONObject cloudLink (String subCloudId, String rtCloudId, CloudosClient client, String type) {
		if ("link".equals(type)) {
			//调用cloudos接口连接路由器
			String linkUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SUBNET_LINK_ROUTE);
			Map<String, Object> linkMap = new HashMap<>();
			linkMap.put("subnet_id", subCloudId);
			linkUri = HttpUtils.tranUrl(linkUri, rtCloudId);
			return client.put(linkUri, linkMap);
		} else if ("unlink".equals(type)) {
			//调用cloudos接口断开连接路由器
			String unlinkUri = singleton.getCloudosApi(CloudosParams
                    .CLOUDOS_API_SUBNET_UNLINK_ROUTE);
			Map<String, Object> unlinkMap = new HashMap<>();
			unlinkMap.put("subnet_id", subCloudId);
			unlinkUri = HttpUtils.tranUrl(unlinkUri, rtCloudId);
			JSONObject unlinkResponse = client.put(unlinkUri, unlinkMap);
			return unlinkResponse;
		}
		return null;
	}
	
	@Override
	public void localLink (Network network, String routeId, String portCloudId, String type, CloudosClient client) {
		if ("link".equals(type)) {
			JSONObject portJson = portBiz.getPortJson(portCloudId, client);
			syncVdcDataBiz.syncPort(portJson, routeId, null);
			network.setRouteId(routeId);
			networkDao.update(network);
			new VdcHandle().updateView(network.getId(), routeId);
		}
		if ("unlink".equals(type)) {
			List<IpAllocation> ipUseds = ipUsedDao.findByPropertyName(IpAllocation.class, "subnetId", network
                    .getSubnetId());
			if (StrUtils.checkParam(ipUseds)) {
				for (IpAllocation ipUsed : ipUseds) {
					Port port = portBiz.findById(Port.class, ipUsed.getPortId());
					if (network.getGatewayIp().equals(ipUsed.getIpAddress())) {
						ipUsedDao.delete(ipUsed);
						portBiz.delete(port);
					} else {
						port.setRouteId(null);
						portBiz.update(port);
					}
				}
			}
			network.setRouteId(null);
			networkDao.update(network);
			new VdcHandle().updateView(network.getId(), null);
		}
		updateStatus(network.getId(), network.getStatus());
	}
	
	@Override
	public ResultType verify (Network network, Subnet subnet) {
		String projectId = network.getTenantId();
		String cidr = network.getCidr();//获取cidr
		String gatewayIp = network.getGatewayIp();//获取网关ip
		String ipPool = network.getIpPool();
		String dnsAddress = network.getDnsAddress();
		String hostRoute = network.getHostRoute();
		Map<String, Object> checkRepeatMap = new HashMap<>();
		checkRepeatMap.put("name", network.getName());
		checkRepeatMap.put("tenantId", projectId);
		if (!networkDao.checkRepeat(Network.class, checkRepeatMap)) {//检查名称重复
			return ResultType.name_repeat;
		}
		boolean external = network.getExternalNetworks();
		if (!external) {//私网校验
			String routeId = network.getRouteId();
			if (null != routeId) {//如果关联了路由器，则判断关联的路由器是否存在以及状态是否正常
				Route route = routeBiz.findById(Route.class, routeId);
				if (null == route) {
					return ResultType.route_not_exist;
				}
				if (!BaseRestControl.checkStatus(route.getStatus())) {
					return ResultType.parent_status_exception;
				}
				if (!network.getTenantId().equals(route.getTenantId())) {
					return ResultType.tenant_inconformity;
				}
			}
			ResultType resultType = quotaUsedBiz.checkQuota(ConfigProperty.NETWORK_QUOTA_CLASSCODE, projectId, 1);
			if (!ResultType.success.equals(resultType)) {//检查租户是否拥有配额以及是否达到最大值
				return resultType;
			}
			if (StrUtils.checkParam(dnsAddress)) {//判断传的dns格式是否正确
				List<String> dnsList = new ArrayList<>();
				String[] addresses = dnsAddress.split(";");
				for (int i = 0; i < addresses.length; i++) {
					String address = addresses[i];
					if (!IpValidator.checkIp(address)) {
						return ResultType.dns_ip_format_error;
					}
					dnsList.add(address);
				}
				network.setDnsList(dnsList);
			}
			if (StrUtils.checkParam(hostRoute)) {//判断传的主机路由格式是否正确
				List<Map<String, String>> hostRouteList = new ArrayList<>();
				String[] hostRoutes = hostRoute.split(";");
				for (int i = 0; i < hostRoutes.length; i++) {
					String hroute = hostRoutes[i];
					String[] cidrIp = hroute.split(",");
					if (cidrIp.length != 2) {
						return ResultType.host_router_format_error;
					}
					Map<String, String> cidrIpMap = new HashMap<>();
					String destination = cidrIp[0];
					if (IpValidator.checkCidr(destination)) {
						cidrIpMap.put("destination", destination);
					}
					String nextHop = cidrIp[1];
					if (!IpValidator.checkIp(nextHop)) {
						return ResultType.host_router_format_error;
					}
					cidrIpMap.put("nexthop", nextHop);
					hostRouteList.add(cidrIpMap);
				}
				network.setHostRouteList(hostRouteList);
			}
		}
		
		Map<String, Object> checkMap = IpValidator.checkIp(gatewayIp, ipPool, cidr, true);
		//验证用户提交的cidr、网关ip、ip段格式是否正确以及是否在可用范围内
		if (StrUtils.checkParam(checkMap.get("error"))) {
			ResultType rs = (ResultType) checkMap.get("error");
			return rs;
		}
		//计算出用户定义的ip段集合
		List<Map<String, String>> ipList = (List<Map<String, String>>) checkMap.get("ipPool");
		String startIp = StrUtils.tranString(checkMap.get("cidrStartIp"));
		String endIp = StrUtils.tranString(checkMap.get("cidrEndIp"));
		
		ResultType checkRs = checkCidr(startIp, endIp, ipList, network.getExternalNetworks(), projectId);
		if (!ResultType.success.equals(checkRs)) {//检查地址是否重叠
			return checkRs;
		}
		
		network.setStartIp(startIp);
		network.setEndIp(endIp);
		network.setIpPoolList(ipList);
		
		if (null == gatewayIp || "".equals(gatewayIp)) {
			gatewayIp = startIp;
		}
		subnet.setGatewayIp(gatewayIp);//如果用户没设置网关ip，则使用默认的网关即首ip
		Map<String, String> validatorMap = ValidatorUtils.validator(network);//验证数据
		if (!validatorMap.isEmpty()) {
			throw MessageException.create(JSONObject.toJSONString(validatorMap), ResultType.parameter_error);
		}
		return ResultType.success;
	}
	
	/**
	 * 视图整体保存时的调用（先保存本地）
	 */
	@Override
	public void vdcSave (Network network, String vdcId, String projectId) {
		network.setTenantId(projectId);
		network.createdUser(this.getLoginUser());
		network.setVdcId(vdcId);
		Subnet subnet = new Subnet(network);
		ResultType veRs = verify(network, subnet);//验证数据
		if (!ResultType.success.equals(veRs)) {
			throw new MessageException(veRs);
		}
		localSave(network, subnet, vdcId, ConfigProperty.RESOURCE_OPTION_STATUS_CREATING);//本地保存
	}
	
	/**
	 * 删除当前网络关联的子网、ip池、可用ip段的信息
	 *
	 * @param networkId
	 */
	public void deleteRelateData (String networkId) {
		List<Subnet> subnets = subnetDao.findByPropertyName(Subnet.class, "networkId", networkId);
		if (null != subnets && subnets.size() > 0) {
			Subnet subnet = subnets.get(0);
			String subnetId = subnet.getId();
			List<IpAllocationPool> ipPools = ipPoolsDao.findByPropertyName(IpAllocationPool.class, "subnetId",
                    subnetId);
			if (null != ipPools && ipPools.size() > 0) {
				IpAllocationPool ipPool = ipPools.get(0);
				String ipPoolId = ipPool.getId();
				List<IpAvailabilityRange> ipAvaiRanges = ipAvailDao.findByPropertyName(IpAvailabilityRange.class,
                        "allocationPoolId", ipPoolId);
				if (null != ipAvaiRanges && ipAvaiRanges.size() > 0) {
					for (IpAvailabilityRange ipAvaiRange : ipAvaiRanges) {
						ipAvailDao.delete(ipAvaiRange);//删除子网下的可用ip段数据
					}
				}
				ipPoolsDao.delete(ipPool);//删除子网下的ip池数据
			}
			List<Sub2Dns> sub2Dnses = sub2DnsDao.findByPropertyName(Sub2Dns.class, "subnetId", subnetId);
			if (StrUtils.checkParam(sub2Dnses)) {
				for (Sub2Dns sub2Dnse : sub2Dnses) {
					sub2DnsDao.delete(sub2Dnse);
				}
			}
			List<Sub2Route> sub2Routes = sub2RouteDao.findByPropertyName(Sub2Route.class, "subnetId", subnetId);
			if (StrUtils.checkParam(sub2Routes)) {
				for (Sub2Route sub2Route : sub2Routes) {
					sub2RouteDao.delete(sub2Route);
				}
			}
			subnetDao.delete(subnet);//删除子网数据
		}
	}
	
	/**
	 * 检查cidr与同级网络是否有重叠
	 *
	 * @param cidrStartIp
	 * @param cidrEndIp
	 * @return
	 */
	public ResultType checkCidr (String cidrStartIp, String cidrEndIp, List<Map<String, String>> poolList, boolean
			external, String projectId) {
		Map<String, Object> map = new HashMap<>();
		if (external) {//检查公网
			map.put("externalNetworks", Boolean.TRUE);
		} else {//检查私网
			List<Project2Network> project2Networks = p2nDao.findByPropertyName(Project2Network.class, "tenantId",
					projectId);
			if (!StrUtils.checkParam(project2Networks)) {//判断cidr是否在配额范围内
				return ResultType.tenant_quota_network_not_exist;
			}
			boolean isRange = false;
			for (Project2Network project2Network : project2Networks) {
				List<Network2Subnet> network2Subnets = n2sDao.findByPropertyName(Network2Subnet.class, "networkId",
						project2Network.getId());
				if (!StrUtils.checkParam(network2Subnets)) {
					return ResultType.tenant_quota_network_not_exist;
				}
				for (Network2Subnet network2Subnet : network2Subnets) {
					String pcStartIp = network2Subnet.getStartIp();//首ip
					String pcEndIp = network2Subnet.getEndIp();//末ip
					if (StrUtils.checkParam(poolList)) {
						if (checkPoolListInRange(pcStartIp, pcEndIp, poolList)) {
							isRange = true;
							break;
						}
					}
				}
			}
			if (!isRange) {
				return ResultType.cidr_notin_range_or_not_contain_quota;
			}
			map.put("externalNetworks", Boolean.FALSE);
			if (!singleton.isIpRepeat()) {//租户内不能重叠
				map.put("tenantId", projectId);
			}
		}
		List<Network> networks = networkDao.listByClass(Network.class, map);
		List<Subnet> subnets = new ArrayList<>();
		if (StrUtils.checkCollection(networks)) {
			for (Network network : networks) {
				Subnet subnet = subnetDao.findById(Subnet.class, network.getSubnetId());
				subnets.add(subnet);
			}
		}
		if (null != subnets && subnets.size() > 0) {
			Map<String, String> ipMap = new HashMap<>();
			ipMap.put("start", cidrStartIp);
			ipMap.put("end", cidrEndIp);
			for (Subnet subnet : subnets) {//分开校验防止网络校验变为全部不重叠时不同租户还存在网段重叠的网络
				List<Map<String, String>> ipList = new ArrayList<>();
				ipList.add(ipMap);
				ipList.addAll(getPoolList(subnet));
				if (ipList.size() > 1 && !IpValidator.checkIpPoolRepeat(ipList)) {//校验是否与之前的网络有重叠
					return ResultType.cidr_contain_repeat;
				}
			}
		}
		return ResultType.success;
	}
	
	public boolean checkPoolListInRange (String pcStartIp, String pcEndIp, List<Map<String, String>> poolList) {
		for (Map<String, String> poolMap : poolList) {
			String startIp = poolMap.get("start");//首ip
			String endIp = poolMap.get("end");//末ip
			boolean a = IpValidator.checkIpScope(startIp, pcStartIp, pcEndIp);
			boolean b = IpValidator.checkIpScope(endIp, pcStartIp, pcEndIp);
			if (a && b) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public List<TenantNetworkBean> getTenantNetwork (String id) {
		CloudosClient client = getSessionBean().getCloudClient();
		CloudosNetwork cloudosNetwork = new CloudosNetwork(client);
		Map<String, Object> params = new HashMap<>();
		params.put("externalNetworks", false);
		params.put("tenantId", id);
		List<Network> networks = networkDao.listByClass(Network.class, params);
		List<TenantNetworkBean> beans = new ArrayList<>();
		for (Network network : networks) {
			TenantNetworkBean bean = new TenantNetworkBean();
			if (cloudosNetwork.isExist(network.getCloudosId())) {
				bean.setCidr(network.getCidr());
				bean.setId(network.getCloudosId());//网络id
				bean.setName(network.getName());
				beans.add(bean);
			}
		}
		return beans;
	}
	
	/**
	 * 保存主机路由和dns信息
	 *
	 * @param dnsList
	 * @param hostRouteList
	 * @param subnetId
	 */
	public void addDnsAndRoute (List<String> dnsList, List<Map<String, String>> hostRouteList, String subnetId) {
		if (StrUtils.checkParam(dnsList)) {
			for (int i = 0; i < dnsList.size(); i++) {
				String address = dnsList.get(i);
				Sub2Dns sub2Dns = new Sub2Dns();
				sub2Dns.setAddress(address);
				sub2Dns.setSubnetId(subnetId);
				sub2DnsDao.add(sub2Dns);
			}
		}
		if (StrUtils.checkParam(hostRouteList)) {
			for (int i = 0; i < hostRouteList.size(); i++) {
				Map<String, String> hostRouteMap = hostRouteList.get(i);
				String destination = hostRouteMap.get("destination");
				String nextHop = hostRouteMap.get("nexthop");
				Sub2Route sub2Route = new Sub2Route();
				sub2Route.setDestination(destination);
				sub2Route.setNextHop(nextHop);
				sub2Route.setSubnetId(subnetId);
				sub2RouteDao.add(sub2Route);
			}
		}
	}
	
	@Override
	public void updateStatus (String id, String status) {
		Network network = networkDao.findById(Network.class, id);
		network.setStatus(status);
		networkDao.update(network);
		Map<String, String> queryMap = new HashMap<>();
		queryMap.put("subnetId", network.getSubnetId());
		queryMap.put("ipAddress", network.getGatewayIp());
		if (StrUtils.checkParam(network.getRouteId())) {
			List<IpAllocation> ipUseds = ipUsedDao.findByMap(IpAllocation.class, queryMap);
			if (StrUtils.checkParam(ipUseds)) {
				IpAllocation ipUsed = ipUseds.get(0);
				Port port = portBiz.findById(Port.class, ipUsed.getPortId());
				port.setStatus(status);
				portBiz.update(port);
			}
		}
		VdcItem vdcItem = vdcItemDao.findById(VdcItem.class, id);
		if (StrUtils.checkParam(vdcItem)) {
			vdcItem.setStatus(status);
			vdcItemDao.update(vdcItem);
		}
	}
	
	@Override
	public List<Map<String, String>> getPoolList (List<Subnet> subnets, List<Map<String, String>> ipList) {
		for (Subnet subnet : subnets) {
			ipList.addAll(getPoolList(subnet));
		}
		return ipList;
	}
	
	private List<Map<String, String>> getPoolList (Subnet subnet) {
		List<Map<String, String>> ipList = new ArrayList<>();
		List<IpAllocationPool> ipAllocationPools = ipPoolsDao.findByPropertyName(IpAllocationPool.class,
				"subnetId", subnet.getId());
		if (null != ipAllocationPools && ipAllocationPools.size() > 0) {
			for (IpAllocationPool ipAllocationPool : ipAllocationPools) {//遍历网络下的ip池
				List<IpAvailabilityRange> ipRanges = ipAvailDao.findByPropertyName(IpAvailabilityRange.class,
						"allocationPoolId", ipAllocationPool.getId());
				if (StrUtils.checkParam(ipRanges)) {//判断该池下是否有用户自定义的允许使用ip段
					for (IpAvailabilityRange ipRange : ipRanges) {//有自定义ip段时，与自定义ip段比较是否重叠
						Map<String, String> ipRangeMap = new HashMap<>();
						ipRangeMap.put("start", ipRange.getFirstIp());
						ipRangeMap.put("end", ipRange.getLastIp());
						ipList.add(ipRangeMap);
					}
				} else {//没有自定义ip段时，与ip池比较是否重叠
					Map<String, String> ipPoolMap = new HashMap<>();
					ipPoolMap.put("start", ipAllocationPool.getFirstIp());
					ipPoolMap.put("end", ipAllocationPool.getLastIp());
					ipList.add(ipPoolMap);
				}
			}
		}
		return ipList;
	}
	
	@Override
	public JSONArray getNetworkArray (CloudosClient client) {
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_NETWORK);
		JSONArray jsonArray = HttpUtils.getArray(uri, "networks", null, client);
		return jsonArray;
	}
	
	@Override
	public JSONObject getNetworkJson (String cloudosId, CloudosClient client) {
		if (!StrUtils.checkParam(cloudosId)) {
			return null;
		}
		String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_NETWORK_ACTION), cloudosId);
		JSONObject json = HttpUtils.getJson(uri, "network", client);
		return json;
	}
	
	@Override
	public JSONObject getSubnetJson (String cloudosId, CloudosClient client) {
		if (!StrUtils.checkParam(cloudosId)) {
			return null;
		}
		String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SUBNET_ACTION), cloudosId);
		JSONObject json = HttpUtils.getJson(uri, "subnet", client);
		return json;
	}
	
	@Override
	public Network getNetworkByJson (JSONObject ntJson) {
		Network network = new Network();
		String ntCdId = ntJson.getString("id");
		String ntName = ntJson.getString("name");
		Boolean stateUp = ntJson.getBoolean("admin_states_up");
		Boolean shared = ntJson.getBoolean("shared");
		String tenantId = ntJson.getString("tenant_id");
		String status = ntJson.getString("status");
		Integer mtu = ntJson.getInteger("mtu");
		Boolean external = ntJson.getBoolean("router:external");
		network.setCloudosId(ntCdId);
		network.setStatus(status);
		network.setTenantId(tenantId);
		network.setName(ntName);
		network.setAdminStateUp(stateUp);
		network.setExternalNetworks(external);
		network.setShared(shared);
		network.setMtu(mtu);
		return network;
	}
	
	@Override
	public Subnet getSubnetByJson (JSONObject sbJson) {
		Subnet subnet = new Subnet();
		String sbCdId = sbJson.getString("id");
		String sbName = sbJson.getString("name");
		Boolean enableDhcp = sbJson.getBoolean("enable_dhcp");
		String tenantId = sbJson.getString("tenant_id");
		Integer ipVersion = sbJson.getInteger("ip_version");
		String cidr = sbJson.getString("cidr");
		String gatewayIp = sbJson.getString("gateway_ip");
		subnet.setTenantId(tenantId);
		subnet.setName(sbName);
		subnet.setCloudosId(sbCdId);
		subnet.setCidr(cidr);
		subnet.setEnableDhcp(enableDhcp);
		subnet.setGatewayIp(gatewayIp);
		subnet.setIpVersion(ipVersion);
		subnet.createdUser(ConfigProperty.SYSTEM_FLAG);
		return subnet;
	}
	
	@Override
	public void addNetworkParam (Network network, JSONObject sbJson) {
		String cidr = sbJson.getString("cidr");
		network.setStartIp(IpValidator.getStartIpByCidr(cidr));
		network.setEndIp(IpValidator.getEndIpByCidr(cidr, true));
		
		JSONArray availArray = sbJson.getJSONArray("allocation_pools");
		if (StrUtils.checkParam(availArray)) {
			List<Map<String, String>> poolList = new ArrayList<>();
			for (int i = 0; i < availArray.size(); i++) {
				Map<String, String> poolMap = new HashMap<>();
				poolMap.put("start", availArray.getJSONObject(i).getString("start"));
				poolMap.put("end", availArray.getJSONObject(i).getString("end"));
				poolList.add(poolMap);
			}
			network.setIpPoolList(poolList);
		}
		
		JSONArray dnsArray = sbJson.getJSONArray("dns_nameservers");
		if (StrUtils.checkParam(dnsArray)) {
			List<String> dnsList = new ArrayList<>();
			for (int i = 0; i < dnsArray.size(); i++) {
				dnsList.add(dnsArray.getString(i));
			}
			network.setDnsList(dnsList);
		}
		
		JSONArray routeArray = sbJson.getJSONArray("host_routes");
		if (StrUtils.checkParam(routeArray)) {
			List<Map<String, String>> routeList = new ArrayList<>();
			for (int i = 0; i < routeArray.size(); i++) {
				Map<String, String> routeMap = new HashMap<>();
				routeMap.put("destination", routeArray.getJSONObject(i).getString("destination"));
				routeMap.put("nexthop", routeArray.getJSONObject(i).getString("nexthop"));
				routeList.add(routeMap);
			}
			network.setHostRouteList(routeList);
		}
	}
	
	@Override
	public void backWrite (Network network, Subnet subnet, CloudosClient client) {
		if (StrUtils.checkParam(network.getRouteId())) {
			Map<String, String> queryMap = new HashMap<>();
			queryMap.put("subnetId", subnet.getId());
			queryMap.put("ipAddress", subnet.getGatewayIp());
			if (StrUtils.checkParam(network.getRouteId())) {
				List<IpAllocation> ipUseds = ipUsedDao.findByMap(IpAllocation.class, queryMap);
				if (StrUtils.checkParam(ipUseds)) {
					IpAllocation ipUsed = ipUseds.get(0);
					Port port = portBiz.findById(Port.class, ipUsed.getPortId());
					port.setCloudosId(network.getPortCloudId());
					port.setStatus(network.getStatus());
					JSONObject portJson = portBiz.getPortJson(network.getPortCloudId(), client);
					String mac = portJson.getString("mac_address");
					String ptName = portJson.getString("name");
					Boolean stateUp = portJson.getBoolean("admin_state_up");
					if (StrUtils.checkParam(ptName)) {
						port.setName(ptName);
					}
					port.setMacAddress(mac);
					port.setAdminStateUp(stateUp);
					portBiz.update(port);
				}
			}
		}
		networkDao.update(network);
		subnetDao.update(subnet);
		VdcItem vdcItem = vdcItemDao.findById(VdcItem.class, network.getId());
		vdcItem.setStatus(network.getStatus());
		vdcItemDao.update(vdcItem);
	}
	
	@Override
	public ResultType checkNetworkConflict (String publicSubnetId, List<Subnet> privateSubnets) {
		if (StrUtils.checkCollection(privateSubnets) && StrUtils.checkParam(publicSubnetId)) {
			Subnet subnet = subnetDao.findById(Subnet.class, publicSubnetId);
			List<Map<String, String>> ipList = new ArrayList<>();
			privateSubnets.add(subnet);
			ipList = this.getPoolList(privateSubnets, ipList);
			if (ipList.size() > 1){
				if (!IpValidator.checkIpPoolRepeat(ipList)){
					return ResultType.conflict_with_private_network;
				}
			}
		}
		return ResultType.success;
	}
	
	public String getNetworkIdByIp(String ip, String tenantId) {
		List<Network> networks = networkDao.findByPropertyName(Network.class, "tenantId", tenantId);
		if (!StrUtils.checkCollection(networks)) {
			return null;
		}
		for (Network network : networks) {
			if (network.getExternalNetworks()) {
				continue;
			}
			List<IpAllocationPool> ipPools = ipPoolsDao.findByPropertyName(IpAllocationPool.class, "subnetId",
					network.getSubnetId());
			for (IpAllocationPool ipPool : ipPools) {
				List<IpAvailabilityRange> ipRanges = ipAvailDao.findByPropertyName(IpAvailabilityRange.class, "allocationPoolId",
						ipPool.getId());
				if (StrUtils.checkCollection(ipRanges)) {
					for (IpAvailabilityRange ipRange : ipRanges) {
						String startIp = ipRange.getFirstIp();
						String endIp = ipRange.getLastIp();
						boolean isContain = IpValidator.checkIpScope(ip, startIp, endIp);
						if (isContain) {
							return network.getId();
						}
					}
				} else {
					String startIp = ipPool.getFirstIp();
					String endIp = ipPool.getLastIp();
					boolean isContain = IpValidator.checkIpScope(ip, startIp, endIp);
					if (isContain) {
						return network.getId();
					}
				}
			}
		}
		return null;
	}
}
