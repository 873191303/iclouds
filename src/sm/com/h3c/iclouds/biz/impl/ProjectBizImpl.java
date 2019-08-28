package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.DepartmentBiz;
import com.h3c.iclouds.biz.MeasureDetailBiz;
import com.h3c.iclouds.biz.Project2QuotaBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.RoleBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.Network2SubnetDao;
import com.h3c.iclouds.dao.Project2AzoneDao;
import com.h3c.iclouds.dao.Project2NetworkDao;
import com.h3c.iclouds.dao.Project2QuotaDao;
import com.h3c.iclouds.dao.ProjectDao;
import com.h3c.iclouds.dao.QuotaClassDao;
import com.h3c.iclouds.dao.QuotaUsedDao;
import com.h3c.iclouds.dao.User2RoleDao;
import com.h3c.iclouds.dao.UserDao;
import com.h3c.iclouds.dao.VdcDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosAzone;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosGroup;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.operate.CloudosProject;
import com.h3c.iclouds.operate.CloudosQuota;
import com.h3c.iclouds.operate.CloudosUser;
import com.h3c.iclouds.opt.IssoClient;
import com.h3c.iclouds.opt.IssoUserOpt;
import com.h3c.iclouds.opt.MonitorClient;
import com.h3c.iclouds.opt.MonitorParams;
import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.po.Department;
import com.h3c.iclouds.po.Network2Subnet;
import com.h3c.iclouds.po.Notice2Project;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Azone;
import com.h3c.iclouds.po.Project2Network;
import com.h3c.iclouds.po.Project2Quota;
import com.h3c.iclouds.po.QuotaClass;
import com.h3c.iclouds.po.QuotaUsed;
import com.h3c.iclouds.po.Role;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.User2Role;
import com.h3c.iclouds.po.Vdc;
import com.h3c.iclouds.po.bean.IssoUserBean;
import com.h3c.iclouds.po.bean.inside.SaveProjectCustomBean;
import com.h3c.iclouds.po.bean.inside.UpdateProject2QuotaBean;
import com.h3c.iclouds.po.bean.inside.UpdateTenantBean;
import com.h3c.iclouds.po.bean.model.AzoneBean;
import com.h3c.iclouds.po.bean.model.NetworksBean;
import com.h3c.iclouds.po.bean.model.Project2NetworkBean;
import com.h3c.iclouds.po.bean.model.TenantBean;
import com.h3c.iclouds.po.bean.outside.ProjectDetailBean;
import com.h3c.iclouds.po.vo.ProjectVO;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.IpValidator;
import com.h3c.iclouds.utils.JacksonUtil;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.PwdUtils;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.VdcHandle;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by yKF7408 on 2016/12/20.
 */
@Service("projectBiz")
public class ProjectBizImpl extends BaseBizImpl<Project> implements ProjectBiz {

	@Resource
	private ProjectDao projectDao;

	@Resource
	private Project2QuotaDao project2QuotaDao;

	@Resource
	private Project2QuotaBiz project2QuotaBiz;

	@Resource
	private UserDao userDao;

	@Resource
	private User2RoleDao user2RoleDao;

	@Resource
	private QuotaUsedDao quotaUsedDao;

	@Resource
	private Project2AzoneDao project2AzoneDao;

	@Resource(name = "baseDAO")
	private BaseDAO<Azone> azoneDao;

	@Resource
	private Project2NetworkDao project2NetworkDao;

	@Resource
	private Network2SubnetDao network2SubnetDao;

	@Resource
	private SqlQueryBiz sqlQueryBiz;

	@Resource
	private VdcDao vdcDao;

	@Resource
	private DepartmentBiz departmentBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Department> departmentDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Notice2Project> notice2ProjectDao;
	
	@Resource
	private RoleBiz roleBiz;
	
	@Resource
	private QuotaClassDao quotaClassDao;

	@Resource
	private MeasureDetailBiz measureDetailBiz;

	@Override
	public PageModel<Project> findForPage(PageEntity entity) {
		return this.projectDao.findForPage(entity);
	}

