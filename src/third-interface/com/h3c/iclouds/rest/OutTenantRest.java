package com.h3c.iclouds.rest;

import com.h3c.iclouds.auth.ThirdPart;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.po.bean.outside.TenantNetworkBean;
import com.h3c.iclouds.biz.AzoneBiz;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.UserDao;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosImage;
import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Rules;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.User2Role;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对外开放的租户接口
 * Created by yKF7317 on 2017/5/15.
 */
@RestController
@RequestMapping("/v2/tenant")
@ThirdPart()
public class OutTenantRest extends BaseRestControl {
	
	@Resource
	private ProjectBiz projectBiz;
	
	@Resource
	private UserDao userDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<User2Role> user2RoleDao;
	
	@Resource
	private NovaVmBiz novaVmBiz;
	
	@Resource
	private PortBiz portBiz;
	
	@Resource
	public BaseDAO<Rules> baseDAO;
	
	@Resource
	private AzoneBiz azoneBiz;
	
	@Resource
	private NetworkBiz networkBiz;
	
	@Resource
	private QuotaUsedBiz quotaUsedBiz;
	
	@ApiOperation(value = "获取所有租户信息")
	@RequestMapping(method = RequestMethod.GET)
	public Object tenantInfo() {
		String vmType = this.request.getParameter("vmType");
		// 默认类似为云备份
		vmType = StrUtils.checkParam(vmType) ? vmType : ConfigProperty.IYYANYBACKUPS;
		List<Map<String, Object>> resultLists = new ArrayList<>();
		List<Project> projects = projectBiz.getAll(Project.class);
		if (StrUtils.checkCollection(projects)) {
			for (Project project : projects) {
				resultLists.add(getTenantInfo(project, vmType));
			}
		}
		return BaseRestControl.tranReturnValue(resultLists);
	}
	
	@ApiOperation(value = "获取单个租户信息")
	@RequestMapping(value = "/{tenantId}", method = RequestMethod.GET)
	public Object tenantInfo(@PathVariable String tenantId) {
		String vmType = this.request.getParameter("vmType");
		// 默认类似为云备份
		vmType = StrUtils.checkParam(vmType) ? vmType : ConfigProperty.IYYANYBACKUPS;
		Project project = projectBiz.findById(Project.class, tenantId);
		Map<String, Object> projectMap = new HashMap<>();
		if (StrUtils.checkParam(project)) {
			projectMap = getTenantInfo(project, vmType);
		}
		return BaseRestControl.tranReturnValue(projectMap);
	}
	
	@ApiOperation(value = "获取单个租户下的所有用户")
	@RequestMapping(value = "/{tenantId}/users", method = RequestMethod.GET)
	public Object getAllUsers(@PathVariable String tenantId) {
		Project project = projectBiz.findById(Project.class, tenantId);
		if (null == project) {
			return BaseRestControl.tranReturnValue(ResultType.not_exist); // 该租户不存在
		}
		
		List<User> users = userDao.findByPropertyName(User.class, "projectId", tenantId);
		List<Object> resultLists = new ArrayList<>();
		if (StrUtils.checkCollection(users)) {
			for (User user : users) {
				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("UserCode", user.getLoginName()); // 用户编码
				tempMap.put("UserID", user.getId()); // 用户UUID
				tempMap.put("UserName", user.getUserName()); // 用户名称
				tempMap.put("DeptID", user.getDeptId()); // 归属部门
				resultLists.add(tempMap);
			}
		}
		return BaseRestControl.tranReturnValue(resultLists);
	}
	
	@ApiOperation(value = "获取租户可用的可用域")
	@RequestMapping(value = "/{tenantId}/azones/{type}", method = RequestMethod.GET)
	public Object getAzones(@PathVariable String tenantId, @PathVariable String type) {
		Project project = projectBiz.findById(Project.class, tenantId);
		if (null == project) {
			return BaseRestControl.tranReturnValue(ResultType.not_exist); // 该租户不存在
		}
		Map<String, Object> queryMap = new HashMap<>();
		String[] azoneIds = azoneBiz.getAzoneIds(tenantId); // 筛选出租户对应的可用域id
		queryMap.put("uuid", azoneIds);
		switch (type) {
			case "nova":
				queryMap.put("resourceType", "nova");
				break;
			case "cinder":
				queryMap.put("resourceType", "cinder");
				break;
			default:
				return BaseRestControl.tranReturnValue(Collections.EMPTY_LIST);
		}
		List<Azone> azones = azoneBiz.listByClass(Azone.class, queryMap);
		return BaseRestControl.tranReturnValue(azones);
	}
	
