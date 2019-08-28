package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.AzoneBiz;
import com.h3c.iclouds.biz.DifferencesBiz;
import com.h3c.iclouds.biz.FirewallBiz;
import com.h3c.iclouds.biz.FloatingIpBiz;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.PolicieBiz;
import com.h3c.iclouds.biz.PolicieRuleBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.Project2NetworkBiz;
import com.h3c.iclouds.biz.Project2QuotaBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.biz.RoleBiz;
import com.h3c.iclouds.biz.RouteBiz;
import com.h3c.iclouds.biz.SyncVdcDataBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.biz.VdcBiz;
import com.h3c.iclouds.biz.VlbBiz;
import com.h3c.iclouds.biz.VolumeBiz;
import com.h3c.iclouds.common.CompareEnum;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.DifferencesDao;
import com.h3c.iclouds.dao.Network2SubnetDao;
import com.h3c.iclouds.dao.NovaFlavorDao;
import com.h3c.iclouds.dao.Project2AzoneDao;
import com.h3c.iclouds.dao.RecycleItemsDao;
import com.h3c.iclouds.dao.User2RoleDao;
import com.h3c.iclouds.dao.VolumeFlavorDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosNovaVm;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.operate.CloudosUser;
import com.h3c.iclouds.opt.MonitorClient;
import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.po.Differences;
import com.h3c.iclouds.po.Firewall;
import com.h3c.iclouds.po.FloatingIp;
import com.h3c.iclouds.po.IpAllocation;
import com.h3c.iclouds.po.IpAllocationPool;
import com.h3c.iclouds.po.IpAvailabilityRange;
import com.h3c.iclouds.po.Metadata;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.Network2Subnet;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Policie;
import com.h3c.iclouds.po.PolicieRule;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Azone;
import com.h3c.iclouds.po.Project2Network;
import com.h3c.iclouds.po.Project2Quota;
import com.h3c.iclouds.po.QuotaUsed;
import com.h3c.iclouds.po.RecycleItems;
import com.h3c.iclouds.po.Role;
import com.h3c.iclouds.po.Route;
import com.h3c.iclouds.po.Rules;
import com.h3c.iclouds.po.Sub2Dns;
import com.h3c.iclouds.po.Sub2Route;
import com.h3c.iclouds.po.Subnet;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.User2Group;
import com.h3c.iclouds.po.User2Role;
import com.h3c.iclouds.po.Vlb;
import com.h3c.iclouds.po.VmExtra;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.po.VolumeFlavor;
import com.h3c.iclouds.utils.DateUtils;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.PwdUtils;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.ResourceNovaHandle;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.ThreadContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service("differencesBiz")
public class DifferencesBizImpl extends BaseBizImpl<Differences> implements DifferencesBiz {
	
	@Resource
	private ProjectBiz projectBiz;
	
	@Resource
	private DifferencesDao differencesDao;
	
	@Resource
	private UserBiz userBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<User2Role> userToRoleDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<User2Group> user2GroupDao;
	
	@Resource
	private Project2QuotaBiz project2QuotaBiz;
	
	@Resource
	private Project2NetworkBiz project2NetworkBiz;
	
	@Resource
	private Network2SubnetDao network2SubnetDao;
	
	@Resource
	private NovaVmBiz novaVmBiz;
	
	@Resource
	private VolumeBiz volumeBiz;
	
	@Resource
	private PortBiz portBiz;
	
	@Resource
	private FirewallBiz firewallBiz;
	
	@Resource
	private RouteBiz routeBiz;
	
	@Resource
	private NetworkBiz networkBiz;
	
	@Resource
	private VlbBiz vlbBiz;
	
	@Resource
	private User2RoleDao user2RoleDao;
	
	@Resource
	private Project2AzoneDao project2AzoneDao;
	
	@Resource
	private PolicieBiz policieBiz;
	
	@Resource
	private PolicieRuleBiz policieRuleBiz;
	
	@Resource
	private VdcBiz vdcBiz;
	
	@Resource
	private SyncVdcDataBiz syncVdcDataBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Subnet> subnetDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<IpAllocation> ipAllocationDao;
	
	@Resource
	private AzoneBiz azoneBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Rules> rulesDao;
	
	@Resource
	public NovaFlavorDao novaFlavorDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<VmExtra> vmExtraDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Sub2Dns> sub2DnsDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Sub2Route> sub2RouteDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<IpAvailabilityRange> rangeDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<IpAllocationPool> poolDao;
	
	@Resource
	private FloatingIpBiz floatingIpBiz;
	
	@Resource
	private VolumeFlavorDao volumeFlavorDao;
	
	@Resource
	private RecycleItemsDao recycleItemsDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Metadata> metadataDao;
	
	@Resource
	private QuotaUsedBiz quotaUsedBiz;
	
	@Resource
	private RoleBiz roleBiz;
	
	@Override
	public String addDiff (String uuid, String projectName, String description, CompareEnum diffType, CompareEnum
			dataType) {
		String taskDispatchId = (String) ThreadContext.get("taskDispatchId");
		if (!StrUtils.checkParam(taskDispatchId)) {
			this.warn("Not found taskDispatchId, diff: uuid[" + uuid + "], description[" +
					description + "], diffType[" + diffType + "], dataType[" + dataType + "]");
			return null;
		}
		Date date = (Date) ThreadContext.get("syncDate");
		if (date == null) {
			date = new Date();
		}
		Differences entity = new Differences();
		entity.setUuid(uuid);
		entity.setDescription("[" + dataType.getOpeValue() + "]" + description);
		entity.setDiffType(diffType.toString());
		entity.setDataType(dataType.toString());
		entity.setProjectName(projectName);
		entity.createdUser(ConfigProperty.SYSTEM_FLAG, date);
		entity.setTaskDispatchId(taskDispatchId);
		entity.setSyncDate(date);
		return super.add(entity);
	}
	
	@Override
	public ResultType handleDiff (Differences entity, CompareEnum handleType) {
		String uuid = entity.getUuid();
		// 差异数据类别
		CompareEnum dataType = CompareEnum.valueOf(entity.getDataType());    // 差异类型
		CompareEnum diffType = CompareEnum.valueOf(entity.getDiffType());    // 差异原因
		CloudosClient client = CloudosClient.createAdmin();
		//本地做删除操作时不需要判断cloudos连接是否正常
		if (!CompareEnum.IN_IYUN.equals(diffType) || !CompareEnum.OPE_DEL.equals(handleType)) {
			if (null == client) {///
				throw new MessageException(ResultType.system_error);
			}
		}
        ResultType resultType = ResultType.success;
		switch (dataType) {
			case PROJECT://租户同步
				this.syncProject(uuid, diffType, handleType, client);
				break;
			case USER://用户同步
				this.syncUser(uuid, diffType, handleType, client);
				break;
			case FIREWALL://防火墙同步
				this.syncFirewall(uuid, diffType, handleType, client);
				break;
			case FIREWALL_RULE://防火墙规则同步
				this.syncRule(uuid, diffType, client);
				break;
			case ROUTER://路由器同步
				this.syncRoute(uuid, diffType, handleType, client);
				break;
			case NETWORK://网络同步
				this.syncNetwork(uuid, diffType, handleType, client);
				break;
			case PUBLIC_NETWORK://公网同步
				this.syncNetwork(uuid, diffType, handleType, client);
				break;
            case CLOUDHOST: //云主机同步
                resultType = this.syncCloudHost(uuid, diffType, handleType, client);
                break;
            case VOLUME: //云主机同步
                resultType = this.syncVolume(uuid, diffType, handleType, client);
                break;
			case FLOATINGIP://公网同步
				this.syncFloatingIp(uuid, diffType, handleType, client);
				break;
			default:
				break;
		}
        return resultType;
	}
	
	/**
	 * 同步用户差异数据
	 * @param uuid 差异数据的id
	 * @param diffType 差异类型
	 * @param handleType 处理类型
	 * @param client cloudos连接
	 */
	private void syncUser (String uuid, CompareEnum diffType, CompareEnum handleType, CloudosClient client) {
		//根据数据差异类型同步差异数据
		switch (diffType) {
			case IN_CLOUDOS://只存在于cloudos
				//判断用户在cloudos是否存在
				String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_USERS_ACTION);
				uri = HttpUtils.tranUrl(uri, uuid);
				JSONObject userJson = HttpUtils.getJson(uri, "user", client);
				if (!StrUtils.checkParam(userJson)) {//数据在cloudos上不存在时抛出异常
					throw new MessageException(ResultType.not_exist_in_cloudos);
				}
				String projectId = userJson.getString("default_project_id");
				if (null == projectId) {
					String keystoneUri = "/keystone/users/" + uuid;
					JSONObject keystoneJSON = HttpUtils.getJson(keystoneUri, "user", client);
					if(keystoneJSON != null) {
						JSONObject project = keystoneJSON.getJSONObject("project");
						projectId = project.getString("id");
						if(!StrUtils.checkParam(projectId)) {
							throw new MessageException(ResultType.cannot_get_projectid);
						}
					}
				}
				//根据处理类型同步差异数据
				switch (handleType) {
					case OPE_SYNC://在iyun补充差异数据
						//保存用户基本信息
						String name = userJson.getString("name");
						String fullname = userJson.getString("fullname");
						Project project = projectBiz.findById(Project.class, projectId);
						if (!StrUtils.checkParam(project)) {//检查关联租户是否存在
							throw new MessageException(ResultType.relate_project_still_not_synchronization);
						}
						
						//判断用户在iyun是否已存在
						List<User> users = userBiz.findByPropertyName(User.class, "cloudosId", uuid);
						if (StrUtils.checkParam(users)) {//已存在则抛出异常
							throw new MessageException(ResultType.diff_user_exists);
						}
						User user = new User();
						user.setCloudosId(uuid);
						user.setLoginName(name);
						user.setUserName(fullname);
						user.setProjectId(projectId);
						user.createdUser(this.getLoginUser());
						userBiz.add(user);
						// 根租户下的用户需要增加默认群组
						if (singleton.getRootProject().equals(this.getProjectId())) {
							// 添加系统默认群组
							User2Group u2g = new User2Group(user.getId(), this.getLoginUser());
							user2GroupDao.add(u2g);
						}
						break;
					case OPE_DEL://在cloudos删除差异数据
						//判断关联资源
						ResultType rs = containResource(getUriMap(CompareEnum.USER, client.getTenantId()), "user_id", uuid);
						if (!ResultType.success.equals(rs)) {//有关联资源时抛出异常
							throw new MessageException(rs);
						}
						JSONObject jsonObject = client.delete(uri);
						if (!ResourceHandle.judgeResponse(jsonObject)) {//删除失败时抛出失败的异常信息
							String error = HttpUtils.getError(jsonObject);
							this.warn("DELETE USER ERROR : " + error);
							throw new MessageException(error);
						}
						break;
					default:break;
				}
				
				break;
			case IN_IYUN://只存在于iyun
				//判断用户在iyun是否存在
				User user = userBiz.findById(User.class, uuid);
				if (!StrUtils.checkParam(user)) {
					throw new MessageException(ResultType.not_exist_in_iyun);//数据在iyun上不存在抛出异常
				}
				if (user.getProjectId().equals(singleton.getCtTenantId())) {
					throw new MessageException(ResultType.telecome_project_not_need_sychronization);//电信租户下的用户不进行数据同步
				}
				
				//根据处理类型同步差异数据
				switch (handleType) {
					case OPE_SYNC://同步到cloudos
						//保存用户基本信息
						CloudosUser cloudosUser = new CloudosUser(client);
						JSONObject jsonObject = cloudosUser.save(user, user.getProjectId(), null);//保存用户
						if (ResourceHandle.judgeResponse(jsonObject)) {
							jsonObject = HttpUtils.getJSONObject(jsonObject, "user");
							String userId = jsonObject.getString("id");
							
							//授予用户角色
							String roleCdId = getGroupId(uuid, user.getProjectId());
							uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_GRANT);
							uri = HttpUtils.tranUrl(uri, user.getProjectId(), userId, roleCdId);
							jsonObject = client.put(uri);
							if (!ResourceHandle.judgeResponse(jsonObject)) {//授权发生错误时回滚并抛出异常
								cloudosUser.delete(userId);//回滚
								String error = HttpUtils.getError(jsonObject);
								this.warn("ADD USER ROLE ERROR : " + error);
								throw new MessageException(error);
							}
						} else {//保存用户发生错误时抛出异常
							String error = HttpUtils.getError(jsonObject);
							this.warn("SAVE USER ERROR : " + error);
							throw new MessageException(error);
						}
						break;
					case OPE_DEL://删除在iyun里面的差异部分
						//检查关联资源
						if (!checkLocalResource(CompareEnum.USER, uuid, null)) {
							throw new MessageException(ResultType.still_relate_resouces);
						}
						Map<String, Object> deleteMap = new HashMap<>();
						deleteMap.put("userId", uuid);
						userToRoleDao.delete(deleteMap, User2Role.class);//删除用户与角色对应关系
						user2GroupDao.delete(deleteMap, User2Group.class);//删除用户与群组对应关系
						userBiz.delete(user);//删除用户
						break;
					default:
						break;
				}
				
