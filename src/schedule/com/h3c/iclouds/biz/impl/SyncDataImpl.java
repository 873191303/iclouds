package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.AzoneBiz;
import com.h3c.iclouds.biz.DepartmentBiz;
import com.h3c.iclouds.biz.FloatingIpBiz;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.Project2NetworkBiz;
import com.h3c.iclouds.biz.Project2QuotaBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.biz.RouteBiz;
import com.h3c.iclouds.biz.SyncDataBiz;
import com.h3c.iclouds.biz.SyncVdcDataBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.biz.VlbPoolBiz;
import com.h3c.iclouds.biz.VolumeBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.Network2SubnetDao;
import com.h3c.iclouds.dao.Project2AzoneDao;
import com.h3c.iclouds.dao.Project2QuotaDao;
import com.h3c.iclouds.dao.QuotaUsedDao;
import com.h3c.iclouds.dao.User2RoleDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.po.Department;
import com.h3c.iclouds.po.FloatingIp;
import com.h3c.iclouds.po.IpAllocation;
import com.h3c.iclouds.po.Metadata;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.Network2Subnet;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Azone;
import com.h3c.iclouds.po.Project2Network;
import com.h3c.iclouds.po.Project2Quota;
import com.h3c.iclouds.po.QuotaClass;
import com.h3c.iclouds.po.QuotaUsed;
import com.h3c.iclouds.po.Rules;
import com.h3c.iclouds.po.Subnet;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.User2Role;
import com.h3c.iclouds.po.VlbVip;
import com.h3c.iclouds.po.VmExtra;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.quartz.ProjectQuotaQuartz;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.PwdUtils;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 
 * @author zkf5485
 *
 */
@Service("syncDataBiz")
public class SyncDataImpl implements SyncDataBiz {
	
	@Resource
	private ProjectBiz projectBiz;

	@Resource
	private UserBiz userBiz;

	@Resource
	private AzoneBiz azoneBiz;

	@Resource
	private Project2AzoneDao project2AzoneDao;

	@Resource
	private NovaVmBiz novaVmBiz;

	@Resource
	private User2RoleDao user2RoleDao;

	@Resource
	private Project2QuotaBiz project2QuotaBiz;

	@Resource
	private Project2NetworkBiz project2NetworkBiz;

	@Resource
	private Network2SubnetDao network2SubnetDao;

	@Resource
	private QuotaUsedBiz quotaUsedBiz;

	@Resource
	private QuotaUsedDao quotaUsedDao;

	@Resource
	private BaseDAO<QuotaClass> quotaClassDao;

	@Resource
	private Project2QuotaDao project2QuotaDao;

	@Resource
	private NetworkBiz networkBiz;

	@Resource
	private FloatingIpBiz floatingIpBiz;

	@Resource
	private PortBiz portBiz;

	@Resource
	private VolumeBiz volumeBiz;

	@Resource
	private DepartmentBiz departmentBiz;

	@Resource(name = "baseDAO")
	private BaseDAO<VmExtra> vmExtraDao;

	@Resource(name = "baseDAO")
	private BaseDAO<Metadata> metadatDao;

	@Resource(name = "baseDAO")
	private BaseDAO<NovaFlavor> novaFlavorDao;

	@Resource(name = "baseDAO")
	private BaseDAO<Rules> rulesDao;

	@Resource
	private SyncVdcDataBiz syncVdcDataBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Subnet> subnetDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<IpAllocation> ipusedDao;
	
	@Resource
	private VlbPoolBiz vlbPoolBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<VlbVip> vipDao;
	
	@Resource
	private RouteBiz routeBiz;
	
	private String rootProjectId = CacheSingleton.getInstance().getConfigValue("rootid");
	
	private Map<String, String> classCode2ClassId = new HashMap<>();
	
	private Map<String, String> cloudosQuotaKey = new HashMap<>();
	
	private Map<String, String> cloudosMaxQuotaKey = new HashMap<>();
	
	private List<Project> projects = new ArrayList<>();
	
	private List<String> projectIds = new ArrayList<>();
	
	private Map<String, String> project2DeptMap = new HashMap<String, String>();
	
	private Map<String, String> userMap = new HashMap<String, String>();