	@ApiOperation(value = "获取租户可用的网络")
	@RequestMapping(value = "/{tenantId}/networks", method = RequestMethod.GET)
	public Object getNetworks(@PathVariable String tenantId) {
		Project project = projectBiz.findById(Project.class, tenantId);
		if (null == project) {
			return BaseRestControl.tranReturnValue(ResultType.project_not_exist); // 该租户不存在
		}
		List<TenantNetworkBean> beans = networkBiz.getTenantNetwork(tenantId);
		Map<String, Object> result = new HashMap<>();
		result.put("networks", beans);
		return BaseRestControl.tranReturnValue(result);
	}
	
	@ApiOperation(value = "获取租户可用的镜像")
	@RequestMapping(value = "/{tenantId}/images", method = RequestMethod.GET)
	public Object getImages(@PathVariable String tenantId) {
		Project project = projectBiz.findById(Project.class, tenantId);
		if (null == project) {
			return BaseRestControl.tranReturnValue(ResultType.not_exist); // 该租户不存在
		}
		Map<String, Object> params = new HashMap<>();
		params.put("isDefault", "0");
		params.put("tenantId", new String[] {tenantId, singleton.getRootProject()});
		List<Rules> list = this.baseDAO.listByClass(Rules.class, params);
		if ("1".equals(singleton.getConfigValue("imageflag"))) {
			//过滤image
			CloudosClient client = getSessionBean().getCloudClient();
			CloudosImage image = new CloudosImage(client);
			list = image.get(list);
		}
		return BaseRestControl.tranReturnValue(list);
	}
	
	private Map<String, Object> getTenantInfo(Project project, String vmType) {
		Map<String, Object> projectMap = new HashMap<>();
		projectMap.put("TenantId", project.getId());
		projectMap.put("TenantName", project.getName());
		int humanSize = quotaUsedBiz.getClassValue(project.getId(), "humansize");
		int storageSize = quotaUsedBiz.getClassValue(project.getId(), "storagesize");
		int monthSize = quotaUsedBiz.getClassValue(project.getId(), "monthsize");
		projectMap.put("QuotaHumanSize", humanSize);
		projectMap.put("QuotaStorageSize", storageSize);
		projectMap.put("QuotaMonthSize", monthSize);
		projectMap.put("QuotaActivationTime", project.getCreatedDate());
		projectMap.put("TenantType", "1");
		//租户的管理员信息
		Map<String, Object> querymap = new HashMap<>();
		querymap.put("projectId", project.getId());
		querymap.put("isAdmin", "0");
		User user = userDao.singleByClass(User.class, querymap);
		if (StrUtils.checkParam(user)) {
			projectMap.put("TenantAdminCode", user.getLoginName());
			projectMap.put("TenantAdminName", user.getUserName());
			//租户类型(0-云管理员;1-租户管理员)
			querymap.clear();
			querymap.put("userId", user.getId());
			querymap.put("roleId", singleton.getCloudRoleId());
			User2Role user2Role = user2RoleDao.singleByClass(User2Role.class, querymap);
			if (StrUtils.checkParam(user2Role)) {
				projectMap.put("TenantType", "0");
			}
		}
		
		//租户的云主机和其mac地址信息
		List<NovaVm> novaVms = novaVmBiz.findListByProfix(project.getId(), vmType);
		List<Map<String, Object>> macList = new ArrayList<>();
		if (StrUtils.checkCollection(novaVms)) {
			for (NovaVm novaVm : novaVms) {
				Map<String, Object> macMap = new HashMap<>();
				macMap.put("UUID", novaVm.getUuid());
				List<Port> ports = portBiz.findByPropertyName(Port.class, "deviceId", novaVm.getUuid());
				List<String> addressList = new ArrayList<>();
				if (StrUtils.checkCollection(ports)) {
					for (Port port : ports) {
						addressList.add(port.getMacAddress());
					}
				}
				macMap.put("MAC", addressList);
				macList.add(macMap);
			}
		}
		projectMap.put("MacList", macList);
		return projectMap;
	}
	
}