	/**
	 * 将租户底下的租户管理员名称拼接成一个字符串赋给租户对象userName属性
	 *
	 * @param project
	 * @return
	 */
	public Project transProject(Project project) {
		StringBuffer userName = new StringBuffer();
		String projectId = project.getId();
		List<User> users = userDao.findByPropertyName(User.class, "projectId", projectId);
		for (User user : users) {
			String userId = user.getId();
			List<User2Role> user2Roles = user2RoleDao.findByPropertyName(User2Role.class, "userId", userId);
			for (User2Role user2Role : user2Roles) {
				String roleId = user2Role.getRoleId();
				if (CacheSingleton.getInstance().getTenantRoleId().equals(roleId)
						|| CacheSingleton.getInstance().getOperationRoleId().equals(roleId)
						|| CacheSingleton.getInstance().getCloudRoleId().equals(roleId)
						|| CacheSingleton.getInstance().getCtRoleId().equals(roleId)) {// 找出角色为租户管理员的用户名称
					userName.append(user.getUserName() + ",");
					break;
				}
			}
		}
		if (userName.length() > 0) {
			String name = userName.substring(0, userName.length() - 1);// 将字符串最后一个逗号去掉
			project.setUserName(name);
		}
		return project;
	}

	/**
	 * 修改租户信息
	 *
	 * @return
	 */
	public Object update(String id, UpdateTenantBean bean) {
		// 关联客户信息
		List<AzoneBean> azoneBeans = bean.getAzones();
		bean.setFlag(new boolean[azoneBeans.size() * 2 + 1]);
		TenantBean entity = bean.getProject();
		Project project = projectDao.getExistProject(id);
		Map<String, Object> params = new HashMap<>();
		params.put("deleted", "0");
		params.put("id", id);
		List<Project2Azone> project2Azones = project2AzoneDao.listByClass(Project2Azone.class, params);
		if (StrUtils.checkParam(project)) {
			// 设置日志的资源名称
			this.request.setAttribute("name", project.getName());
			InvokeSetForm.copyFormProperties(entity, project);
			if (StrUtils.checkParam(entity)) {
				project.setCusId(entity.getCusId());
				project.updatedUser(getLoginUser());
				projectDao.update(project);
			}
			project2AzoneDao.update(azoneBeans, project.getId());
			CloudosClient client = getSessionBean().getCloudClient();
			CloudosProject cloudosProject = new CloudosProject(client);
			CloudosAzone cloudosAzone = new CloudosAzone(client);
			if (StrUtils.checkCollection(project2Azones)) {
				bean.setProject2Azones(project2Azones);
				for (int i = 0; i < project2Azones.size(); i++) {
					JSONObject result = cloudosAzone.delete(project2Azones.get(i), id);
					if (ResourceHandle.judgeResponse(result)) {
						this.warn("删除原来的租户和可用域关系");
					} else {
						this.warn("删除失败异常信息" + result.toJSONString());
						bean.getFlag()[i] = true;
						throw new MessageException(ResultType.cloudos_api_error);
					}
				}
			}
			if (StrUtils.checkCollection(azoneBeans)) {
				for (int i = 0; i < azoneBeans.size(); i++) {
					JSONObject result = cloudosAzone.save(azoneBeans.get(i), id);
					if (ResourceHandle.judgeResponse(result)) {
						this.warn("保存用户操作的租户和可用域关系");
					} else {
						this.warn("保存失败异常信息" + result.toJSONString());
						bean.getFlag()[project2Azones.size() + i] = true;
						throw new MessageException(ResultType.cloudos_api_error);
					}
				}
			}
			// 顶级租户不允许修改
			if (!CacheSingleton.getInstance().getConfigValue("rootid").equals(project.getId())) {
				bean.setTenant(project);
				JSONObject result = cloudosProject.update(project);
				if (ResourceHandle.judgeResponse(result)) {
					this.warn("更新cloudos租户信息");
				} else {
					this.warn("更新cloudos租户失败");
					bean.getFlag()[azoneBeans.size() * 2] = true;
					this.warn(result.getString("record"));
					throw new MessageException(ResultType.cloudos_api_error);
				}
			}

			return BaseRestControl.tranReturnValue(ResultType.success, project);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	public ProjectDetailBean get(Project project) {
		ProjectDetailBean projectDetailBean = new ProjectDetailBean();
		if (project.getParentId() == null) {
			projectDetailBean.setRoot(true);
		}
		List<Project2Azone> project2Azones = project2AzoneDao.getProject2Azone(project.getId());
		List<Azone> azones = new ArrayList<>();
		for (Project2Azone project2Azone : project2Azones) {
			Map<String, String> map1 = new HashMap<>();
			map1.put("uuid", project2Azone.getIyuUuid());
			map1.put("deleted", "0");
			List<Azone> azones2 = azoneDao.findByMap(Azone.class, map1);
			azones.addAll(azones2);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("tenantId", project.getId());
		String hql = "from Project2Quota p where p.deleted=0 and p.tenantId=:tenantId";
		List<Project2Quota> list = project2QuotaDao.findByHql(hql, map);

		List<Project2Quota> computer_resource = new ArrayList<>();
		List<Project2Quota> storage_resource = new ArrayList<>();
		List<Project2Quota> network_resource = new ArrayList<>();
		List<Project2Quota> third_resource = new ArrayList<>();
		for (Project2Quota project2Quota : list) {
			String classCode = project2Quota.getClassCode();
			if (ConfigProperty.class_computer_resource.contains(classCode)) {
				computer_resource.add(project2Quota);
			} else if (ConfigProperty.class_storage_resource.contains(classCode)) {
				storage_resource.add(project2Quota);
			} else if (ConfigProperty.class_network_resource.contains(classCode)) {
				network_resource.add(project2Quota);
			} else if (ConfigProperty.class_third_resource.contains(classCode)) {
				third_resource.add(project2Quota);
			}
		}
		//兼容上个版本已存在租户没有第三方配额问题
		if (!StrUtils.checkCollection(third_resource)) {
			for (String classCode : ConfigProperty.class_third_resource) {
				QuotaClass quotaClass = quotaClassDao.singleByClass(QuotaClass.class, StrUtils.createMap("classCode",
						classCode));
				if (StrUtils.checkParam(quotaClass)) {
					Project2Quota project2Quota = new Project2Quota();
					project2Quota.setId(UUID.randomUUID().toString());
					project2Quota.setTenantId(project.getId());
					project2Quota.setClassCode(classCode);
					project2Quota.setClassId(quotaClass.getId());
					project2Quota.setHardLimit(0);
					project2Quota.createDate();
					project2QuotaBiz.add(project2Quota);
					third_resource.add(project2Quota);
				}
			}
		}
		projectDetailBean.setComputer_resource(computer_resource);
		projectDetailBean.setStorage_resource(storage_resource);
		projectDetailBean.setNetwork_resource(network_resource);
		projectDetailBean.setThird_resource(third_resource);
		hql = "from Project2Network p where p.deleted=0 and p.tenantId=:tenantId";
		List<Project2Network> project2Networks = project2NetworkDao.findByHql(hql, map);
		List<NetworksBean> networks = new ArrayList<>();
		for (Project2Network cidr : project2Networks) {
			Map<String, Object> map1 = new HashMap<>();
			map1.put("networkId", cidr.getId());
			hql = "from Network2Subnet n where n.deleted=0 and n.networkId=:networkId";
			List<Network2Subnet> network2Subnets = network2SubnetDao.findByHql(hql, map1);

			Project2NetworkBean project2NetworkBean = new Project2NetworkBean();
			project2NetworkBean.setCidr(cidr.getCidr());
			project2NetworkBean.setId(cidr.getId());
			// 一个网络信息元
			NetworksBean networkBean = new NetworksBean();
			networkBean.setCidr(project2NetworkBean);
			networkBean.setSubnets(networkBean.po2Bean(network2Subnets));
			networks.add(networkBean);

		}
		projectDetailBean.setNetworks(networks);
		projectDetailBean.setAzones(azones);
		return projectDetailBean;
	}

	@Override
	public Object updateProject2Quota(UpdateProject2QuotaBean bean) {
		CloudosClient client = getSessionBean().getCloudClient();
		CloudosQuota cloudosQuota = new CloudosQuota(client);
		request.setAttribute("id", bean.getProjectId());
		Project project = projectDao.getExistProject(bean.getProjectId());
		if (StrUtils.checkParam(project)) {
			request.setAttribute("name", project.getName());
			List<Project2Quota> quotaBeans = bean.getQuotas();
			if (quotaBeans.size() != 20) {
				return BaseRestControl.tranReturnValue(ResultType.tenant_quota_num_wrongfulness);
			}
			// cloudos更新的数据
			List<Map<String, Object>> errorList = new ArrayList<>();
			Map<String, Integer> map = new HashMap<String, Integer>();
			boolean errorFlag = false;
			for (Project2Quota project2Quota : quotaBeans) {
				Project2Quota temp = project2QuotaDao.findById(Project2Quota.class, project2Quota.getId());
				if (!StrUtils.checkParam(temp)) {
					return BaseRestControl.tranReturnValue(ResultType.tenant_quota_not_contain);
				}
				String classCode = project2Quota.getClassCode();
				Integer hardlimit = project2Quota.getHardLimit();
				Map<String, Integer> range = project2QuotaBiz.check(project, project2Quota);
				if(range.get("max")==-1) {
					hardlimit = -1;
				}
				
				if (!ConfigProperty.class_third_resource.contains(classCode)) {
					map.put(classCode, hardlimit);
				} 
				
				//加个判断如果 hardlimit =0 ，就忽略此判断
				if(hardlimit > 0) {
					if (!(hardlimit >= range.get("min") && hardlimit <= range.get("max"))) {
						Map<String, Object> result = new HashMap<>();
						result.put("classCode", classCode);
						result.put("range", range);
						errorList.add(result);
						this.warn(result);
						errorFlag = true;
					}
				}
				
			}
			if (errorFlag) {
				return BaseRestControl.tranReturnValue(ResultType.quota_use_more_hardlimit, errorList);
			}
			for (Project2Quota project2Quota : quotaBeans) {
				Integer hardlimit = project2Quota.getHardLimit();
				Project2Quota project2Quota2 = project2QuotaBiz.findById(Project2Quota.class, project2Quota.getId());
				if (StrUtils.checkParam(project2Quota2)) {
					project2Quota2.setHardLimit(hardlimit);
					project2QuotaDao.update(project2Quota2);
				}
			}
			if (StrUtils.checkParam(project.getParentId())) {
				this.warn("修改租户配额");
				cloudosQuota.updateCloudQuota(map, project.getParentId(), bean);
			} else {
				this.warn("租户修改配额");
				cloudosQuota.updateCloudQuota(map, bean.getProjectId(), bean);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted, project);
	}

	@Override
	public Map<String, Object> save(SaveProjectCustomBean bean, IssoClient adminClient, MonitorClient monitorClient) throws Exception {
		CloudosClient client = getSessionBean().getCloudClient();
		if (!StrUtils.checkParam(client)) {
			throw new MessageException(ResultType.system_error);
		}
		bean.setFlag(new boolean[3 + bean.getAzone().size()]);
		TenantBean tenantBean = bean.getProject();
		Project project = new Project();
		InvokeSetForm.copyFormProperties(tenantBean, project);
		if (!userDao.checkRepeat(User.class, "loginName", bean.getUserName())) {
			throw new MessageException(ResultType.login_name_exist);
		}
		
		User user = new User();
		user.setLoginName(bean.getUserName());
		user.setUserName(bean.getUserName());
		user.setPassword(bean.getPassword());
		project.setFlag(0);
		project.setEnabled(true);
		String parentId = this.getSessionBean().getUser().getProjectId();
		project.setParentId(this.getSessionBean().getUser().getProjectId());// 记录创建租户的当前租户
		project.createdUser(getLoginUser());
		List<AzoneBean> azoneBeans = bean.getAzone();
		project.setDomainId("66213f235cb046d091b67976f4726677");
		
		//cloudos保存租户和用户信息
		Map<String, Object> resultMap = saveProjectAndUser(user, project, bean, client);
		project = (Project) resultMap.get("project");
		user = (User) resultMap.get("user");
		String projectId = project.getId();
		
		//cloudos同步可用域信息
		CloudosAzone cloudosAzone = new CloudosAzone(client);
		for (int i = 0; i < azoneBeans.size(); i++) {
			JSONObject result = cloudosAzone.save(azoneBeans.get(i), projectId);
			if (!ResourceHandle.judgeResponse(result)) {
				bean.setCloudosError(result);
				bean.getFlag()[3 + i] = true;
				throw MessageException.create(HttpUtils.getError(result), ResultType.cloudos_api_error);
			}
		}
		
		//cloudos初始化配额信息
		CloudosQuota cloudosQuota = new CloudosQuota(client);
		cloudosQuota.init(parentId, project);
		
		//isso保存用户
		String issoUserId = null;
		if (null != adminClient) {
			IssoUserOpt userOpt = new IssoUserOpt(adminClient);
			IssoUserBean userBean = new IssoUserBean(user);
			JSONObject jsonObject = userOpt.save(userBean);
			if (!adminClient.checkResult(jsonObject)) {
				throw MessageException.create(adminClient.getError(jsonObject), ResultType.failure_in_isso);
			}
			issoUserId = adminClient.getValue(jsonObject, "id");
			bean.setIssoUserId(issoUserId);
		}

		/*
		//监控保存用户和租户
		if (null != monitorClient) {
			//设置监控租户对应新增参数(必须是已经新增后生成id的租户与密码没有加密的用户)
			ProjectVO projectVO = ProjectVO.create(project, user);
			JSONObject jsonObject = monitorClient.post(monitorClient.tranUrl(MonitorParams.MONITOR_API_PROJECT_SAVE),
					InvokeSetForm.tranClassToMap(projectVO));
			if (!monitorClient.checkResult(jsonObject)) {//操作失败
				throw MessageException.create(monitorClient.getError(jsonObject), ResultType.failure_in_monitor);
			}
		}
		*/
		
		this.localSave(project, user, azoneBeans);
		return resultMap;
	}

	private Map<String, Object> saveProjectAndUser(User user, Project project, SaveProjectCustomBean bean, CloudosClient client)
			throws Exception {
		CloudosProject cloudosProject = new CloudosProject(client);
		if (cloudosProject.checkName(project)) {
			this.warn("cloudos租户名重复" + JacksonUtil.toJSon(project));
			throw new MessageException(ResultType.cloudos_project_name_repeat);
		}
		JSONObject result = cloudosProject.save(project);
		if (!ResourceHandle.judgeResponse(result)) {
			// bean.setFlag(flag);
			bean.getFlag()[0] = true;
			bean.setCloudosError(result);
			this.warn(result.getString("record"));
			throw new MessageException(ResultType.cloudos_api_error);
		}
		JSONObject projectObject = HttpUtils.getJSONObject(result, "project");
		String projectId = projectObject.getString("id");
		String domainId = projectObject.getString("domain_id");
		project.setId(projectId);
		// 记录创建的租户
		bean.setProjectId(projectId);
		CloudosUser cloudosUser = new CloudosUser(client);
		result = cloudosUser.save(user, projectId, domainId);
		if (!ResourceHandle.judgeResponse(result)) {
			bean.getFlag()[1] = true;
			bean.setCloudosError(result);
			this.warn(result.getString("record"));
			client.delete(HttpUtils.tranUrl(CloudosParams.CLOUDOS_API_PROJECTS_ACTION, projectId));
			throw new MessageException(ResultType.cloudos_api_error);
		}
		result = HttpUtils.getJSONObject(result, "user");
		String userId = result.getString("id");
		// 记录创建的用户
		bean.setUserId(userId);
		CloudosGroup cloudosGroup = new CloudosGroup(client);
		result = cloudosGroup.put(userId, projectId);
		if (!ResourceHandle.judgeResponse(result)) {
			bean.getFlag()[2] = true;
			bean.setCloudosError(result);
			this.warn(result.getString("record"));
			client.delete(HttpUtils.tranUrl(CloudosParams.CLOUDOS_API_USERS_ACTION, userId));
			client.delete(HttpUtils.tranUrl(CloudosParams.CLOUDOS_API_PROJECTS_ACTION, projectId));
			throw new MessageException(ResultType.cloudos_api_error);
		}
		// 用户id随机生成
		user.setProjectId(projectId);// 关联租户
		user.setIsAdmin(ConfigProperty.YES);
		user.setLoginName(user.getUserName());
		user.createdUser(this.getLoginUser());
		user.setCloudosId(userId);

		Map<String, Object> project_user = new HashMap<>();
		project_user.put("project", project);
		project_user.put("user", user);
		return project_user;
	}

	@Override
	public Object saveNetwork(String id, String cidr, Map<String, Object> ips) {
		List<Map<String, String>> ipPool = (List<Map<String, String>>) ips.get("ipPool");
		// id为当前租户id
		Project project = projectDao.getExistProject(id);
		// 创建私有网络
		CloudosClient client = getSessionBean().getCloudClient();
		CloudosProject cloudosProject = new CloudosProject(client);
		JSONObject result = cloudosProject.saveQuotaNetwork(project, getProjectId(), cidr, ipPool);

		if (!result.getInteger("result").equals(200)) {
			throw new MessageException(result.getString("record"));
		}
		
		// 创建网络子网
		Project2Network project2Network = new Project2Network();
		String networkId = UUID.randomUUID().toString();
		project2Network.setId(networkId);
		project2Network.setTenantId(id);
		project2Network.createDate();
		project2Network.setCidr(cidr);
		project2NetworkDao.add(project2Network);
		
		for (Map<String, String> map2 : ipPool) {
			Network2Subnet subnet = new Network2Subnet();
			String endIp = map2.get("end");
			String startIp = map2.get("start");
			String subnetId = UUID.randomUUID().toString();
			subnet.setId(subnetId);
			subnet.setNetworkId(networkId);
			subnet.setStartIp(startIp);
			subnet.setEndIp(endIp);
			subnet.createDate();
			network2SubnetDao.add(subnet);
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}

	@Override
	public Object deleteProject2Network(String id, String tenantId) {
		// 判断网络配额正在使用
		Project project = projectDao.getExistProject(tenantId);
		Map<String, Object> map = new HashMap<>();

		// 当前租户云主机在使用的正在使用
		map.put("tenantId", tenantId);
		List<Map<String, Object>> cidrs = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_NOVA_CIDR, map);
		Set<String> set = new HashSet<>();
		for (Map<String, Object> map2 : cidrs) {
			String cidr = (String) map2.get("cidr");
			if (StrUtils.checkParam(cidr)) {
				String temp[] = cidr.split(";");
				set.addAll(Arrays.asList(temp));
			}
		}
		List<String> projectNames = new ArrayList<>();
		if (StrUtils.checkParam(project)) {
			Project2Network project2Network = project2NetworkDao.findById(Project2Network.class, id);
			// 如果为根租户时不能删除子租户在使用的网段
			if (tenantId.equals(CacheSingleton.getInstance().getConfigValue("rootid"))) {
				List<Project> projects = projectDao.getChildParent(tenantId);
				for (Project project2 : projects) {
					map.put("tenantId", project2.getId());
					String hql = "from Project2Network pn where pn.tenantId=:tenantId";
					List<Project2Network> project2Networks = project2NetworkDao.findByHql(hql, map);
					for (Project2Network project2Network2 : project2Networks) {
						if (project2Network2.getCidr().equals(project2Network.getCidr())) {
							projectNames.add(project2.getName());
						}
					}
				}
			}
			if (StrUtils.checkCollection(projectNames)) {
				return projectNames;
			}
			if (set.contains(project2Network.getCidr())) {
				throw new MessageException(ResultType.cidr_use_in_nova);
			}
			List<Map<String, Object>> ips = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_VDC_IPS, map);
			List<Map<String, String>> temp = new ArrayList<>();
			for (Map<String, Object> map2 : ips) {
				Map<String, String> temp1 = new HashMap<>();
				temp1.put("start", (String) map2.get("startip"));
				temp1.put("end", (String) map2.get("endip"));
				temp.add(temp1);
			}
			// 判断网段是否以及被使用
			if (StrUtils.checkCollection(temp)) {
				List<Map<String, String>> quotaIps = network2SubnetDao.get(project2Network);
				temp.addAll(quotaIps);
				if (!IpValidator.checkIpPoolRepeat(temp)) {
					throw new MessageException(ResultType.cidr_use_in_vdc);
				}
			}
			List<Network2Subnet> subnets = network2SubnetDao.findByPropertyName(Network2Subnet.class, "networkId", id);
			network2SubnetDao.delete(subnets);
			project2NetworkDao.delete(project2Network);
			// 删除私有网络
			CloudosClient client = getSessionBean().getCloudClient();
			String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_QUOTA);
			uri = HttpUtils.tranUrl(uri, getProjectId(), tenantId);
			JSONObject result = client.get(uri);
			if (!ResourceHandle.judgeResponse(result)) {
				LogUtils.warn(Project.class, result.getString("record"));
				throw new MessageException(ResultType.cloudos_api_error);
			}
			result = result.getJSONObject("record");
			JSONObject networkObject = result.getJSONObject("network");
			if (null == networkObject) {
				LogUtils.warn(Project.class, "Get Cloudos Quota Empty");
				throw new MessageException(ResultType.cloudos_api_error);
			}
			JSONArray networkAddresses = networkObject.getJSONArray("network_addresses");
			if (networkAddresses != null) {
				Iterator<Object> iterator = networkAddresses.iterator();
				while (iterator.hasNext()) {
					JSONObject networkAddress = (JSONObject) iterator.next();
					if (project2Network.getCidr().equals(networkAddress.getString("cidr"))) {
						iterator.remove();
					}
				}
				networkObject.put("network_addresses", networkAddresses);
				client.put(uri, result);
			}

			return BaseRestControl.tranReturnValue(ResultType.success);

		}
		return BaseRestControl.tranReturnValue(ResultType.tenant_not_exist);
	}

	@Override
	public Object deleteProject(Project project, CloudosClient client, MonitorClient monitorClient) {
		String hql = "from Project2Quota p where p.tenantId=:tenantId ";
		Map<String, Object> map = new HashMap<>();
		map.put("tenantId", project.getId());
		List<Project2Quota> list = project2QuotaDao.findByHql(hql, map);
		if (StrUtils.checkCollection(list)) {
			project2QuotaDao.delete(list);
		}
		hql = "from QuotaUsed pu where pu.tenantId=:tenantId";
		List<QuotaUsed> quotaUseds = quotaUsedDao.findByHql(hql, map);
		quotaUsedDao.delete(quotaUseds);
		hql = "from Project2Azone pa where pa.id=:tenantId";
		List<Project2Azone> project2Azones = project2AzoneDao.findByHql(hql, map);
		project2AzoneDao.delete(project2Azones);
		hql = "from Project2Network pn where pn.tenantId=:tenantId";
		List<Project2Network> project2Networks = project2NetworkDao.findByHql(hql, map);
		for (Project2Network project2Network : project2Networks) {
			Map<String, Object> deleteWhere = new HashMap<>();
			deleteWhere.put("networkId", project2Network.getId());
			network2SubnetDao.delete(deleteWhere, Network2Subnet.class);
		}
		project2NetworkDao.delete(project2Networks);
		List<Vdc> vdcs = vdcDao.findByPropertyName(Vdc.class, "projectId", project.getId());
		if (StrUtils.checkCollection(vdcs)) {
			for (Vdc vdc : vdcs) {
				new VdcHandle().deleteViewAndItem(vdc.getId());
				vdcDao.delete(vdc);
			}
		}

		List<Department> departments = departmentDao.findByPropertyName(Department.class, "projectId", project.getId());
		if (StrUtils.checkCollection(departments)) {
			for (Department department : departments) {
				departmentDao.delete(department);
			}
		}
		List<Role> roles = roleBiz.findByPropertyName(Role.class, "projectId", project.getId());
		if (StrUtils.checkCollection(roles)) {
			for (Role role : roles) {
				roleBiz.delete(role);
			}
		}
		
		// 删除消息
		notice2ProjectDao.delete(StrUtils.createMap("tenantId", project.getId()), Notice2Project.class);

		// 结算流量数据
		Date endDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date startDate = calendar.getTime();
		Map<String, Object> queryMap = StrUtils.createMap("startDate", startDate);
		queryMap.put("endDate", endDate);
		queryMap.put("tenantId", project.getId());
		Map<String, Object> resultMap = sqlQueryBiz.querySingleByName(SqlQueryProperty.QUERY_NETFLOW_TENANTID_GROUP, queryMap);
		Long value = 0l;
		if(resultMap != null) {
			value = StrUtils.tranLong(resultMap.get("total"));
			value /= (1024 * 1024 * 1024);	// 转为GB
		}
		measureDetailBiz.stop(project.getId(), this.getLoginUser(), true, value);

		this.delete(project);
		
		CloudosProject cloudosProject = new CloudosProject(client);
		if (cloudosProject.isExist(project.getId())) {
			JSONObject result = cloudosProject.delete(project.getId());
			if (!ResourceHandle.judgeResponse(result)) {
				throw MessageException.create(HttpUtils.getError(result), ResultType.cloudos_api_error);
			}
		}
		
		//monitor删除
		if (singleton.isMonitorSyn()) {
			JSONObject jsonObject = monitorClient.delete(monitorClient.tranUrl(MonitorParams
					.MONITOR_API_PROJECT_DELETE, project.getId()), null);
			if (!monitorClient.checkResult(jsonObject) && !ResultType.deleted.toString().equals(jsonObject.getJSONObject("record").getString("result"))) {//操作失败
				throw MessageException.create(monitorClient.getError(jsonObject), ResultType.failure_in_monitor);
			}
		}
		
		map.put("project2Networks", project2Networks);
		map.put("project2Azones", project2Azones);
		map.put("quotaUseds", quotaUseds);
		map.put("project2Quota", list);
		return map;
	}

	@Override
	public Project get(String id) {
		return projectDao.getExistProject(id);
	}

	@Override
	public List<Project> checkName(Project project) {
		List<Project> projects = new ArrayList<>();
		projects.add(project);
		project = projectDao.findById(Project.class, project.getId());
		projects = checkName(project);
		projects.addAll(projects);
		return projects;

	}

	/**
	 * 检查vdc模块租户创建资源权限(检查当前租户是不是租户管理员)
	 * @return
	 */
	public boolean checkRole() {
		return this.getSessionBean().getSuperRole();
	}

	/**
	 * 检查vdc模块租户操作资源权限(当前租户是资源的所属租户且拥有租户管理员角色或拥有管理员角色)
	 * @param projectId 租户id
	 * @return 布尔
	 */
	@Override
	public boolean checkOptionRole(String projectId) {
		if (this.getProjectId().equals(projectId) && this.getSessionBean().getSuperRole()) {
			return true;
		}
		return this.getSessionBean().getSuperUser();
	}

	/**
	 * 检查vdc模块租户查看权限(是当前租户的租户管理员或当期用户或者是超级管理员)
	 * @param projectId 租户id
	 * @return 布尔
	 */
	@Override
	public boolean checkLookRole(String projectId) {
		if (this.getSessionBean().getSuperUser()) {
			return true;
		}
		if (this.getProjectId().equals(projectId) && this.getSessionBean().getSuperRole()) {
			return true;
		}
		return false;
	}

	@Override
	public int monthCount () {
		return projectDao.monthCount();
	}
	
	@Override
	public String getFilterProjectId (String projectId, String flag) {
		boolean isAdmin = this.getSessionBean().getSuperUser();//是否超级管理员
		boolean isTenant = this.getSessionBean().getSuperRole();//是否租户管理员
		if (isAdmin || isTenant) {
			if (StrUtils.checkParam(flag) && flag.equals("normal")) {//为其它资源的上级列表
				if (isAdmin) {//操作人为管理员角色时分为获取子租户列表和获取本租户列表
					if (!StrUtils.checkParam(projectId)) {//获取本租户列表
						projectId = this.getProjectId();
					}
				} else {//租户管理员获取本租户列表
					projectId = this.getProjectId();
				}
			} else {//获取所有列表
				if (isAdmin) {//管理员获取所有
					projectId = null;
				} else {//租户管理员获取本租户下所有资源
					projectId = this.getProjectId();
				}
			}
		} else {//普通用户获取不到资源
			projectId = "-1";
		}
		return projectId;
	}
	
	@Override
	public boolean checkOptionRole(String projectId, String userId) {
		if (this.getSessionBean().getSuperUser()) {
			return true;
		}
		boolean isSuperRole = this.getSessionBean().getSuperRole();
		if (isSuperRole) {
			if (!this.getProjectId().equals(projectId)) {
				return false;
			}
		} else {
			if (!this.getLoginUser().equals(userId)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean checkSaveRole (String userId, String projectId) {
		if (this.getSessionBean().getSuperUser()) {//云管理员可以跨租户新增资源
			return true;
		}
		if (!projectId.equals(this.getProjectId())) {//非云管理员必须同租户
			return false;
		}
		if (this.getSessionBean().getSuperRole()) {//租户管理员可以跨用户新增资源
			return true;
		}
		if (!userId.equals(this.getLoginUser())) {//非租户管理员必须同用户
			return false;
		}
		return true;
	}
	
	@Transactional
	@Override
	public void localSave(Project project, User user, List<AzoneBean> azoneBeans) throws Exception {
		String projectId = project.getId();
		//本地保存
		projectDao.add(project);
		// 设置日志的资源id
		this.request.setAttribute("id", projectId);
		String userId = userDao.add(user);// 保存用户
		user2RoleDao.save(userId);// 保存用户与角色的关系
		user = userDao.findById(User.class, userId);
		try {
			user.setPassword(PwdUtils.encrypt(user.getPassword(), user.getLoginName() + user.getId()));
		} catch (Exception e) {
			throw e;
		}
		// 创建一个顶级部门，用户归属于选择的部门
		Department dept = new Department();
		// dept.setId(UUID.randomUUID().toString());
		dept.createdUser(getLoginUser());
		dept.setDepth(1);
		dept.setParentId("-1"); // 作为最高级使用
		dept.setDeptName(project.getName());
		dept.setProjectId(projectId); // 与当前用户同一个租户下
		dept.setDeptCode(project.getName());
		dept.setDeptDesc("创建租户同步创建部门");
		this.departmentBiz.add(dept);
		user.setDeptId(dept.getId());
		userDao.update(user);
		
		// 保存租户与可用域关系
		project2AzoneDao.save(azoneBeans, project.getId());
		// 初始化配额信息
		project2QuotaDao.save(project);
		// 保存配额使用表记录
		quotaUsedDao.save(project);
		
		// 初始化租户的vdc视图信息
		Vdc vdc = new Vdc();
		vdc.setName(user.getUserName() + "-vdc");
		vdc.setUuid(UUID.randomUUID().toString());
		vdc.setProjectId(projectId);
		vdc.createdUser(this.getLoginUser());
		String vdcId = vdcDao.add(vdc);// 默认创建一个vdc
		new VdcHandle().saveViewAndItem(vdcId, vdc.getUuid(), vdc.getName(), vdcId, null, "0", 0,
				ConfigProperty.RESOURCE_OPTION_STATUS_SUCCESS);// 创建vdc在vdc视图和视图对象里面的数据
	}
}
