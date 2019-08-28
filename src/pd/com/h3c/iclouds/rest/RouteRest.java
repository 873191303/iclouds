package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.FirewallBiz;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.RouteBiz;
import com.h3c.iclouds.biz.VdcBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Firewall;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Route;
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

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Api(value = "云管理路由器")
@RestController
@RequestMapping("/route")
public class RouteRest extends BaseRest<Route> {
	
	@Resource
	private RouteBiz routeBiz;
	
	@Resource
	private VdcBiz vdcBiz;
	
	@Resource
	private ProjectBiz projectBiz;
	
	@Resource
	private FirewallBiz firewallBiz;
	
	@Resource
	private NetworkBiz networkBiz;
	
	@Resource
	private PortBiz portBiz;
	
	@Override
	@ApiOperation(value = "获取云管理路由器列表", response = Route.class)
	@RequestMapping(method = RequestMethod.GET)
	//@Perms(value = "vdc.ope.route.simple")
	public Object list () {
		PageEntity entity = this.beforeList();
		PageModel<Route> pageModel = routeBiz.findForPage(entity);
		PageList<Route> page = new PageList<Route>(pageModel, entity.getsEcho());
		List<Route> routes = page.getAaData();
		addParam(routes);
		return BaseRestControl.tranReturnValue(page);
	}
	