	@Override
	public void start() {
		if (!StrUtils.checkParam(getClient())) {
			throw new MessageException(ResultType.system_error);
		}
		List<QuotaClass> quotaClasses = quotaClassDao.getAll(QuotaClass.class);
		for (QuotaClass quotaClass : quotaClasses) {
			classCode2ClassId.put(quotaClass.getClassCode(), quotaClass.getId());
		}
		projects = projectBiz.getAll(Project.class);
		for (Project project : projects) {
			projectIds.add(project.getId());
		}
		cloudosQuotaKey.put("cores", "totalCoresUsed");
		cloudosMaxQuotaKey.put("cores", "maxTotalCores");
		cloudosQuotaKey.put("instances", "totalInstancesUsed");
		cloudosMaxQuotaKey.put("instances", "maxTotalInstances");
		cloudosQuotaKey.put("ram", "totalRAMUsed");
		cloudosMaxQuotaKey.put("ram", "maxTotalRAMSize");
		cloudosQuotaKey.put("gigabytes", "totalGigabytesUsed");
		cloudosMaxQuotaKey.put("gigabytes", "maxTotalVolumeGigabytes");
		cloudosQuotaKey.put("snapshots", "totalSnapshotsUsed");
		cloudosMaxQuotaKey.put("snapshots", "maxTotalSnapshots");
		cloudosQuotaKey.put("volumes", "totalVolumesUsed");
		cloudosMaxQuotaKey.put("volumes", "maxTotalVolumes");
		cloudosQuotaKey.put("router", "totalRoutersUsed");
		cloudosMaxQuotaKey.put("router", "maxRouters");
		cloudosQuotaKey.put("vpnservice", "totalVpnServicesUsed");
		cloudosMaxQuotaKey.put("vpnservice", "maxVpnServices");
		cloudosQuotaKey.put("network", "totalNetworksUsed");
		cloudosMaxQuotaKey.put("network", "maxNetworks");
		cloudosQuotaKey.put("firewall", "totalFirewallsUsed");
		cloudosMaxQuotaKey.put("firewall", "maxFirewalls");
		cloudosQuotaKey.put("ips", "totalVnicUsed");
		cloudosMaxQuotaKey.put("ips", "maxVnic");
		cloudosQuotaKey.put("loadbalancer", "totalLoadbalancersUsed");
		cloudosMaxQuotaKey.put("loadbalancer", "maxLoadbalancers");
		cloudosQuotaKey.put("floatingip", "totalFloatingIpsUsed");
		cloudosMaxQuotaKey.put("floatingip", "maxTotalFloatingIps");
		cloudosQuotaKey.put("listener", "totalListenersUsed");
		cloudosMaxQuotaKey.put("listener", "maxListeners");
		cloudosQuotaKey.put("security_group", "totalSecurityGroupsUsed");
		cloudosMaxQuotaKey.put("security_group", "maxSecurityGroups");
		cloudosQuotaKey.put("ipsecpolicy", "totalIpsecPoliciesUsed");
		cloudosMaxQuotaKey.put("ipsecpolicy", "maxIpsecPolicies");
		cloudosQuotaKey.put("security_group_rule", "totalSecurityGroupRulesUsed");
		cloudosMaxQuotaKey.put("security_group_rule", "maxSecurityGroupRules");
		
		// 同步租户,租户的配额及网络,租户与可用域关系
		saveProject();
		quota();
		
		// 同步用户,及对应的角色
		saveUser();
		
		//vdc(防火墙、策略集、规则、路由器、网络)
		syncVdcDataBiz.startSyncVdc();
		
		// 云主机-关联网络
		saveNovaVm();
		
		//(负载均衡组、负载均衡、负载均衡成员-关联云主机)
		syncVdcDataBiz.syncVlb();
		
		// 公网IP-关联云主机和负载均衡
		saveFloatingIp();
		
		// 云硬盘-关联云主机
		saveVolumes();
		
		//同步配额
		new ProjectQuotaQuartz().syn();
		
	}

