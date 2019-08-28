package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.SecurityGroupBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.PortDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Group2Port;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.SecurityGroup;
import com.h3c.iclouds.po.User;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/28.
 */
@Api(value = "云管理虚拟网卡")
@RestController
@RequestMapping("/port")
public class PortRest extends BaseRest<Port> {

	@Resource
	private PortBiz portBiz;

	@Resource
	private SecurityGroupBiz securityGroupBiz;

	@Resource(name = "baseDAO")
	private BaseDAO<Group2Port> group2PortDao;
	
	@Resource
	private NovaVmBiz novaVmBiz;
	
	@Resource
	private PortDao portDao;
	
	@Resource
	private UserBiz userBiz;
	
	@Override
	@ApiOperation(value = "虚拟网卡列表", response = Port.class)
	@RequestMapping(method = RequestMethod.GET)
	public Object list() {
		List<Port> ports = portBiz.getPorts();
		return BaseRestControl.tranReturnValue(ports);
	}

	@ApiOperation(value = "虚拟网卡分页列表", response = Port.class)
	@RequestMapping(value = "/pageList", method = RequestMethod.GET)
	public Object pageList() {
		PageEntity entity = this.beforeList();
		if (!this.getSessionBean().getSuperUser()) {
			entity.setSpecialParam(this.getProjectId());
		}
		Map<String, Object> queryMap = entity.getQueryMap();
		queryMap.put("flag", "port");
		PageModel<Port> pageModel = portBiz.findForPage(entity);
		PageList<Port> page = new PageList<Port>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "可以绑定到主机的虚拟网卡列表", response = Port.class)
	@RequestMapping(value = "/attaches", method = RequestMethod.GET)
	public Object attachHostUseableList() {
		//当前用户的网卡
		PageEntity entity = this.beforeList();
		Map<String, Object> queryMap = entity.getQueryMap();
		if (this.getSessionBean().getSuperRole()) {//管理员获取租户的列表，非管理员只能获取自己的列表
			String userId = StrUtils.tranString(queryMap.get("userId"));
			if (!StrUtils.checkParam(userId)) {
				queryMap.put("userId", this.getLoginUser());
			} else {
				User user = userBiz.findById(User.class, userId);
				if (!StrUtils.checkParam(user)) {
					return BaseRestControl.tranReturnValue(ResultType.user_not_exist);
				}
				if (!this.getSessionBean().getSuperUser() && !this.getProjectId().equals(user.getProjectId())) {//租户管理员不能操作其它租户的网卡
					return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
				}
			}
		} else {
			queryMap.put("userId", this.getLoginUser());
		}
		queryMap.put("flag", "attachable");
		PageModel<Port> pageModel = portDao.findForPage(entity);
		PageList<Port> page = new PageList<Port>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "主机下的虚拟网卡列表(id - 云主机id)", response = Port.class)
	@RequestMapping(value = "/novavm/{id}", method = RequestMethod.GET)
	public Object novavmPorts(@PathVariable String id) {
		NovaVm host = novaVmBiz.findById(NovaVm.class, id);
		List<Port> ports = portBiz.findByPropertyName(Port.class, "deviceId", host.getUuid());
		List<Object> result = new ArrayList<Object>();
		if (null != ports && ports.size() > 0) {
			for(int i= 0; i < ports.size(); i++) {
				JSONObject sub = this.addSecurityGroupInfo(ports.get(i));
				result.add(sub);
			}
		}
		return BaseRestControl.tranReturnValue(result);
	}

	@ApiOperation(value = "某个租户下的虚拟网卡列表", response = Port.class)
	@RequestMapping(value = "/get/{tenantId}", method = RequestMethod.GET)
	public Object tenantList(@PathVariable String tenantId) {
		List<Port> ports = portBiz.findByPropertyName(Port.class, "tenantId", tenantId);
		return BaseRestControl.tranReturnValue(ports);
	}

