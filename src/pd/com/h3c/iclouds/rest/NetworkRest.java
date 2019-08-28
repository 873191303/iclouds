package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.FloatingIpBiz;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.Project2NetworkBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.VdcBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.RouteDao;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.FloatingIp;
import com.h3c.iclouds.po.GwRoute;
import com.h3c.iclouds.po.IpAllocation;
import com.h3c.iclouds.po.IpAllocationPool;
import com.h3c.iclouds.po.IpAvailabilityRange;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Project2Network;
import com.h3c.iclouds.po.Route;
import com.h3c.iclouds.po.Sub2Dns;
import com.h3c.iclouds.po.Sub2Route;
import com.h3c.iclouds.po.Subnet;
import com.h3c.iclouds.po.VlbVip;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Api(value = "云管理网络")
@RestController
@RequestMapping("/network")
public class NetworkRest extends BaseRest<Network> {

    @Resource
    private NetworkBiz networkBiz;

    @Resource(name = "baseDAO")
    private BaseDAO<IpAllocation> ipUsedDao;

    @Resource(name = "baseDAO")
    private BaseDAO<Subnet> subnetDao;

    @Resource(name = "baseDAO")
    private BaseDAO<Sub2Dns> sub2DnsDao;

    @Resource(name = "baseDAO")
    private BaseDAO<Sub2Route> sub2RouteDao;

    @Resource(name = "baseDAO")
    private BaseDAO<IpAllocationPool> ipAllDao;

    @Resource(name = "baseDAO")
    private BaseDAO<IpAvailabilityRange> ipRangeDao;

    @Resource(name = "baseDAO")
    private BaseDAO<VlbVip> vipDao;

    @Resource
    private PortBiz portBiz;

    @Resource
    private VdcBiz vdcBiz;
    
    @Resource
    private RouteDao routeDao;

    @Resource
    private ProjectBiz projectBiz;
    
    @Resource
    private SqlQueryBiz sqlQueryBiz;
    
    @Resource
    private FloatingIpBiz floatingIpBiz;
    
    @Resource
    private Project2NetworkBiz project2NetworkBiz;
    
    @Override
    @ApiOperation(value = "获取云管理网络分页列表", response = Network.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list() {
        PageEntity entity = this.beforeList();
        PageModel<Network> pageModel = networkBiz.findForPage(entity);
        PageList<Network> page = new PageList<>(pageModel, entity.getsEcho());
        List<Network> networks = page.getAaData();
        addParams(networks);
        return BaseRestControl.tranReturnValue(page);
    }
    
    @ApiOperation(value = "获取公网分页列表", response = Network.class)
    @RequestMapping(value = "/public/pageList", method = RequestMethod.GET)
    public Object publicPageList() {
        PageEntity entity = this.beforeList();
        entity.setSpecialParam("public");
        PageModel<Network> pageModel = networkBiz.findForPage(entity); // 获取公网分页列表
        PageList<Network> page = new PageList<>(pageModel, entity.getsEcho());
        List<Network> networks = page.getAaData();
        publicAddParams(networks);
        return BaseRestControl.tranReturnValue(page);
    }
    
    @ApiOperation(value = "获取公网列表", response = Network.class)
    @RequestMapping(value = "/public", method = RequestMethod.GET)
    public Object publicList() {
        Map<String, Object> map = new HashMap<>();
        map.put("externalNetworks", Boolean.TRUE);
        List<Network> networks = networkBiz.listByClass(Network.class, map);
        publicAddParams(networks);
        return BaseRestControl.tranReturnValue(networks);
    }
    
    @ApiOperation(value = "获取当前租户下所有网络列表", response = Network.class)
    @RequestMapping(value = "/currentproject", method = RequestMethod.GET)
    public Object currentList() {
        Map<String, Object> queryMap = new HashMap<>();
        String tenantId = request.getParameter("projectId");
        String platForm = request.getParameter("platForm");
        if (this.getSessionBean().getSuperUser()) {//管理员获取租户的列表，非管理员只能获取自己的列表
            if (!StrUtils.checkParam(tenantId)) {
                tenantId = this.getProjectId();
            }
            if (!StrUtils.checkParam(platForm) || !"opsplat".equals(platForm)) {
            }
        } else {
            tenantId = this.getProjectId();
        }
        queryMap.put("tenantId", tenantId);
        queryMap.put("externalNetworks", false);
        List<Network> networks = networkBiz.listByClass(Network.class, queryMap);
        return BaseRestControl.tranReturnValue(networks);
    }
    
    @Override
    @ApiOperation(value = "获取云管理网络详细信息", response = Network.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String id) {
        return get(id, Boolean.FALSE);
    }
    
    @ApiOperation(value = "获取公网详细信息", response = Network.class)
    @RequestMapping(value = "/public/{id}", method = RequestMethod.GET)
    public Object getPublic(@PathVariable String id) {
        return get(id, Boolean.TRUE);
    }

    @Override
    @ApiOperation(value = "删除网络")
    //@Perms(value = "vdc.ope.network.private")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Object delete(@PathVariable String id) {
        return delete(id, Boolean.FALSE);
    }
    