				break;
			default:break;
		}
	}
	
	/**
	 * 同步租户差异数据
	 * @param uuid 差异数据的id
	 * @param diffType 差异类型
	 * @param handleType 处理类型
	 * @param client cloudos连接
	 */
	private void syncProject (String uuid, CompareEnum diffType, CompareEnum handleType, CloudosClient client) {
		//根据数据差异类型类型同步差异数据
		switch (diffType) {
			case IN_CLOUDOS://只存在cloudos
				String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_PROJECTS_ACTION);
				uri = HttpUtils.tranUrl(uri, uuid);
				//判断租户在cloudos是否存在
				JSONObject projectJson = HttpUtils.getJson(uri, "project", client);
				if (!StrUtils.checkParam(projectJson)) {
					throw new MessageException(ResultType.not_exist_in_cloudos);//在cloudos不存在则抛出异常
				}
				//根据处理类型同步差异数据
				switch (handleType) {
					case OPE_SYNC://同步到iyun
						//保存租户
						String name = projectJson.getString("name");
						String parentId = projectJson.getString("parent_id");
						String domainId = projectJson.getString("domain_id");
						String description = projectJson.getString("description");
						Boolean enabled = projectJson.getBoolean("enabled");
						Project project = projectBiz.findById(Project.class, uuid);
						if (StrUtils.checkParam(project)) {//租户已存在抛出异常
							throw new MessageException(ResultType.diff_project_exists);
						}
						project = new Project(name, null, name, parentId);
						project.setId(uuid);
						project.setEnabled(enabled);
						project.setDomainId(domainId);
						project.setFlag(0);
						project.createdUser(this.getLoginUser());
						projectBiz.add(project);
						singleton.getProjectNameMap().put(project.getId(), project.getName());

						//同步配额
						syncIyunQuota(uuid, client);
							
						//同步可用域
						syncIyunAzone(uuid, client);
							
						break;
					case OPE_DEL://删除在cloudos里面的差异部分
						//判断关联资源
						ResultType rs = containResource(getUriMap(CompareEnum.PROJECT, client.getTenantId()), "tenant_id", uuid);
						if (!ResultType.success.equals(rs)) {//有关联资源时抛出异常
							throw new MessageException(rs);
						}
						
						//删除租户
						JSONObject jsonObject = client.delete(uri);
						if (!ResourceHandle.judgeResponse(jsonObject)) {//删除失败时抛出失败的异常信息
							String error = HttpUtils.getError(jsonObject);
							throw new MessageException(error);
						}
						break;
					default:break;
				}
				
				break;
			case IN_IYUN://只存在于iyun
				//判断租户在iyun是否存在
				Project project = projectBiz.findById(Project.class, uuid);
				if (!StrUtils.checkParam(project)) {
					throw new MessageException(ResultType.not_exist_in_iyun);//不存在抛出异常
				}
				
				//根据处理类型同步差异数据
				switch (handleType) {
					case OPE_SYNC://同步到cloudos
						//同步租户基本信息-----(id无法同步)
						/*CloudosProject cloudosProject = new CloudosProject(client);
						JSONObject jsonObject = cloudosProject.save(project);
						if (!ResourceHandle.judgeResponse(jsonObject)) {//新增失败时抛出失败的异常信息
							String error = getError(jsonObject);
							this.warn("SAVE PROJECT ERROR : " + error);
							throw new MessageException(error);
						}
						
						//同步可用域
						syncAzone(uuid, client);
						
						//同步配额
						syncQuota(uuid, project.getParentId(), client);*/
						
						break;
					case OPE_DEL:// 删除在iyun中多出来的部分
						//判断关联用户
						List<User> users = userBiz.findByPropertyName(User.class, "projectId", uuid);
						if (StrUtils.checkCollection(users)) {
							throw new MessageException(ResultType.project_have_user);
						}
						//判断关联角色
						List<Role> roles = roleBiz.findByPropertyName(Role.class, "projectId", project.getId());
						if (StrUtils.checkCollection(roles)) {
							throw new MessageException(ResultType.project_have_role);
						}
						//判断关联资源
						if (!checkLocalResource(CompareEnum.PROJECT, null, uuid)) {
							throw new MessageException(ResultType.still_relate_resouces);
						}
						
						User user = userBiz.findById(User.class, "989116e3-79a2-426b-bfbe-668165104885");
						//删除租户
						try {
							projectBiz.deleteProject(project, client, MonitorClient.createAdmin(user));
						} catch (Exception e) {
							this.exception(e, "Delete project failure.");
							throw new MessageException(ResultType.project_exist_resource);
						}
						break;
					default:break;
				}
				
				break;
			case DATA_DIFF://数据差异
				String url = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_PROJECTS_ACTION);
				url = HttpUtils.tranUrl(url, uuid);
				Project tenant = projectBiz.findById(Project.class, uuid);
				if (StrUtils.checkParam(tenant)) {
					JSONObject jsonObject = HttpUtils.getJson(url, "project", client);
					if (StrUtils.checkParam(jsonObject)) {
						String name = jsonObject.getString("name");
						tenant.setName(name);//修改名称
						projectBiz.update(tenant);
					}
				} else {
					throw new MessageException(ResultType.not_exist);
				}
				break;
			default:break;
		}
	}
	
	private void setQuotaValues (String uuid, JSONObject jsonObj, String[] cloudosKeys, String[] iyunKeys) {
		for (int i = 0; i < cloudosKeys.length; i++) {
			Integer gigabytes = 0;
			try {
				if ("ram".equals(cloudosKeys[i])) {
					gigabytes = jsonObj.getInteger(cloudosKeys[i])/1024;
				}else {
					gigabytes = jsonObj.getInteger(cloudosKeys[i]);// 存储
				}
			} catch (Exception e) {
				this.exception(e, "Value to Integer failure. cloudosKey: " + cloudosKeys[i] + ". json:" + jsonObj
						.toJSONString());
			}
			//uuid,class查询,如果数据不存在就新增，如果数据存在，判断
			Map<String,String> map = new HashMap<String,String>();
			map.put("tenantId", uuid);
			String classCode = cloudosKeys[i];
			if(classCode.equals("vnic")) {
				classCode = "ips";
			} 
			map.put("classCode", classCode);
			List<Project2Quota> dtolist = project2QuotaBiz.findByMap(Project2Quota.class, map);
			if(dtolist.size()==0) {
				project2QuotaBiz.addQuota(uuid, iyunKeys[i], gigabytes);
			}else{
				Project2Quota dto = dtolist.get(0);
				if(dto.getHardLimit().intValue() != gigabytes.intValue()) {
					dto.setHardLimit(gigabytes);
					dto.setUpdatedAt(new Date());
					project2QuotaBiz.update(dto);
				}

			}
			
			 
		}
	}
	

//	/**
//	 * 同步配额到cloudos
//	 * @param tenantId
//	 * @param parentId
//	 * @param client
//	 */
//	private void syncQuota (String tenantId, String parentId, CloudosClient client) {
//		List<Project2Quota> project2Quotas = project2QuotaBiz.findByPropertyName(Project2Quota.class,
//				"tenantId", tenantId);
//		List<Project2Network> project2Networks = project2NetworkBiz.findByPropertyName
//				(Project2Network.class, "tenantId", tenantId);
//		List<String> storageCodes = new ArrayList<>(Arrays.asList("gigabytes", "snapshots", "volumes"));
//		List<String> computeCodes = new ArrayList<>(Arrays.asList("ram", "instances", "cores"));
//		List<String> networkCodes = new ArrayList<>(Arrays.asList("firewall", "vnic", "loadbalancer", "floatingip",
//				"listener", "security_group", "ipsecpolicy", "router", "vpnservice", "network",
//				"security_group_rule"));
//		Map<String, Object> param = new HashMap<>();
//		Map<String, Object> networkParam = new HashMap<>();
//		Map<String, Object> storageParam = new HashMap<>();
//		Map<String, Object> cpmputeParam = new HashMap<>();
//		Map<String, Object> storageQuotaMap = new HashMap<>();
//		Map<String, Object> computeQuotaMap = new HashMap<>();
//		List<Map<String, Object>> networkAddress = new ArrayList<>();
//		if (StrUtils.checkCollection(project2Quotas)) {
//			for (Project2Quota project2Quota : project2Quotas) {
//				String classCode = project2Quota.getClassCode();
//				Integer limit = project2Quota.getHardLimit();
//				if (null == limit) {
//					limit = 0;
//				}
//				if (storageCodes.contains(classCode)) {
//					storageQuotaMap.put(classCode, limit);
//				} else if (computeCodes.contains(classCode)) {
//					computeQuotaMap.put(classCode, limit);
//				} else if (networkCodes.contains(classCode)) {
//					if ("ips".equals(classCode)) {
//						networkParam.put("port", limit);
//					} else {
//						networkParam.put(classCode, limit);
//					}
//				}
//			}
//			storageParam.put("quota_set", storageQuotaMap);
//			cpmputeParam.put("quota_set", computeQuotaMap);
//		}
//		if (StrUtils.checkCollection(project2Networks)) {
//			for (Project2Network project2Network : project2Networks) {
//				Map<String, Object> addressMap = new HashMap<>();
//				String cidr = project2Network.getCidr();
//				addressMap.put("cidr", cidr);
//				List<Network2Subnet> network2Subnets = network2SubnetDao.findByPropertyName(Network2Subnet.class,
//						"networkId",
//						project2Network.getId());
//				if (StrUtils.checkCollection(network2Subnets)) {
//					List<Map<String, String>> poolList = new ArrayList<>();
//					for (Network2Subnet network2Subnet : network2Subnets) {
//						String startIp = network2Subnet.getStartIp();
//						String endIp = network2Subnet.getEndIp();
//						Map<String, String> poolMap = new HashMap<>();
//						poolMap.put("start_ip", startIp);
//						poolMap.put("end_ip", endIp);
//						poolList.add(poolMap);
//					}
//					addressMap.put("ip_pools", poolList);
//				}
//
//			}
//			networkParam.put("network_addresses", networkAddress);
//		}
//		param.put("network", networkParam);
//		param.put("block_storage", storageParam);
//		param.put("compute", cpmputeParam);
//		if (!StrUtils.checkParam(parentId)) {
//			parentId = tenantId;
//		}
//		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_QUOTA);
//		uri = HttpUtils.tranUrl(uri, parentId, tenantId);
//		JSONObject result = client.put(uri, param);
//		if (!ResourceHandle.judgeResponse(result)) {
//			this.warn("Synchronization Project QUOTA Error : " + result);
//		}
//	}
	