	@Override
	public void quota() {
		if (StrUtils.checkParam(getClient())) {
			String uri = "/keystone/all-limits";
			JSONObject result = getClient().get(uri);
			if (ResourceHandle.judgeResponse(result)) {
				JSONArray array = HttpUtils.getJSONArray(result, "all_limits");
				if (StrUtils.checkCollection(array)) {
					for (int i = 0; i < array.size(); i++) {
						JSONObject item = array.getJSONObject(i).getJSONObject("absolute");
						String projectId = item.getString("projectId");
						if (!ConfigProperty.STOP_SET.contains(projectId)) {
							Project project = projectBiz.findById(Project.class, projectId);
							List<QuotaUsed> quotaUseds = quotaUsedBiz.findByPropertyName(QuotaUsed.class, "tenantId",
									projectId);
							if (StrUtils.checkCollection(quotaUseds)) {
								for (QuotaUsed quotaUsed : quotaUseds) {
									String key = getQuotaUsedKey(quotaUsed.getClassCode());
									Integer quotaUse = item.getInteger(key);
									quotaUsed.setQuotaUsed(quotaUse);
									quotaUsedBiz.update(quotaUsed);
								}
							} else {
								// 初始化信息
								quotaUsedDao.save(project);
							}
							List<Project2Quota> project2Quotas2 = project2QuotaBiz.findByPropertyName(Project2Quota.class, "tenantId", projectId);
							if (!StrUtils.checkCollection(project2Quotas2)) {
								project2QuotaDao.save(project);
							}
							JSONArray network_addresses = item.getJSONArray("network_addresses");
							if (StrUtils.checkCollection(network_addresses)) {
								for (int j = 0; j < network_addresses.size(); j++) {
									JSONObject network_address = network_addresses.getJSONObject(j);
									Map<String, Object> paramsNetwork = new HashMap<>();
									paramsNetwork.put("cidr", network_address.getString("cidr"));
									paramsNetwork.put("tenantId", project.getId());
									Project2Network project2Network = project2NetworkBiz
											.singleByClass(Project2Network.class, paramsNetwork);
									if (!StrUtils.checkParam(project2Network)) {
										project2Network = new Project2Network(project.getId(),
												network_address.getString("cidr"));
										String networkId = UUID.randomUUID().toString();
										project2Network.setId(networkId);
										project2NetworkBiz.add(project2Network);
										JSONArray ip_pools = network_address.getJSONArray("ip_pools");
										for (int k = 0; k < ip_pools.size(); k++) {
											JSONObject ip_pool = ip_pools.getJSONObject(k);
											Network2Subnet subnet = new Network2Subnet(ip_pool.getString("start_ip"),
													ip_pool.getString("end_ip"), networkId);
											network2SubnetDao.add(subnet);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private String getQuotaUsedKey(String classCode) {
		return cloudosQuotaKey.get(classCode);
	}

	public void saveProject() {
		List<Project> projects = projectBiz.getAll(Project.class);
		List<String> list = new ArrayList<>();
		for (Project project : projects) {
			list.add(project.getId());
		}
		List<Azone> azones = azoneBiz.getAll(Azone.class);
		List<String> azoneIds = new ArrayList<>();
		for (Azone azone : azones) {
			azoneIds.add(azone.getUuid());
		}
		if (StrUtils.checkParam(getClient())) {
			String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_PROJECTS);
			JSONObject result = getClient().get(uri);
			if (ResourceHandle.judgeResponse(result)) {
				JSONArray array = HttpUtils.getJSONArray(result, "projects");
				// JSONArray array = result.getJSONArray("record");
				if (StrUtils.checkParam(array)) {
					for (int i = 0; i < array.size(); i++) {
						JSONObject item = array.getJSONObject(i);
						String id = item.getString("id");
						if (!list.contains(id) && !ConfigProperty.STOP_SET.contains(id)) {
							Project project = new Project();
							if (!rootProjectId.equals(item.get("id"))) {
								project.setParentId(rootProjectId);
							}
							InvokeSetForm.settingForm(item, project);
							convert(project, item);

							projectBiz.add(project);
							Department dept = new Department();
							dept.createdUser(ConfigProperty.SYSTEM_FLAG);
							dept.setDepth(1);
							dept.setParentId("-1"); // 作为最高级使用
							dept.setDeptName(project.getName());
							dept.setProjectId(project.getId()); // 与当前用户同一个租户下
							dept.setDeptCode(project.getName());
							dept.setDeptDesc("创建租户同步创建部门");
							String deptId = departmentBiz.add(dept);
							project2DeptMap.put(project.getId(), deptId);
						}
					}
				}
			} else {
				return;
			}
			uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_AZONES);
			result = getClient().get(uri);
			if (ResourceHandle.judgeResponse(result)) {
				JSONArray array = result.getJSONArray("record");
				if (StrUtils.checkParam(array)) {
					for (int i = 0; i < array.size(); i++) {
						JSONObject azone = array.getJSONObject(i);
						String id = azone.getString("id");
						if (!azoneIds.contains(id)) {
							Azone azone2 = new Azone();
							InvokeSetForm.settingForm(azone, azone2);
							azone2.setUuid(id);
							azone2.setLableName(azone.getString("labelName"));
							azone2.setDescription(azone.getString("description"));
							azone2.setCreatedByName("h3c");
							azone2.createdUser("h3c");
							azone2.setDeleted("0");
							// 有可能要做记录
							LogUtils.warn(this.getClass(), "新增的" + azone2.getLableName());
							azoneBiz.add(azone2);
						}
					}
				}
			}

			List<Project2Azone> links = project2AzoneDao.getAll(Project2Azone.class);
			if(StrUtils.checkCollection(links)) {
				project2AzoneDao.delete(links);
			}
			uri = "/keystone/types/AzoneProject/assigments";
			result = getClient().get(uri);
			if (ResourceHandle.judgeResponse(result)) {
				JSONArray array = HttpUtils.getJSONArray(result, "assigments");
				// JSONArray array = result.getJSONArray("record");
				if (StrUtils.checkParam(array)) {
					for (int i = 0; i < array.size(); i++) {
						JSONObject item = array.getJSONObject(i);
						String projectId = item.getString("targetId");
						String azoneId = item.getString("actorId");
						Map<String, String> map = new HashMap<>();
						map.put("id", projectId);
						map.put("iyuUuid", azoneId);
						List<Project2Azone> project2Azones = project2AzoneDao.findByMap(Project2Azone.class, map);
						if (!StrUtils.checkCollection(project2Azones) || !ConfigProperty.STOP_SET.contains(projectId)) {
							Project project = projectBiz.findById(Project.class, projectId);
							if (StrUtils.checkParam(project)) {
								Project2Azone project2Azone = new Project2Azone();
								project2Azone.setId(project.getId());
								project2Azone.setIyuUuid(azoneId);
								project2Azone.setDeleted("0");
								project2AzoneDao.add(project2Azone);
							}
						}
					}
				}
			} else {
				return;
			}
		}
	}

	public void saveUser() {
		List<User> users = userBiz.getAll(User.class);
		List<String> list = new ArrayList<>();
		for (User user : users) {
			list.add(user.getCloudosId());
		}
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_USERS);
		if (StrUtils.checkParam(getClient())) {
			JSONObject result = getClient().get(uri);
			uri = "/keystone/types/UserProjectGroup/assigments";
			result = getClient().get(uri);
			if (ResourceHandle.judgeResponse(result)) {
				JSONArray array = HttpUtils.getJSONArray(result, "assigments");
				if (StrUtils.checkParam(array)) {
					for (int i = 0; i < array.size(); i++) {
						JSONObject item = array.getJSONObject(i);
						String id = item.getString("actorId");
						// root用户是没有cloudosId 的
						if (!list.contains(id) && !ConfigProperty.STOP_SET.contains(id)) {
							User user = new User();
							convert(user, item);
							// 设置租户和部门
							String projectId = item.getString("targetId");
							user.setProjectId(projectId);
							user.setDeptId(project2DeptMap.get(projectId));
							userBiz.add(user);
							userMap.put(user.getCloudosId(), user.getId());
							try {
								if (StrUtils.checkParam(user.getLoginName(), user.getLoginName(), user.getId())) {
									String password = PwdUtils.encrypt(user.getLoginName(),
											user.getLoginName() + user.getId());
									user.setPassword(password);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							userBiz.update(user);
							String groupId = item.getString("groupId");
							String roleId = getRoleId(groupId);
							if (StrUtils.checkParam(roleId)) {
								User2Role user2Role = new User2Role(user.getId(), roleId, groupId);
								user2RoleDao.add(user2Role);
							}
						}
					}
				}
			} else {
				return;
			}
		}
	}

	public void saveNovaVm() {
		List<NovaVm> novaVms = novaVmBiz.getAll(NovaVm.class);
		List<String> list = new ArrayList<>();
		for (NovaVm novaVm : novaVms) {
			list.add(novaVm.getUuid());
		}
		JSONArray serverArray = novaVmBiz.getServerArray(getClient());
		if (StrUtils.checkParam(serverArray)) {
			for (int i = 0; i < serverArray.size(); i++) {
				JSONObject serverJson = serverArray.getJSONObject(i);
				String id = serverJson.getString("id");
				if (!list.contains(id)) {
					NovaVm target = new NovaVm();
					InvokeSetForm.settingForm(serverJson, target);
					convert(target, serverJson);
					novaVmBiz.add(target);
					// 关联对应的云主机
					Map<String, Object> params = new HashMap<>();
					params.put("uuid", serverJson.getString("id"));
					NovaVm novaVm = novaVmBiz.singleByClass(NovaVm.class, params);
					
					JSONObject metadata2 = serverJson.getJSONObject("metadata");
					if (StrUtils.checkParam(metadata2)) {
						// 同步附带的信息
						Metadata metadata = new Metadata();
						metadata.setId(UUID.randomUUID().toString());
						metadata.setInstanceUuid(novaVm.getId());
						metadata.createdUser(serverJson.getString("user_id"));
						metadata.setDeleted(ConfigProperty.YES);
						metadata.setDeleteBy(null);
						metadata.setKey(null);
						metadata.setValue(null);
						metadata.setDeleteDate(null);
						metadatDao.add(metadata);
					}
					
					VmExtra vmExtra = new VmExtra();
					vmExtra.setOsUser(serverJson.getString("user_id"));
					vmExtra.setOsPasswd(null);
					vmExtra.setSshKey(null);
					vmExtra.setId(novaVm.getId());
					vmExtraDao.add(vmExtra);
					
					//同步网卡
					JSONArray portArray = portBiz.getPortArray(id, null, getClient());
					if (StrUtils.checkCollection(portArray)) {
						for (int j = 0; j < portArray.size(); j++) {
							JSONObject portJson = portArray.getJSONObject(j);
							syncVdcDataBiz.syncPort(portJson, id, target.getOwner());
						}
					}
					
				}
			}
		}
	}

	public void saveRoles() {

		List<User2Role> user2Roles = user2RoleDao.getAll(User2Role.class);
		List<String> list = new ArrayList<>();
		for (User2Role user2Role : user2Roles) {
			list.add(user2Role.getId());
		}
	}

	public void saveFloatingIp() {
		JSONArray floatingIpArray = floatingIpBiz.getFloatingIpArray(getClient());
		if (StrUtils.checkCollection(floatingIpArray)) {
			for (int i = 0; i < floatingIpArray.size(); i++) {
				JSONObject floatingIpJson = floatingIpArray.getJSONObject(i);
				syncVdcDataBiz.syncFloatingIp(floatingIpJson);
			}
		}
	}

	public void convert(Object target, JSONObject item) {
		if (target instanceof NovaVm) {
			NovaVm novaVm = (NovaVm) target;
			novaVm.setProjectId(item.getString("tenant_id"));
			novaVm.setOwner(getUserId(item.getString("user_id")));
			novaVm.setUuid(item.getString("id"));
			novaVm.setHost(item.getString("hostId"));
			novaVm.setHostName(item.getString("name"));
			String flavorId = item.getJSONObject("flavor").getString("id");
			NovaFlavor novaFlavor = novaFlavorDao.findById(NovaFlavor.class, flavorId);
			novaVm.setFlavorId(flavorId);
			novaVm.setVcpus(novaFlavor.getVcpus());
			novaVm.setMemory(novaFlavor.getRam());
			String imageId = item.getJSONObject("image").getString("id");
			novaVm.setImageRef(imageId);
			novaVm.setVmState(getVmState(item.getString("status")));
			System.out.println("=========>===============================" + imageId);
			System.out.println("=========>===============================" + item);
			Rules rules = rulesDao.findById(Rules.class, imageId);
			novaVm.setOsType(rules.getOsMirId());
			novaVm.createdUser(item.getString("user_id"));

			String azoneId = item.getJSONObject("metadata").getString("azone_uuid");
			if (StrUtils.checkParam(azoneId)) {
				novaVm.setAzoneId(azoneId);
			} else {
				String azoneName = item.getString("OS-EXT-AZ:availability_zone");
				Map<String, Object> paramsAzone = new HashMap<>();
				paramsAzone.put("zone", azoneName);
				Azone azone = azoneBiz.singleByClass(Azone.class, paramsAzone);
				novaVm.setAzoneId(azone.getUuid());
			}

			novaVm.setRamdisk(novaFlavor.getDisk());
			// 可用域，硬盘大小，操作系统类型，状态，电源
			// 保存网卡，关联网络
		} else if (target instanceof Project) {
			Project project = (Project) target;
			Boolean enabled = item.getBoolean("enabled");
			if (enabled) {
				project.setFlag(0);
			} else {
				project.setFlag(1);
			}
			project.setDomainId(item.getString("domain_id"));
			project.setDescription(item.getString("description"));
			project.createdUser("h3c");
		} else if (target instanceof User) {
			User user = (User) target;
			JSONObject userItem = item.getJSONObject("user");
			user.setCloudosId(userItem.getString("id"));

			String loginName = userItem.getString("name");
			user.setLoginName(loginName);
			System.out.println();
			if (StrUtils.checkParam(loginName)) {
				// if (loginName.matches("^[0-9a-zA-Z\u0000-\u00FF]+$")) {

				// } else {
				// System.out.println("不符合正则表达式" + loginName);
				// }
			} else {
				System.out.println("用户名不存在" + userItem.getString("id"));
				System.out.println("不存在登录名" + loginName);
			}
			String userName = userItem.getString("fullname");
			if (StrUtils.checkParam(userName)) {
				user.setUserName(userName);
			} else {
				System.out.println("用户名不存在" + loginName);
			}
			Boolean enabled = userItem.getBoolean("enabled");
			if (StrUtils.checkParam(enabled)) {
				if (enabled) {
					user.setStatus("0");
				} else {
					user.setStatus("1");
				}
			}
			user.setEmail(userItem.getString("email"));
			// 密码为空
			// String password = "root";

			user.createdUser("h3c");
			user.setCreatedBy("h3c");
		} else if (target instanceof FloatingIp) {
			FloatingIp floatingIp = (FloatingIp) target;
			floatingIp.setTenantId(item.getString("tenant_id"));
			floatingIp.setCloudosId(item.getString("id"));
			floatingIp.setFloatingIp(item.getString("floating_ip_address"));
			floatingIp.setNetworkId(item.getString("floating_network_id"));
			floatingIp.setNorm(item.getString("h3c_norm"));
			floatingIp.setFixedPortId(null);
			floatingIp.setOwner(getUserId(item.getString("h3c_user_id")));
			// port相关信息？？？？
		} else if (target instanceof Subnet) {
			Subnet subnet = (Subnet) target;
			subnet.setTenantId(item.getString("tenant_id"));
			// 外键
			String networkId = item.getString("network_id");
			List<Network> networks = networkBiz.findByPropertyName(Network.class, "cloudosId", networkId);
			Network network = networks.get(0);
			System.out.println(networkId.length());
			String subnetName = item.getString("name");
			subnet.setNetworkId(network.getId());
			subnet.setIpVersion(item.getInteger("ip_version"));
			subnet.setGatewayIp(item.getString("gateway_ip"));
			subnet.setCloudosId(item.getString("id"));
			subnet.setName(subnetName);
			subnet.createdUser("h3c");
		} else if (target instanceof Network) {
			Network network = (Network) target;
			Boolean external = item.getBoolean("router:external");
			network.setTenantId(item.getString("tenant_id"));
			network.setExternalNetworks(external);
			network.setMtu(item.getInteger("mtu"));
			network.createdUser("h3c");
			network.setCloudosId(item.getString("id"));
		} else if (target instanceof Volume) {
			Volume volume = (Volume) target;
			volume.setOwner2(getUserId(item.getString("user_id")));
			volume.setProjectId(item.getString("os-vol-tenant-attr:tenant_id"));
			JSONObject metadata = item.getJSONObject("metadata");
			volume.setAzoneId(metadata.getString("azone_uuid"));
			volume.setOwnerName(metadata.getString("user_name"));
			volume.setLableName(metadata.getString("azone_label"));
			volume.setUuid(item.getString("id"));
			String status = item.getString("status");
			if ("error".equals(status)) {
				volume.setStatus(ConfigProperty.VOLUME_STATE_ERROR);
			} else if ("available".equals(status)) {
				volume.setStatus(ConfigProperty.VOLUME_STATE_AVAILABLE);
			} else if ("in-use".equals(status)) {
				volume.setStatus(ConfigProperty.VOLUME_STATE_ATTACHED);
			}
			String volume_type = item.getString("volume_type");
			if (StrUtils.checkParam(volume_type)) {
				volume.setVolumeType("2");
			} else {
				volume.setVolumeType("1");
			}
			volume.setDeleted("0");
			// volume.setDescription(description);
			volume.createdUser(getUserId(volume.getOwner2()));
			Boolean multiattach = item.getBoolean("multiattach");
			if (multiattach) {
				volume.setAttachStatus("0");
			} else {
				volume.setAttachStatus("1");
			}
			JSONArray attachments = item.getJSONArray("attachments");
			for (int i = 0; i < attachments.size(); i++) {
				JSONObject attachment = attachments.getJSONObject(i);
				// 相同步云主机
				String host = attachment.getString("server_id");
				List<NovaVm> novaVms = novaVmBiz.findByPropertyName(NovaVm.class, "uuid", host);
				if (StrUtils.checkCollection(novaVms)) {
					NovaVm novaVm = novaVms.get(0);
					volume.setHost(novaVm.getId());
				}
				volume.setUuid(attachment.getString("volume_id"));
			}
		}
	}

	@SuppressWarnings("unused")
	private void saveQuota(Integer hardLimit, String classCode, Project project) {
		Project2Quota project2Quota = new Project2Quota();
		project2Quota.setClassCode(classCode);
		if (hardLimit < 0) {
			project2Quota.setHardLimit(0);
		} else {
			project2Quota.setHardLimit(hardLimit);
		}
		project2Quota.setTenantId(project.getId());
		project2Quota.setDeleted(0);
		project2Quota.createDate();

		QuotaUsed quotaUsed = new QuotaUsed(project.getId(), classCode2ClassId.get(classCode), classCode, hardLimit);
		quotaUsedBiz.add(quotaUsed);
		project2QuotaBiz.add(project2Quota);
	}

	// cloudos.tenant.normal.user = 5dcd7b3a-ec8a-4d80-be8b-8f4d65d71f3a
	// cloudos.tenant.comptroller = e449e969-5705-4d05-9e4d-64cd322c3ffd
	// cloudos.tenant.normal.manager = 67bcfea5-0428-44be-9219-37d56a5454a1
	// cloudos.tenant.cloud.manager = baae5505-cc96-4cf1-ad5a-a87285af90ae
	private String getRoleId(String groupId) {
		String roleId = null;
		CacheSingleton instance = CacheSingleton.getInstance();
		if (instance.getConfigValue("cloudos.tenant.comptroller").equals(groupId)
				|| instance.getConfigValue("cloudos.tenant.normal.user").equals(groupId)) {

		} else if (groupId.equals(instance.getConfigValue("cloudos.tenant.normal.manager"))) {
			roleId = CacheSingleton.getInstance().getTenantRoleId();
			return roleId;
		} else if (groupId.equals(instance.getConfigValue("cloudos.tenant.cloud.manager"))) {
			roleId = CacheSingleton.getInstance().getCloudRoleId();
			return roleId;
		}
		return roleId;
	}

	public void saveVolumes() {
		CloudosClient client = CloudosClient.createAdmin();
		List<Volume> volumes = volumeBiz.findByPropertyName(Volume.class, "deleted", "0");
		List<String> list = new ArrayList<>();
		for (Volume volume : volumes) {
			// list.add(novaVm.getId());
			list.add(volume.getUuid());
		}
		String uri = "/v2/{tenant_id}/volumes/detail";
		uri = HttpUtils.tranUrl(uri, rootProjectId);
		if (StrUtils.checkParam(client)) {
			JSONObject result = client.get(uri);
			if (ResourceHandle.judgeResponse(result)) {
				JSONArray array = HttpUtils.getJSONArray(result, "volumes");
				// JSONArray array = result.getJSONArray("record");
				if (StrUtils.checkParam(array)) {
					for (int i = 0; i < array.size(); i++) {
						JSONObject item = array.getJSONObject(i);
						String id = item.getString("id");
						if (!list.contains(id)) {
							Volume target = new Volume();
							InvokeSetForm.settingForm(item, target);
							target.setUuid(item.getString("id"));
							convert(target, item);
							volumeBiz.add(target);
						}
					}
				}
			} else {
				return;
			}
		}
	}

	public String getVmState(String state) {
		String vmState = null;
		if (StrUtils.checkParam(state)) {
			switch (state) {
			case "SHUTOFF":
				vmState = "state_stop";
				break;
			case "ACTIVE":
				vmState = "state_normal";
				break;
			case "BUILD":
				vmState = "state_creating";
				break;
			case "REBOOT":
				vmState = "state_rebooting";
				break;
			}
		}
		return vmState;
	}

	public String getUserId(String userId) {
		String iyunUserId = userMap.get(userId);
		if (iyunUserId == null) {
			return userId;
		}
		return iyunUserId;
	}

	public void diffProject() {
		List<Project> projects = projectBiz.getAll(Project.class);
		CloudosClient client = CloudosClient.createAdmin();
		for (Project project : projects) {
			if (StrUtils.checkParam(client)) {
				// String uri =
				// CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_PROJECTS);
				String uri = "/v3/projects/{project_id}";
				uri = HttpUtils.tranUrl(uri, project.getId());
				JSONObject result = client.get(uri);
				if (ResourceHandle.judgeResponse(result)) {
					JSONObject item = HttpUtils.getJSONObject(result, "project");
					String id = item.getString("id");
					if (!ConfigProperty.STOP_SET.contains(id)||!id.equals(CacheSingleton.getInstance().getCtTenantId())) {
						
						InvokeSetForm.settingForm(item, project);
						projectBiz.add(project);
					}

				} else {
					return;
				}
			}
		}
	}

	@Override
	public void revicePort() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 校正网卡数据
	 */
	// TODO: 2017/4/18 校正网卡数据
	/*public void revicePort() {
		List<String> keeplist = new ArrayList<>();
		
		//云主机网卡校正
		JSONArray serverArray = novaVmBiz.getServerArray(client);
		if (StrUtils.checkCollection(serverArray)) {
			for (int i = 0; i < serverArray.size(); i++) {
				JSONObject serverJson = serverArray.getJSONObject(i);
				String id = serverJson.getString("id");
				List<NovaVm> novaVms = novaVmBiz.findByPropertyName(NovaVm.class, "uuid", id);
				if (StrUtils.checkCollection(novaVms)) {
					JSONArray portArray = portBiz.getPortArray(id, null, client);
					if (StrUtils.checkParam(portArray)) {
						for (int j = 0; j < portArray.size(); j++) {
							JSONObject portJson = portArray.getJSONObject(j);
							syncPort(portJson, id, keeplist);
						}
					}
				}
			}
		}
		
		//路由器网卡校正-外部网关和接口
		JSONArray routerArray = routeBiz.getRouteArray(client);
		if (StrUtils.checkCollection(routerArray)) {
			for (int i = 0; i < routerArray.size(); i++) {
				JSONObject rtJson = routerArray.getJSONObject(i);
				String rtCdId = rtJson.getString("id");
				List<Route> routes = routeBiz.findByPropertyName(Route.class, "cloudosId", rtCdId);
				if (StrUtils.checkCollection(routes)) {
					Route route = routes.get(0);
					JSONArray portArray = portBiz.getPortArray(rtCdId, null, client);
					if (StrUtils.checkParam(portArray)) {
						for (int j = 0; j < portArray.size(); j++) {
							JSONObject portJson = portArray.getJSONObject(j);
							String networkCdId = portJson.getString("network_id");
							if (StrUtils.checkParam(networkCdId)) {
								List<Network> networks = networkBiz.findByPropertyName(Network.class,
										"cloudosId", networkCdId);
								if (StrUtils.checkCollection(networks)) {
									String deviceOwner = portJson.getString("device_owner");
									if ("network:router_gateway".equals(deviceOwner)) {//同步外部网关信息
										String portId = syncPort(portJson, route.getId(), keeplist);
										if (!StrUtils.checkParam(route.getGwPortId()) || !route.getGwPortId().equals
												(portId)) {
											route.setGwPortId(portId);
											routeBiz.update(route);
										}
									} else if ("network:router_interface".equals(deviceOwner)) {
										Network network = networks.get(0);
										String routeId = network.getRouteId();
										syncPort(portJson, route.getId(), keeplist);
										if (!StrUtils.checkParam(routeId) || !routeId.equals(route.getId())) {
											network.setRouteId(routeId);
											networkBiz.update(network);
										}
									}
								}
							}
						}
					}
				}
			}
			
		}
		
		//负载均衡网卡校正
		JSONArray vipArray = vlbPoolBiz.getVipArray(client);
		if (StrUtils.checkCollection(vipArray)) {
			for (int i = 0; i < vipArray.size(); i++) {
				JSONObject vipJson = vipArray.getJSONObject(i);
				String poolCdId = vipJson.getString("pool_id");
				List<VlbPool> pools = vlbPoolBiz.findByPropertyName(VlbPool.class, "cloudosId", poolCdId);
				if (StrUtils.checkCollection(pools)) {
					VlbPool pool = pools.get(0);
					VlbVip vip = vipDao.findById(VlbVip.class, pool.getVipId());
					String portCdId = vipJson.getString("port_id");
					JSONObject portJson = portBiz.getPortJson(portCdId, client);
					String portId = syncPort(portJson, pool.getId(), keeplist);
					if (!StrUtils.checkParam(vip.getPortId()) || !vip.getPortId().equals(portId)) {
						vip.setPortId(portId);
						vipDao.update(vip);
					}
				}
			}
		}
		
		//公网ip校正
		JSONArray floatingIpArray = floatingIpBiz.getFloatingIpArray(client);
		if (StrUtils.checkCollection(floatingIpArray)) {
			for (int i = 0; i < floatingIpArray.size(); i++) {
				JSONObject floatingIpJson = floatingIpArray.getJSONObject(i);
				String floatingIpCdId = floatingIpJson.getString("id");
				List<FloatingIp> floatingIps = floatingIpBiz.findByPropertyName(FloatingIp.class, "cloudosId", floatingIpCdId);
				if (StrUtils.checkCollection(floatingIps)) {
					FloatingIp floatingIp = floatingIps.get(0);
					JSONArray portArray = portBiz.getPortArray(floatingIpCdId, null, client);
					if (StrUtils.checkCollection(portArray)) {
						JSONObject portJson = portArray.getJSONObject(0);
						String portId = syncPort(portJson, floatingIp.getId(), keeplist);
						floatingIp.setFloatingPortId(portId);
						//获取用户本地id
						String userCdId = floatingIpJson.getString("h3c_user_id");
						if (null != userCdId && !"".equals(userCdId)) {
							List<User> users = userBiz.findByPropertyName(User.class, "cloudosId", userCdId);
							Port port = portBiz.findById(Port.class, portId);
							port.setUserId(users.get(0).getId());
							portBiz.update(port);
						}
					}
					String fixPortCdId = floatingIpJson.getString("port_id");
					String fixedIp = floatingIpJson.getString("fixed_ip_address");
					String routeCdId = floatingIpJson.getString("router_id");
					//找出分配资源的ip对应的网卡和关联路由（检查分配资源的ip、路由、公网是否已经同步）
					if (null != fixPortCdId && !"".equals(fixPortCdId)) {
						Map<String, Object> queryMap = new HashMap<>();
						queryMap.put("cloudosId", fixPortCdId);
						Port port = portBiz.singleByClass(Port.class, queryMap);
						queryMap.put("cloudosId", routeCdId);
						Route route = routeBiz.singleByClass(Route.class, queryMap);
						if (StrUtils.checkParam(port)) {
							floatingIp.setFixedPortId(port.getId());
							floatingIp.setRouterId(route.getId());
							floatingIp.setFixedIp(fixedIp);
						} else {
							floatingIp.setFixedPortId(null);
							floatingIp.setRouterId(null);
							floatingIp.setFixedIp(null);
						}
					} else {
						floatingIp.setFixedPortId(null);
						floatingIp.setRouterId(null);
						floatingIp.setFixedIp(null);
					}
					floatingIpBiz.update(floatingIp);
				}
			}
		}
		
		//删除多余网卡
		List<Port> ports = portBiz.getAll(Port.class);
		if (StrUtils.checkCollection(keeplist)) {
			for (Port port : ports) {
				String id = port.getId();
				if (!keeplist.contains(id)) {
					List<IpAllocation> ipAllocations = ipusedDao.findByPropertyName(IpAllocation.class, "portId", id);
					if (StrUtils.checkCollection(ipAllocations)) {
						for (IpAllocation ipAllocation : ipAllocations) {
							ipusedDao.delete(ipAllocation);
						}
					}
					portBiz.delete(port);
				}
			}
		}
	}*/
	
	/*public String syncPort(JSONObject ptJson, String deviceId, List<String> keeplist){
		String portId = null;
		String ptCdId = ptJson.getString("id");
		String ptName = ptJson.getString("name");
		if (!StrUtils.checkParam(ptName)) {
			ptName = UUID.randomUUID().toString();
		}
		Boolean stateUp = ptJson.getBoolean("admin_state_up");
		String tenantId = ptJson.getString("tenant_id");
		String mac = ptJson.getString("mac_address");
		String status = ptJson.getString("status");
		String deviceOwner = ptJson.getString("device_owner");
		String subnetId = null;
		String routeId = null;
		String ipAddress = null;
		JSONArray fixed_ips = ptJson.getJSONArray("fixed_ips");
		if (StrUtils.checkCollection(fixed_ips)) {
			JSONObject ip = fixed_ips.getJSONObject(0);
			ipAddress = ip.getString("ip_address");
			String subnetCdId = ip.getString("subnet_id");
			List<Subnet> subnets = subnetDao.findByPropertyName(Subnet.class, "cloudosId",
					subnetCdId);
			Subnet subnet = subnets.get(0);
			subnetId = subnet.getId();
			Network network = networkBiz.findById(Network.class, subnet.getNetworkId());
			routeId = network.getRouteId();
		}
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("cloudosId", ptCdId);
		List<Port> ports = portBiz.findByPropertyName(Port.class, "cloudosId", ptCdId);
		if (!StrUtils.checkCollection(ports)){
			Port port = new Port();
			port.setCloudosId(ptCdId);
			port.setName(ptName);
			port.setTenantId(tenantId);
			port.setStatus(status);
			port.setAdminStateUp(stateUp);
			port.setDeviceOwner(deviceOwner);
			port.setMacAddress(mac);
			port.setDeviceId(deviceId);
			port.setRouteId(routeId);
			port.createdUser(ConfigProperty.SYSTEM_FLAG);
			portId = portBiz.add(port);
			IpAllocation ipUsed = new IpAllocation();
			ipUsed.setSubnetId(subnetId);
			ipUsed.setIpAddress(ipAddress);
			ipUsed.setPortId(portId);
			ipusedDao.add(ipUsed);
		} else {
			Port port = ports.get(0);
			port.setName(ptName);
			port.setTenantId(tenantId);
			port.setStatus(status);
			port.setAdminStateUp(stateUp);
			port.setDeviceOwner(deviceOwner);
			port.setMacAddress(mac);
			port.setDeviceId(deviceId);
			portBiz.update(port);
			List<IpAllocation> ipAllocations = ipusedDao.findByPropertyName(IpAllocation.class, "portId", port.getId());
			if (StrUtils.checkCollection(ipAllocations)) {
				IpAllocation ipAllocation = ipAllocations.get(0);
				ipAllocation.setSubnetId(subnetId);
				ipAllocation.setIpAddress(ipAddress);
				ipusedDao.update(ipAllocation);
				if (ipAllocations.size() > 1) {
					for (int i = 1; i < ipAllocations.size(); i++) {
						IpAllocation allocation = ipAllocations.get(i);
						ipusedDao.delete(allocation);
					}
				}
			} else {
				IpAllocation ipUsed = new IpAllocation();
				ipUsed.setSubnetId(subnetId);
				ipUsed.setIpAddress(ipAddress);
				ipUsed.setPortId(port.getId());
				ipusedDao.add(ipUsed);
			}
			portId = port.getId();
		}
		keeplist.add(portId);
		return portId;
	}*/


	public CloudosClient getClient() {
		return CloudosClient.createAdmin();
	}

}