    @ApiOperation(value = "删除公网")
    //@Perms(value = "vdc.ope.network.public")
    @RequestMapping(value = "/public/{id}", method = RequestMethod.DELETE)
    public Object deletePublic(@PathVariable String id) {
        return delete(id, Boolean.TRUE);
    }

    @ApiOperation(value = "保存网络")
    //@Perms(value = "vdc.ope.network.private")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Object save(@RequestBody Network entity) {
        if (!projectBiz.checkRole()) {
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        return save(entity, this.getProjectId(), false);
    }
    
    @ApiOperation(value = "管理员保存租户网络")
    //@Perms(value = "vdc.ope.network.private")
    @RequestMapping(value = "/admin/save/{projectId}", method = RequestMethod.POST)
    public Object save(@PathVariable String projectId, @RequestBody Network entity) {
        if (!this.getSessionBean().getSuperUser()) {
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        return this.save(entity, projectId, false);
    }
    
    @ApiOperation(value = "保存公网")
    //@Perms(value = "vdc.ope.network.public")
    @RequestMapping(value = "/public/save", method = RequestMethod.POST)
    public Object publicSave(@RequestBody Network entity) {
        if (!this.getSessionBean().getSuperUser()){
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        entity.setShared(true);
        entity.setEnableDhcp(false);
        return this.save(entity, this.getProjectId(), true);
    }

    @ApiOperation(value = "绑定路由器")
    //@Perms(value = "vdc.ope.network.private")
    @RequestMapping(value = "/{id}/linkRoute/{routeId}", method = RequestMethod.POST)
    public Object linkRoute(@PathVariable String id, @PathVariable String routeId){
        request.setAttribute("id", id);
        Network network = networkBiz.findById(Network.class, id);
		if (null == network){
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		request.setAttribute("name", network.getName());
		if (!projectBiz.checkOptionRole(network.getTenantId())){
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		if (!vdcBiz.checkLock(network.getTenantId())){
			return BaseRestControl.tranReturnValue(ResultType.vdc_option_lock);
		}
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client){
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        try {
            this.warn("Link route, networkId:" + id + ", routeId:" + routeId);
            return networkBiz.linkRoute(id, routeId, client);
        } catch (Exception e) {
            network = networkBiz.findById(Network.class, id);
            Route route = routeDao.findById(Route.class, routeId);
            networkBiz.cloudLink(network.getSubnetCloudosId(), route.getCloudosId(), client, "unlink");
            networkBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_LINK_FAILURE);
            this.exception(Network.class, e, id);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @ApiOperation(value = "解除绑定路由器")
    //@Perms(value = "vdc.ope.network.private")
    @RequestMapping(value = "/{id}/unlinkRoute", method = RequestMethod.POST)
    public Object unlinkRoute(@PathVariable String id){
        request.setAttribute("id", id);
        Network network = networkBiz.findById(Network.class, id);
		if (null == network){
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		request.setAttribute("name", network.getName());
		if (!projectBiz.checkOptionRole(network.getTenantId())){
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		if (!vdcBiz.checkLock(network.getTenantId())){
			return BaseRestControl.tranReturnValue(ResultType.vdc_option_lock);
		}
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client){
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        try {
            this.warn("Unlink route, networkId:" + id);
	        return networkBiz.unlinkRoute(id, client);
        } catch (Exception e) {
            network = networkBiz.findById(Network.class, id);
            Route route = routeDao.findById(Route.class, network.getRouteId());
            JSONObject linkResponse = networkBiz.cloudLink(network.getSubnetCloudosId(), route.getCloudosId(), client, "link");
            if (ResourceHandle.judgeResponse(linkResponse)){
                String portCloudId = ResourceHandle.getParam(linkResponse, null, "port_id");//获取cloudos的真实id
                Map<String, String> queryMap = new HashMap<>();
                queryMap.put("subnetId", network.getSubnetId());
                queryMap.put("ipAddress", network.getGatewayIp());
                List<IpAllocation> ipUseds = ipUsedDao.findByMap(IpAllocation.class, queryMap);
                if (StrUtils.checkParam(ipUseds)){
                    IpAllocation ipUsed = ipUseds.get(0);
                    Port port = portBiz.findById(Port.class, ipUsed.getPortId());
                    port.setCloudosId(portCloudId);
                    portBiz.update(port);
                }
            }
            networkBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_LINK_FAILURE);
            this.exception(Network.class, e, id);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @ApiOperation(value = "检查名称是否重复")
    @RequestMapping(value = "/check/{name}", method = RequestMethod.GET)
    public Object checkRepeat(@PathVariable String name){
        boolean networkNameRepeat = false;
        String id = request.getParameter("id");//修改时传入一个id则查重时会排除对象本身
        String projectId = request.getParameter("projectId");//超级管理员操作租户资源时传入租户id进行查重
        if (!this.getSessionBean().getSuperUser() || !StrUtils.checkParam(projectId)) {
            projectId = this.getProjectId();
        }
        try {
            Map<String, Object> checkMap = new HashMap<>();
            checkMap.put("name", name);
            checkMap.put("tenantId", projectId);
            networkNameRepeat = networkBiz.checkRepeat(Network.class, checkMap, id);
            if (!networkNameRepeat){//查重(名称)
                return BaseRestControl.tranReturnValue(ResultType.repeat);
            }
            return BaseRestControl.tranReturnValue(ResultType.success);
        } catch (Exception e) {
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }

    @ApiOperation(value = "检查ip是否被使用")
    @RequestMapping(value = "/{subnetId}/check/{ip:.+}", method = RequestMethod.GET)
    public Object checkUsedIp(@PathVariable String subnetId, @PathVariable String ip){
        Subnet subnet = subnetDao.findById(Subnet.class, subnetId);
        if (null == subnet){
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        List<IpAllocation> ipUseds = ipUsedDao.findByPropertyName(IpAllocation.class, "subnetId", subnetId);
        if (StrUtils.checkParam(ipUseds)){
            for (int i = 0; i < ipUseds.size(); i++) {
                IpAllocation ipUsed = ipUseds.get(i);
                if (ip.equals(ipUsed.getIpAddress()) || ip.equals(subnet.getGatewayIp())){
                    return BaseRestControl.tranReturnValue(ResultType.ip_was_used);
                }
            }
        }
        return BaseRestControl.tranReturnValue(ResultType.success);
    }

    @ApiOperation(value = "获取网络下的虚拟网卡列表")
    @RequestMapping(value = "/{networkId}/port", method = RequestMethod.GET)
    public Object portList(@PathVariable String networkId){
        Network network = networkBiz.findById(Network.class, networkId);
        if (null == network){
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        Subnet subnet = subnetDao.findById(Subnet.class, network.getSubnetId());
        List<String> portIds = new ArrayList<>();
        PageEntity entity = this.beforeList();
        Set<IpAllocation> ipuseds = subnet.getIpUseds();
        String gateWayIp = network.getGatewayIp();
        if (StrUtils.checkParam(ipuseds)){
            for (IpAllocation ipUsed : ipuseds) {
                if (!gateWayIp.equals(ipUsed.getIpAddress())){
                    List<VlbVip> vips = vipDao.findByPropertyName(VlbVip.class, "portId", ipUsed.getPortId());
                    if (!StrUtils.checkParam(vips)){
                        portIds.add(ipUsed.getPortId());
                    }
                }
            }
        }
        if (!StrUtils.checkParam(portIds)){
            portIds.add("null");
        }
        entity.setSpecialParams(portIds.toArray(new String[portIds.size()]));
        PageModel<Port> pageModel = portBiz.findForPage(entity);
        PageList<Port> page = new PageList<Port>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }
    
    @Override
    public Object update(@PathVariable String id, @RequestBody Network entity) throws IOException {
        return null;
    }
    
    /**
     * 用于私网添加参数
     * @param networks
     */
    private void addParams(List<Network> networks){
        for (Network network : networks) {
            List<String> ipUsedIps = new ArrayList<>();
            List<IpAllocation> ipAllocationList = ipUsedDao.findByPropertyName(IpAllocation.class, "subnetId", network.getSubnetId());
            if (null != ipAllocationList && ipAllocationList.size() > 0){
                for (IpAllocation ipAllocation : ipAllocationList) {
                    String ip = ipAllocation.getIpAddress();
                    ipUsedIps.add(ip);
                }
                network.setIpUsedIps(ipUsedIps);
            }
            List<Sub2Dns> sub2Dnses = sub2DnsDao.findByPropertyName(Sub2Dns.class, "subnetId", network.getSubnetId());
            if (null != sub2Dnses && sub2Dnses.size() > 0){
                StringBuffer dns = new StringBuffer();
                for (Sub2Dns sub2Dnse : sub2Dnses) {
                    String ip = sub2Dnse.getAddress();
                    dns.append(ip + ";");
                }
                network.setDnsAddress(dns.substring(0, dns.length() - 1));
            }
            List<Sub2Route> sub2Routes = sub2RouteDao.findByPropertyName(Sub2Route.class, "subnetId", network.getSubnetId());
            if (null != sub2Routes && sub2Routes.size() > 0){
                StringBuffer hostRoutes = new StringBuffer();
                for (Sub2Route sub2Route : sub2Routes) {
                    String destination = sub2Route.getDestination();
                    String nextHop = sub2Route.getNextHop();
                    hostRoutes.append(destination + "," + nextHop + ";");
                }
                network.setHostRoute(hostRoutes.substring(0, hostRoutes.length() - 1));
            }
            setIpPool(network);
        }
    }
    
    /**
     * 用于公网添加路由相关参数
     * @param networks
     */
    private void publicAddParams(List<Network> networks) {
    	if (null == networks || networks.size() == 0) {
    		return;
    	}
    	for (int i = 0; i < networks.size(); i++) {
    		Network network = networks.get(i);
    		List<GwRoute> routes = new ArrayList<GwRoute>();
    		// 添加ip范围
    		setIpPool(network);
    		
    		// 添加关联路由列表
    		Map<String, Object> queryMap = new HashMap<String, Object>();
    		queryMap.put("networkId", network.getId());
    		List <Map<String, Object>> result = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_PUBLIC_NETWORK_ROUTE, queryMap);
    		for (Map<String, Object> subMap : result) {
    			String str = JSON.toJSONString(subMap);
    			GwRoute gwRoute = JSON.parseObject(str, GwRoute.class);
    			routes.add(gwRoute);
    		}
    		network.setRoutes(routes);
    		
    		// 添加ip资源类型
    		List<FloatingIp> newIps = new ArrayList<FloatingIp>();
    		newIps.addAll(network.getFloatingIps());
    		floatingIpBiz.addResource(newIps);
    	}
    }
    
    private void setIpPool(Network network) {
        List<IpAllocationPool> ipAlls = ipAllDao.findByPropertyName(IpAllocationPool.class, "subnetId", network.getSubnetId());
        if (StrUtils.checkParam(ipAlls)){
            StringBuffer ipPool = new StringBuffer();
            IpAllocationPool ipAll = ipAlls.get(0);
            List<IpAvailabilityRange> ipRanges = ipRangeDao.findByPropertyName(IpAvailabilityRange.class, "allocationPoolId", ipAll.getId());
            if (StrUtils.checkParam(ipRanges)){
                for (IpAvailabilityRange ipRange : ipRanges) {
                    ipPool.append(ipRange.getFirstIp() + "-" + ipRange.getLastIp() +";");
                }
                ipPool.substring(0, ipPool.length() - 1);
            }else {
                ipPool.append(ipAll.getFirstIp() + "-" + ipAll.getLastIp());
            }
            network.setIpPool(ipPool.toString());
        }
    }
    
    /**
     * 删除网络(私网和公网)
     * @param id
     * @return
     */
    private Object delete(String id, boolean external) {
        request.setAttribute("id", id);
		Network network = networkBiz.findById(Network.class, id);
		if (null == network){
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		request.setAttribute("name", network.getName());
        if (external) {
            if (!this.getSessionBean().getSuperUser()){ // 只有管理员才能删除公网
                return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
            }
        } else {
			if (!projectBiz.checkOptionRole(network.getTenantId())){
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
			}
			if (!vdcBiz.checkLock(network.getTenantId())){
				return BaseRestControl.tranReturnValue(ResultType.vdc_option_lock);
			}
        }
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client){//判断连接是否正常
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        try {
            this.warn("Delete network, networkId:" + id);
            return networkBiz.delete(id, client, external);
        } catch (Exception e) {
            this.exception(Network.class, e, id);
            networkBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_EXCEPTION);
            return BaseRestControl.tranReturnValue(ResultType.failure);
        }
    }
    
    /**
     * 获取网络详细信息(私网和公网)
     * @param id
     * @param external
     * @return
     */
    private Object get(String id, boolean external) {
        Network network = networkBiz.findById(Network.class, id);
        if (null == network) {
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        if (network.getExternalNetworks() != external) {
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }
        if (external) {
            List<Network> networks = new ArrayList<>();
            networks.add(network);
            publicAddParams(networks);
            return BaseRestControl.tranReturnValue(networks.get(0));
        } else {
            if (!this.getSessionBean().getSuperUser() && !this.getProjectId().equals(network.getTenantId())){
                return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
            }
            List<Network> networks = new ArrayList<>();
            networks.add(network);
            addParams(networks);
            return BaseRestControl.tranReturnValue(networks.get(0));
        }
    }
    
    private Object save (Network entity, String projectId, boolean external) {
        request.setAttribute("name", entity.getName());
        CloudosClient client = this.getSessionBean().getCloudClient();
        if (null == client) {
            return BaseRestControl.tranReturnValue(ResultType.system_error);
        }
        String vdcId = vdcBiz.getVdc(projectId).getId();
        if (!external) {
            if (!vdcBiz.checkLock(projectId)){//检查vdc视图锁
                return BaseRestControl.tranReturnValue(ResultType.vdc_option_lock);
            }
            entity.setVdcId(vdcId);
        }
        entity.setTenantId(projectId);
        entity.createdUser(this.getLoginUser());
        Subnet subnet = new Subnet(entity);
        ResultType veRs = networkBiz.verify(entity, subnet);//验证数据
        if (!ResultType.success.equals(veRs)) {
            return BaseRestControl.tranReturnValue(veRs);
        }
        String rs = networkBiz.cloudSave(entity, subnet, client, null); //调用cloudos保存
        if ("success".equals(rs)) {
            try {
                networkBiz.localSave(entity, subnet, vdcId, entity.getStatus());//本地保存
                request.setAttribute("id", entity.getId());
                this.warn("Save network, network:" + entity);
                return BaseRestControl.tranReturnValue(ResultType.success);
            } catch (Exception e) {
                networkBiz.cloudDelete(entity.getCloudosId(), subnet.getCloudosId(), client);
                this.exception(Network.class, e, entity);
                return BaseRestControl.tranReturnValue(ResultType.failure);
            }
        } else {
            return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, rs);
        }
    }
    
    @ApiOperation(value = "网络数量")
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public Object count() {
		int count=0;
		Map<String, Object> map=StrUtils.createMap("externalNetworks", false);
	    if(this.getSessionBean().getSuperUser()){ // 超级管理员显示所有
	    	count=networkBiz.count(Network.class, map);
	    }else{  //非超级管理员显示当前租户拥有
	    	map.put("tenantId",this.getProjectId());
	    	count=networkBiz.count(Network.class, map);
	    }
        return BaseRestControl.tranReturnValue(count);
    }
    
    
    @ApiOperation(value = "获取租户可用的cidr列表")
    @RequestMapping(value = "/cidr/{projectId}", method = RequestMethod.GET)
    public Object cidr(@PathVariable String projectId) {
        List<Project2Network> project2Networks = this.project2NetworkBiz.findByPropertyName(Project2Network.class,
                "tenantId", projectId);
        return BaseRestControl.tranReturnValue(project2Networks);
    }
}