//	/**
//	 * 同步可用域到cloudos
//	 * @param tenantId
//	 * @param client
//	 */
//	private void syncAzone (String tenantId, CloudosClient client) {
//		List<Project2Azone> project2Azones = p2aDao.findByPropertyName(Project2Azone.class, "id",
//				tenantId);
//		if (StrUtils.checkCollection(project2Azones)) {
//			for (Project2Azone project2Azone : project2Azones) {
//				String azoneId = project2Azone.getIyuUuid();
//				String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_KEYSTONE);
//				uri = HttpUtils.tranUrl(uri, tenantId, azoneId);
//				JSONObject result = client.put(uri);
//				if (!ResourceHandle.judgeResponse(result)) {
//					this.warn("Synchronization Project Azone Error : " + result);
//				}
//			}
//		}
//	}
	
	/**
	 * 同步配额到iyun
	 * @param tenantId 租户id
	 */
	@Override
	public void syncIyunQuota (String tenantId, CloudosClient client) {
		String url = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_QUOTA, client.getTenantId(),
				tenantId);
		JSONObject record = HttpUtils.getJSONObject(client.get(url));
		if (null != record) {
			JSONObject storageObject = record.getJSONObject("block_storage");
			if (StrUtils.checkParam(storageObject)) {                                //存储配额
				JSONObject storageJson = storageObject.getJSONObject("quota_set");
				if (StrUtils.checkParam(storageJson)) {
					String[] cloudosKeys = {"gigabytes", "snapshots", "volumes"};
					String[] iyunKeys = {
							ConfigProperty.RESOURCE_TYPE_GIGABYTES,    //存储容量
							ConfigProperty.RESOURCE_TYPE_SNAPSHOTS,    //快照
							ConfigProperty.RESOURCE_TYPE_VOLUME        //硬盘
					};
					this.setQuotaValues(tenantId, storageJson, cloudosKeys, iyunKeys);
				}
			}
			JSONObject computeObject = record.getJSONObject("compute");                //计算配额
			if (StrUtils.checkParam(computeObject)) {
				JSONObject computeJson = computeObject.getJSONObject("quota_set");
				if (StrUtils.checkParam(computeJson)) {
					String[] cloudosKeys = {"cores", "ram", "instances"};
					String[] iyunKeys = {
							ConfigProperty.RESOURCE_TYPE_CORES,        //CPU
							ConfigProperty.RESOURCE_TYPE_RAM,        //内存
							ConfigProperty.RESOURCE_TYPE_INSTANCES    //主机数
					};
					this.setQuotaValues(tenantId, computeJson, cloudosKeys, iyunKeys);
				}
			}
			JSONObject networkJson = record.getJSONObject("network");                //网络配额
			if (StrUtils.checkParam(networkJson)) {
				String[] cloudosKeys = {"firewall", "floatingip", "ipsecpolicy", "listener",
						"loadbalancer",
						"network", "vnic", "router", "security_group", "security_group_rule",
						"vpnservice"};
				String[] iyunKeys = {
						ConfigProperty.RESOURCE_TYPE_FIREWALL,        // 防火墙
						ConfigProperty.RESOURCE_TYPE_FLOATINGIP,    // 公网ip
						ConfigProperty.RESOURCE_TYPE_IPSECPOLICY,    // IPsec策略
						ConfigProperty.RESOURCE_TYPE_VLBPOOL,        // 负载均衡
						ConfigProperty.RESOURCE_TYPE_VLB,            // 负载均衡组
						ConfigProperty.RESOURCE_TYPE_NETWORK,        // 网络
						ConfigProperty.RESOURCE_TYPE_PORT,            // 网卡
						ConfigProperty.RESOURCE_TYPE_ROUTE,          // 路由器
						ConfigProperty.RESOURCE_TYPE_SECURITYGROUP, // 安全组
						ConfigProperty.RESOURCE_TYPE_SECURITYGROUPRULE, // 安全组规则
						ConfigProperty.RESOURCE_TYPE_VPNSERVICE        // vpn
				};
				this.setQuotaValues(tenantId, networkJson, cloudosKeys, iyunKeys);
				JSONArray addressArray = networkJson.getJSONArray("network_addresses");// 网段配额
				if (StrUtils.checkParam(addressArray)) {
					for (int i = 0; i < addressArray.size(); i++) {
						JSONObject addressJson = addressArray.getJSONObject(i);
						String cidr = addressJson.getString("cidr");
						Map<String,String> map = new HashMap<String,String>();
						map.put("tenantId", tenantId);
						map.put("cidr", cidr);
						List<Project2Network> ProList = project2NetworkBiz.findByMap(Project2Network.class, map);
						if(ProList.size() > 0) {
							continue;
						}
						Project2Network project2Network = new Project2Network(tenantId, cidr);
						project2Network.createdUser(this.getLoginUser());
						String ntId = project2NetworkBiz.add(project2Network);
						JSONArray poolArray = addressJson.getJSONArray("ip_pools");// 网络允许ip段
						if (StrUtils.checkParam(poolArray)) {
							for (int j = 0; j < poolArray.size(); j++) {
								JSONObject poolJson = poolArray.getJSONObject(j);
								String startIp = poolJson.getString("start_ip");
								String endIp = poolJson.getString("end_ip");
								Network2Subnet network2Subnet = new Network2Subnet(startIp, endIp, ntId);
								network2Subnet.createdUser(this.getLoginUser());
								network2SubnetDao.add(network2Subnet);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 同步可用域到iyun
	 * @param tenantId 租户id
	 * @param client cloudos连接
	 */
	@Override
	public void syncIyunAzone (String tenantId, CloudosClient client) {
		JSONObject projectAzones = client.get(singleton.getCloudosApi(CloudosParams
				.CLOUDOS_API_PROJECT_AZONE));
		if (ResourceHandle.judgeResponse(projectAzones)) {
			JSONObject recordJson = projectAzones.getJSONObject("record");
			if (StrUtils.checkParam(recordJson)) {
				JSONArray assigments = recordJson.getJSONArray("assigments");
				if (StrUtils.checkCollection(assigments)) {
					for (int i = 0; i < assigments.size(); i++) {
						JSONObject assigment = assigments.getJSONObject(i);
						JSONObject azoneObject = assigment.getJSONObject("azone");
						JSONObject projectObject = assigment.getJSONObject("project");
						if (tenantId.equals(projectObject.getString("id"))) {
							Project2Azone project2Azone = new Project2Azone();
							project2Azone.setId(tenantId);
							project2Azone.setIyuUuid(azoneObject.getString("id"));
							project2AzoneDao.add(project2Azone);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 判断cloudos用户或租户下是否关联相关资源
	 * @param uriMap uri集合
	 * @param key key
	 * @param value 值
	 * @return 返回resultType
	 */
	private ResultType containResource (Map<String, String> uriMap, String key, String value) {
		for (String s : uriMap.keySet()) {
			ResultType rs = containResource(uriMap.get(s), s, key, value);
			if (!ResultType.success.equals(rs)) {
				return rs;
			}
		}
		return ResultType.success;
	}
	
	/**
	 * 判断cloudos用户或租户下是否关联相关资源
	 * @param uri 获取数据的uri
	 * @param resourceType 资源类型
	 * @param key key
	 * @param value key对应的值
	 * @return 返回resultType
	 */
	private ResultType containResource (String uri, String resourceType, String key, String value) {
		CloudosClient client = CloudosClient.createAdmin();
		JSONArray jsonArray = HttpUtils.getArray(uri, resourceType, null, client);
		if (StrUtils.checkCollection(jsonArray)) {
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				if ("users".equals(resourceType) && "tenant_id".equals(key)) {
					if (value.equals(json.getString("default_project_id"))) {//判断租户底下是否有用户
						return ResultType.still_relate_user;
					}
				}else {
					if (value.equals(json.getString(key))) {//当有一个资源为当前用户或租户时，返回提示信息
						return ResultType.still_relate_resouces;
					}
				}
			}
		}
		return ResultType.success;
	}
	
	/**
	 * 根据用户角色和租户权限获取cloudos用户对应角色id
	 * @param userId 用户id
	 * @param projectId 租户id
	 * @return 返回组id
	 */
	private String getGroupId (String userId, String projectId) {
		List<User2Role> user2Roles = user2RoleDao.findByPropertyName(User2Role.class, "userId", userId);
		List<String> roleIds = new ArrayList<>();
		if (StrUtils.checkCollection(user2Roles)) {
			for (User2Role user2Role : user2Roles) {
				roleIds.add(user2Role.getRoleId());
			}
		}
		return getGroupId(roleIds, projectId);
	}
	
	public String getGroupId(List<String> roleIds, String projectId) {
		String groupId;
		if (projectId.equals(singleton.getRootProject())) {//根租户
			if (roleIds.contains(singleton.getCloudRoleId())) {//云管理员赋云管理员角色
				groupId = singleton.getConfigValue("cloudos.tenant.cloud.manager");
			} else {//非云管理员赋审计管理员角色
				groupId = singleton.getConfigValue("cloudos.tenant.comptroller");
			}
		} else {//非根租户
			if (roleIds.contains(singleton.getTenantRoleId())) {//租户管理员赋组织管理员角色
				groupId = singleton.getConfigValue("cloudos.tenant.normal.manager");
			} else {//非租户管理员赋普通用户角色
				groupId = singleton.getConfigValue("cloudos.tenant.normal.user");
			}
		}
		return groupId;
	}
	
	/**
	 * 获取cloudos资源uri
	 * @param type 数据类型
	 * @param tenantId 租户id
	 * @return 返回uri集合
	 */
	private Map<String, String> getUriMap (CompareEnum type, String tenantId) {
		Map<String, String> uriMap = new HashMap<>();
		switch (type) {
			case PROJECT:
				String volumeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VOLUMES);
				volumeUri = HttpUtils.tranUrl(volumeUri, tenantId);//硬盘资源
				uriMap.put("volumes", volumeUri);
				uriMap.put("firewalls", singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FIREWALL));
				uriMap.put("routers", singleton.getCloudosApi(CloudosParams.CLOUDOS_API_ROUTE));
				uriMap.put("networks", singleton.getCloudosApi(CloudosParams.CLOUDOS_API_NETWORK));
				uriMap.put("vlb", singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VLB));
				uriMap.put("ports", singleton.getCloudosApi(CloudosParams.CLOUDOS_API_PORTS));
				uriMap.put("users", singleton.getCloudosApi(CloudosParams.CLOUDOS_API_USERS));
				break;
			case USER:
				String serverUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SERVER);
				serverUri = HttpUtils.tranUrl(serverUri, tenantId);//主机资源
				uriMap.put("servers", serverUri);
				String volumeUrl = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VOLUMES);
				volumeUrl = HttpUtils.tranUrl(volumeUrl, tenantId);//硬盘资源
				uriMap.put("volumes", volumeUrl);
				uriMap.put("floatingips", singleton.getCloudosApi(CloudosParams.CLOUDOS_API_FLOATINGIP));
				//公网ip资源
				break;
			default:break;
		}
		return uriMap;
	}
	
	/**
	 * 检查用户或租户本地资源关联情况
	 * @param type 类型
	 * @param userId 用户id
	 * @param tenantId 租户id
	 * @return 返回判断结果
	 */
	public boolean checkLocalResource (CompareEnum type, String userId, String tenantId) {
		switch (type) {
			case PROJECT:
				int volumes = volumeBiz.findCountByPropertyName(Volume.class, "projectId", tenantId);
				if (volumes > 0) {
					return false;
				}
				int firewalls = firewallBiz.findCountByPropertyName(Firewall.class, "tenantId", tenantId);
				if (firewalls > 0) {
					return false;
				}
				int routes = routeBiz.findCountByPropertyName(Route.class, "tenantId", tenantId);
				if (routes > 0) {
					return false;
				}
				int networks = networkBiz.findCountByPropertyName(Network.class, "tenantId", tenantId);
				if (networks > 0) {
					return false;
				}
				int vlbs = vlbBiz.findCountByPropertyName(Vlb.class, "projectId", tenantId);
				if (vlbs > 0) {
					return false;
				}
				int ports = portBiz.findCountByPropertyName(Port.class, "tenantId", tenantId);
				if (ports > 0) {
					return false;
				}
				int vms = novaVmBiz.findCountByPropertyName(NovaVm.class, "projectId", tenantId);
				if (vms > 0) {
					return false;
				}
				break;
			case USER:
				int portList = portBiz.findCountByPropertyName(Port.class, "userId", userId);
				if (portList > 0) {
					return false;
				}
				int volumeList = volumeBiz.findCountByPropertyName(Volume.class, "owner2", userId);
				if (volumeList > 0) {
					return false;
				}
				int vmList = novaVmBiz.findCountByPropertyName(NovaVm.class, "owner", userId);
				if (vmList > 0) {
					return false;
				}
				break;
			default:break;
		}
		return true;
	}
	
	/**
	 * 同步防火墙差异信息
	 * @param uuid 差异数据的id
	 * @param diffType 差异类型
	 * @param handleType 处理类型
	 * @param client cloudos连接
	 */
	private void syncFirewall (String uuid, CompareEnum diffType, CompareEnum handleType, CloudosClient client) {
		String localId = null;
		String cloudId = null;
		String cloudPyId = null;
		Firewall firewall = null;
		JSONObject fwJson = null;
		JSONObject pyJson = null;
		JSONObject normJson = null;
		//数据只存在iyun与两边存在数据差异时
		if (CompareEnum.IN_IYUN.equals(diffType) || CompareEnum.DATA_DIFF.equals(diffType)) {
			localId = uuid;//防火墙在iyun的id
		}
		//数据只存在cloudos时
		if (CompareEnum.IN_CLOUDOS.equals(diffType)) {
			cloudId = uuid;//防火墙在cloudos的id
		}
		if (null != localId) {//判断防火墙在iyun是否存在
			firewall = firewallBiz.findById(Firewall.class, uuid);//iyun的防火墙信息
			if (!StrUtils.checkParam(firewall)) {
				throw new MessageException(ResultType.not_exist_in_iyun);
			}
			if (CompareEnum.DATA_DIFF.equals(diffType)) {
				cloudId = firewall.getCloudosId();//防火墙在cloudos的id
			}
		}
		if (null != cloudId) {//判断防火墙在cloudos是否存在
			fwJson = firewallBiz.getFirewallJson(cloudId, client);//cloudos的防火墙信息
			if (!StrUtils.checkParam(fwJson)) {
				throw new MessageException(ResultType.not_exist_in_cloudos);
			}
			normJson = firewallBiz.getNormJson(cloudId, client);//cloudos的吞吐量信息
			cloudPyId = fwJson.getString("firewall_policy_id");
			pyJson = policieBiz.getPolicyJson(cloudPyId, client);//cloudos的规则集信息
		}
		//根据数据差异类型处理差异数据
		switch (diffType) {
			case IN_CLOUDOS://只存在cloudos
				
				List<Firewall> firewalls = firewallBiz.findByPropertyName(Firewall.class, "cloudosId", cloudId);
				if (StrUtils.checkCollection(firewalls)) {//检查本地是否已经存在
					throw new MessageException(ResultType.already_exist_in_iyun);
				}
				
				switch (handleType) {
					case OPE_DEL://在cloudos上删除防火墙
						// 判断关联资源
						JSONArray rtIds = fwJson.getJSONArray("router_ids");
						if (StrUtils.checkCollection(rtIds)) {//判断关联路由器
							throw new MessageException(ResultType.still_relate_route);
						}
						if (StrUtils.checkParam(pyJson)) {
							JSONArray ruleArray = pyJson.getJSONArray("firewall_rules");
							if (StrUtils.checkCollection(ruleArray)) {//判断关联规则
								throw new MessageException(ResultType.still_relate_policieRule);
							}
						}
						String rs = firewallBiz.cloudDelete(uuid, cloudPyId, client);//在cloudos上删除防火墙
						if (!"success".equals(rs)) {
							throw new MessageException(rs);
						}
						break;
					case OPE_SYNC://同步到iyun
						
						Firewall entity = firewallBiz.getFirewallByJson(fwJson);
						String vdcId = vdcBiz.getVdc(entity.getTenantId()).getId();
						entity.setUuid(UUID.randomUUID().toString());
						if (StrUtils.checkParam(normJson)) {
							Integer throughPut = normJson.getInteger("throughPut");
							entity.setThroughPut(throughPut);
						}
						entity.createdUser(this.getLoginUser());
						Policie policie = policieBiz.getPolicyByJson(pyJson);
						policie.createdUser(this.getLoginUser());
						firewallBiz.localSave(entity, policie, vdcId, entity.getStatus());//本地保存防火墙和规则集信息
						break;
					default:break;
				}
				break;
			case IN_IYUN://只存在iyun
				switch (handleType) {
					case OPE_DEL://在iyun上删除
						// 判断关联资源
						List<PolicieRule> rules = policieRuleBiz.findByPropertyName(PolicieRule.class, "policyId",
								firewall.getPolicyId());
						if (StrUtils.checkCollection(rules)) {//判断关联路由器
							throw new MessageException(ResultType.still_relate_policieRule);
						}
						List<Route> routes = routeBiz.findByPropertyName(Route.class, "fwId", localId);
						if (StrUtils.checkCollection(routes)) {//判断关联规则
							throw new MessageException(ResultType.still_relate_route);
						}
						firewallBiz.localDelete(firewall);//删除
						break;
					case OPE_SYNC://在cloudos上新增
						Policie policie = policieBiz.findById(Policie.class, firewall.getPolicyId());
						String rs = firewallBiz.cloudSave(firewall, policie, client, "vdc");
						if (!"success".equals(rs)) {
							throw new MessageException(rs);
						}
						firewallBiz.updateStatus(localId, firewall.getStatus());
						break;
					default:break;
				}
				break;
			case DATA_DIFF://两边存在数据差异（吞吐量和描述）
				String description = fwJson.getString("description");
				String status = fwJson.getString("status");
				firewall.setDescription(description);
				firewall.setStatus(status);
				if (StrUtils.checkParam(normJson)) {
					Integer throughPut = normJson.getInteger("throughPut");
					firewall.setThroughPut(throughPut);
				}
				firewallBiz.localUpdate(localId, firewall);
				break;
			default:break;
		}
	}
	
	/**
	 * 同步防火墙规则差异信息(将所有规则同步)
	 * @param uuid 规则所属的防火墙在iyun上的id
	 * @param diffType 差异类型
	 * @param client cloudos连接
	 */
	private void syncRule (String uuid, CompareEnum diffType, CloudosClient client) {
		Firewall firewall = firewallBiz.findById(Firewall.class, uuid);
		if (!StrUtils.checkParam(firewall)) {
			throw new MessageException(ResultType.firewall_not_exist);
		}
		JSONObject pyJson = policieBiz.getPolicyJson(firewall.getPolicyCloudosId(), client);//cloudos的规则集信息
		if (!StrUtils.checkParam(pyJson)) {
			throw new MessageException(ResultType.relate_firewall_still_not_synchronization);
		}
		if (diffType.equals(CompareEnum.DATA_DIFF)) {
			List<PolicieRule> rules = policieRuleBiz.findByPropertyName(PolicieRule.class, "policyId", firewall.getPolicyId());
			if (StrUtils.checkCollection(rules)) {//删除原本的规则
				for (PolicieRule rule : rules) {
					policieRuleBiz.delete(rule);
				}
			}
			JSONArray ruleIds = pyJson.getJSONArray("firewall_rules");
			if (StrUtils.checkParam(ruleIds)){//查询规则信息
				for (int i = 0; i < ruleIds.size(); i++) {
					String ruleCdId = ruleIds.getString(i);
					JSONObject ruleJson = policieRuleBiz.getRuleJson(ruleCdId, client);
					if (StrUtils.checkParam(ruleJson)){
						syncVdcDataBiz.syncRule(ruleJson, firewall.getPolicyId());//同步规则信息
					}
				}
			}
		} else {
			throw new MessageException(ResultType.diff_handle_method_error);
		}
	}
	
	
	/**
	 * 同步路由器差异信息
	 * @param uuid 差异数据的id
	 * @param diffType 差异类型
	 * @param handleType 处理类型
	 * @param client cloudos连接
	 */
	private void syncRoute (String uuid, CompareEnum diffType, CompareEnum handleType, CloudosClient client) {
		String localId = null;
		String cloudId = null;
		Route route = null;
		JSONObject routeJson = null;
		//数据只存在iyun与两边存在数据差异时
		if (CompareEnum.IN_IYUN.equals(diffType) || CompareEnum.DATA_DIFF.equals(diffType)) {
			localId = uuid;//路由器在iyun的id
		}
		//数据只存在cloudos时
		if (CompareEnum.IN_CLOUDOS.equals(diffType)) {
			cloudId = uuid;//路由器在cloudos的id
		}
		if (null != localId) {//判断路由器在iyun是否存在
			route = routeBiz.findById(Route.class, uuid);//iyun的防火墙规则信息
			if (!StrUtils.checkParam(route)) {
				throw new MessageException(ResultType.not_exist_in_iyun);
			}
			cloudId = route.getCloudosId();//防火墙规则在cloudos的id
		}
		String networkCdId = null;
		String ip = null;
		Network network = null;
		JSONObject portJson = null;
		if (null != cloudId) {//判断路由器在cloudos是否存在
			routeJson = routeBiz.getRouteJson(cloudId, client);//cloudos的路由器信息
			if (CompareEnum.IN_CLOUDOS.equals(diffType) || CompareEnum.DATA_DIFF.equals(diffType)) {
				if (!StrUtils.checkParam(routeJson)) {
					throw new MessageException(ResultType.not_exist_in_cloudos);
				}
				JSONObject gatewayJson = routeJson.getJSONObject("external_gateway_info");
				if (StrUtils.checkParam(gatewayJson)) {
					JSONArray ipArray = gatewayJson.getJSONArray("external_fixed_ips");
					networkCdId = gatewayJson.getString("network_id");
					if (StrUtils.checkParam(networkCdId)) {
						Map<String, Object> queryMap = new HashMap<>();
						queryMap.put("cloudosId", networkCdId);
						network = networkBiz.singleByClass(Network.class, queryMap);
						ip = ipArray.getJSONObject(0).getString("ip_address");
						JSONArray ports = portBiz.getPortArray(cloudId, "network:router_gateway", client);
						if (StrUtils.checkCollection(ports)) {//查看路由器的外部网关网卡
							portJson = ports.getJSONObject(0);
						}
					}
				}
			}
		}
		
		//根据数据差异类型处理差异数据
		switch (diffType) {
			case IN_CLOUDOS://只存在cloudos
				
				List<Route> routes = routeBiz.findByPropertyName(Route.class, "cloudosId", cloudId);
				if (StrUtils.checkCollection(routes)) {//检查本地是否已经存在
					throw new MessageException(ResultType.already_exist_in_iyun);
				}
				
				//查出该路由器挂载的防火墙
				String fwCdId = getFwCdId(cloudId, client);
				
				switch (handleType) {
					case OPE_DEL://在cloudos上删除路由器
						
						if (StrUtils.checkParam(portJson)) {
							throw new MessageException(ResultType.still_relate_gateway);
						}
						
						//检查路由器底下是否挂载网络(查看是否有网卡)
						JSONArray portArray = portBiz.getPortArray(cloudId, null, client);
						if (StrUtils.checkCollection(portArray)) {
							throw new MessageException(ResultType.still_relate_network);
						}
						
						//检查路由器是否挂载防火墙
						if (null != fwCdId) {
							throw new MessageException(ResultType.still_relate_firewall);
						}
						
						//删除路由器
						String rs = routeBiz.cloudDelete(cloudId, client);
						if (!"success".equals(rs)) {
							throw new MessageException(rs);
						}
						break;
					case OPE_SYNC://同步到iyun
						route = routeBiz.getRouteByJson(routeJson);
					
						if (StrUtils.checkParam(networkCdId)) {
							if (!StrUtils.checkParam(network) || !StrUtils.checkParam(portJson)) {
								throw new MessageException(ResultType.relate_gateway_still_not_synchronization);
							}
							route.setGwNetworkId(network.getId());
							route.setGwPortCdId(portJson.getString("id"));
							route.setExternalGateway(ip);
						}
						
						//本地保存路由器信息
						String vdcId = vdcBiz.getVdc(route.getTenantId()).getId();
						
						if (null != fwCdId) {
							String fwId = getFwId(fwCdId);
							routes = routeBiz.findByPropertyName(Route.class, "fwId", fwId);
							if (StrUtils.checkParam(routes)) {//检查防火墙是否已经关联路由器
								throw new MessageException(ResultType.already_link_route);
							}
							route.setFwId(fwId);
						}
						
						route.setVdcId(vdcId);
						route.setUuid(UUID.randomUUID().toString());
						route.createdUser(this.getLoginUser());
						routeBiz.localSave(route, vdcId, route.getStatus());
						
						break;
					default:break;
				}
				break;
			case IN_IYUN://只存在iyun
				switch (handleType) {
					case OPE_DEL://在iyun上删除
						if (null != route.getFwId()) {
							throw new MessageException(ResultType.still_relate_firewall);
						}
						List<Network> networks = networkBiz.findByPropertyName(Network.class, "routeId", localId);
						if (StrUtils.checkCollection(networks)) {//检查是否关联网络
							throw new MessageException(ResultType.still_relate_network);
						}
						if (StrUtils.checkParam(route.getGwPortId())) {//检查是否设置外部网关
							throw new MessageException(ResultType.already_has_gateway);
						}
						routeBiz.localDelete(route);//删除
						break;
					case OPE_SYNC://在cloudos上新增
						
						if (null != route.getFwId()) {
							Firewall firewall = firewallBiz.findById(Firewall.class, route.getFwId());
							JSONObject fwJson = firewallBiz.getFirewallJson(firewall.getCloudosId(), client);
							if (!StrUtils.checkParam(fwJson)) {//检查关联防火墙是否已经同步到cloudos
								throw new MessageException(ResultType.relate_firewall_still_not_synchronization);
							}
							JSONArray rtIds = fwJson.getJSONArray("router_ids");
							if (StrUtils.checkCollection(rtIds)) {//检查防火墙是否已经关联路由器
								throw new MessageException(ResultType.already_link_route);
							}
						}
						
						if (null != route.getGwPortId()) {//检查外部网关所属网络是否已经同步
							Port gwPort = portBiz.findById(Port.class, route.getGwPortId());
							Network publicNetwork = networkBiz.findById(Network.class, gwPort.getNetWorkId());
							JSONObject networkJson = networkBiz.getNetworkJson(publicNetwork.getCloudosId(), client);
							if (!StrUtils.checkParam(networkJson)) {
								throw new MessageException(ResultType.relate_gateway_still_not_synchronization);
							}
							route.setGwNetworkId(publicNetwork.getId());
						}
						
						String rs = routeBiz.cloudSave(route, client, "vdc");
						if (!"success".equals(rs)) {
							throw new MessageException(rs);
						}
						routeBiz.updateStatus(localId, route.getStatus());
						break;
					default:break;
				}
				break;
			case DATA_DIFF://连接差异
				
				String gwPortId = route.getGwPortId();
				if (StrUtils.checkParam(networkCdId)) {
					if (!StrUtils.checkParam(network) || !StrUtils.checkParam(portJson)) {
						throw new MessageException(ResultType.relate_gateway_still_not_synchronization);
					}
					if (!StrUtils.checkParam(gwPortId)) {
						gwPortId = syncVdcDataBiz.syncPort(portJson, localId, null);
						route.setGwPortId(gwPortId);
					} else {
						Port gwPort = portBiz.findById(Port.class, gwPortId);
						if (!portJson.getString("id").equals(gwPort.getCloudosId())) {
							Map<String, Object> queryMap = new HashMap<>();
							queryMap.put("portId", gwPortId);
							ipAllocationDao.delete(queryMap, IpAllocation.class);
							portBiz.deleteById(Port.class, gwPortId);
							gwPortId = syncVdcDataBiz.syncPort(portJson, localId, null);
							route.setGwPortId(gwPortId);
						}
					}
				} else {
					if (StrUtils.checkParam(gwPortId)) {
						Map<String, Object> queryMap = new HashMap<>();
						queryMap.put("portId", gwPortId);
						ipAllocationDao.delete(queryMap, IpAllocation.class);
						portBiz.deleteById(Port.class, gwPortId);
						route.setGwPortId(null);
					}
				}
				
				//查出该路由器挂载的防火墙
				String fwCloudId = getFwCdId(cloudId, client);
				String fwId = null;
				if (null != fwCloudId) {
					fwId = getFwId(fwCloudId);
				}
				routeBiz.localLink(route, fwId);
				if (null != fwId) {
					firewallBiz.updateStatus(fwId, "ACTIVE");
				}
				break;
			default:break;
		}
	}
	
	/**
	 * 同步网络差异信息
	 * @param uuid 差异数据的id
	 * @param diffType 差异类型
	 * @param handleType 处理类型
	 * @param client cloudos连接
	 */
	private void syncNetwork (String uuid, CompareEnum diffType, CompareEnum handleType, CloudosClient client) {
		String localId = null;
		String cloudId = null;
		Network network = null;
		JSONObject networkJson = null;
		//数据只存在iyun与两边存在数据差异时
		if (CompareEnum.IN_IYUN.equals(diffType) || CompareEnum.DATA_DIFF.equals(diffType)) {
			localId = uuid;//网络在iyun的id
		}
		//数据只存在cloudos时
		if (CompareEnum.IN_CLOUDOS.equals(diffType)) {
			cloudId = uuid;//网络在cloudos的id
		}
		if (null != localId) {//判断网络在iyun是否存在
			network = networkBiz.findById(Network.class, uuid);//iyun的网络信息
			if (!StrUtils.checkParam(network)) {
				throw new MessageException(ResultType.not_exist_in_iyun);
			}
			if (CompareEnum.DATA_DIFF.equals(diffType)) {
				cloudId = network.getCloudosId();//网络在cloudos的id
			}
		}
		if (null != cloudId) {//判断网络在cloudos是否存在
			networkJson = networkBiz.getNetworkJson(cloudId, client);//cloudos的网络信息
			if (!StrUtils.checkParam(networkJson)) {
				throw new MessageException(ResultType.not_exist_in_cloudos);
			}
		}
		//根据数据差异类型处理差异数据
		switch (diffType) {
			case IN_CLOUDOS://只存在cloudos
				//检验本地是否已经存在
				List<Network> networks = networkBiz.findByPropertyName(Network.class, "cloudosId", cloudId);
				if (StrUtils.checkCollection(networks)) {
					throw new MessageException(ResultType.already_exist_in_iyun);
				}
				
				String sbCdId = null;
				
				//查找网络关联的路由器与网关网卡在cloudos上的id
				Map<String, Object> map = getRelateRoute(cloudId, client);
				String portCdId = null == map.get("portCdId") ? null : StrUtils.tranString(map.get("portCdId"));
				String rtCdId = null == map.get("routeCdId") ? null : StrUtils.tranString(map.get("routeCdId"));
				@SuppressWarnings("unchecked")
				List<JSONObject> ntList = null == map.get("portList") ? null : (List<JSONObject>) map.get("portList");
						
				//获取子网的cloudosId
				JSONArray subnets = networkJson.getJSONArray("subnets");
				if (StrUtils.checkCollection(subnets)) {
					sbCdId = subnets.getString(0);
				}
				
				switch (handleType) {
					case OPE_DEL://在cloudos上删除网络
						if (null != rtCdId) {
							throw new MessageException(ResultType.still_relate_route);
						}
						if (StrUtils.checkCollection(ntList)) {
							throw new MessageException(ResultType.still_relate_port);
						}
						networkBiz.cloudDelete(cloudId, sbCdId, client);
						break;
					case OPE_SYNC://同步到iyun
						network = networkBiz.getNetworkByJson(networkJson);
						String vdcId = vdcBiz.getVdc(network.getTenantId()).getId();
						network.setUuid(UUID.randomUUID().toString());
						if (null != rtCdId) {//路由器的cloudosId
							Map<String, Object> queryMap = new HashMap<>();
							queryMap.put("cloudosId", rtCdId);
							Route route = routeBiz.singleByClass(Route.class, queryMap);
							if (!StrUtils.checkParam(route)) {
								throw new MessageException(ResultType.relate_route_still_not_synchronization);
							}
							network.setRouteId(route.getId());
						}
						network.setVdcId(vdcId);
						network.setPortCloudId(portCdId);
						network.createdUser(ConfigProperty.SYSTEM_FLAG);
						
						JSONObject subnetJson = networkBiz.getSubnetJson(sbCdId, client);
						networkBiz.addNetworkParam(network, subnetJson);
						
						Subnet subnet = networkBiz.getSubnetByJson(subnetJson);
						if (!StrUtils.checkParam(subnet.getName())) {
							subnet.setName(network.getName());
						}
						subnet.createdUser(ConfigProperty.SYSTEM_FLAG);
						if(!StrUtils.checkParam(subnet.getName())) {
							subnet.setName(network.getName());
						}
						networkBiz.localSave(network, subnet, vdcId, network.getStatus());
						break;
					default:break;
				}
				break;
			case IN_IYUN://只存在iyun
				String subnetId = network.getSubnetId();
				Subnet subnet = subnetDao.findById(Subnet.class, subnetId);
				switch (handleType) {
					case OPE_DEL://在iyun上删除
						
						//检查是否关联路由器
						if (null != network.getRouteId()) {
							throw new MessageException(ResultType.still_relate_route);
						}
						
						//检查是否关联网卡
						List<IpAllocation> ipAllocations = ipAllocationDao.findByPropertyName(IpAllocation.class, "subnetId",
								subnet.getId());
						if (StrUtils.checkCollection(ipAllocations)) {
							throw new MessageException(ResultType.still_relate_port);
						}
						
						networkBiz.localDelete(network);
						break;
					case OPE_SYNC://在cloudos上新增
						List<Sub2Dns> sub2Dnss = sub2DnsDao.findByPropertyName(Sub2Dns.class, "subnetId", subnetId);
						if (StrUtils.checkCollection(sub2Dnss)) {//DNS数据
							List<String> dnsList = new ArrayList<>();
							for (Sub2Dns sub2Dns : sub2Dnss) {
								dnsList.add(sub2Dns.getAddress());
							}
							network.setDnsList(dnsList);
						}
						List<Sub2Route> sub2Routes = sub2RouteDao.findByPropertyName(Sub2Route.class, "subnetId", subnetId);
						if (StrUtils.checkCollection(sub2Routes)) {
							List<Map<String, String>> routeList = new ArrayList<>();
							for (Sub2Route sub2Route : sub2Routes) {//主机路由数据
								Map<String, String> routeMap = new HashMap<>();
								routeMap.put("destination", sub2Route.getDestination());
								routeMap.put("nexthop", sub2Route.getNextHop());
								routeList.add(routeMap);
							}
							network.setHostRouteList(routeList);
						}
						Map<String, Object> queryMap = new HashMap<>();
						queryMap.put("subnetId", subnetId);
						IpAllocationPool pool = poolDao.singleByClass(IpAllocationPool.class, queryMap);
						List<IpAvailabilityRange> rangeList = rangeDao.findByPropertyName(IpAvailabilityRange.class,
								"allocationPoolId", pool.getId());
						if (StrUtils.checkCollection(rangeList)) {//ip地址池数据
							List<Map<String, String>> poolList = new ArrayList<>();
							for (IpAvailabilityRange ipAvailabilityRange : rangeList) {
								Map<String, String> poolMap = new HashMap<>();
								poolMap.put("start", ipAvailabilityRange.getFirstIp());
								poolMap.put("end", ipAvailabilityRange.getLastIp());
								poolList.add(poolMap);
							}
							network.setIpPoolList(poolList);
						}
						String rs = networkBiz.cloudSave(network, subnet, client, "vdc");
						if (!"success".equals(rs)) {
							throw new MessageException(rs);
						}
						networkBiz.updateStatus(localId, network.getStatus());
						break;
					default:break;
				}
				break;
			case DATA_DIFF://连接差异
				Map<String, Object> relateMap = getRelateRoute(cloudId, client);
				String routeCdId = null == relateMap.get("routeCdId") ? null : StrUtils.tranString(relateMap.get("routeCdId"));
				if (null != routeCdId && !"".equals(routeCdId)) {
					String rtId = getRouterId(routeCdId);//获取路由器id
					String ptCdId = null == relateMap.get("portCdId") ? null : StrUtils.tranString(relateMap.get
							("portCdId"));//获取网卡的cloudosId
					networkBiz.localLink(network, rtId, ptCdId, "link", this.getSessionBean().getCloudClient());
				}else {
					networkBiz.localLink(network, null, null, "unlink", null);
				}
				break;
			default:break;
		}
	}
	
	/**
	 * 同步公网ip差异信息
	 * @param uuid 差异数据的id
	 * @param diffType 差异类型
	 * @param handleType 处理类型
	 * @param client cloudos连接
	 */
	private void syncFloatingIp (String uuid, CompareEnum diffType, CompareEnum handleType, CloudosClient client) {
		String localId = null;
		String cloudId = null;
		FloatingIp floatingIp = null;
		JSONObject floatingIpJson = null;
		//数据只存在iyun与两边存在数据差异时
		if (CompareEnum.IN_IYUN.equals(diffType) || CompareEnum.DATA_DIFF.equals(diffType)) {
			localId = uuid;//公网ip在iyun的id
		}
		//数据只存在cloudos时
		if (CompareEnum.IN_CLOUDOS.equals(diffType)) {
			cloudId = uuid;//公网ip在cloudos的id
		}
		if (null != localId) {//判断公网ip在iyun是否存在
			floatingIp = floatingIpBiz.findById(FloatingIp.class, uuid);//iyun的公网ip信息
			if (!StrUtils.checkParam(floatingIp)) {
				throw new MessageException(ResultType.not_exist_in_iyun);
			}
			if (CompareEnum.DATA_DIFF.equals(diffType)) {
				cloudId = floatingIp.getCloudosId();//公网ip在cloudos的id
			}
		}
		if (null != cloudId) {//判断公网ip在cloudos是否存在
			floatingIpJson = floatingIpBiz.getFloatingIpJson(cloudId, client);//cloudos的网络信息
			if (!StrUtils.checkParam(floatingIpJson)) {
				throw new MessageException(ResultType.not_exist_in_cloudos);
			}
		}
		//根据数据差异类型处理差异数据
		switch (diffType) {
			case IN_CLOUDOS://只存在cloudos
				List<FloatingIp> floatingIps = floatingIpBiz.findByPropertyName(FloatingIp.class, "cloudosId", cloudId);
				if (StrUtils.checkCollection(floatingIps)) {
					throw new MessageException(ResultType.already_exist_in_iyun);
				}
				switch (handleType) {
					case OPE_DEL://在cloudos上删除
						//检查是否已经分配
						String fixIp = floatingIpJson.getString("fixed_ip_address");
						if (null != fixIp && !"".equals(fixIp)) {
							throw new MessageException(ResultType.already_allocation_to_resource);
						}
						String rs = floatingIpBiz.cloudDelete(cloudId, client);
						if (!"success".equals(rs)) {
							throw new MessageException(rs);
						}
						break;
					case OPE_SYNC://同步到iyun
						String networkCdId = floatingIpJson.getString("floating_network_id");
						Map<String, Object> queryMap = new HashMap<>();
						queryMap.put("cloudosId", networkCdId);//检查网络信息是否已经同步
						Network network = networkBiz.singleByClass(Network.class, queryMap);
						if (!StrUtils.checkParam(network)) {
							throw new MessageException(ResultType.relate_public_network_still_not_synchronization);
						}
						
						floatingIp = floatingIpBiz.getFloatingIpByJson(floatingIpJson);
						floatingIp.setNetworkId(network.getId());
						
						String fixPortCdId = floatingIpJson.getString("port_id");
						String fixedIp = floatingIpJson.getString("fixed_ip_address");
						String routeCdId = floatingIpJson.getString("router_id");
						//找出分配资源的ip对应的网卡和关联路由（检查分配资源的ip、路由、公网是否已经同步）
						if (null != fixPortCdId && !"".equals(fixPortCdId)) {
							floatingIp.setFixedPortId(getFixedPortId(fixPortCdId));
							floatingIp.setRouterId(getRouterId(routeCdId));
							floatingIp.setFixedIp(fixedIp);
						}
						
						//获取用户本地id
						String userCdId = floatingIpJson.getString("h3c_user_id");
						if (null != userCdId && !"".equals(userCdId)) {
							List<User> users = userBiz.findByPropertyName(User.class, "cloudosId", userCdId);
							if (!StrUtils.checkCollection(users)) {
								throw new MessageException(ResultType.relate_user_still_not_synchronization);
							}
							floatingIp.setOwner(users.get(0).getId());
						}
						
						//获取公网网卡的cloudosId
						JSONArray portArray = portBiz.getPortArray(cloudId, null, client);
						if (StrUtils.checkCollection(portArray)) {
							JSONObject portJson = portArray.getJSONObject(0);
							floatingIp.setPortCdId(portJson.getString("id"));
						}
						floatingIpBiz.localSave(floatingIp);
						
						break;
					default:break;
				}
				break;
			case IN_IYUN://只存在iyun
				switch (handleType) {
					case OPE_DEL://在iyun上删除
						//检查是否已经分配
						String fixPortId = floatingIp.getFixedPortId();
						if (null != fixPortId) {
							throw new MessageException(ResultType.already_allocation_to_resource);
						}
						floatingIpBiz.localDelete(localId);
						break;
					case OPE_SYNC://同步到cloudos
						//检查分配资源是否已经同步到cloudos
						if (null != floatingIp.getFixedPortId()) {
							Port port = portBiz.findById(Port.class, floatingIp.getFixedPortId());
							JSONObject portJson = portBiz.getPortJson(port.getCloudosId(), client);
							if (!StrUtils.checkParam(portJson)) {
								throw new MessageException(ResultType.allocation_resource_still_not_synchronization);
							}
							//检查关联路由器是否已经同步到cloudos
							Route route = routeBiz.findById(Route.class, floatingIp.getRouterId());
							JSONObject routeJson = routeBiz.getRouteJson(route.getCloudosId(), client);
							if (!StrUtils.checkParam(routeJson)) {
								throw new MessageException(ResultType.relate_route_still_not_synchronization);
							}
						}
						
						//检查关联网络是否已经同步到cloudos
						Network network = networkBiz.findById(Network.class, floatingIp.getNetworkId());
						JSONObject networkJson = networkBiz.getNetworkJson(network.getCloudosId(), client);
						if (!StrUtils.checkParam(networkJson)) {
							throw new MessageException(ResultType.relate_network_still_not_synchronization);
						}
						
						String rs = floatingIpBiz.cloudSave(floatingIp, client, "sync");
						if (!"success".equals(rs)) {
							throw new MessageException(rs);
						}
						break;
					default:break;
				}
				break;
			case DATA_DIFF://数据差异
				String fixPortCdId = floatingIpJson.getString("port_id");
				String ipAddress = floatingIpJson.getString("floating_ip_address");
				String routeCdId = floatingIpJson.getString("router_id");
				//找出分配资源的ip对应的网卡（检查分配资源的ip是否已经同步）
				if (null != fixPortCdId && !"".equals(fixPortCdId)) {
					floatingIp.setFixedPortId(getFixedPortId(fixPortCdId));
					floatingIp.setFixedIp(ipAddress);
					floatingIp.setRouterId(getRouterId(routeCdId));
				}else {
					floatingIp.setFixedPortId(null);//未分配则置空
					floatingIp.setFixedIp(null);
					floatingIp.setRouterId(null);
				}
				String norm = floatingIpJson.getString("h3c_norm");
				floatingIp.setNorm(norm);//只修改带宽
				floatingIpBiz.update(floatingIp);
				break;
			default:break;
		}
	}
	
	/**
	 * 获取公网分配资源的ip网卡的本地id
	 * @param fixPortCdId 公网ip分配ip的网卡在cloudos的id
	 * @return 返回该网卡在本地的id
	 */
	public String getFixedPortId(String fixPortCdId) {
		if (!StrUtils.checkParam(fixPortCdId)) {
			return null;
		}
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("cloudosId", fixPortCdId);
		Port port = portBiz.singleByClass(Port.class, queryMap);
		if (!StrUtils.checkParam(port)) {
			throw new MessageException(ResultType.allocation_resource_still_not_synchronization);
		}
		return port.getId();
	}
	
	/**
	 * 获取路由器本地id
	 * @param routeCdId 路由器在cloudos的id
	 * @return 路由器的本地id
	 */
	public String getRouterId(String routeCdId) {
		if (!StrUtils.checkParam(routeCdId)) {
			return null;
		}
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("cloudosId", routeCdId);
		Route route = routeBiz.singleByClass(Route.class, queryMap);
		if (!StrUtils.checkParam(route)) {
			throw new MessageException(ResultType.relate_route_still_not_synchronization);
		}
		return route.getId();
	}
	
	/**
	 * 获取cloudos的路由器连接的防火墙cloudosId
	 * @param routeCdId 路由器的cloudosId
	 * @return 路由器连接的防火墙的cloudosId
	 */
	private String getFwCdId(String routeCdId, CloudosClient client) {
		String fwCdId = null;
		JSONArray fwArray = firewallBiz.getFirewallArray(client);
		if (StrUtils.checkCollection(fwArray)) {
			for (int i = 0; i < fwArray.size(); i++) {
				JSONObject fwJson = fwArray.getJSONObject(i);
				JSONArray rtIds = fwJson.getJSONArray("router_ids");
				if (rtIds.contains(routeCdId)) {
					fwCdId = fwJson.getString("id");
					break;
				}
			}
		}
		return fwCdId;
	}
	
	/**
	 * 获取防火墙本地id（检查防火墙是否已经同步）
	 * @param fwCdId 防火墙的cloudosId
	 * @return 返回防火墙的本地id
	 */
	private String getFwId(String fwCdId) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("cloudosId", fwCdId);
		Firewall firewall = firewallBiz.singleByClass(Firewall.class, queryMap);
		if (!StrUtils.checkParam(firewall)) {//检查防火墙是否存在
			throw new MessageException(ResultType.relate_firewall_still_not_synchronization);
		}
		return firewall.getId();
	}
	
	/**
	 * 获取cloudos的网络关联的路由器的cloudosId、网卡id、路由器下是否挂载网卡
	 * @param ntCdId 网络的cloudosId
	 * @param client cloudo连接
	 * @return 返回关联的路由器id和网关ip的网卡id
	 */
	private Map<String, Object> getRelateRoute(String ntCdId, CloudosClient client) {
		Map<String, Object> result = new HashMap<>();
		List<JSONObject> portList = new ArrayList<>();
		String rtCdId = null;
		String portCdId = null;
		//查看网络与网卡、路由器关联关系
		JSONArray portArray = portBiz.getPortArray(null, null, client);
		if (StrUtils.checkCollection(portArray)) {
			for (int i = 0; i < portArray.size(); i++) {
				JSONObject portJson = portArray.getJSONObject(i);
				if (ntCdId.equals(portJson.getString("network_id"))) {
					portList.add(portJson);
					result.put("portList", portList);
					if ("network:router_interface".equals(portJson
							.getString("device_owner"))) {
						rtCdId = portJson.getString("device_id");
						portCdId = portJson.getString("id");
						result.put("portCdId", portCdId);
						result.put("routeCdId", rtCdId);
						break;
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 同步云主机差异数据
	 * @param uuid 差异数据的id
	 * @param diffType 差异类型
	 * @param handleType 处理类型
	 * @param client cloudos连接
	 */
	private ResultType syncCloudHost(String uuid, CompareEnum diffType, CompareEnum handleType, CloudosClient client) {
		//根据数据差异类型同步差异数据
		switch (diffType) {
			case DATA_DIFF: {
				NovaVm novaVm = this.novaVmBiz.findById(NovaVm.class, uuid);
				if(null == novaVm) {
					throw new MessageException(ResultType.novam_not_exist);
				}
				String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_GET, client.getTenantId(), novaVm.getUuid());
				JSONObject server = HttpUtils.getJSONObject(client.get(uri), "server");
				if (!StrUtils.checkParam(server)) {//数据在cloudos上不存在时抛出异常
					throw new MessageException(ResultType.not_exist_in_cloudos);
				}
				novaVm.setHostName(server.getString("name"));
				this.novaVmBiz.update(novaVm);
				break;
			}
			case IN_CLOUDOS://只存在于cloudos
				//判断用户在cloudos是否存在
				String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_GET, client.getTenantId(), uuid);
				JSONObject server = HttpUtils.getJSONObject(client.get(uri), "server");
				if (!StrUtils.checkParam(server)) {//数据在cloudos上不存在时抛出异常
					throw new MessageException(ResultType.not_exist_in_cloudos);
				}
				//根据处理类型同步差异数据
				switch (handleType) {
					case OPE_SYNC://在iyun补充差异数据
						// 获取云主机信息
						String userId = server.getString("user_id");
						String tenantId = server.getString("tenant_id");
						String adminPass = server.getString("adminPass");
						String zoneName = server.getString("OS-EXT-AZ:availability_zone");
						String name = server.getString("name");
						String hostId = server.getString("hostId");
						String status = CloudosParams.getVmState(server.getString("status"));
						User user = this.userBiz.singleByClass(User.class, StrUtils.createMap("cloudosId", userId));
						if(user == null) {
							// TODO 可能需要查询出用户信息
							throw new MessageException(ResultType.diff_user_not_exists);
						}
						// 检查可用域
						Azone zone = azoneBiz.singleByClass(Azone.class, StrUtils.createMap("lableName", zoneName));
						if(zone == null) {
							throw new MessageException(ResultType.azone_not_exist);
						}
						// 检查镜像
						JSONObject image = server.getJSONObject("image");
						if(image == null) {
							throw new MessageException(ResultType.image_not_exist);
						}
						String imageId = image.getString("id");
						Rules rules = rulesDao.findById(Rules.class, imageId);
						if(rules == null) {
							// TODO 可能需要查询出用户信息
							throw new MessageException(ResultType.image_not_exist);
						}
						// 检查规格
						JSONObject flavor = server.getJSONObject("flavor");
						if(flavor == null) {
							throw new MessageException(ResultType.flavor_not_exist);
						}
						String flavorId = flavor.getString("id");
						NovaFlavor flavorEntity = novaFlavorDao.findById(NovaFlavor.class, flavorId);
						if(flavorEntity == null) {
							throw new MessageException(ResultType.flavor_not_exist);
						}
						
						NovaVm novaVm = new NovaVm();
						novaVm.setAzoneId(zone.getUuid());
						novaVm.setFlavorId(flavorId);
						novaVm.setHostName(name);
						novaVm.setHost("宿主机");
						novaVm.setUuid(uuid);
						novaVm.setVmState(status);
						novaVm.setVcpus(flavorEntity.getVcpus());
						novaVm.setMemory(flavorEntity.getRam());
						novaVm.setRamdisk(flavorEntity.getDisk());
						novaVm.setOsType(rules.getOsType());
						novaVm.setPowerState(1);
						novaVm.setProjectId(tenantId);
						novaVm.setOwner(user.getId());
						novaVm.createdUser(this.getLoginUser());
						novaVm.setImageRef(imageId);
						novaVm.setHost(hostId);
						this.novaVmBiz.add(novaVm);
						if(StrUtils.checkParam(adminPass)) {
							VmExtra vmExtra = new VmExtra();
							vmExtra.setOsUser(getLoginUser());
							vmExtra.setId(novaVm.getId());
							vmExtra.setSshKey(null);
							try {
								String encryptPassword = PwdUtils.encrypt(adminPass, novaVm.getId() + user.getId());
								vmExtra.setOsPasswd(encryptPassword);
							} catch (Exception e) {
								e.printStackTrace();
							}
							vmExtraDao.add(vmExtra);
						}
						
						uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_OSINTERFACE, client.getTenantId(), uuid);
						JSONArray interfaceAttachments = HttpUtils.getJSONArray(client.get(uri), "interfaceAttachments");
						int ipsCount = 0;
						if(StrUtils.checkCollection(interfaceAttachments)) {
							ipsCount = interfaceAttachments.size();
							CloudosNovaVm cloudosNovaVm = new CloudosNovaVm(client);
							for (int j = 0; j < ipsCount; j++) {
								JSONObject interfaceAttachment = interfaceAttachments.getJSONObject(j);
								String networkId = interfaceAttachment.getString("net_id");
								Network network = this.networkBiz.singleByClass(Network.class, StrUtils.createMap("cloudosId", networkId));
								if(network == null) {
									throw new MessageException(ResultType.network_not_exist);
								}
								cloudosNovaVm.savePort(novaVm, interfaceAttachment, client.getTenantId());
							}
						}
						
						ResourceNovaHandle resourceNovaHandle = new ResourceNovaHandle();
						// true使用配额
						ResultType resultType = resourceNovaHandle.updateQuota(novaVm,  true, ipsCount);
						if (resultType != ResultType.success) {
							this.warn("修改配额失败");
							throw new MessageException(resultType);
						}
						break;
					case OPE_DEL://在cloudos删除差异数据
						JSONArray array = server.getJSONArray("os-extended-volumes:volumes_attached");
						if(StrUtils.checkCollection(array)) {
							throw new MessageException(ResultType.cloud_host_exist_volume);
						}
						
						JSONObject addresses = server.getJSONObject("addresses");
						if(StrUtils.checkParam(addresses)) {
							for (String key : addresses.keySet()) {
								array = addresses.getJSONArray(key);
								for (int i = 0; i < array.size(); i++) {
									String ipsType = array.getJSONObject(i).getString("OS-EXT-IPS:type");
									if("floating".equals(ipsType)) {
										throw new MessageException(ResultType.cloud_host_exist_floatip);
									}
								}
							}
						}
						
						JSONObject obj = client.delete(uri);
						String result = obj.getString("result");
						if(!result.startsWith("2")) {
							String record = StrUtils.tranString(obj.get("record"));
							throw new MessageException(record);
						}
						break;
					default:break;
				}
				break;
			case IN_IYUN:
                NovaVm novaVm = this.novaVmBiz.findById(NovaVm.class, uuid);
                if(null == novaVm) {
                    throw new MessageException(ResultType.novam_not_exist);
                }
                String cloudosId = novaVm.getUuid();
				switch (handleType) {
					case OPE_SYNC: {
                        // 同步到cloudos
                        if(cloudosId == null) {
                            throw new MessageException(ResultType.cloud_host_lack_of_network);
                        }
                        List<Port> ports = this.portBiz.findByPropertyName(Port.class, "deviceId", cloudosId);
                        if(!StrUtils.checkCollection(ports)) {
                            throw new MessageException(ResultType.cloud_host_lack_of_network);
                        }
                        List<Map<String, Object>> networks = new ArrayList<>();
                        Map<String, Port> ip2PortMap = new HashMap<>();
                        ports.forEach(port -> {
                        	if (port.getIsinit()) {
								IpAllocation ipa = ipAllocationDao.singleByClass(IpAllocation.class, StrUtils.createMap("portId", port.getId()));
								Map<String, Object> map = StrUtils.createMap("fixed_ip", ipa.getIpAddress());
								ip2PortMap.put(ipa.getIpAddress(), port);
								if(ipa != null) {
									String subnetId = ipa.getSubnetId();
									Subnet subnet = this.subnetDao.findById(Subnet.class, subnetId);
									if(subnet.getNetworkId() != null) {
										Network network = this.networkBiz.findById(Network.class, subnet.getNetworkId());
										map.put("uuid", network.getCloudosId());
										networks.add(map);
									}
								}
							}
                        });
                        if(!StrUtils.checkCollection(networks)) {
                            throw new MessageException(ResultType.cloud_host_lack_of_network);
                        }
                        if(novaVm.getAzoneId() == null) {
                            throw new MessageException(ResultType.azone_not_exist);
                        }
                        Azone zone = this.azoneBiz.findById(Azone.class, novaVm.getAzoneId());

                        List<Map<String, Object>> security_groups = new ArrayList<Map<String, Object>>();
                        Map<String, Object> security_group = StrUtils.createMap("name", "default");
                        security_groups.add(security_group);

                        VmExtra vmExtra = this.vmExtraDao.findById(VmExtra.class, novaVm.getId());
                        if(!StrUtils.checkParam(novaVm.getOwner())) {
                            throw new MessageException(ResultType.user_not_exist);
                        }
                        User user = this.userBiz.findById(User.class, novaVm.getOwner());
                        if(user == null) {
                            throw new MessageException(ResultType.user_not_exist);
                        }
                        CloudosClient userClient = CloudosClient.create(user.getCloudosId(), user.getLoginName());
                        if(userClient == null) {
                            throw new MessageException(ResultType.cloudos_exception);
                        }
                        if(!StrUtils.checkParam(novaVm.getOwner())) {
                            throw new MessageException(ResultType.user_not_exist);
                        }

                        Map<String, Object> serverMap = StrUtils.createMap("name", novaVm.getHostName());
                        serverMap.put("flavorRef", novaVm.getFlavorId());
                        serverMap.put("max_count", 1);
                        serverMap.put("min_count", 1);
                        serverMap.put("imageRef", novaVm.getImageRef());
                        serverMap.put("availability_zone", zone.getZone());
                        serverMap.put("security_groups", security_groups);
                        serverMap.put("networks", networks);

                        if(vmExtra != null) {
                            // 修改bean的密码
                            try {
                                String password = PwdUtils.decrypt(vmExtra.getOsPasswd(), novaVm.getId() + user.getLoginName());
                                serverMap.put("adminPass", password);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER, novaVm.getProjectId());
                        JSONObject jsonObj = userClient.post(uri, StrUtils.createMap("server", serverMap));
                        server = HttpUtils.getJSONObject(jsonObj, "server");
                        if(server == null) {
                            throw new MessageException(jsonObj.toJSONString());
                        }
                        String serverId = server.getString("id");
                        int create_query_num = StrUtils.tranInteger(singleton.getConfigValue("create_query_num"));
                        if(create_query_num == 0) {
                            create_query_num = 100;
                        }
                        boolean createdSuccess = false;
                        uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_GET, user.getProjectId(), serverId);
                        for (int i = 0; i < create_query_num; i++) {
                            jsonObj = userClient.get(uri);
                            server = HttpUtils.getJSONObject(jsonObj, "server");
                            if(server == null) {
                                throw MessageException.create(ResultType.diff_cloud_host_create_failure);
                            }
                            String state = server.getString("status");
                            if (!"BUILD".equals(state)) {
                                createdSuccess = true;
                                break;
                            }
                            try {
                                TimeUnit.SECONDS.sleep(5);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(!createdSuccess) {   // 创建失败则删除创建的虚拟机
                            uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_DELETE, user.getProjectId(), serverId);
                            jsonObj = userClient.delete(uri);
                            this.warn("Delete diff create cloud host, cause by time over, result:" + jsonObj.toJSONString());
                            throw MessageException.create(ResultType.diff_cloud_host_create_timeover);
                        }
                        novaVm.setUuid(serverId);
                        this.novaVmBiz.update(novaVm);
                        // 重写网卡信息
                        uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_OSINTERFACE, user.getProjectId(), serverId);
                        JSONArray interfaceAttachments = HttpUtils.getJSONArray(userClient.get(uri), "interfaceAttachments");
                        if(interfaceAttachments == null) {
                            this.warn("Delete diff create cloud host, cause by time over, result:" + jsonObj.toJSONString());
                            return ResultType.diff_cloud_host_get_ip_failure;
                        }
                        for(int i = 0; i < interfaceAttachments.size(); i++) {
                            JSONObject interfaceAttachment = interfaceAttachments.getJSONObject(i);
                            JSONArray fixed_ips = interfaceAttachment.getJSONArray("fixed_ips");
                            if(StrUtils.checkCollection(fixed_ips)) {
                                String ip = fixed_ips.getJSONObject(0).getString("ip_address");
                                Port port = ip2PortMap.get(ip);
                                if(port != null) {
                                	port.setDeviceId(serverId);
                                    port.setCloudosId(interfaceAttachment.getString("port_id"));
                                    this.portBiz.update(port);
                                }
                            }
                        }
                        break;
                    }
					case OPE_DEL:// 删除在iyun里面的差异部分
						// 是否存在硬盘
						int count = this.volumeBiz.findCountByPropertyName(Volume.class, "host", uuid);
                        if(count > 0) {
                            throw new MessageException(ResultType.cloud_host_exist_volume);
                        }
                        int portCount = 0;
                        if(cloudosId != null) {
                            List<Port> ports = this.portBiz.findByPropertyName(Port.class, "deviceId", cloudosId);
                            if(StrUtils.checkCollection(ports)) {
                                portCount = ports.size();
                                Set<String> portIds = new HashSet<>();
                                ports.forEach(port -> {
                                    portIds.add(port.getId());
                                    this.ipAllocationDao.delete(StrUtils.createMap("portId", port.getId()), IpAllocation.class);
                                    this.portBiz.delete(port);
                                });
                                count = this.floatingIpBiz.count(FloatingIp.class, StrUtils.createMap("fixedPortId", portIds));
                                if(count > 0) {
                                    throw new MessageException(ResultType.cloud_host_exist_floatip);
                                }
                            }
                        }
                        this.metadataDao.delete(StrUtils.createMap("instanceUuid", novaVm.getId()), Metadata.class);
                        this.vmExtraDao.deleteById(VmExtra.class, novaVm.getId());
                        this.novaVmBiz.delete(novaVm);
                        new ResourceNovaHandle().updateQuota(novaVm,  false, portCount);
						break;
					default: break;
				}
				break;
			default:
				break;
		}
        return ResultType.success;
	}
	
	/**
	 * 同步云主机差异数据
	 * @param uuid 差异数据的id
	 * @param diffType 差异类型
	 * @param handleType 处理类型
	 * @param client cloudos连接
	 */
	private ResultType syncVolume(String uuid, CompareEnum diffType, CompareEnum handleType, CloudosClient client) {
		//根据数据差异类型同步差异数据
		switch (diffType) {
			case DATA_DIFF: {
				Volume entity = this.volumeBiz.findById(Volume.class, uuid);
				if(entity == null) {
					throw new MessageException(ResultType.deleted);
				}
				
				String cloudUUID = entity.getUuid();
				if(!StrUtils.checkParam(cloudUUID)) {
					throw new MessageException(ResultType.missing_require_parameter);
				}
				//判断用户在cloud os是否存在
				String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_VOLUME_ACTION, client.getTenantId(), cloudUUID);
				JSONObject volume = HttpUtils.getJSONObject(client.get(uri), "volume");
				if (null == volume) {//数据在cloudos上不存在时抛出异常
					throw new MessageException(ResultType.not_exist_in_cloudos);
				}
				
				JSONArray attachments = volume.getJSONArray("attachments");
				if(StrUtils.checkCollection(attachments)) {
					String server_id = attachments.getJSONObject(0).getString("server_id");
					NovaVm novaVm = novaVmBiz.singleByClass(NovaVm.class, StrUtils.createMap("uuid", server_id));
					if(novaVm == null) {
						throw new MessageException(ResultType.volume_cloud_host_not_exist);
					}
					entity.setHost(novaVm.getId());
					entity.setAttachStatus("0"); // 更新挂载状态
				} else {
					entity.setHost(null);
					entity.setAttachStatus("1"); // 更新挂载状态
				}
				entity.setName(volume.getString("name"));
				entity.setStatus(volumeBiz.tranState(volume.getString("status")));
				this.volumeBiz.update(entity);
				break;
			}
			case IN_CLOUDOS://只存在于cloud os
				//判断用户在cloud os是否存在
				String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_VOLUME_ACTION, client.getTenantId(), uuid);
				JSONObject volume = HttpUtils.getJSONObject(client.get(uri), "volume");
				if (!StrUtils.checkParam(volume)) {//数据在cloudos上不存在时抛出异常
					throw new MessageException(ResultType.not_exist_in_cloudos);
				}
				//根据处理类型同步差异数据
				switch (handleType) {
					case OPE_SYNC: {//在iyun补充差异数据
						String tenantId = volume.getString("os-vol-tenant-attr:tenant_id");
						Project project = this.projectBiz.findById(Project.class, tenantId);
						if(project == null) {
							throw new MessageException(ResultType.project_not_exist);
						}
						
						String userId = volume.getString("user_id");
						User user = this.userBiz.singleByClass(User.class, StrUtils.createMap("cloudosId", userId));
						if(user == null) {
							throw new MessageException(ResultType.diff_user_not_exists);
						}
						
						String zoneName = volume.getString("availability_zone");
						Azone zone = azoneBiz.singleByClass(Azone.class, StrUtils.createMap("lableName", zoneName));
						if(zone == null) {
							throw new MessageException(ResultType.azone_not_exist);
						}
						
						Volume entity = new Volume();
						JSONArray attachments = volume.getJSONArray("attachments");
						if(StrUtils.checkCollection(attachments)) {
							String server_id = attachments.getJSONObject(0).getString("server_id");
							NovaVm novaVm = novaVmBiz.singleByClass(NovaVm.class, StrUtils.createMap("uuid", server_id));
							if(novaVm == null) {
								throw new MessageException(ResultType.azone_not_exist);
							}
							entity.setHost(novaVm.getId());
						}
						entity.setOwner2(user.getId());
						entity.setAzoneId(zone.getUuid());
						entity.setSize(volume.getIntValue("size"));
						entity.setName(volume.getString("name"));
						entity.setAttachStatus(entity.getHost() == null ? "1" : "0");
						entity.setUuid(uuid);
						entity.setVolumeType("1");
						entity.setDeleted("0");
						entity.setProjectId(tenantId);
						entity.setStatus(volumeBiz.tranState(volume.getString("status")));
						JSONObject itemObj = volume.getJSONObject("metadata");
						entity.setMetaData(itemObj.toJSONString());
						entity.createdUser(this.getLoginUser());
						entity.createdDate(DateUtils.getDateByString(volume.getString("created_at"), DateUtils.apiFormat));
						this.volumeBiz.add(entity);
						quotaUsedBiz.change(ConfigProperty.VOLUME_QUOTA_CLASSCODE, tenantId, true, 1);
						quotaUsedBiz.change(ConfigProperty.GIGABYTES_QUOTA_CLASSCODE, tenantId, true, entity.getSize());
						break;
					}
					case OPE_DEL://在cloudos删除差异数据
						JSONArray attachments = volume.getJSONArray("attachments");
						if(StrUtils.checkCollection(attachments)) {
							throw new MessageException(ResultType.volume_cloud_host_not_exist);
						}
						
						JSONObject obj = client.delete(uri);
						String result = obj.getString("result");
						if(!result.startsWith("2")) {
							String record = StrUtils.tranString(obj.get("record"));
							throw new MessageException(record);
						}
						break;
					default:break;
				}
				break;
			case IN_IYUN:
				Volume entity = this.volumeBiz.findById(Volume.class, uuid);
				if(entity == null) {
					throw new MessageException(ResultType.deleted);
				}
				switch (handleType) {
					case OPE_SYNC:// 同步到cloudos
						if(entity.getAzoneId() == null) {
							throw new MessageException(ResultType.missing_require_parameter);
						}
						Azone azone = this.azoneBiz.findById(Azone.class, entity.getAzoneId());

						if(entity.getOwner2() == null) {
							throw new MessageException(ResultType.missing_require_parameter);
						}
						User user = this.userBiz.findById(User.class, entity.getOwner2());
						if(user == null || !StrUtils.checkParam(user.getCloudosId())) {
							throw new MessageException(ResultType.volume_owner_not_exist);
						}

                        CloudosClient userClient = CloudosClient.create(user.getCloudosId(), user.getLoginName());
                        if(userClient == null) {
                            throw new MessageException(ResultType.cloudos_exception);
                        }

						Map<String, Object> create = StrUtils.createMap("size", entity.getSize());
						create.put("project_id", entity.getProjectId());
						create.put("user_id", user.getCloudosId());
						create.put("name", entity.getName());
						create.put("availability_zone", azone.getZone());

						Map<String, Object> metadata = StrUtils.createMap("azone_label", azone.getZone());
						metadata.put("azone_uuid", azone.getUuid());
						metadata.put("user_name", user.getLoginName());
						create.put("metadata", metadata);
						Map<String, Object> createMap = StrUtils.createMap("volume", create);
						String createUri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_VOLUMES, userClient.getTenantId());
						JSONObject createObj = userClient.post(createUri, createMap);
						JSONObject record = HttpUtils.getJSONObject(createObj, "volume");
						if(record == null) {
							throw new MessageException(createObj.toJSONString());	// 直接提示错误的内容
						}
						String id = record.getString("id");
						entity.setUuid(id);
						this.volumeBiz.update(entity);

                        for (int i = 0; i < 100; i++) {
                            uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_VOLUME_ACTION, userClient.getTenantId(), id);
                            record = HttpUtils.getJSONObject(userClient.get(uri), "volume");
                            if(record != null) {
                                String status = record.getString("status");
                                if (!ConfigProperty.VOLUME_STATE_CLOUDOS_CREATING.equals(status)) {
                                    break;
                                }
                            }
                            try {
                                TimeUnit.SECONDS.sleep(5);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(entity.getHost() != null) {
                            NovaVm novaVm = this.novaVmBiz.findById(NovaVm.class, entity.getHost());
                            uri = HttpUtils.tranUrl(CloudosParams.CLOUDOS_API_OSVOLUMEATTACHMENTS, userClient.getTenantId(), novaVm.getPortCloudosId());
                            Map<String, Object> dataMap = new HashMap<>();
                            dataMap.put("volumeId", id);
                            Map<String, Object> paramMap = ResourceHandle.getParamMap(dataMap, "volumeAttachment");
                            userClient.post(uri, paramMap);

                            uri = HttpUtils.tranUrl(CloudosParams.CLOUDOS_API_VOLUME_ACTION, userClient.getTenantId(), id);
                            record = HttpUtils.getJSONObject(userClient.get(uri), "volume");
                            if(record != null) {
                                JSONArray attachments = record.getJSONArray("attachments");
                                if(!StrUtils.checkCollection(attachments)) {
                                    return ResultType.sync_volume_but_attachment;
                                }
                            }
                        }
						break;
					case OPE_DEL:// 删除在iyun里面的差异部分
						// 删除回收站的内容
						Map<String, Object> queryMap = StrUtils.createMap("resId", entity.getId());
						queryMap.put("classId", ConfigProperty.TEMPLATES_CLASS2_DISK);
						List<RecycleItems> items = recycleItemsDao.listByClass(RecycleItems.class, queryMap);
						if(StrUtils.checkCollection(items)) {
							recycleItemsDao.delete(items);
						}
						this.volumeBiz.delete(entity);
						// 删除规格
						String flavorId = entity.getFlavorId();
						if (null != flavorId && !"".equals(flavorId)) {
							VolumeFlavor flavor = volumeFlavorDao.findById(VolumeFlavor.class, flavorId);
							if(flavor != null) {
								volumeFlavorDao.delete(flavor);	
							}
						}
						quotaUsedBiz.change(ConfigProperty.VOLUME_QUOTA_CLASSCODE, entity.getProjectId(), false, 1);
						quotaUsedBiz.change(ConfigProperty.GIGABYTES_QUOTA_CLASSCODE, entity.getProjectId(), false, entity.getSize());
						break;
					default: break;
				}
				break;
			default: break;
		}
        return ResultType.success;
	}
	
	@Override
	public PageModel<Differences> findForPage(PageEntity entity) {
		return differencesDao.findForPage(entity);
	}
	
	
}