	@Override
	@ApiOperation(value = "获取云管理路由器详细信息", response = Route.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	//@Perms(value = "vdc.ope.route.simple")
	public Object get (@PathVariable String id) {
		Route route = routeBiz.findById(Route.class, id);
		if (null != route) {
			if (!projectBiz.checkLookRole(route.getTenantId())) {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
			}
			List<Route> routes = new ArrayList<>();
			routes.add(route);
			addParam(routes);
			return BaseRestControl.tranReturnValue(route);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@Override
	@ApiOperation(value = "删除云管理路由器", response = Route.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	//@Perms(value = "vdc.ope.route")
	public Object delete (@PathVariable String id) {
		request.setAttribute("id", id);
		Route route = routeBiz.findById(Route.class, id);
		if (null == route) {//检查本地是否存在
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		request.setAttribute("name", route.getName());
		if (!projectBiz.checkOptionRole(route.getTenantId())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		if (!vdcBiz.checkLock(route.getTenantId())) {
			return BaseRestControl.tranReturnValue(ResultType.vdc_option_lock);
		}
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		try {
			this.warn("Delete route:" + id);
			return routeBiz.delete(id, client);
		} catch (Exception e) {
			routeBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_EXCEPTION);
			this.exception(Route.class, e, id);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@Override
	@ApiOperation(value = "保存云管理路由器", response = Route.class)
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	//@Perms(value = "vdc.ope.route.simple")
	public Object save (@RequestBody Route entity) {
		if (!projectBiz.checkRole()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		return this.save(entity, this.getProjectId());
	}
	
	@ApiOperation(value = "保存云管理路由器", response = Route.class)
	@RequestMapping(value = "/admin/save/{projectId}", method = RequestMethod.POST)
	public Object save (@PathVariable String projectId, @RequestBody Route entity) {
		if (!this.getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		return this.save(entity, projectId);
	}
	
	@ApiOperation(value = "连接防火墙", response = Route.class)
	@RequestMapping(value = "/{id}/linkFirewall/{firewallId}", method = RequestMethod.POST)
	//@Perms(value = "vdc.ope.route")
	public Object linkFirewall (@PathVariable String id, @PathVariable String firewallId) {
		request.setAttribute("id", id);
		Route route = routeBiz.findById(Route.class, id);
		if (null == route) {//检查本地是否存在
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		request.setAttribute("name", route.getName());
		if (!projectBiz.checkOptionRole(route.getTenantId())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		if (!vdcBiz.checkLock(route.getTenantId())) {
			return BaseRestControl.tranReturnValue(ResultType.vdc_option_lock);
		}
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		try {
			this.warn("Link firewall, routeId:" + id + ", firewallId:" + firewallId);
			return routeBiz.linkFirewall(id, firewallId, client);
		} catch (Exception e) {
			route = routeBiz.findById(Route.class, id);
			Firewall firewall = firewallBiz.findById(Firewall.class, firewallId);
			routeBiz.cloudLink(firewall, route.getCloudosId(), "unlink", client);
			routeBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_LINK_FAILURE);
			this.exception(Route.class, e, id, firewallId);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "断开连接防火墙", response = Route.class)
	@RequestMapping(value = "/{id}/unlinkFirewall", method = RequestMethod.POST)
	//@Perms(value = "vdc.ope.route")
	public Object unlinkFirewall (@PathVariable String id) {
		request.setAttribute("id", id);
		Route route = routeBiz.findById(Route.class, id);
		if (null == route) {//检查本地是否存在
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		request.setAttribute("name", route.getName());
		if (!projectBiz.checkOptionRole(route.getTenantId())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		if (!vdcBiz.checkLock(route.getTenantId())) {
			return BaseRestControl.tranReturnValue(ResultType.vdc_option_lock);
		}
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		try {
			this.warn("Unlink firewall, routeId:" + id);
			return routeBiz.unlinkFirewall(id, client);
		} catch (Exception e) {
			route = routeBiz.findById(Route.class, id);
			Firewall firewall = firewallBiz.findById(Firewall.class, route.getFwId());
			routeBiz.cloudLink(firewall, route.getCloudosId(), "link", client);
			routeBiz.updateStatus(id, ConfigProperty.RESOURCE_OPTION_STATUS_LINK_FAILURE);
			this.exception(Route.class, e, id);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "路由设置网关(用于连接外网)", response = Route.class)
	@RequestMapping(value = "/{id}/setGateway/{networkId}", method = RequestMethod.PUT)
	//@Perms(value = "vdc.ope.route")
	public Object setGateway (@PathVariable String id, @PathVariable String networkId) {
		request.setAttribute("id", id);
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		try {
			this.warn("Set gateway, routeId: " + id + ", networkId: " + networkId + ".");
			return routeBiz.setGateway(id, networkId, client);
		} catch (Exception e) {
			Route route = routeBiz.findById(Route.class, id);
			routeBiz.cloudSetGateway(route.getCloudosId(), null, client);
			this.exception(Network.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "路由清除外部网关")
	@RequestMapping(value = "/{id}/unsetGateway", method = RequestMethod.PUT)
	//@Perms(value = "vdc.ope.route")
	public Object unSetGateway (@PathVariable String id) {
		request.setAttribute("id", id);
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		try {
			this.warn("UnSet gateway, routeId: " + id);
			return routeBiz.unSetGateway(id, client);
		} catch (Exception e) {
			Route route = routeBiz.findById(Route.class, id);
			Port port = portBiz.findById(Port.class, route.getGwPortId());
			Network network = networkBiz.findById(Network.class, port.getNetWorkId());
			routeBiz.cloudSetGateway(route.getCloudosId(), network.getCloudosId(), client);
			routeBiz.localSetGateway(route, network.getId());
			this.exception(Network.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "检查名称是否重复")
	@RequestMapping(value = "/check/{name}", method = RequestMethod.GET)
	//@Perms(value = "vdc.ope.route.simple")
	public Object checkRepeat (@PathVariable String name) {
		boolean routeNameRepeat = false;
		String id = request.getParameter("id");//修改时传入一个id则查重时会排除对象本身
		String projectId = request.getParameter("projectId");//超级管理员操作租户资源时传入租户id进行查重
		if (!this.getSessionBean().getSuperUser() || !StrUtils.checkParam(projectId)) {
			projectId = this.getProjectId();
		}
		try {
			Map<String, Object> checkMap = new HashMap<>();
			checkMap.put("name", name);
			checkMap.put("tenantId", projectId);
			routeNameRepeat = routeBiz.checkRepeat(Route.class, checkMap, id);
			if (!routeNameRepeat) {//查重(名称)
				return BaseRestControl.tranReturnValue(ResultType.repeat);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@Override
	public Object update (@PathVariable String id, @RequestBody Route entity) throws IOException {
		return null;
	}
	
	public void addParam (List<Route> routes) {
		if (StrUtils.checkParam(routes)) {
			for (Route route : routes) {
				List<Network> networks = networkBiz.findByPropertyName(Network.class, "routeId", route.getId());
				if (StrUtils.checkParam(networks)) {
					StringBuffer ipBuffer = new StringBuffer();
					StringBuffer netNameBuffer = new StringBuffer();
					for (Network network : networks) {
						String gateWayIp = network.getGatewayIp();
						ipBuffer.append(gateWayIp + ",");
						netNameBuffer.append(network.getName() + ",");
					}
					if (ipBuffer.length() > 0) {
						ipBuffer.subSequence(0, ipBuffer.length() - 1);
						netNameBuffer.subSequence(0, netNameBuffer.length() - 1);
					}
					route.setPrivateNetworkIp(ipBuffer.toString());
					route.setNetworkName(netNameBuffer.toString());
				}
			}
		}
	}
	
	private Object save (Route entity, String projectId) {
		request.setAttribute("name", entity.getName());
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		if (!vdcBiz.checkLock(projectId)) {//检查vdc视图锁
			return BaseRestControl.tranReturnValue(ResultType.vdc_option_lock);
		}
		entity.setTenantId(projectId);
		entity.createdUser(this.getLoginUser());
		String vdcId = vdcBiz.getVdc(projectId).getId();
		entity.setVdcId(vdcId);
		ResultType veRs = routeBiz.verify(entity, client);//校验参数
		if (!ResultType.success.equals(veRs)) {
			return BaseRestControl.tranReturnValue(veRs);
		}
		String rs = routeBiz.cloudSave(entity, client, null);//cloudos保存
		if ("success".equals(rs)) {
			try {
				routeBiz.localSave(entity, vdcId, entity.getStatus());//本地保存
				request.setAttribute("id", entity.getId());
				this.warn("Save route:" + entity);
				return BaseRestControl.tranReturnValue(ResultType.success);
			} catch (Exception e) {
				if (StrUtils.checkParam(entity.getFwId())) {
					Firewall firewall = firewallBiz.findById(Firewall.class, entity.getFwId());
					routeBiz.cloudLink(firewall, entity.getCloudosId(), "unlink", client);
				}
				routeBiz.cloudDelete(entity.getCloudosId(), client);//回删cloudos资源
				this.exception(Route.class, e, entity);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		} else {
			return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, rs);
		}
	}
	
	@ApiOperation(value = "路由器数量")
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public Object count() {
	   int count=0;
       if(this.getSessionBean().getSuperUser()){ // 超级管理员显示所有
    	   count=routeBiz.count(Route.class, StrUtils.createMap());
       }else{  //非超级管理员显示当前租户拥有
    	   count=routeBiz.count(Route.class, StrUtils.createMap("tenantId",this.getProjectId()));
       }
       return BaseRestControl.tranReturnValue(count);
    }
	
}