	@Override
	@ApiOperation(value = "虚拟网卡详情", response = Port.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get(@PathVariable String id) {
		Port port = portBiz.findById(Port.class, id);
		if (null != port) {
			JSONObject result = this.addSecurityGroupInfo(port);
			String createdDate = result.getString("createdDate");
			if(!"".equals(createdDate) && null != createdDate) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
				String tcreatTime = sdf.format(new Date(Long.parseLong(createdDate)));
				result.remove("createdDate");
				result.put("createdDate", tcreatTime);
			}
			return BaseRestControl.tranReturnValue(result);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@ApiOperation(value = "删除虚拟网卡")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete(@PathVariable String id) {
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		try {
			this.warn("Delete Port:" + id);
			return portBiz.delete(id, client);
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@Override
	@ApiOperation(value = "新建虚拟网卡")
	@RequestMapping(method = RequestMethod.POST)
	public Object save(@RequestBody Port entity) {
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		request.setAttribute("name", entity.getName());
		try {
			entity.setIsinit(false);
			if (!StrUtils.checkParam(entity.getUserId())) {
				entity.setUserId(this.getLoginUser());
			}
			Object obj = portBiz.save(entity, client);
			request.setAttribute("id", entity.getId());
			this.warn("Save port. Parameter: [" + JSONObject.toJSONString(entity) +"].");
			return obj;
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@Override
	public Object update(@PathVariable String id, @RequestBody Port entity) throws IOException {
		return null;
	}

	@ApiOperation(value = "虚拟网卡绑定到云主机(id-虚拟网卡id；serverId-主机id)")
	@RequestMapping(value = "/{id}/attach/{serverId}", method = RequestMethod.POST)
	public Object attachHost(@PathVariable String id, @PathVariable String serverId) {
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		try {
			this.warn("Port bind host. port:" + id + ", host:" + serverId);
			return portBiz.attachHost(id, serverId, client);
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "云主机卸载虚拟网卡( serverId-云主机id, id-虚拟网卡id)")
	@RequestMapping(value = "/{id}/os-interface/{serverId}", method = RequestMethod.DELETE)
	public Object dettach(@PathVariable String serverId, @PathVariable String id) {
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		try {
			this.warn("Host dettach port, " + "host:" + serverId + ", " + "port:" + id+ ".");
			return portBiz.dettach(id, serverId, client);
		} catch (MessageException e) {
            this.exception(e, e.getMessage());
            if(e.getResultCode() != null) {
                return BaseRestControl.tranReturnValue(e.getResultCode());
            } else {
                return BaseRestControl.tranReturnValue(e.getMessage());
            }
        } catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "检查名称是否重复")
	@RequestMapping(value = "/check/{name}", method = RequestMethod.GET)
	public Object checkRepeat(@PathVariable String name) {
		boolean nameRepeat = false;
		String id = request.getParameter("id"); // 修改时传入一个id则查重时会排除对象本身
		try {
			Map<String, Object> checkMap = new HashMap<>();
			checkMap.put("name", name);
			checkMap.put("tenantId", this.getProjectId());
			nameRepeat = portBiz.checkRepeat(Port.class, checkMap, id);
			if (!nameRepeat) { // 查重(名称)
				return BaseRestControl.tranReturnValue(ResultType.repeat);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "ip列表")
	@RequestMapping(value = "/ip/{projectId}", method = RequestMethod.GET)
	public Object ipList(@PathVariable String projectId) {
		if (!projectId.equals(this.getProjectId()) && !this.getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(new ArrayList<>());
		}
		PageEntity pageEntity = this.beforeList();
		return BaseRestControl.tranReturnValue(portBiz.getIpsByTenant(projectId, pageEntity.getPageSize(), pageEntity.getPageNo()));
	}

	/**
	 * 虚拟网卡关联的安全组信息
	 * 
	 * @param port
	 */
	private JSONObject addSecurityGroupInfo(Port port) {
		List<Group2Port> s2ports = group2PortDao.findByPropertyName(Group2Port.class, "portId", port.getId());
		List<Object> list = new ArrayList<Object>();
		if (null != s2ports && s2ports.size() > 0) {
			for (Group2Port s2Port : s2ports) {
				Map<String, String> groupMap = new HashMap<String, String>();
				String sId = s2Port.getSecurityGroupId();
				List<Group2Port> ports = group2PortDao.findByPropertyName(Group2Port.class, "securityGroupId", sId);
				SecurityGroup group = securityGroupBiz.findById(SecurityGroup.class, sId);
				groupMap.put("id", sId);
				groupMap.put("name", group.getName());
				groupMap.put("description", group.getDescription());
				groupMap.put("portNumber", Integer.toString(ports.size()));
				list.add(groupMap);
			}
		}
		JSONArray securityArr = new JSONArray(list);

		String json = JSON.toJSONString(port);
		JSONObject result = JSONObject.parseObject(json);
		result.put("securityGroup", securityArr);
		return result;
	}
	
}
