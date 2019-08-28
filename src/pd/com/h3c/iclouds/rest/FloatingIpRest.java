package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.FloatingIpBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.FloatingIp;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.PatternCheckUtils;
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
 * @author zKF7420
 * @date 2017年1月7日 上午9:41:10
 */

@Api(value = "公网ip")
@RestController
@RequestMapping("/floatingIp")
public class FloatingIpRest extends BaseRest<FloatingIp> {
	
	@Resource
	private FloatingIpBiz floatingIpBiz;
	
	@Resource
	private PortBiz portBiz;
	
	@Resource
	private UserBiz userBiz;
	
	@Resource
	private ProjectBiz projectBiz;
	
	@Override
	@ApiOperation(value = "获取公网ip分页列表", response = FloatingIp.class)
	@RequestMapping(value = "/pageList", method = RequestMethod.GET)
	public Object list () {
		PageEntity entity = this.beforeList();
		PageModel<FloatingIp> pageModel = floatingIpBiz.findForPage(entity);
		floatingIpBiz.addResource(pageModel.getDatas());
		PageList<FloatingIp> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@Override
	@ApiOperation(value = "获取公网ip详情", response = FloatingIp.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get (@PathVariable String id) {
		FloatingIp ip = floatingIpBiz.findById(FloatingIp.class, id);
		if (null != ip) {
			List<FloatingIp> list = new ArrayList<>();
			list.add(ip);
			floatingIpBiz.addResource(list);
			return BaseRestControl.tranReturnValue(ip);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@Override
	@ApiOperation(value = "删除公网ip", response = FloatingIp.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete (@PathVariable String id) {
		request.setAttribute("id", id);
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		try {
			FloatingIp floatingIp = floatingIpBiz.findById(FloatingIp.class, id);
			if (!StrUtils.checkParam(floatingIp)) {
				return BaseRestControl.tranReturnValue(ResultType.deleted);
			}
			request.setAttribute("name", floatingIp.getName());
			if (!projectBiz.checkOptionRole(floatingIp.getTenantId(), floatingIp.getOwner())) {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
			}
			this.warn("Delete floatingIp, floatingIpId:" + id);
			return floatingIpBiz.delete(floatingIp, client);
		} catch (Exception e) {
			this.exception(FloatingIp.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@Override
	@ApiOperation(value = "新建公网ip", response = FloatingIp.class)
	@RequestMapping(method = RequestMethod.POST)
	public Object save (@RequestBody FloatingIp entity) {
		if(!this.getSessionBean().getSelfAllowed()) {
			return BaseRestControl.tranReturnValue(ResultType.project_cannot_create_resource);
		}
		return save(entity, this.getProjectId(), this.getLoginUser());
	}
	
	@ApiOperation(value = "新建公网ip", response = FloatingIp.class)
	@RequestMapping(value = "/admin/save/{userId}", method = RequestMethod.POST)
	public Object save (@PathVariable String userId, @RequestBody FloatingIp entity) {
		User user = userBiz.findById(User.class, userId);
		Map<String, Object> check = this.adminSave(entity.getTenantId(), user);
		if (null != check) {
			return check;
		}
		return this.save(entity, user.getProjectId(), userId);
	}
	
	@Override
	@ApiOperation(value = "修改公网ip", response = FloatingIp.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update (@PathVariable String id, @RequestBody FloatingIp entity) throws IOException {
		request.setAttribute("id", id);
		request.setAttribute("name", entity.getName());
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		FloatingIp floatingIp = floatingIpBiz.findById(FloatingIp.class, id);
		if (null == floatingIp) {
			return BaseRestControl.tranReturnValue(ResultType.floating_ip_not_exist);
		}
		if (!projectBiz.checkOptionRole(floatingIp.getTenantId(), floatingIp.getOwner())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		if (!StrUtils.checkParam(entity.getNorm())) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		if (floatingIp.getNorm().equals(entity.getNorm())) {
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		floatingIp.setNorm(entity.getNorm());
		if (!StrUtils.checkParam(floatingIpBiz.getFloatingIpJson(floatingIp.getCloudosId(), client))) {
			return BaseRestControl.tranReturnValue(ResultType.floatingIp_not_exist_in_cloudos);
		}
		if (StrUtils.checkParam(floatingIp.getFixedPortId())) {
			return BaseRestControl.tranReturnValue(ResultType.floating_ip_allocated_to_resource);
		}
		try {
			this.warn("Update floatingip:" + id + ". Parameter: [" + JSONObject.toJSONString(entity) + "].");
			return floatingIpBiz.update(floatingIp, client);
		} catch (Exception e) {
			this.exception(FloatingIp.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "回收公网IP", response = FloatingIp.class)
	@RequestMapping(value = "/unbind/{id}", method = RequestMethod.PUT)
	public Object unbindIp (@PathVariable String id) {
		this.request.setAttribute("id", id);
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		FloatingIp floatingIp = floatingIpBiz.findById(FloatingIp.class, id);
		if (!StrUtils.checkParam(floatingIp)) {
			return BaseRestControl.tranReturnValue(ResultType.floating_ip_not_exist);
		}
		request.setAttribute("name", floatingIp.getName());
		if (!projectBiz.checkOptionRole(floatingIp.getTenantId(), floatingIp.getOwner())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		if (!StrUtils.checkParam(floatingIpBiz.getFloatingIpJson(floatingIp.getCloudosId(), client))) {
			return BaseRestControl.tranReturnValue(ResultType.floatingIp_not_exist_in_cloudos);
		}
		try {
			floatingIp.setFixedPortId(null);
			floatingIp.setLastRouterId(floatingIp.getRouterId());
			this.warn("unbind floatingip:" + id + ". Parameter: [" + JSONObject.toJSONString(floatingIp) + "].");
			return floatingIpBiz.update(floatingIp, client);
		} catch (Exception e) {
			floatingIp = floatingIpBiz.findById(FloatingIp.class, id);
			floatingIpBiz.cloudUpdate(floatingIp, client);
			this.exception(FloatingIp.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "绑定公网IP", response = FloatingIp.class)
	@RequestMapping(value = "/{id}/{portId}", method = RequestMethod.PUT)
	public Object bindIp (@PathVariable String id, @PathVariable String portId) {
		this.request.setAttribute("id", id);
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {//检查连接
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		FloatingIp floatingIp = floatingIpBiz.findById(FloatingIp.class, id);
		if (!StrUtils.checkParam(floatingIp)) {//校验公网是否存在
			return BaseRestControl.tranReturnValue(ResultType.floating_ip_not_exist);
		}
		request.setAttribute("name", floatingIp.getName());
		if (!projectBiz.checkOptionRole(floatingIp.getTenantId(), floatingIp.getOwner())) {//校验操作权限
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		Port port = portBiz.findById(Port.class, portId);
		if (!StrUtils.checkParam(port)) {//校验分配资源的网卡是否存在
			return BaseRestControl.tranReturnValue(ResultType.port_not_exist);
		}
		if ((!port.getDeviceOwner().contains("compute") && !"neutron:LOADBALANCER".equals(port.getDeviceOwner()))) {
			return BaseRestControl.tranReturnValue(ResultType.port_type_error);//检查网卡是否是属于云主机或者负载均衡
		}
		if (port.getDeviceOwner().contains("compute") && !port.getUserId().equals(floatingIp.getOwner())) {
			return BaseRestControl.tranReturnValue(ResultType.user_inconformity);//云主机判断是否是该用户下
		} else {//负载均衡判断是否是该租户下
			if (!port.getTenantId().equals(floatingIp.getTenantId())) {
				return BaseRestControl.tranReturnValue(ResultType.tenant_inconformity);
			}
		}
		if (!StrUtils.checkParam(floatingIpBiz.getFloatingIpJson(floatingIp.getCloudosId(), client))) {
			return BaseRestControl.tranReturnValue(ResultType.floatingIp_not_exist_in_cloudos);
		}
		if (!StrUtils.checkParam(portBiz.getPortJson(port.getCloudosId(), client))) {
			return BaseRestControl.tranReturnValue(ResultType.port_not_exist_in_cloudos);
		}
		ResultType checkResult = floatingIpBiz.checkBind(floatingIp, port);
		if (!ResultType.success.equals(checkResult)) {
			return BaseRestControl.tranReturnValue(checkResult);
		}
		try {
			floatingIp.setFixedPortId(portId);
			this.warn("bind floatingip:" + id + ". Parameter: [" + JSONObject.toJSONString(floatingIp) + "].");
			return floatingIpBiz.update(floatingIp, client);
		} catch (Exception e) {
			floatingIp = floatingIpBiz.findById(FloatingIp.class, id);
			floatingIpBiz.cloudUpdate(floatingIp, client);
			this.exception(FloatingIp.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "获取公网IP允许绑定的云主机网卡列表，用于floatingIp邦定主机")
	@RequestMapping(value = "/{id}/novavm", method = RequestMethod.GET)
	public Object novavmList (@PathVariable String id) {
		FloatingIp floatingIp = floatingIpBiz.findById(FloatingIp.class, id);
		if (!StrUtils.checkParam(floatingIp)) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		PageEntity entity = this.beforeList();
		List<String> portIds = floatingIpBiz.getPortIds(floatingIp.getNetworkId());
		if (StrUtils.checkCollection(portIds)) {
			entity.setSpecialParams(portIds.toArray(new String[portIds.size()]));
		} else {
			entity.setSpecialParams(new String[] {"null"});
		}
		Map<String, Object> queryMap = entity.getQueryMap();
		queryMap.put("deviceOwner", "compute");
		queryMap.put("userId", floatingIp.getOwner());
		PageModel<Port> pageModel = portBiz.findForPage(entity);
		PageList<Port> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "获取公网IP允许绑定的监听器列表，用于floatingIp邦定到监听器", response = FloatingIp.class)
	@RequestMapping(value = "/{id}/vlbPool", method = RequestMethod.GET)
	public Object vlbPoolList (@PathVariable String id) {
		FloatingIp floatingIp = floatingIpBiz.findById(FloatingIp.class, id);
		if (!StrUtils.checkParam(floatingIp)) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		PageEntity entity = this.beforeList();
		List<String> portIds = floatingIpBiz.getPortIds(floatingIp.getNetworkId());
		if (StrUtils.checkCollection(portIds)) {
			entity.setSpecialParams(portIds.toArray(new String[portIds.size()]));
		} else {
			entity.setSpecialParams(new String[] {"null"});
		}
		entity.setSpecialParam(floatingIp.getTenantId());
		Map<String, Object> queryMap = entity.getQueryMap();
		queryMap.put("deviceOwner", "neutron:LOADBALANCER");
		PageModel<Port> pageModel = portBiz.findForPage(entity);
		PageList<Port> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "ip是否被占用-传参{'ip':'ipAddress'}")
	@RequestMapping(value = "/ip", method = RequestMethod.POST)
	public Object ipWasUsed (@RequestBody Map<String, String> map) {
		if (map.containsKey("ip")) {
			String ip = map.get("ip");
			if (null != ip && !"".equals(ip)) {
				if (!PatternCheckUtils.checkIp(ip)) {
					return BaseRestControl.tranReturnValue(ResultType.ip_format_error);
				}
				List<FloatingIp> ips = floatingIpBiz.findByPropertyName(FloatingIp.class, "floatingIp", ip);
				if (null != ips && ips.size() > 0) {
					return BaseRestControl.tranReturnValue(ResultType.ip_was_used);
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}
	
	@ApiOperation(value = "检查名称是否重复")
	@RequestMapping(value = "/check/{name}", method = RequestMethod.GET)
	public Object checkRepeat (@PathVariable String name) {
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@ApiOperation(value = "获取公网总数量和本月新增数量")
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Object count () {
		Map<String, Integer> countMap = new HashMap<>();
		int totalCount = floatingIpBiz.count(FloatingIp.class, new HashMap<>());
		countMap.put("total", totalCount);
		int allotionCount = floatingIpBiz.allotionCount();
		countMap.put("allocation", allotionCount);
		return BaseRestControl.tranReturnValue(countMap);
	}
	
	private Object save(FloatingIp entity, String projectId, String userId) {
		request.setAttribute("name", entity.getName());
		entity.setTenantId(projectId);
		entity.setOwner(userId);
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {//检查cloudos连接
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		ResultType rs = floatingIpBiz.verify(entity, client);//检验参数
		if (!ResultType.success.equals(rs)) {
			return BaseRestControl.tranReturnValue(rs);
		}
		String result = floatingIpBiz.cloudSave(entity, client, null);//cloudos保存
		if (!"success".equals(result)) {
			return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, result);
		}
		try {
			//本地保存
			floatingIpBiz.localSave(entity);//本地保存
			request.setAttribute("id", entity.getId());
			this.warn("Save floatingip, Parameter: [" + JSONObject.toJSONString(entity) + "].");
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {//回删cloudos
			floatingIpBiz.cloudDelete(entity.getCloudosId(), client);
			this.exception(FloatingIp.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "公网IP数量")
    @RequestMapping(value = "/{id}/count", method = RequestMethod.GET)
    public Object countByUser(@PathVariable String id) {
		int count = 0;
		if(this.getSessionBean().getSuperUser()){
			count = floatingIpBiz.count(FloatingIp.class, null);
		}else if(this.getSessionBean().getSuperRole()){
			count = floatingIpBiz.count(FloatingIp.class, StrUtils.createMap("tenantId",this.getProjectId()));
		}else {
			count=floatingIpBiz.count(FloatingIp.class, StrUtils.createMap("owner",id));
		}
        return BaseRestControl.tranReturnValue(count);
    }
}
